package com.android.popularmovies.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.android.popularmovies.DetailActivity;
import com.android.popularmovies.Model.MoviesData;
import com.android.popularmovies.R;
import com.android.popularmovies.Utils.MovieConstants;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.BindView;

public class MovieAdapter extends ArrayAdapter<MoviesData> {
    private final List<MoviesData> mResultList;
    private final Context mContext;
    private final int resource;
    public @BindView(R.id.tv_imagepath) ImageView posterImage;


    public MovieAdapter(Context context, int resource, List<MoviesData> movieResources) {
        super(context, resource, movieResources);
        this.mContext = context;
        this.resource = resource;
        this.mResultList = movieResources;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);

        View view = layoutInflater.inflate(resource, null, false);
        final MoviesData movie = mResultList.get(position);

        posterImage = view.findViewById(R.id.tv_imagepath);
        Picasso.get()
                .load((MovieConstants.CARD_IMAGE_VIEW+ movie.getPosterPath()).trim())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(posterImage);


        posterImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, DetailActivity.class);
                intent.putExtra("movie_id" , movie.getId());
                mContext.startActivity(intent);
            }
        });

        //finally returning the view
        return view;
    }

}