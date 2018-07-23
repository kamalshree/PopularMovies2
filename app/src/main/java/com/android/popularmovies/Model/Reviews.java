package com.android.popularmovies.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Reviews {

    @SerializedName("id")
    @Expose
    private Integer id;

    @SerializedName("results")
    @Expose
    private List<ReviewsData> results = null;
    @SerializedName("total_results")
    @Expose
    private Integer totalResults;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public List<ReviewsData> getResults() {
        return results;
    }


    public Integer getTotalResults() {
        return totalResults;
    }

}