package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Lore on 6/1/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder> {

    private final Context mContext;
    private ArrayList<String[]> mReviewsArraylist;


    //constructor
    public ReviewsAdapter(Context context, ArrayList<String[]> reviewsArraylist){
        mContext = context;
        mReviewsArraylist = reviewsArraylist;
    }

    @Override
    public ReviewViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate review_cell layout
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.review_cell, parent, false);
        //return ViewHolder instance
        ReviewViewHolder viewholder = new ReviewViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        String[] review = mReviewsArraylist.get(position);
        holder.mAuthor_tv.setText(review[0] + ":");
        holder.mContent_tv.setText(review[1]);
    }

    @Override
    public int getItemCount() {
        if (mReviewsArraylist == null) return 0;
        return mReviewsArraylist.size();
    }

    //viewholder  inner class
    public class ReviewViewHolder extends RecyclerView.ViewHolder {

        public final TextView mAuthor_tv;
        public final TextView mContent_tv;

        //viewholder constructor
        public ReviewViewHolder(View view){
            super(view);
            mAuthor_tv = (TextView)view.findViewById(R.id.review_author_tv);
            mContent_tv = (TextView)view.findViewById(R.id.review_content_tv);
        }
    }

    public void setReviewData(ArrayList<String[]> reviewData) {
        mReviewsArraylist = reviewData;
        notifyDataSetChanged();
    }
}
