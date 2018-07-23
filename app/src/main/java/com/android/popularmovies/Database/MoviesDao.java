package com.android.popularmovies.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;


import com.android.popularmovies.Model.MoviesData;

import java.util.List;

@Dao
public interface MoviesDao{

    @Query("SELECT * FROM Movies")
    List<MoviesData> getAllMovies();

    @Query("SELECT * FROM Movies WHERE id = :id")
    LiveData<MoviesData> getMoviesLiveData(String id);

    @Insert
    void insertMovie(MoviesData... moviesResult);

    @Update
    void updateMovie(MoviesData moviesResult);

    @Delete
    void deleteMovies(MoviesData moviesResult);

}