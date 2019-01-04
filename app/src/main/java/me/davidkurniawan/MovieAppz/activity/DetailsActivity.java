package me.davidkurniawan.MovieAppz.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import me.davidkurniawan.MovieAppz.R;
import com.fxn.cue.enums.Type;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.davidkurniawan.MovieAppz.adapters.ReviewAdapter;
import me.davidkurniawan.MovieAppz.adapters.TrailerAdapter;
import me.davidkurniawan.MovieAppz.database.FavouriteMovies;
import me.davidkurniawan.MovieAppz.database.MovieDatabase;
import me.davidkurniawan.MovieAppz.model.Movie;
import me.davidkurniawan.MovieAppz.model.Review;
import me.davidkurniawan.MovieAppz.model.ReviewResponse;
import me.davidkurniawan.MovieAppz.model.Trailer;
import me.davidkurniawan.MovieAppz.model.TrailerResponse;
import me.davidkurniawan.MovieAppz.utils.AppExecutors;
import me.davidkurniawan.MovieAppz.utils.Toaster;
import me.davidkurniawan.MovieAppz.viewmodel.DetailsViewModel;
import me.davidkurniawan.MovieAppz.viewmodel.DetailsViewModelFactory;
import me.davidkurniawan.MovieAppz.viewmodel.ReviewsViewModel;
import me.davidkurniawan.MovieAppz.viewmodel.ReviewsViewModelFactory;
import me.davidkurniawan.MovieAppz.viewmodel.TrailerViewModel;
import me.davidkurniawan.MovieAppz.viewmodel.TrailerViewModelFactory;
import retrofit2.Response;

