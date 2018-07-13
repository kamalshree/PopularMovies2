package com.android.popularmovies.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.popularmovies.Adapter.MovieAdapter;
import com.android.popularmovies.BuildConfig;
import com.android.popularmovies.Presenter.PresenterCall;
import com.android.popularmovies.R;
import com.android.popularmovies.Utils.MovieConstants;
import com.android.popularmovies.Utils.NetworkUtils;
import com.android.popularmovies.Model.MovieResource;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import java.util.ArrayList;


public class MainActivity extends AppCompatActivity implements PresenterCall,View.OnClickListener {

    private GridView gridView;
    private ProgressBar progressBar;

    private PresenterCall myPresenter;
    private ArrayList<MovieResource> movieList;
    private MovieAdapter movieAdapter;
    private LinearLayout no_connection;
    private Button refresh_con;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        gridView=findViewById(R.id.gv_movie_gridview);
        progressBar=findViewById(R.id.pb_progressBar);
        no_connection=findViewById(R.id.layout_noconnection);
        refresh_con=findViewById(R.id.refresh);

        refresh_con.setOnClickListener(this);
        myPresenter =  this;
        movieList = new ArrayList<>();

        if (!NetworkUtils.connectionStatus(this)) {
            ShowNoInternetMessage();
            buildDialog(this).show();
        }
        else{
            getPostersDetails(MovieConstants.POPULAR_MOVIE_URL);
            ShowMovieContent();
        }
    }

    /*Display Movie Posters */
    private void getPostersDetails(String movieUrl) {

        Uri.Builder params = new Uri.Builder();
        params.appendQueryParameter("api_key", BuildConfig.API_KEY);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, movieUrl + params, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject j = new JSONObject(response);
                    JSONArray array = j.getJSONArray("results");

                    for (int i = 0; i < array.length(); i++) {
                        JSONObject jsonObject1 = array.getJSONObject(i);
                        MovieResource listItemProgramList = new MovieResource(jsonObject1.getString("poster_path"),
                                jsonObject1.getString("original_title"),
                                jsonObject1.getString("id"));
                        movieList.add(listItemProgramList);
                    }
                    if (movieAdapter == null) {
                        movieAdapter = new MovieAdapter(getApplicationContext(), R.layout.list_items, movieList, myPresenter);
                        gridView.setAdapter(movieAdapter);
                    } else
                        movieAdapter.notifyDataSetChanged();
                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(stringRequest);

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
        movieList = new ArrayList<>();
        movieAdapter = null;
        if (NetworkUtils.connectionStatus(this)) {
            ShowMovieContent();
            if (id == R.id.popular_movies) {
                getPostersDetails(MovieConstants.POPULAR_MOVIE_URL);
                progressBar.setVisibility(View.GONE);
            } else if (id == R.id.top_rates_movies) {
                getPostersDetails(MovieConstants.TOP_RATED_MOVIE_URL);
                progressBar.setVisibility(View.GONE);
            }
        }
        else{
            ShowNoInternetMessage();
        }
        return true;
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

    /* Movie Detail Screen Intent */
    @Override
    public void DetailsScreen(String movieID, String movieTitle) {
        if (NetworkUtils.connectionStatus(this)) {
            Intent intent = new Intent(getApplicationContext(), DetailsActivity.class);
            Bundle bundle = new Bundle();
            intent.putExtra(MovieConstants.MOVIE_ID, movieID);
            intent.putExtra(MovieConstants.MOVIE_NAME, movieTitle);
            intent.putExtras(bundle);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        else{
            ShowNoInternetMessage();
        }
    }


    @Override
    public void onClick(View view) {
        if (NetworkUtils.connectionStatus(this)) {
            ShowMovieContent();
            getPostersDetails(MovieConstants.POPULAR_MOVIE_URL);
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
}
