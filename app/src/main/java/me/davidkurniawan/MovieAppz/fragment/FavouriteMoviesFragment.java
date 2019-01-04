package me.davidkurniawan.MovieAppz.fragment;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import me.davidkurniawan.MovieAppz.R;
import com.fxn.cue.enums.Type;

import java.util.List;

import me.davidkurniawan.MovieAppz.activity.DetailsActivity;
import me.davidkurniawan.MovieAppz.adapters.FavouriteMoviesAdapter;
import me.davidkurniawan.MovieAppz.database.FavouriteMovies;
import me.davidkurniawan.MovieAppz.database.MovieDatabase;
import me.davidkurniawan.MovieAppz.helper.AutoFitGridLayoutManager;
import me.davidkurniawan.MovieAppz.utils.Toaster;
import me.davidkurniawan.MovieAppz.viewmodel.FavouritesViewModel;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavouriteMoviesFragment extends Fragment implements FavouriteMoviesAdapter.ListItemClickListener {
    private RecyclerView recyclerView;
    private MovieDatabase mDatabase;
    private FavouriteMoviesAdapter mAdapter;

    public FavouriteMoviesFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_favourite_movies, container, false);
        recyclerView = view.findViewById(R.id.fav_recycler_view);
        populateUI();
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatabase = MovieDatabase.getInstance(getActivity());
        setupViewModels();
    }

    private void setupViewModels() {
        FavouritesViewModel viewModel = ViewModelProviders.of(this).get(FavouritesViewModel.class);
        viewModel.getFavouriteMovies().observe(this, new Observer<List<FavouriteMovies>>() {
            @Override
            public void onChanged(@Nullable List<FavouriteMovies> favouriteMovies) {
                if (favouriteMovies != null) {
                    mAdapter.setFavouriteMoviesList(favouriteMovies);
                } else {
                    Toaster.makeToast(getActivity().getString(R.string.no_data), Type.DANGER, getActivity());
                }
            }
        });
    }

    private void populateUI() {
        mAdapter = new FavouriteMoviesAdapter(getActivity(), this);
        recyclerView.setHasFixedSize(true);
        recyclerView.setAdapter(mAdapter);
        recyclerView.setLayoutManager(new AutoFitGridLayoutManager(getActivity(), 500));
    }

    @Override
    public void onListItemClick(FavouriteMovies favouriteMovies) {
        Bundle extras = new Bundle();
        Intent intent = new Intent(getActivity(), DetailsActivity.class);
        extras.putInt(getString(R.string.extra_favourite_movie), favouriteMovies.getId());
        extras.putInt(getString(R.string.fav_extra_id), favouriteMovies.getMovieId());
        intent.putExtras(extras);
        getActivity().startActivity(intent);
    }
}
