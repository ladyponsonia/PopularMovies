package com.example.android.popularmovies;


import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.support.v4.content.AsyncTaskLoader;
import android.widget.Toast;
import com.example.android.popularmovies.MovieContract.MovieEntry;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Objects;

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String[]>, VideosAdapter.VideoOnClickHandler{

    //views
    private TextView titleTV;
    private ImageView posterThumbIV;
    private TextView overviewTV;
    private TextView ratingTV;
    private TextView dateTV;
    private TextView videoErrorTV;
    private TextView reviewErrorTV;
    private RecyclerView videosRV;
    private RecyclerView reviewsRV;
    private CheckBox starCB;
    private ScrollView detailScroll;

    private Movie selectedMovie;
    private String[] movieVideosReviews = null; //variable to cache the data
    private VideosAdapter videoAdapter;
    private ArrayList<String> videosArraylist;
    private ReviewsAdapter reviewsAdapter;
    private ArrayList<String[]> reviewsArraylist;
    private  DividerItemDecoration mDividerItemDecoration;

    private static final String ID_BUNDLE_KEY = "movieID";
    private static final int VIDEOS_LOADER_ID = 555;
    private static final String API_VIDEOS_MODE = "/videos";
    private static final String API_REVIEWS_MODE = "/reviews";
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    private static final String SCROLL_KEY = "scrollPosition";

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        //get views
        posterThumbIV = findViewById(R.id.poster_thumb_iv);
        titleTV = findViewById(R.id.movie_title_tv);
        overviewTV = findViewById(R.id.movie_overview_tv);
        ratingTV = findViewById(R.id.movie_rating_tv);
        dateTV =  findViewById(R.id.movie_date_tv);
        videosRV = findViewById(R.id.videos_rv);
        reviewsRV = findViewById(R.id.reviews_rv);
        videoErrorTV = findViewById(R.id.videos_error_tv);
        reviewErrorTV = findViewById(R.id.reviews_error_tv);
        starCB = findViewById(R.id.star_button);
        detailScroll = (ScrollView)findViewById(R.id.detail_scrollview);

        //get parcelable
        Intent intentReceived = getIntent();
        selectedMovie = (Movie) intentReceived.getParcelableExtra(MainActivity.MOVIE_PARCEL_KEY);

        //variables for UI content
        String movieTitle = "";
        String movieOverview = "";
        float movieRating = 0;
        String movieDate = "";
        String moviePoster = "";
        String videosRawJson = "";
        String reviewsRawJson = "";

        //check if movie in db
        Cursor singleMovieCursor  = getContentResolver().query(MovieEntry.CONTENT_URI, null,
                MovieEntry.COLUMN_EXT_ID + "=" + selectedMovie.getId(), null, null);
        if (singleMovieCursor.getCount() == 0){
            //if not in database,  set text from parcelable, make API call to get videos and reviews
            movieTitle = selectedMovie.getTitle();
            movieOverview = selectedMovie.getOverview();
            movieRating = selectedMovie.getRating();
            movieDate = selectedMovie.getDate();
            moviePoster = selectedMovie.getPoster();
            //initialize loader for http request if there is internet connection
            if(NetworkUtils.isOnline(this)) {
                //put movie id in bundle
                Bundle idBundle = new Bundle();
                idBundle.putInt(ID_BUNDLE_KEY, selectedMovie.getId());
                getSupportLoaderManager().initLoader(VIDEOS_LOADER_ID, idBundle, this);
            }else{
                //show error messages if no connection available
                showReviewsErrorMsg(true);
                showVideosErrorMsg(true);
            }

        }else{
            //load data from db
            //some code here taken from https://stackoverflow.com/questions/10690018/how-do-i-retrieve-data-from-database-using-cursor-android-sqlite
            if (singleMovieCursor.moveToFirst()){
                while(!singleMovieCursor.isAfterLast()){
                    movieTitle = singleMovieCursor.getString(singleMovieCursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
                    movieOverview = singleMovieCursor.getString(singleMovieCursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
                    movieRating = singleMovieCursor.getFloat(singleMovieCursor.getColumnIndex(MovieEntry.COLUMN_RATING));
                    movieDate = singleMovieCursor.getString(singleMovieCursor.getColumnIndex(MovieEntry.COLUMN_DATE));
                    moviePoster = singleMovieCursor.getString(singleMovieCursor.getColumnIndex(MovieEntry.COLUMN_POSTER));
                    videosRawJson = singleMovieCursor.getString(singleMovieCursor.getColumnIndex(MovieEntry.COLUMN_TRAILER_IDS));
                    reviewsRawJson = singleMovieCursor.getString(singleMovieCursor.getColumnIndex(MovieEntry.COLUMN_REVIEWS));
                    singleMovieCursor.moveToNext();
                }
            }
            singleMovieCursor.close();

            //set star color yellow
            starCB.setChecked(true);
        }


        //set texts
        setTitle(movieTitle);
        titleTV.setText(movieTitle);
        overviewTV.setText(movieOverview);
        ratingTV.setText(String.valueOf(movieRating));
        dateTV.setText(movieDate);
        Picasso.with(this)
                .load(moviePoster)
                .fit()
                .into(posterThumbIV);


        //instantiate and set videos adapter
        videoAdapter = new VideosAdapter(this, videosArraylist, this );
        videosRV.setAdapter(videoAdapter);
        //set videos layout manager
        LinearLayoutManager videoLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        videosRV.setLayoutManager(videoLayoutManager);

        //instantiate and set reviews adapter
        reviewsAdapter = new ReviewsAdapter(this, reviewsArraylist );
        reviewsRV.setAdapter(reviewsAdapter);
        reviewsRV.setNestedScrollingEnabled(false);//disabled scrolling because it's already inside a scrollview
        //set reviews layout manager
        LinearLayoutManager reviewLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        reviewsRV.setLayoutManager(reviewLayoutManager);
        //add divider
        //help from https://developer.android.com/reference/android/support/v7/widget/DividerItemDecoration
        mDividerItemDecoration = new DividerItemDecoration(reviewsRV.getContext(),LinearLayoutManager.VERTICAL);
        reviewsRV.addItemDecoration(mDividerItemDecoration);
        mDividerItemDecoration.setDrawable(getDrawable(R.drawable.review_divider));

        //if movie in db, set data for video and reviews adapters
        if (!Objects.equals(videosRawJson, "")){
            try{
                parseSetVideos(videosRawJson);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (!Objects.equals(reviewsRawJson, "")){
            try{
                parseSetReviews(reviewsRawJson);
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public android.support.v4.content.Loader<String[]> onCreateLoader(int i, final Bundle bundle) {
        return new AsyncTaskLoader<String[]>(this) {

            //get movie id from bundle
            final int movieID = bundle.getInt(ID_BUNDLE_KEY);

            @Override
            protected void onStartLoading() {
                if (movieVideosReviews != null) {
                    deliverResult(movieVideosReviews);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String[] loadInBackground() {
                String videosRawJSON;
                String reviewsRawJSON;

                URL videosQueryURL = NetworkUtils.buildVideosReviewsUrl(movieID, API_VIDEOS_MODE);
                URL reviewsQueryURL = NetworkUtils.buildVideosReviewsUrl(movieID, API_REVIEWS_MODE);
                try {
                    videosRawJSON = NetworkUtils.getHttpResponse( videosQueryURL );
                    reviewsRawJSON = NetworkUtils.getHttpResponse( reviewsQueryURL );
                    return new String[] {videosRawJSON, reviewsRawJSON};
                } catch (IOException e) {
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(String[] data) {
                movieVideosReviews = data;//result that is sent to onLoadFinished
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<String[]> loader, String[] data) {

        try {
            //parse JSON and set new data on adapter
            parseSetVideos (data[0]);
            parseSetReviews(data[1]);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<String[]> loader) {

    }

    @Override
    public void onClick(String movieKey) {
        String movieURL = YOUTUBE_BASE_URL + movieKey;
        //open trailer with youtube or browser with help from
        //https://stackoverflow.com/questions/1572107/android-intent-for-playing-video
        Intent videoIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieURL));
        startActivity(videoIntent);
    }

    //error messages
    private void showVideosErrorMsg(boolean show){
        if(show){
            videosRV.setVisibility(View.GONE);
            videoErrorTV.setVisibility(View.VISIBLE);
        }else{
            videoErrorTV.setVisibility(View.GONE);
            videosRV.setVisibility(View.VISIBLE);
        }
    }

    private void showReviewsErrorMsg(boolean show){
        if(show){
            reviewsRV.setVisibility(View.GONE);
            reviewErrorTV.setVisibility(View.VISIBLE);
        }else{
            reviewErrorTV.setVisibility(View.GONE);
            reviewsRV.setVisibility(View.VISIBLE);
        }
    }

    public void onStarClicked(View view){
        boolean favorite = ((CheckBox)view).isChecked();

        if (favorite){
            favoriteMovie();
        }else{
            unfavoriteMovie();
        }
    }

    //insert movie to db
    private void favoriteMovie(){
        //Insert new movie data via a ContentResolver
        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieEntry.COLUMN_EXT_ID, selectedMovie.getId());
        contentValues.put(MovieEntry.COLUMN_TITLE, selectedMovie.getTitle());
        contentValues.put(MovieEntry.COLUMN_POSTER, selectedMovie.getPoster());
        contentValues.put(MovieEntry.COLUMN_DATE, selectedMovie.getDate());
        contentValues.put(MovieEntry.COLUMN_RATING, selectedMovie.getRating());
        contentValues.put(MovieEntry.COLUMN_OVERVIEW, selectedMovie.getOverview());
        contentValues.put(MovieEntry.COLUMN_TRAILER_IDS, movieVideosReviews[0]);
        contentValues.put(MovieEntry.COLUMN_REVIEWS, movieVideosReviews[1]);

        Uri uri = getContentResolver().insert(MovieEntry.CONTENT_URI, contentValues);

        // Display the URI that's returned with a Toast
        String msg = selectedMovie.getTitle() + " " + getString(R.string.insert_movie_toast);
        if(uri != null) {
            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        }
    }

    //delete movie form db
    private void unfavoriteMovie(){
        int movieId = selectedMovie.getId();
        String stringId = Integer.toString(movieId);
        Uri uri = MovieEntry.CONTENT_URI;

        // Delete movie using ContentResolver
        int numDeleted = getContentResolver().delete(uri, null, new String[]{stringId});
        // Display confirmation Toast
        String msg = selectedMovie.getTitle() + " "  + getString(R.string.delete_movie_toast);
        if(numDeleted > 0) {
            Toast.makeText(getBaseContext(), msg, Toast.LENGTH_LONG).show();
        }

    }

    private void parseSetVideos(String videosJSON){
        videosArraylist = JsonUtils.parseVideosJSON(videosJSON);
        videoAdapter.setVideoData(videosArraylist);
        //if there's no data show error msg
        if (videosArraylist == null || videosArraylist.size()==0) {
            showVideosErrorMsg(true);
        } else {
            showVideosErrorMsg(false);
        }
    }

    private void parseSetReviews(String reviewsJSON){
        reviewsArraylist = JsonUtils.parseReviewsJSON(reviewsJSON);
        reviewsAdapter.setReviewData(reviewsArraylist);
        //if there's no data show error msg
        if (reviewsArraylist == null || reviewsArraylist.size() == 0) {
            showReviewsErrorMsg(true);
        } else {
            showReviewsErrorMsg(false);
        }
    }

    //save scroll position
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d ("DETAIL_ACTIVITY", "entered onSaveInstanceState");
        outState.putIntArray(SCROLL_KEY,
                new int[] {detailScroll.getScrollX(), detailScroll.getScrollY()});
    }

    //some code from https://asishinwp.wordpress.com/2013/04/15/save-scrollview-position-resume-scrollview-from-that-position/
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        final int[] scrollPosition = savedInstanceState.getIntArray(SCROLL_KEY);
        Log.d ("DETAIL_ACTIVITY", "scrollPosition: " + scrollPosition[0] + "," + scrollPosition[1]);
        if(scrollPosition != null) {
            detailScroll.postDelayed(new Runnable() {
                public void run() {
                    detailScroll.scrollTo(scrollPosition[0], scrollPosition[1]);
                }
            }, 300);
        }
    }
}

