package com.example.android.popularmovies;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Lore on 5/27/2018.
 */

public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideoViewHolder> {

    private final Context mContext;
    private ArrayList<String> mVideosArraylist;
    private final VideoOnClickHandler mClickHandler;
    private static final String YOUTUBE_THUMBNAIL_BASE= "https://img.youtube.com/vi/";
    private static final String YOUTUBE_THUMBNAIL_END= "/hqdefault.jpg";

    public interface VideoOnClickHandler {
        void onClick(String movieKey); //on click method will be implemented in detailActivity
    }

    //constructor
    public VideosAdapter(Context context, ArrayList<String> videosArraylist, VideoOnClickHandler clickHandler ){
        mContext = context;
        mVideosArraylist = videosArraylist;
        mClickHandler = clickHandler;
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        //inflate video_thumbnail layout
        Context context = parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View view = layoutInflater.inflate(R.layout.video_thumbnail, parent, false);
        //return ViewHolder instance
        VideoViewHolder viewholder = new VideoViewHolder(view);
        return viewholder;
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        String movieKey = mVideosArraylist.get(position);
        String thumbnailURL = YOUTUBE_THUMBNAIL_BASE + movieKey + YOUTUBE_THUMBNAIL_END;
        Picasso.with(mContext)
                .load(thumbnailURL)
                .resize(480,360)
                .placeholder(R.drawable.video_placeholder)
                .into(holder.mThumbnail_iv);
    }

    @Override
    public int getItemCount() {
        if (mVideosArraylist == null) return 0;
        return mVideosArraylist.size();
    }

    //viewholder  inner class
    public class VideoViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public final ImageView mThumbnail_iv;
        //viewholder constructor
        public VideoViewHolder(View view){
            super(view);
            mThumbnail_iv = (ImageView)view.findViewById(R.id.video_thumb_iv);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            String movieKey = mVideosArraylist.get(adapterPosition);
            mClickHandler.onClick(movieKey);
        }
    }

    public void setVideoData(ArrayList<String> videoData) {
        mVideosArraylist = videoData;
        notifyDataSetChanged();
    }

}
