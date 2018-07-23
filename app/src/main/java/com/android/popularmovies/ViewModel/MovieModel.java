package com.android.popularmovies.ViewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.android.popularmovies.Database.MoviesDatabase;
import com.android.popularmovies.Model.MoviesData;

public class MovieModel extends ViewModel {

    private LiveData<MoviesData> moviesEntity;

    public MovieModel(@NonNull MoviesDatabase movieDatabase , String id) {
        moviesEntity = movieDatabase.moviesDao().getMoviesLiveData(id);
    }

    public LiveData<MoviesData> getMoviesResults() {
        return moviesEntity;
    }

}