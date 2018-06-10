package com.example.android.popularmovies;


import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.popularmovies.MovieContract.MovieEntry;


import java.io.IOException;
import java.net.URL;
import java.util.Objects;

/**
 * Created by Lore on 5/5/2018.
 * Some code copied from Udacity Sunshine exercise
 */

public class MainActivity extends AppCompatActivity implements MovieAdapter.MovieOnClickHandler, LoaderManager.LoaderCallbacks {

    private static final String API_POPULAR = "3/movie/popular";
    private static final String API_TOP_RATED = "3/movie/top_rated";
    private static final String SORT_FAVORITES = "favorites";


    private static final String SORT_BUNDLE_KEY = "sortMode";
    private static final String SORT_PREFERENCE_KEY = "sortSelection";
    public static final String MOVIE_PARCEL_KEY = "selectedMovie";

    private static final int API_QUERY_LOADER_ID = 999;
    private static final int DB_QUERY_LOADER_ID = 888;

    private String rawJSON;
    private RecyclerView  mainRV;
    private TextView errorTV;
    private MovieAdapter movieAdapter;
    private Movie[] moviesArray;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //get recyclerView
        mainRV = (RecyclerView) findViewById(R.id.main_rv);
        //get error textview
        errorTV = (TextView)findViewById(R.id.error_msg_tv);

        //get shared preference for sort
        String sortSelection;
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        if (sharedPreferences.contains(SORT_PREFERENCE_KEY)){
            sortSelection = sharedPreferences.getString(SORT_PREFERENCE_KEY, API_POPULAR);
        }else{
            sortSelection = API_POPULAR; //default if preference not saved
        }

        //set activity title
        String activityTitle;
        switch(sortSelection){
            case API_POPULAR:
                activityTitle = getString(R.string.popular_title);
                break;
            case API_TOP_RATED:
                activityTitle = getString(R.string.top_rated_title);
                break;
            case SORT_FAVORITES:
                activityTitle = getString(R.string.favorites_title);
                break;
            default:
                activityTitle = getString(R.string.popular_title);
                break;
        }
        setTitle(activityTitle);


        //if favorites selected start loader to read database, else star loader for api call
        if (Objects.equals(sortSelection, SORT_FAVORITES)){
            getSupportLoaderManager().initLoader(DB_QUERY_LOADER_ID, null, this);
        }else {
            //initialize loader for http request if there is internet connection
            if (NetworkUtils.isOnline(this)) {
                //1st time sort by popularity
                Bundle sortBundle = new Bundle();
                sortBundle.putString(SORT_BUNDLE_KEY, sortSelection);
                getSupportLoaderManager().initLoader(API_QUERY_LOADER_ID, sortBundle, this);
                //show recyclerView
                showErrorMsg(false);
            } else {
                //show error message
                errorTV.setText(R.string.connection_error);
                showErrorMsg(true);
            }
        }

        //instantiate and set Movies adapter
        movieAdapter = new MovieAdapter(this, moviesArray,this );
        mainRV.setAdapter(movieAdapter);
        mainRV.setSaveEnabled(true);

