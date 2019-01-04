package me.davidkurniawan.MovieAppz.viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import me.davidkurniawan.MovieAppz.helper.MovieRepository;
import me.davidkurniawan.MovieAppz.model.ReviewResponse;
import retrofit2.Response;

public class ReviewsViewModel extends ViewModel {

    private LiveData<Response<ReviewResponse>> reviewResponse;

    ReviewsViewModel(String id, int currentPage) {
        reviewResponse = MovieRepository.getInstance().getReviewResponse(id, currentPage);
    }

    public LiveData<Response<ReviewResponse>> getReviewResponse() {
        return reviewResponse;
    }
}
