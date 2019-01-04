package me.davidkurniawan.MovieAppz.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import me.davidkurniawan.MovieAppz.R;
import com.fxn.cue.enums.Type;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.davidkurniawan.MovieAppz.database.FavouriteMovies;
import me.davidkurniawan.MovieAppz.utils.Toaster;

import static me.davidkurniawan.MovieAppz.utils.AppConstants.IMAGE_BASE_URL;
import static me.davidkurniawan.MovieAppz.utils.AppConstants.IMAGE_SIZE;

public class FavouriteMoviesAdapter extends RecyclerView.Adapter<FavouriteMoviesAdapter.ViewHolder> {

    private final ListItemClickListener mOnClickItemListener;
    private Context mContext;
    private List<FavouriteMovies> favouriteMoviesList;

    public FavouriteMoviesAdapter(Context context, ListItemClickListener listener) {
        this.mContext = context;
        this.mOnClickItemListener = listener;
        favouriteMoviesList = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.favourite_single_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        FavouriteMovies favouriteMovies = favouriteMoviesList.get(position);
        if (favouriteMovies != null) {
            String originalTitle = favouriteMovies.getOriginal_title();
            String posterPath = favouriteMovies.getPoster_path();
            holder.bind(originalTitle, posterPath);
        } else {
            Toaster.makeToast(mContext.getString(R.string.no_data), Type.DANGER, mContext);
        }
    }

    @Override
    public int getItemCount() {
        return favouriteMoviesList.size();
    }

    private List<FavouriteMovies> getFavouriteMoviesList() {
        return favouriteMoviesList;
    }

    public void setFavouriteMoviesList(List<FavouriteMovies> favouriteMoviesList) {
        this.favouriteMoviesList = favouriteMoviesList;
        notifyDataSetChanged();
    }

    public interface ListItemClickListener {

        void onListItemClick(FavouriteMovies favouriteMovies);
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        @BindView(R.id.fav_item_movie_poster_iv)
        ImageView moviePoster;
        @BindView(R.id.fav_item_original_title_tv)
        TextView originalTitle;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);
        }

        private void bind(String title, String posterPath) {
            originalTitle.setText(title);
            String imagePath = IMAGE_BASE_URL + IMAGE_SIZE + posterPath;
            Picasso.with(mContext)
                    .load(imagePath)
                    .error(R.drawable.default_image_view)
                    .placeholder(R.drawable.default_image_view)
                    .into(moviePoster);
        }

        @Override
        public void onClick(View v) {
            int clickedItemPosition = getAdapterPosition();
            mOnClickItemListener.onListItemClick(getFavouriteMoviesList().get(clickedItemPosition));
        }
    }
}
