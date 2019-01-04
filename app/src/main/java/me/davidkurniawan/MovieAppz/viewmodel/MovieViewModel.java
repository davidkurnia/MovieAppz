package me.davidkurniawan.MovieAppz.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import me.davidkurniawan.MovieAppz.helper.MovieRepository;
import me.davidkurniawan.MovieAppz.model.MovieResponse;
import retrofit2.Response;

public class MovieViewModel extends ViewModel {

    private LiveData<Response<MovieResponse>> popularMovies;
    private LiveData<Response<MovieResponse>> topRatedMovies;

    MovieViewModel(int currentPage) {

        popularMovies = MovieRepository.getInstance().getMoviesResponse(currentPage);
        topRatedMovies = MovieRepository.getInstance().getTopRatedMovies(currentPage);
    }

    public LiveData<Response<MovieResponse>> getPopularMovieResponse() {
        return popularMovies;
    }

    public LiveData<Response<MovieResponse>> getTopRatedMoviesResponse() {
        return topRatedMovies;
    }
}
