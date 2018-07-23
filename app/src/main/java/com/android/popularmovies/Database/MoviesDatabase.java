package com.android.popularmovies.Database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

import com.android.popularmovies.Model.MoviesData;


@Database(entities = {MoviesData.class}, version = 1, exportSchema = false)
public abstract class MoviesDatabase extends RoomDatabase {

    private static final Object LOCK = new Object();
    private static final String DATABASE_NAME = "movies";
    private static MoviesDatabase myInstance;

    public static MoviesDatabase getInstance(Context context) {
        if (myInstance == null) {
            synchronized (LOCK) {
                myInstance = Room.databaseBuilder(context.getApplicationContext(),
                        MoviesDatabase.class, MoviesDatabase.DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build();
            }
        }
        return myInstance;
    }
    public abstract MoviesDao moviesDao();
}