import static me.davidkurniawan.MovieAppz.utils.AppConstants.IMAGE_BASE_URL;
import static me.davidkurniawan.MovieAppz.utils.AppConstants.IMAGE_SIZE;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.movie_poster_iv)
    ImageView moviePoster;
    @BindView(R.id.original_title_tv)
    TextView originalTitleTextView;
    @BindView(R.id.overview_tv)
    TextView overviewTextView;
    @BindView(R.id.release_date_tv)
    TextView releaseDateTextView;
    @BindView(R.id.vote_average_tv)
    TextView voteAverageTextView;
    @BindView(R.id.details_pb)
    ProgressBar detailsProgressBar;
    @BindView(R.id.reviews_rv)
    RecyclerView reviewRecyclerView;
    @BindView(R.id.trailer_rv)
    RecyclerView trailerRecyclerView;
    FavouriteMovies favouriteMovie;
    private MovieDatabase mDatabase;
    private int movieId;
    private int favId;
    private int favMovieId;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private boolean isFav;
    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
        extras = getIntent().getExtras();
        mDatabase = MovieDatabase.getInstance(this);

        if (extras != null) {
            movieId = extras.getInt(getString(R.string.movie_extra_id));
            favMovieId = extras.getInt(getString(R.string.fav_extra_id));
            favId = extras.getInt(getString(R.string.extra_favourite_movie));
        }

        checkIsFav();
        populateUI(extras);
    }

    private void checkIsFav() {
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                if (favMovieId != 0) {
                    favouriteMovie = mDatabase.favouriteMoviesDao().loadFavouriteMovieByMovieId(favMovieId);
                    isFav = favouriteMovie != null && favouriteMovie.getMovieId() == favMovieId;
                } else if (movieId != 0) {
                    favouriteMovie = mDatabase.favouriteMoviesDao().loadFavouriteMovieByMovieId(movieId);
                    isFav = favouriteMovie != null && favouriteMovie.getMovieId() == movieId;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_menu, menu);
        if (isFav) {
            menu.findItem(R.id.action_star).setIcon(R.drawable.ic_action_star_checked);
        } else {
            menu.findItem(R.id.action_star).setIcon(R.drawable.ic_action_star);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_star:
                if (isFav) {
                    item.setIcon(R.drawable.ic_action_star);
                    Toaster.makeToast(getString(R.string.removed_favourites), Type.SUCCESS, DetailsActivity.this);
                    removeFavouriteFromDatabase();
                    isFav = false;
                } else {
                    item.setIcon(R.drawable.ic_action_star_checked);
                    Toaster.makeToast(getString(R.string.added_favourite), Type.SUCCESS, DetailsActivity.this);
                    addFavouritesToDatabase();
                    isFav = true;
                }
                return true;
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void populateUI(Bundle extras) {
        if (extras != null) {
            if (movieId != 0) {
                if (!isFav) {
                    Movie movie = extras.getParcelable(getString(R.string.extra_movie));
                    if (movie != null) {
                        loadMovies(movie);
                        callReviews(movie.getId());
                        callTrailers(movie.getId());
                        detailsProgressBar.setVisibility(View.GONE);
                    } else {
                        closeOnError();
                    }
                }
            } else if (favMovieId != 0) {
                loadFavouriteMovies();
                callReviews(favMovieId);
                callTrailers(favMovieId);
            }
        }
    }

    private void loadFavouriteMovies() {
        DetailsViewModelFactory factory = new DetailsViewModelFactory(mDatabase, favId);
        DetailsViewModel viewModel = ViewModelProviders.of(this, factory).get(DetailsViewModel.class);
        viewModel.getFavouriteMovies().observe(this, new Observer<FavouriteMovies>() {
            @Override
            public void onChanged(@Nullable FavouriteMovies favouriteMovies) {
                if (favouriteMovies != null) {
                    detailsProgressBar.setVisibility(View.GONE);
                    setFavouriteMovies(favouriteMovies);
                }
            }
        });
    }

    private void addFavouritesToDatabase() {
        int movieId;
        String title;
        String originalTitle;
        String posterPath;
        String overview;
        String releaseDate;
        String voteAverage;
        if (extras != null) {
            Movie movie = extras.getParcelable(getString(R.string.extra_movie));
            if (movie != null) {
                movieId = movie.getId();
                title = movie.getTitle();
                originalTitle = movie.getOriginal_title();
                posterPath = movie.getPoster_path();
                overview = movie.getOverview();
                releaseDate = movie.getRelease_date();
                voteAverage = movie.getVote_average();

                final FavouriteMovies favouriteMovies =
                        new FavouriteMovies(movieId, title, originalTitle, posterPath, overview, releaseDate, voteAverage);

                AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        mDatabase.favouriteMoviesDao().addFavouriteMovie(favouriteMovies);
                    }
                });
            } else {
                Toaster.makeToast(getString(R.string.add_favourite_error), Type.INFO, DetailsActivity.this);
            }
        } else {
            Toaster.makeToast(getString(R.string.add_favourite_error), Type.INFO, DetailsActivity.this);
        }
    }

    private void removeFavouriteFromDatabase() {
        AppExecutors.getInstance().getDiskIO().execute(new Runnable() {
            @Override
            public void run() {
                mDatabase.favouriteMoviesDao().deleteFavouriteMovieById(favId);
            }
        });
    }

    private void loadMovies(Movie movie) {
        String posterPath = movie.getPoster_path();
        String imagePath = IMAGE_BASE_URL + IMAGE_SIZE + posterPath;

        Picasso.with(DetailsActivity.this)
                .load(imagePath)
                .error(R.drawable.default_image_view)
                .placeholder(R.drawable.default_image_view)
                .into(moviePoster);

        String title = movie.getTitle();
        String originalTitle = movie.getOriginal_title();
        String overview = movie.getOverview();
        String releaseDate = movie.getRelease_date();
        String voteAverage = movie.getVote_average();

        setTitle(title);

        originalTitleTextView.setText(originalTitle);
        overviewTextView.setText(overview);
        releaseDateTextView.setText(releaseDate);
        voteAverageTextView.setText(voteAverage);
    }

    private void setFavouriteMovies(FavouriteMovies favouriteMovies) {
        String posterPath = favouriteMovies.getPoster_path();
        String imagePath = IMAGE_BASE_URL + IMAGE_SIZE + posterPath;

        Picasso.with(DetailsActivity.this)
                .load(imagePath)
                .error(R.drawable.default_image_view)
                .placeholder(R.drawable.default_image_view)
                .into(moviePoster);

        String title = favouriteMovies.getTitle();
        String originalTitle = favouriteMovies.getOriginal_title();
        String overview = favouriteMovies.getOverview();
        String releaseDate = favouriteMovies.getRelease_date();
        String voteAverage = favouriteMovies.getVote_average();

        setTitle(title);

        originalTitleTextView.setText(originalTitle);
        overviewTextView.setText(overview);
        releaseDateTextView.setText(releaseDate);
        voteAverageTextView.setText(voteAverage);
    }

    private void closeOnError() {
        Toaster.makeToast(getString(R.string.detail_error_message), Type.DANGER, DetailsActivity.this);
        finish();
    }

    private void doReview() {
        reviewAdapter = new ReviewAdapter(this);
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        reviewRecyclerView.setHasFixedSize(true);
        reviewRecyclerView.setAdapter(reviewAdapter);
        reviewRecyclerView.setLayoutManager(mLayoutManager);
    }

    private void callReviews(int movieId) {
        int currentPage = 1;
        ReviewsViewModelFactory factory = new ReviewsViewModelFactory(String.valueOf(movieId), currentPage);
        ReviewsViewModel viewModel = ViewModelProviders.of(this, factory).get(ReviewsViewModel.class);
        viewModel.getReviewResponse().observe(this, new Observer<Response<ReviewResponse>>() {
            @Override
            public void onChanged(@Nullable Response<ReviewResponse> response) {
                if (response != null) {
                    List<Review> reviewList = getReviewResponse(response);
                    doReview();
                    reviewAdapter.setReviews(reviewList);
                } else {
                    Toaster.makeToast(getString(R.string.detail_error_message), Type.DANGER, DetailsActivity.this);
                }
            }
        });
    }

    private List<Review> getReviewResponse(Response<ReviewResponse> response) {
        ReviewResponse reviewResponse = response.body();
        return reviewResponse != null ? reviewResponse.getReviews() : null;
    }

    private void doTrailer() {
        trailerAdapter = new TrailerAdapter(this);
        LinearLayoutManager trailerLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        trailerRecyclerView.setHasFixedSize(true);
        trailerRecyclerView.setAdapter(trailerAdapter);
        trailerRecyclerView.setLayoutManager(trailerLayoutManager);
    }

    private void callTrailers(int movieId) {
        TrailerViewModelFactory factory = new TrailerViewModelFactory(String.valueOf(movieId));
        TrailerViewModel viewModel = ViewModelProviders.of(this, factory).get(TrailerViewModel.class);
        viewModel.getTrailerResponse().observe(this, new Observer<Response<TrailerResponse>>() {
            @Override
            public void onChanged(@Nullable Response<TrailerResponse> response) {
                if (response != null) {
                    List<Trailer> trailerList = getTrailerResponse(response);
                    doTrailer();
                    trailerAdapter.setTrailerList(trailerList);
                } else {
                    Toaster.makeToast(getString(R.string.trailer_error_message), Type.DANGER, DetailsActivity.this);
                }
            }
        });
    }

    private List<Trailer> getTrailerResponse(Response<TrailerResponse> response) {
        TrailerResponse trailerResponse = response.body();
        return trailerResponse != null ? trailerResponse.getTrailerList() : null;
    }
}
