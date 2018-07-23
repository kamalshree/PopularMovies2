package com.android.popularmovies;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AlertDialog;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.android.popularmovies.Adapters.MovieAdapter;
import com.android.popularmovies.Database.MoviesDatabase;
import com.android.popularmovies.Model.Movies;
import com.android.popularmovies.Model.MoviesData;
import com.android.popularmovies.Networking.RetrofitInterface;
import com.android.popularmovies.Networking.RetrofitClient;
import com.android.popularmovies.Utils.NetworkUtils;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    private ProgressBar progressBar;
    private Button refresh_con;
    private LinearLayout no_connection;

    private GridView gridView;

    private Call<Movies> call;
    private Movies mMovieModel;
    private List<MoviesData> moviesResultList;
    private MovieAdapter adapter;
    private MoviesDatabase moviesDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridView=findViewById(R.id.gv_movie_gridview);
        progressBar=findViewById(R.id.pb_progressBar);
        no_connection=findViewById(R.id.layout_noconnection);
        refresh_con=findViewById(R.id.refresh);
        refresh_con.setOnClickListener(this);
        call = null;


        if (!NetworkUtils.connectionStatus(this)) {
            ShowNoInternetMessage();
            buildDialog(this).show();
        }

        else{
            loadPopularMovies();
        }


        moviesDatabase = MoviesDatabase.getInstance(MainActivity.this);
        moviesResultList = new ArrayList<>();

        adapter = new MovieAdapter(this ,R.layout.list_item, moviesResultList);
        gridView.setAdapter(adapter);
    }

    /*Retrofit Call Implementation */
    private void RetrofitCall(Call<Movies> call){
        call.enqueue(new Callback<Movies>() {
            @Override
            public void onResponse(@NonNull Call<Movies> call, @NonNull Response<Movies> response) {
                mMovieModel = null;
                mMovieModel = response.body();

                if(moviesResultList != null){
                    moviesResultList.clear();
                }

                if (mMovieModel != null) {
                    moviesResultList.addAll(mMovieModel.getResults());
                }else{
                }

                gridView.setVisibility(View.VISIBLE);
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(@NonNull Call<Movies> call, @NonNull Throwable t) {
                if(call.isCanceled()){

                }else {
                }
            }
        });
    }

    /* Popular Movies */
    private void loadPopularMovies(){
        progressBar.setVisibility(View.GONE);
        if(call != null) {
            call.cancel();
        }
        getSupportActionBar().setTitle(R.string.menu_popular_movies);
        RetrofitInterface retrofitInterface = RetrofitClient.getRetrofitInstance().create(RetrofitInterface.class);
        call = retrofitInterface.getPopularMovies(BuildConfig.API_KEY);
        RetrofitCall(call);
    }

    /* Top Rated Movies*/
    private void loadTopRatedMovies(){
        progressBar.setVisibility(View.GONE);
        if(call != null) {
            call.cancel();
        }
        getSupportActionBar().setTitle(R.string.menu_top_rated_movies);
        RetrofitInterface retrofitInterface = RetrofitClient.getRetrofitInstance().create(RetrofitInterface.class);
        call = retrofitInterface.getTopRatedMovies(BuildConfig.API_KEY);
        RetrofitCall(call);
    }

    /*Favourite Movies */
    private void loadFavourites(){
        if(call != null) {
            call.cancel();
        }
        getSupportActionBar().setTitle(R.string.menu_fav_movies);

        if(moviesResultList != null){
            moviesResultList.clear();
        }

        if(moviesDatabase.moviesDao().getAllMovies().size() == 0){
        }else{
            for (int i = 0; i < moviesDatabase.moviesDao().getAllMovies().size(); i++) {
                MoviesData result = new MoviesData(moviesDatabase.moviesDao().getAllMovies().get(i).getId(),
                        moviesDatabase.moviesDao().getAllMovies().get(i).getTitle(),
                        moviesDatabase.moviesDao().getAllMovies().get(i).getOverview(),
                        moviesDatabase.moviesDao().getAllMovies().get(i).getPosterPath(),
                        moviesDatabase.moviesDao().getAllMovies().get(i).getRating(),
                        moviesDatabase.moviesDao().getAllMovies().get(i).getReleaseDate(),
                        moviesDatabase.moviesDao().getAllMovies().get(i).getBackdropPath(),
                        moviesDatabase.moviesDao().getAllMovies().get(i).getFavourite());

                moviesResultList.add(result);
            }
        }

        gridView.setVisibility(View.VISIBLE);
        adapter.notifyDataSetChanged();
    }


    /* Menu Creation */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (NetworkUtils.connectionStatus(this)) {
            if (id == R.id.nav_popular_movies) {
                loadPopularMovies();
                progressBar.setVisibility(View.GONE);
            } else if (id == R.id.nav_top_rated) {
                loadTopRatedMovies();
                progressBar.setVisibility(View.GONE);
            } else {
                loadFavourites();
                progressBar.setVisibility(View.GONE);
            }
        }
        else{
            if (id == R.id.nav_favourites) {
                ShowMovieContent();
                loadFavourites();
            }
            else {
                ShowNoInternetMessage();
            }
        }
        return true;
    }

    /*Refresh button implementation*/
    @Override
    public void onClick(View view) {
        if (NetworkUtils.connectionStatus(this)) {
            ShowMovieContent();
            loadPopularMovies();
        }
        else{
            ShowNoInternetMessage();
        }
    }

    /* Action when Internet available */
    private void ShowMovieContent() {
        gridView.setVisibility(View.VISIBLE);
        no_connection.setVisibility(View.GONE);
        refresh_con.setVisibility(View.GONE);
    }

    /*Action when internet not available */
    private void ShowNoInternetMessage() {
        gridView.setVisibility(View.INVISIBLE);
        progressBar.setVisibility(View.GONE);
        no_connection.setVisibility(View.VISIBLE);
        refresh_con.setVisibility(View.VISIBLE);
    }

    /* No Internet Dialog */
    private AlertDialog.Builder buildDialog(Context c) {

        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle(getString(R.string.no_internet_title));
        builder.setMessage(getString(R.string.no_internet_message));

        builder.setPositiveButton(getString(R.string.no_interent_okbutton), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }

        });

        return builder;
    }

}