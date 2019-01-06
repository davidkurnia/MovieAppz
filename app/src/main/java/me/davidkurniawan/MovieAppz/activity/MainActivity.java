package me.davidkurniawan.MovieAppz.activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.Toast;

import me.davidkurniawan.MovieAppz.R;
import com.fxn.cue.enums.Type;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.davidkurniawan.MovieAppz.adapters.MoviesAdapter;
import me.davidkurniawan.MovieAppz.fragment.FavouriteMoviesFragment;
import me.davidkurniawan.MovieAppz.helper.ApiClient;
import me.davidkurniawan.MovieAppz.helper.AutoFitGridLayoutManager;
import me.davidkurniawan.MovieAppz.helper.Preference;
import me.davidkurniawan.MovieAppz.helper.WebService;
import me.davidkurniawan.MovieAppz.model.Movie;
import me.davidkurniawan.MovieAppz.model.MovieResponse;
import me.davidkurniawan.MovieAppz.utils.NetworkUtil;
import me.davidkurniawan.MovieAppz.utils.Toaster;
import me.davidkurniawan.MovieAppz.viewmodel.MovieViewModel;
import me.davidkurniawan.MovieAppz.viewmodel.MovieViewModelFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static me.davidkurniawan.MovieAppz.helper.Preference.LANGUAGE;
import static me.davidkurniawan.MovieAppz.utils.AppConstants.api_key;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.ListItemClickListener {

    @BindView(R.id.progressBar)
    ProgressBar progressBar;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.swipe_refresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    @BindView(R.id.fab)
    FloatingActionButton fab;

    private MoviesAdapter mAdapter;
    private AutoFitGridLayoutManager layoutManager;
    private WebService webService;
    private boolean isPopular = true;
    private int currentPage = 1;
    private Parcelable recyclerViewState;
    public String language = "id";
    private Preference preference;
    private PopupMenu popupMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            isPopular = savedInstanceState.getBoolean(getString(R.string.save_state));
        }

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        webService = ApiClient.getRetrofit().create(WebService.class);
        loadProgressBar(true);
        setupViewModels();
        preference = new Preference(MainActivity.this);


        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (mAdapter != null) {
                    mAdapter.emptyMovieList();
                }
                currentPage = 1;
                setupViewModels();
            }
        });

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupMenu = new PopupMenu(MainActivity.this, v);

                if(language == "id"){
                    popupMenu.getMenuInflater().inflate(R.menu.menu_fab_id, popupMenu.getMenu());
                } else {
                    popupMenu.getMenuInflater().inflate(R.menu.menu_fab, popupMenu.getMenu());
                }



                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.sort_by_popular_action:
                                findViewById(R.id.favourite_fragment_container).setVisibility(GONE);
                                recyclerView.setVisibility(VISIBLE);
                                //setTitle(getString(R.string.sort_by_most_popular));
                                setTitle(language == "id" ? "Terpopuler" : "Most Popular");
                                loadPopularMovies(language,true);
                                isPopular = true;
                                return true;
                            case R.id.sort_by_rated_action:
                                findViewById(R.id.favourite_fragment_container).setVisibility(GONE);
                                recyclerView.setVisibility(VISIBLE);
                                //setTitle(getString(R.string.sort_by_most_rated));
                                setTitle(language == "id" ? "Teratas" : "Top Rated");
                                loadTopRatedMovies(language,true);
                                isPopular = false;
                                return true;
                            case R.id.view_favourites:
                                // Create new fragment and transaction
                                //setTitle(getString(R.string.favourite_activity_label));
                                setTitle("Favoritku");
                                findViewById(R.id.favourite_fragment_container).setVisibility(VISIBLE);
                                recyclerView.setVisibility(GONE);
                                Fragment newFragment = new FavouriteMoviesFragment();
                                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                                transaction.replace(R.id.favourite_fragment_container, newFragment);
                                transaction.addToBackStack(null);
                                transaction.commit();
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (layoutManager != null) {
            recyclerViewState = layoutManager.onSaveInstanceState();
            outState.putParcelable(getString(R.string.recyclerview_save_state), recyclerViewState);
            outState.putBoolean(getString(R.string.save_state), isPopular);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        recyclerViewState = savedInstanceState.getParcelable(getString(R.string.recyclerview_save_state));
        isPopular = savedInstanceState.getBoolean(getString(R.string.save_state));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (recyclerViewState != null) {
            layoutManager.onRestoreInstanceState(recyclerViewState);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.EN) {
            currentPage = 1;
            language = "en";
            preference.saveSPString(LANGUAGE, language);

            if(isPopular){
                findViewById(R.id.favourite_fragment_container).setVisibility(GONE);
                recyclerView.setVisibility(VISIBLE);
                setTitle(getString(R.string.sort_by_most_popular));
                loadPopularMovies("en",true);
                isPopular = true;
            }
            else {
                findViewById(R.id.favourite_fragment_container).setVisibility(GONE);
                recyclerView.setVisibility(VISIBLE);
                setTitle(getString(R.string.sort_by_most_rated));
                loadTopRatedMovies("en",true);
                isPopular = false;
            }
            //Intent intent = new Intent(getApplicationContext(), AboutActivity.class);
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            //startActivity(intent);
            return true;
        }
        else if(id== R.id.ID ) {
            language = "id";
            currentPage = 1;
            if(isPopular){

                findViewById(R.id.favourite_fragment_container).setVisibility(GONE);
                recyclerView.setVisibility(VISIBLE);
                setTitle("Terpopuler");
                loadPopularMovies("id",true);
                isPopular = true;
            }
            else {

                findViewById(R.id.favourite_fragment_container).setVisibility(GONE);
                recyclerView.setVisibility(VISIBLE);
                setTitle("Teratas");
                loadTopRatedMovies("id",true);
                isPopular = false;
            }
            return true;
        }

        Log.d("gantibahasa",language == null ? "kosong" : language);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onListItemClick(Movie movie) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(this, DetailsActivity.class);
        extras.putParcelable(getString(R.string.extra_movie), movie);
        extras.putInt(getString(R.string.movie_extra_id), movie.getId());
        intent.putExtras(extras);
        startActivity(intent);
    }

    private void populateUI() {
        mAdapter = new MoviesAdapter(this, this);
        recyclerView.setHasFixedSize(true);

        layoutManager = new AutoFitGridLayoutManager(this, 500);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        swipeRefreshLayout.setRefreshing(false);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            boolean loading = true;
            int firstVisibleItem, visibleItemCount, totalItemCount;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0) {
                    fab.setVisibility(GONE);
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    firstVisibleItem = layoutManager.findFirstVisibleItemPosition();

                    if (loading) {
                        if ((visibleItemCount + firstVisibleItem) >= totalItemCount) {
                            loading = false;
                            currentPage += 1;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    loadMoreMovies(currentPage);
                                }
                            }, 1000);
                        }
                    }
                } else if (dy < 0) {
                    fab.setVisibility(VISIBLE);
                } else {
                    fab.setVisibility(VISIBLE);
                }
            }
        });
    }

    private void setupViewModels() {
        loadProgressBar(true);
        if (NetworkUtil.isNetworkAvailable(MainActivity.this)) {
            if (!isPopular) {
                loadTopRatedMovies(language,true);
            } else {
                loadPopularMovies(language,true);
            }
        } else {
            loadProgressBar(false);
            swipeRefreshLayout.setRefreshing(false);
            Toaster.makeToast(getString(R.string.connection_error_message), Type.DANGER, MainActivity.this);
        }
    }

    private void loadPopularMovies(String lan, final boolean clearList) {
        MovieViewModelFactory factory = new MovieViewModelFactory(currentPage, lan);
        MovieViewModel viewModel = ViewModelProviders.of(this, factory).get(MovieViewModel.class);
        (new MovieViewModel(currentPage,lan)).getPopularMovieResponse().observe(this, new Observer<Response<MovieResponse>>() {
            @Override
            public void onChanged(@Nullable Response<MovieResponse> movieResponse) {
                swipeRefreshLayout.setRefreshing(false);
                loadProgressBar(false);



                Toast.makeText(MainActivity.this, "BAHASA : " + language, Toast.LENGTH_SHORT).show();

                if (movieResponse != null) {
                    if (movieResponse.isSuccessful()) {
                        List<Movie> movies = getMovieResponse(movieResponse);

                        for (Movie item :
                                movies) {
                            Log.d("movies",item.getOverview());
                        }

                        if (movies != null) {
                            populateUI();
                            mAdapter.setMovieList(movies, clearList);
                            if (recyclerViewState != null) {
                                layoutManager.onRestoreInstanceState(recyclerViewState);
                            }
                        }
                    } else {
                        switch (movieResponse.code()) {
                            case 401:
                                Toaster.makeToast(getString(R.string.error_401), Type.WARNING, MainActivity.this);
                                break;
                            case 404:
                                Toaster.makeToast(getString(R.string.error_404), Type.WARNING, MainActivity.this);
                                break;
                        }
                    }
                } else {
                    Toaster.makeToast(getString(R.string.connection_error_message), Type.DANGER, MainActivity.this);
                }
            }
        });
    }

    private void loadTopRatedMovies(String lan, final boolean clearList) {
        MovieViewModelFactory factory = new MovieViewModelFactory(currentPage, lan);
        MovieViewModel viewModel = ViewModelProviders.of(this, factory).get(MovieViewModel.class);
        //viewModel
        (new MovieViewModel(currentPage,lan)).getTopRatedMoviesResponse().observe(this, new Observer<Response<MovieResponse>>() {
            @Override
            public void onChanged(@Nullable Response<MovieResponse> movieResponse) {
                swipeRefreshLayout.setRefreshing(false);
                loadProgressBar(false);
                if (movieResponse != null) {

                    for (Movie item :
                            movieResponse.body().getMovieList()) {

                        Log.d("loadtop",item.getOverview());

                    }

                    Toast.makeText(MainActivity.this, "BAHASA : " + language, Toast.LENGTH_SHORT).show();

                    if (movieResponse.isSuccessful()) {
                        List<Movie> movies = getMovieResponse(movieResponse);
                        if (movies != null) {
                            populateUI();
                            mAdapter.setMovieList(movies,clearList);
                            if (recyclerViewState != null) {
                                layoutManager.onRestoreInstanceState(recyclerViewState);
                            }
                        }
                    } else {
                        switch (movieResponse.code()) {
                            case 401:
                                Toaster.makeToast(getString(R.string.error_401), Type.WARNING, MainActivity.this);
                                break;
                            case 404:
                                Toaster.makeToast(getString(R.string.error_404), Type.WARNING, MainActivity.this);
                                break;
                            default:
                                Toaster.makeToast(getString(R.string.connection_error_message), Type.DANGER, MainActivity.this);
                        }
                    }
                } else {
                    Toaster.makeToast(getString(R.string.connection_error_message), Type.DANGER, MainActivity.this);
                }
            }
        });
    }

    private List<Movie> getMovieResponse(Response<MovieResponse> response) {
        MovieResponse movieResponse = response.body();
        List<Movie> result = movieResponse != null ? movieResponse.getMovieList() : null;

        String tidakTersedia = language == "id" ? "Tidak Tersedia" : "Not Available";

        if(result != null){

            for (Movie item:result) {
                String over = item.getOverview();
                item.setOverview(over.isEmpty()? tidakTersedia : over );
            }

        }

        return  result;
    }

    public void loadMoreMovies(int currentPage) {
        loadProgressBar(true);
        Call<MovieResponse> call;
        if (!isPopular) {
            call = webService.getTopRatedMovies(currentPage, api_key, language);
        } else {
            call = webService.getPopularMovies(currentPage, api_key, language);
        }
        call.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(@NonNull Call<MovieResponse> call, @NonNull Response<MovieResponse> response) {
                loadProgressBar(false);
                Toast.makeText(MainActivity.this, "BAHASA : " + language, Toast.LENGTH_SHORT).show();
                swipeRefreshLayout.setRefreshing(false);
                List<Movie> movies = getMovieResponse(response);
                if (movies != null) {
                    mAdapter.setMovieList(movies,false);
                }
            }

            @Override
            public void onFailure(@NonNull Call<MovieResponse> call, @NonNull Throwable t) {
                loadProgressBar(false);
                swipeRefreshLayout.setRefreshing(false);
                Toaster.makeToast(getString(R.string.connection_error_message), Type.DANGER, MainActivity.this);
            }
        });
    }

    private void loadProgressBar(boolean load) {
        if (load) {
            progressBar.setVisibility(VISIBLE);
        } else {
            progressBar.setVisibility(GONE);
        }
    }
}
