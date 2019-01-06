package me.davidkurniawan.MovieAppz.viewmodel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class MovieViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private int currentPage;
    private  String language;

    public MovieViewModelFactory(int currentPage, String language) {
        this.currentPage = currentPage;
        this.language = language;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new MovieViewModel(currentPage,language);
    }
}
