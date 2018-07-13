package com.android.popularmovies.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.popularmovies.BuildConfig;
import com.android.popularmovies.Model.MovieResource;
import com.android.popularmovies.R;
import com.android.popularmovies.Utils.MovieConstants;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;

import org.json.JSONException;
import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;

import java.util.Calendar;
import java.util.Date;

import java.text.ParseException;
import java.util.Locale;

public class DetailsActivity extends AppCompatActivity {


    @BindView(R.id.tv_title)
    TextView textTitle;

    @BindView(R.id.tv_overview)
    TextView textOverview;

    @BindView(R.id.tv_rating)
    TextView textRate;

    @BindView(R.id.tv_releasedate)
    TextView textReleaseDate;

    @BindView(R.id.tv_movie_title)
    TextView movie_toolbar_title;

    @BindView(R.id.img_backdrop)
    ImageView imageBackDrop;

    @BindView(R.id.img_poster)
    ImageView ImagePoster;

    @BindView(R.id.movie_toolbar)
    Toolbar movie_toolbar;


    private String movieId, movieName;
    private MovieResource movieData;

    private void initViews() {
        setContentView(R.layout.activity_details);
        ButterKnife.bind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getIntent() != null) {
            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            movieId = bundle.getString(MovieConstants.MOVIE_ID);
            movieName = bundle.getString(MovieConstants.MOVIE_NAME);
        }

        initViews();

        /* Toolbar */
        movie_toolbar.setNavigationIcon(R.drawable.ic_back);
        movie_toolbar_title.setText(movieName);
        setSupportActionBar(movie_toolbar);
        movie_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        getMovieDetails();

    }

    /* Set the Movie Details */
    private void setMovieDetails() {
        Picasso.get().load((MovieConstants.BACKDROP_IMAGE_VIEW + movieData.getBackdrop_Path()).trim())
                .placeholder(R.drawable.ic_launcher_background)
                .into(imageBackDrop);
        Picasso.get().load((MovieConstants.CARD_IMAGE_VIEW + movieData.getPoster_Url()).trim())
                .placeholder(R.drawable.ic_launcher_background)
                .into(ImagePoster);
        textTitle.setText(movieData.getOriginalTitle());

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try {
            Date date = simpleDateFormat.parse(movieData.getRelease_Date());
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(date);
            textReleaseDate.setText(String.valueOf(calendar.get(Calendar.DATE)) + "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-" + String.valueOf(calendar.get(Calendar.YEAR)));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        textRate.setText(movieData.getVote_Average());
        textOverview.setText(movieData.getMovie_Desc());
    }

    /* Get the Movie Details */
    private void getMovieDetails() {
        Uri.Builder params = new Uri.Builder();
        params.appendEncodedPath(movieId);
        params.appendQueryParameter("api_key", BuildConfig.API_KEY);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, MovieConstants.MOVIE_URL + params, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject movieDetails = new JSONObject(response);

                    movieData = new MovieResource(movieDetails.getString("backdrop_path"),
                            movieDetails.getString("poster_path"),
                            movieDetails.getString("original_title"),
                            movieDetails.getString("vote_average"),
                            movieDetails.getString("release_date"),
                            movieDetails.getString("overview"));

                    setMovieDetails();

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

}
