package me.davidkurniawan.MovieAppz.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import java.util.List;

import me.davidkurniawan.MovieAppz.database.FavouriteMovies;
import me.davidkurniawan.MovieAppz.database.MovieDatabase;

public class FavouritesViewModel extends AndroidViewModel {

    private LiveData<List<FavouriteMovies>> favouriteMovies;

    public FavouritesViewModel(@NonNull Application application) {
        super(application);
        favouriteMovies = MovieDatabase.getInstance(this.getApplication())
                .favouriteMoviesDao()
                .loadAllFavouriteMovies();
    }

    public LiveData<List<FavouriteMovies>> getFavouriteMovies() {
        return favouriteMovies;
    }
}
