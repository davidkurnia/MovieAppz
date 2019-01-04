package me.davidkurniawan.MovieAppz.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import me.davidkurniawan.MovieAppz.database.FavouriteMovies;
import me.davidkurniawan.MovieAppz.database.MovieDatabase;

public class DetailsViewModel extends ViewModel {

    private LiveData<FavouriteMovies> favouriteMovies;

    DetailsViewModel(MovieDatabase database, int id) {
        favouriteMovies = database.favouriteMoviesDao().loadFavouriteMovieById(id);
    }

    public LiveData<FavouriteMovies> getFavouriteMovies() {
        return favouriteMovies;
    }
}
