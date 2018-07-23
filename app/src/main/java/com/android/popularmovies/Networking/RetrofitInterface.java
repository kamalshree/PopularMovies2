package com.android.popularmovies.Networking;

import com.android.popularmovies.Model.Movies;
import com.android.popularmovies.Model.MoviesData;
import com.android.popularmovies.Model.Reviews;
import com.android.popularmovies.Model.Trailers;
import com.android.popularmovies.Utils.MovieConstants;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

    @GET("movie/{id}")
    Call<MoviesData> getMovieDetailsById(@Path(value = "id", encoded = true) String id, @Query("api_key") String api_key);

    @GET("movie/{id}/reviews")
    Call<Reviews> getMovieReviews(@Path(value = "id", encoded = true) String id, @Query("api_key") String api_key);

    @GET("movie/{id}/videos")
    Call<Trailers> getMovieTrailers(@Path(value = "id", encoded = true) String id, @Query("api_key") String api_key);

    @GET(MovieConstants.POPULAR_MOVIE_URL)
    Call<Movies> getPopularMovies(@Query("api_key") String api_key);

    @GET(MovieConstants.TOP_RATED_MOVIE_URL)
    Call<Movies> getTopRatedMovies(@Query("api_key") String api_key);
}