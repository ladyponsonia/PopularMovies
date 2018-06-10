package com.example.android.popularmovies;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Lore on 6/2/
 * some code taken from Udacity's "To Do List" exercise
 */
public class MovieContract {

    public static final String AUTHORITY = "com.example.android.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);
    public static final String PATH_MOVIES = "movies";

    /* inner class that defines the contents of the  table */
    public static final class MovieEntry implements BaseColumns {

        // MovieEntry content URI = base content URI + path
        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIES).build(
);

        // Movie table and column names
        public static final String TABLE_NAME = "movies";

        // table has an automatically produced "_ID" column in addition to the two below
        public static final String COLUMN_EXT_ID = "externalID";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_POSTER = "poster";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_RATING = "rating";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_TRAILER_IDS = "trailerURLs";
        public static final String COLUMN_REVIEWS = "reviews";


    }
}
