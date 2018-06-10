package com.example.android.popularmovies;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;

import static com.example.android.popularmovies.MovieContract.MovieEntry;

/**
 * Created by Lore on 6/2/2018.
 * some code taken from Udacity's "To Do List" exercise
 */

public class MovieContentProvider extends ContentProvider{

    private static final int MOVIES = 100;
    private static final int MOVIE_WITH_ID = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private static UriMatcher buildUriMatcher() {

        // Initialize a UriMatcher with no matches
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES, MOVIES);
        uriMatcher.addURI(MovieContract.AUTHORITY, MovieContract.PATH_MOVIES + "/#", MOVIE_WITH_ID);

        return uriMatcher;
    }

    // Member variable for a DbHelper that's initialized in the onCreate() method
    private MovieDbHelper mMovieDbHelper;
    @Override
    public boolean onCreate() {
        Context context = getContext();
        mMovieDbHelper = new MovieDbHelper(context);
        return true;
    }


    // Insert new movie to movies table
    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        // Get access to the movie database
        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        Uri returnUri;
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                long id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(MovieEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            // default case for unknown URI's
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Notify the content resolver
        getContext().getContentResolver().notifyChange(uri, null);

        return returnUri;
    }


    // query all movies to display in grid layout or query to check if movie exists in db
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        final SQLiteDatabase db = mMovieDbHelper.getReadableDatabase();

        Cursor retCursor;
        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                retCursor =  db.query(MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Set a notification URI on the Cursor and return that Cursor
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }


    // delete a single movie from movies table
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        final SQLiteDatabase db = mMovieDbHelper.getWritableDatabase();

        int movieDeleted;

        switch (sUriMatcher.match(uri)) {
            case MOVIES:
                movieDeleted = db.delete(MovieEntry.TABLE_NAME, MovieEntry.COLUMN_EXT_ID+"=?", selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        if (movieDeleted != 0) {
            // A movie was deleted, set notification
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return movieDeleted;
    }


    @Override
    public int update(@NonNull Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {

        throw new UnsupportedOperationException("Not yet implemented");
    }


    @Override
    public String getType(@NonNull Uri uri) {

        throw new UnsupportedOperationException("Not yet implemented");
    }
}
