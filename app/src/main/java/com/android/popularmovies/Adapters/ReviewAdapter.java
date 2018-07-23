package com.android.popularmovies.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.TextView;

import com.android.popularmovies.Model.ReviewsData;
import com.android.popularmovies.R;

import java.util.List;

import at.blogc.android.views.ExpandableTextView;
import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by kamalshree on 7/22/2018.
 */

public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final List<ReviewsData> reviewList;
    private final Context context;
    int resource;

    static class ReviewHolder extends RecyclerView.ViewHolder {

        @BindView(R.id.review_content)
        ExpandableTextView reviewContent;

        @BindView(R.id.review_name)
        TextView reviewName;

        @BindView(R.id.review_more)
        TextView reviewMore;


        private ReviewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }

    public ReviewAdapter(Context context, int resource, List<ReviewsData> reviewList) {
        this.reviewList = reviewList;
        this.resource = resource;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(resource, null, false);
        return new ReviewHolder(view);
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, final int position) {

        final ReviewHolder reviewHolder = (ReviewHolder) holder;
        final ReviewsData reviewModel = reviewList.get(position);

        reviewHolder.reviewContent.setAnimationDuration(750L);
        reviewHolder.reviewContent.setInterpolator(new OvershootInterpolator());

        reviewHolder.reviewMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                reviewHolder.reviewMore.setText(reviewHolder.reviewContent.isExpanded() ? context.getString(R.string.read_more) : context.getString(R.string.read_less));
                reviewHolder.reviewContent.toggle();
            }
        });
        reviewHolder.reviewName.setText(reviewModel.getAuthor());
        reviewHolder.reviewContent.setText(reviewModel.getContent());

    }


    @Override
    public int getItemCount() {
        return reviewList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }
}
