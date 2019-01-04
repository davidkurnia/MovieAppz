package me.davidkurniawan.MovieAppz.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import me.davidkurniawan.MovieAppz.helper.MovieRepository;
import me.davidkurniawan.MovieAppz.model.TrailerResponse;
import retrofit2.Response;

public class TrailerViewModel extends ViewModel {

    private LiveData<Response<TrailerResponse>> trailerResponse;

    TrailerViewModel(String id) {
        trailerResponse = MovieRepository.getInstance().getTrailerResponse(id);
    }

    public LiveData<Response<TrailerResponse>> getTrailerResponse() {
        return trailerResponse;
    }
}
