package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/**
 * Created by Lore on 5/7/2018.
 * RecyclerView with help from https://guides.codepath.com/android/using-the-recyclerview
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieViewHolder>{

    private final Context mContext;
    private Movie[] mMovieArray;
    private final MovieOnClickHandler mClickHandler;


    public interface MovieOnClickHandler {
        void onClick(Movie movie); //on click method will be implemented in mainActivity
    }

    //constructor
    public MovieAdapter(Context context, Movie[] movieArray, MovieOnClickHandler clickHandler){
        mContext = context;
        mMovieArray = movieArray;
        mClickHandler = clickHandler;
    }

    @Override
    public MovieAdapter.MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate poster_cell layout
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.poster_cell, parent, false);
        //return ViewHolder instance
        MovieViewHolder viewholder = new MovieViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(MovieAdapter.MovieViewHolder holder, int position) {
        Movie movie = mMovieArray[position];
        Picasso.with(mContext).setLoggingEnabled(true);//to see logs
        Picasso.with(mContext)
                .load(movie.getPoster())
                .resize(555,831)
                .into(holder.mPoster_iv);
    }

    @Override
    public int getItemCount() {
        if (mMovieArray == null) return 0;
        return mMovieArray.length;
    }

    //viewholder  inner class
    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView mPoster_iv;
        //viewholder constructor
        public MovieViewHolder(View view){
            super(view);
            mPoster_iv = (ImageView)view.findViewById(R.id.poster_iv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie movie = mMovieArray[adapterPosition];
            mClickHandler.onClick(movie);
        }
    }

    public void setMovieData(Movie[] movieData) {
        mMovieArray = movieData;
        notifyDataSetChanged();
    }
}
