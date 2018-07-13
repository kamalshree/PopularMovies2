package com.android.popularmovies.Model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by kamalshree on 6/11/2018.
 */

public class MovieResource {
    @SerializedName("id")
    private String movieId;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("poster_path")
    private String poster_Url;

    @SerializedName("overview")
    private String movie_Desc;

    @SerializedName("vote_average")
    private String vote_Average;

    @SerializedName("release_date")
    private String release_Date;

    @SerializedName("backdrop_path")
    private String backdrop_Path;

    public MovieResource(String poster_url,String originalTitle,String movieId) {
        this.poster_Url=poster_url;
        this.originalTitle=originalTitle;
        this.movieId=movieId;
    }

    public MovieResource(String backdrop_Path,String poster_Url,String originalTitle,String vote_Average,String release_Date,String movie_Desc) {
        this.backdrop_Path=backdrop_Path;
        this.poster_Url=poster_Url;
        this.originalTitle=originalTitle;
        this.vote_Average=vote_Average;
        this.release_Date=release_Date;
        this.movie_Desc=movie_Desc;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getPoster_Url() {
        return poster_Url;
    }

    public void setPoster_Url(String poster_Url) {
        this.poster_Url = poster_Url;
    }

    public String getMovie_Desc() {
        return movie_Desc;
    }

    public void setMovie_Desc(String movie_Desc) {
        this.movie_Desc = movie_Desc;
    }

    public String getVote_Average() {
        return vote_Average;
    }

    public void setVote_Average(String vote_Average) {
        this.vote_Average = vote_Average;
    }

    public String getRelease_Date() {
        return release_Date;
    }

    public void setRelease_Date(String release_Date) {
        this.release_Date = release_Date;
    }

    public String getBackdrop_Path() {
        return backdrop_Path;
    }

    public void setBackdrop_Path(String backdrop_Path) {
        this.backdrop_Path = backdrop_Path;
    }

    public String getMovieId() {
        return movieId;
    }

    public void setMovieId(String movieId) {
        this.movieId = movieId;
    }
}
