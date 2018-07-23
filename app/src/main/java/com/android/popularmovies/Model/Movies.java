package com.android.popularmovies.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Movies {


    @SerializedName("results")
    @Expose
    private List<MoviesData> results = null;

    public List<MoviesData> getResults() {
        return results;
    }

    public void setResults(List<MoviesData> results) {
        this.results = results;
    }

}