        //set layout manager depending on orientation
        //with help from https://stackoverflow.com/questions/29579811/changing-number-of-columns-with-gridlayoutmanager-and-recyclerview
        GridLayoutManager portraitLayoutManager = new GridLayoutManager(this, 2);
        GridLayoutManager landscapeLayoutManager = new GridLayoutManager(this, 3);
        if(this.getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
            mainRV.setLayoutManager(portraitLayoutManager);
        }
        else{
            mainRV.setLayoutManager(landscapeLayoutManager);
        }
    }

    @Override
    protected void onResume() {
        //if favorites selected, restart loader to get db changes that may have been made in detailActivity
        if (sharedPreferences.contains(SORT_PREFERENCE_KEY)) {
            String sortSelection = sharedPreferences.getString(SORT_PREFERENCE_KEY, SORT_FAVORITES);
            if (Objects.equals(sortSelection, SORT_FAVORITES)){
                movieAdapter.setMovieData(null);
                getSupportLoaderManager().restartLoader(DB_QUERY_LOADER_ID, null, this);
            }
        }
        super.onResume();
    }

    //asyncTaskLoader with help from https://medium.com/@sanjeevy133/an-idiots-guide-to-android-asynctaskloader-76f8bfb0a0c0
    //multiple loaders help from https://stackoverflow.com/questions/10872949/cursorloaders-and-asynctaskloaders-using-same-loadermanager
    @Override
    public Loader onCreateLoader(int id, final Bundle bundle) {
        if (id == API_QUERY_LOADER_ID ) {

            return new AsyncTaskLoader<String>(this) {

                //variable to cache the data
                String movieData = null;
                //get sort order from bundle
                final String sortBy = bundle.getString(SORT_BUNDLE_KEY);

                @Override
                protected void onStartLoading() {
                    if (movieData != null) {
                        deliverResult(movieData);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public String loadInBackground() {
                    URL queryURL = NetworkUtils.buildQueryUrl(sortBy);
                    try {
                        rawJSON = NetworkUtils.getHttpResponse( queryURL );
                        return rawJSON;
                    } catch (IOException e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                public void deliverResult(String data) {
                    movieData = data;//result that is sent to onLoadFinished
                    super.deliverResult(data);
                }

            };
        } else if (id == DB_QUERY_LOADER_ID ) {

            return new AsyncTaskLoader<Cursor>(this) {

                // Initialize a Cursor to hold all the movie data
                Cursor movieData = null;

                @Override
                protected void onStartLoading() {
                    if (movieData != null) {
                        deliverResult(movieData);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public Cursor loadInBackground() {
                    try {
                        return getContentResolver().query(MovieEntry.CONTENT_URI,
                                null,
                                null,
                                null,
                                null);

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }

                // deliverResult sends the result of the load, a Cursor, to the registered listener
                public void deliverResult(Cursor data) {
                    movieData = data;
                    super.deliverResult(data);
                }
            };
        }
        return null;
    }

    @Override
    public void onLoadFinished(Loader loader, Object data) {
        int id = loader.getId();
        if (id == API_QUERY_LOADER_ID ) {
            String s = (String)data;
            try {
                //parse JSON
                moviesArray = JsonUtils.parseJSON(s);
                //set new data on adapter
                movieAdapter.setMovieData(moviesArray);
                //if there's no data show error msg
                if (moviesArray == null) {
                    errorTV.setText(R.string.no_data);
                    showErrorMsg(true);
                } else {
                    showErrorMsg(false);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == DB_QUERY_LOADER_ID ) {
            moviesArray = cursorToMovieArray((Cursor)data);
            movieAdapter.setMovieData(moviesArray);
            //if there's no data show error msg
            if (moviesArray == null) {
                errorTV.setText(R.string.no_data);
                showErrorMsg(true);
            } else {
                showErrorMsg(false);
            }
        }
    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    //show recyclerView or error message
    private void showErrorMsg(boolean show){
        if(show){
            mainRV.setVisibility(View.GONE);
            errorTV.setVisibility(View.VISIBLE);
        }else{
            errorTV.setVisibility(View.GONE);
            mainRV.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onClick(Movie movie) {
        //open detail activity
        Intent movieDetailsIntent = new Intent(this, DetailActivity.class);
        movieDetailsIntent.putExtra( MOVIE_PARCEL_KEY, movie);
        startActivity(movieDetailsIntent);
    }

    @Override
    //sort by menu
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sort_by_options, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        Bundle sortBundle = new Bundle();
        SharedPreferences.Editor preferenceEditor = sharedPreferences.edit();

        if (id == R.id.sort_popular) {
            //clear data
            movieAdapter.setMovieData(null);
            //change activity title
            setTitle(getString(R.string.popular_title));
            //add arg to loader bundle
            sortBundle.putString(SORT_BUNDLE_KEY, API_POPULAR);
            //update shared preference
            preferenceEditor.putString(SORT_PREFERENCE_KEY, API_POPULAR);
            preferenceEditor.commit();

            //restart loader for http request if there is internet connection
            if(NetworkUtils.isOnline(this)) {
                getSupportLoaderManager().restartLoader(API_QUERY_LOADER_ID, sortBundle, this);
                return true;
            }else{
                //show error message
                errorTV.setText(R.string.connection_error);
                showErrorMsg(true);
            }
        }

        if (id == R.id.sort_top_rated) {
            //clear data
            movieAdapter.setMovieData(null);
            //change activity title
            setTitle(getString(R.string.top_rated_title));
            //add arg to loader bundle
            sortBundle.putString(SORT_BUNDLE_KEY, API_TOP_RATED);
            //update shared preference
            preferenceEditor.putString(SORT_PREFERENCE_KEY, API_TOP_RATED);
            preferenceEditor.commit();
            //restart loader for http request if there is internet connection
            if (NetworkUtils.isOnline(this)) {
                getSupportLoaderManager().restartLoader(API_QUERY_LOADER_ID, sortBundle, this);
                return true;
            } else {
                //show error message
                errorTV.setText(R.string.connection_error);
                showErrorMsg(true);
            }
        }

        if (id == R.id.sort_favorites) {
            //clear data
            movieAdapter.setMovieData(null);
            //change activity title
            setTitle(getString(R.string.favorites_title));
            //update shared preference
            preferenceEditor.putString(SORT_PREFERENCE_KEY, SORT_FAVORITES);
            preferenceEditor.commit();
            //restart loader to read db
            getSupportLoaderManager().restartLoader(DB_QUERY_LOADER_ID, null, this);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //put Cursor data into Movie[] for MovieAdapter
    private Movie[] cursorToMovieArray(Cursor moviesCursor){
        int count = moviesCursor.getCount();
        moviesArray = new Movie[count];
        if (moviesCursor.moveToFirst()){
            int i = 0;
            while(!moviesCursor.isAfterLast()){
                int movieId = moviesCursor.getInt(moviesCursor.getColumnIndex(MovieEntry.COLUMN_EXT_ID));
                String movieTitle = moviesCursor.getString(moviesCursor.getColumnIndex(MovieEntry.COLUMN_TITLE));
                String movieOverview = moviesCursor.getString(moviesCursor.getColumnIndex(MovieEntry.COLUMN_OVERVIEW));
                float movieRating = moviesCursor.getFloat(moviesCursor.getColumnIndex(MovieEntry.COLUMN_RATING));
                String movieDate = moviesCursor.getString(moviesCursor.getColumnIndex(MovieEntry.COLUMN_DATE));
                String moviePoster = moviesCursor.getString(moviesCursor.getColumnIndex(MovieEntry.COLUMN_POSTER));
                moviesCursor.moveToNext();
                //create new movie with data from cursor row
                Movie movie = new Movie( movieId, movieTitle, moviePoster, movieOverview, movieRating, movieDate);
                moviesArray[i] = movie;
                i++;
            }
        }
        return moviesArray;
    }
}
