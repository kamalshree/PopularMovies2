package com.android.popularmovies.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.android.popularmovies.BuildConfig;
import com.android.popularmovies.Model.TrailerData;
import com.android.popularmovies.R;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by kamalshree on 7/22/2018.
 */

public class TrailerAdapter extends PagerAdapter {
    Context context;
    private List<TrailerData> list;
    LayoutInflater layoutInflater;


    public TrailerAdapter(Context context, List<TrailerData> list) {
        this.context = context;
        this.list = list;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int position) {
        View itemView = layoutInflater.inflate(R.layout.trailer_items, container, false);

        ImageView trailerImage = (ImageView) itemView.findViewById(R.id.trailer_image);
        Picasso.get().load(BuildConfig.YOUTUBE_IMAGE_BASE_URL + list.get(position).getKey() + context.getString(R.string.trailer_default_image)).resize(300,200)
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(trailerImage);
        container.addView(itemView);

        //listening to image click
        trailerImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(BuildConfig.YOUTUBE_BASE_URL + list.get(position).getKey()));
                try {
                    appIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(appIntent);
                } catch (ActivityNotFoundException ex) {
                    context.startActivity(appIntent);
                }
            }
        });

        return itemView;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((RelativeLayout) object);
    }
}
