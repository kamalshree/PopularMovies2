package com.android.popularmovies;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.text.ParseException;
import java.util.ArrayList;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.android.popularmovies.Adapters.ReviewAdapter;
import com.android.popularmovies.Adapters.TrailerAdapter;
import com.android.popularmovies.Database.MoviesDatabase;
import com.android.popularmovies.Model.MoviesData;
import com.android.popularmovies.Model.Reviews;
import com.android.popularmovies.Model.ReviewsData;
import com.android.popularmovies.Model.TrailerData;
import com.android.popularmovies.Model.Trailers;
import com.android.popularmovies.Networking.RetrofitInterface;
import com.android.popularmovies.Networking.RetrofitClient;
import com.android.popularmovies.Utils.MovieConstants;
import com.android.popularmovies.Utils.NetworkUtils;
import com.android.popularmovies.ViewModel.MovieModel;
import com.android.popularmovies.ViewModel.ViewModelFactory;
import com.squareup.picasso.Picasso;
import android.support.v7.widget.Toolbar;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DetailActivity extends AppCompatActivity implements View.OnClickListener{

    private Context mContext;
    private static Reviews reviewModel;
    private MoviesData moviesResultObject;
    private static Trailers trailersModel;
    private static List<TrailerData> trailerDataList;
    private static List<ReviewsData> reviewDataList;
    private static List<MoviesData> moviesDataList;
    private static int movieId;
    private static int movieposition;
    private MoviesDatabase moviesDatabase;
    public Observer observer;
    private MovieModel mainViewModel;

    private Button refresh_con;
    private LinearLayout no_connection;

    @BindView(R.id.tv_overview)
    TextView overview;

    @BindView(R.id.tv_rating)
    TextView rating;

    @BindView(R.id.tv_releasedate)
    TextView release_date;

    @BindView(R.id.tv_movie_title)
    TextView movie_toolbar_title;

    @BindView(R.id.iv_backdrop)
    ImageView backdrop_iv;

    @BindView(R.id.movie_toolbar)
    Toolbar movie_toolbar;

    @BindView(R.id.img_poster)
    ImageView poster_img;

    @BindView(R.id.tv_title)
    TextView tv_title;

    @BindView(R.id.tv_review)
    TextView tv_review;

    @BindView(R.id.tv_trailer)
    TextView tv_trailer;

    @BindView(R.id.vp_trailer)
    ViewPager trailerPager;

    @BindView(R.id.star_button)
    com.like.LikeButton heartbutton;

    public @BindView(R.id.rv_review)
    RecyclerView recyclerview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        no_connection=findViewById(R.id.layout_noconnection);
        refresh_con=findViewById(R.id.refresh);
        refresh_con.setOnClickListener(this);
        movieposition = -1;
        ButterKnife.bind(this);
        moviesDatabase = MoviesDatabase.getInstance(DetailActivity.this);

         /* Toolbar */
        movie_toolbar.setNavigationIcon(R.drawable.ic_back);
        setSupportActionBar(movie_toolbar);
        movie_toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        mContext = getApplicationContext();
        mainViewModel = ViewModelProviders.of(this , new ViewModelFactory(moviesDatabase , Integer.toString(movieId))).get(MovieModel.class);

        reviewDataList = new ArrayList<>();
        trailerDataList = new ArrayList<>();
        moviesDataList = moviesDatabase.moviesDao().getAllMovies();

        recyclerview.setHasFixedSize(true);
        recyclerview.setLayoutManager(new LinearLayoutManager(this));
        movieId = getIntent().getIntExtra("movie_id" , -1);

        RetrofitRequest();

        observer = new Observer<MoviesData>() {
            @Override
            public void onChanged(@Nullable MoviesData moviesResult) {
                moviesResultObject = moviesResult;
                mainViewModel.getMoviesResults().removeObserver(this);
            }
        };

        mainViewModel.getMoviesResults().observe(DetailActivity.this, observer);


        /* Favorites Implementation */
        heartbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                moviesDataList = moviesDatabase.moviesDao().getAllMovies();
                int i = 0;
                do{
                    if(moviesDataList.size() == 0){
                        moviesDatabase.moviesDao().insertMovie(moviesResultObject);
                        moviesResultObject.setFavourite(true);
                        if(mainViewModel.getMoviesResults().getValue() != null) {
                            mainViewModel.getMoviesResults().getValue().setFavourite(true);
                        }
                        moviesDatabase.moviesDao().updateMovie(moviesResultObject);
                        FavouriteData();
                        break;
                    }

                    if(Objects.equals(moviesResultObject.getId(), moviesDataList.get(i).getId())){
                        moviesDatabase.moviesDao().deleteMovies(moviesResultObject);
                        moviesResultObject.setFavourite(false);
                        if(mainViewModel.getMoviesResults().getValue() != null) {
                            mainViewModel.getMoviesResults().getValue().setFavourite(false);
                        }
                        moviesDatabase.moviesDao().updateMovie(moviesResultObject);
                        FavouriteData();
                        break;
                    }

                    if(i == (moviesDataList.size() - 1)){
                        moviesDatabase.moviesDao().insertMovie(moviesResultObject);
                        moviesResultObject.setFavourite(true);
                        if(mainViewModel.getMoviesResults().getValue() != null) {
                            mainViewModel.getMoviesResults().getValue().setFavourite(true);
                        }
                        moviesDatabase.moviesDao().updateMovie(moviesResultObject);
                        FavouriteData();
                        break;
                    }
                    i++;
                }while (i < moviesDataList.size());
            }
        });

    }

    /*Trailer Implementation*/
    private void loadTrailers(){
        if(trailerDataList != null){
            trailerDataList.clear();
        }
        RetrofitInterface retrofitInterface = RetrofitClient.getRetrofitInstance().create(RetrofitInterface.class);
        Call<Trailers> call = retrofitInterface.getMovieTrailers(Integer.toString(moviesResultObject.getId()), BuildConfig.API_KEY);
        call.enqueue(new Callback<Trailers>() {
            @Override
            public void onResponse(Call<Trailers> call, Response<Trailers> response) {
                trailersModel = response.body();

                trailerDataList.addAll(trailersModel.getResults());

                if (trailerDataList.size() == 0) {
                    Toast.makeText(DetailActivity.this, getString(R.string.no_trailer), Toast.LENGTH_SHORT).show();
                } else {
                    TrailerAdapter sectionsPagerAdapter = new TrailerAdapter(mContext, trailerDataList);
                    trailerPager.setAdapter(sectionsPagerAdapter);
                    trailerPager.setClipToPadding(false);
                    trailerPager.setPadding(48, 0, 48, 0);
                    trailerPager.setPageMargin(14);

                }
            }

            @Override
            public void onFailure(Call<Trailers> call, Throwable t) {
                Toast.makeText(mContext, R.string.failed, Toast.LENGTH_SHORT).show();
            }
        });
    }

    /*Review Implementation*/
    private void loadReviews(){
        RetrofitInterface retrofitInterface = RetrofitClient.getRetrofitInstance().create(RetrofitInterface.class);
        Call<Reviews> call = retrofitInterface.getMovieReviews(Integer.toString(moviesResultObject.getId()), BuildConfig.API_KEY);
        call.enqueue(new Callback<Reviews>() {
            @Override
            public void onResponse(Call<Reviews> call, Response<Reviews> response) {
                reviewModel = response.body();
                if (reviewDataList != null) {
                    reviewDataList.clear();
                }

                reviewDataList.addAll(reviewModel.getResults());

                if (reviewModel.getTotalResults() == 0) {
                    tv_review.setVisibility(View.INVISIBLE);
                    Toast.makeText(DetailActivity.this, R.string.no_reviews, Toast.LENGTH_SHORT).show();
                } else {
                    ReviewAdapter recyclerAdapter = new ReviewAdapter(mContext,R.layout.review_items, reviewDataList);
                    recyclerview.setAdapter(recyclerAdapter);
                }
            }

            @Override
            public void onFailure(Call<Reviews> call, Throwable t) {
                Toast.makeText(mContext, R.string.failed, Toast.LENGTH_SHORT).show();
            }
        });
    }



    /*Favorite Icon*/
    public void FavouriteData(){
        for (int i = 0; i < moviesDatabase.moviesDao().getAllMovies().size(); i++) {
            if(Objects.equals(moviesResultObject.getId(), moviesDatabase.moviesDao().getAllMovies().get(i).getId())){
                movieposition = i;
                break;
            }else{
                movieposition = -1;
            }
        }
        if(movieposition >=0){
            heartbutton.setLiked(true);

        }else{
            heartbutton.setLiked(false);
        }
    }


    /* Retrofit Call */
    private void RetrofitRequest(){
        RetrofitInterface retrofitInterface = RetrofitClient.getRetrofitInstance().create(RetrofitInterface.class);
        Call<MoviesData> callResult = retrofitInterface.getMovieDetailsById
                (Integer.toString(movieId) , BuildConfig.API_KEY);

        callResult.enqueue(new Callback<MoviesData>() {
            @Override
            public void onResponse(Call<MoviesData> call, Response<MoviesData> response) {
                moviesResultObject = response.body();

                for (int i = 0; i < moviesDataList.size(); i++) {
                    if (Objects.equals(moviesResultObject.getId(), moviesDataList.get(i).getId())){
                        movieposition = i;
                        break;
                    }
                }
                DecimalFormat precision = new DecimalFormat("0.0");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
                Date date = null;
                try {
                    date = simpleDateFormat.parse(moviesResultObject.getReleaseDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date);
                release_date.setText(getString(R.string.release_text) +String.valueOf(calendar.get(Calendar.DATE)) + "-" + String.valueOf(calendar.get(Calendar.MONTH) + 1) + "-" + String.valueOf(calendar.get(Calendar.YEAR)));

                Picasso.get()
                        .load(MovieConstants.BACKDROP_IMAGE_VIEW + moviesResultObject.getBackdropPath())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(backdrop_iv);

                Picasso.get()
                        .load(MovieConstants.CARD_IMAGE_VIEW + moviesResultObject.getPosterPath())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.error)
                        .into(poster_img);

                movie_toolbar_title.setText(moviesResultObject.getTitle());
                tv_title.setText(moviesResultObject.getTitle());
                rating.setText(getString(R.string.rating_text) + precision.format(moviesResultObject.getRating()));
                overview.setText(moviesResultObject.getOverview());


                FavouriteData();
                loadReviews();
                loadTrailers();

            }

            @Override
            public void onFailure(Call<MoviesData> call, Throwable t) {
                ShowNoInternetMessage();
            }
        });
    }

    /*No Internet Features */
    private void ShowNoInternetMessage() {
        heartbutton.setVisibility(View.INVISIBLE);
        tv_title.setVisibility(View.INVISIBLE);
        tv_trailer.setVisibility(View.INVISIBLE);
        tv_review.setVisibility(View.INVISIBLE);
        movie_toolbar_title.setVisibility(View.INVISIBLE);
        rating.setVisibility(View.INVISIBLE);
        release_date.setVisibility(View.INVISIBLE);
        overview.setVisibility(View.INVISIBLE);
        backdrop_iv.setVisibility(View.INVISIBLE);
        no_connection.setVisibility(View.VISIBLE);
        refresh_con.setVisibility(View.VISIBLE);
    }

    /*Refresh button Implementation*/
    @Override
    public void onClick(View view) {
        if (NetworkUtils.connectionStatus(this)) {
            RetrofitRequest();
        }
        else{
            ShowNoInternetMessage();
        }
    }
}