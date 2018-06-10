package com.example.android.popularmovies;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.popularmovies.MovieContract.MovieEntry;

/**
 * Created by Lore on 6/2/2018.
 * some code taken from Udacity's "To Do List" exercise
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favoriteMoviesDb.db";
    private static final int VERSION = 1;

    // Constructor
    MovieDbHelper(Context context) {

        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        // Create movies table
        final String CREATE_TABLE = "CREATE TABLE "  + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID                + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_EXT_ID + " INTEGER NOT NULL, " +
                MovieEntry.COLUMN_TITLE  + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_POSTER + " TEXT, " +
                MovieEntry.COLUMN_DATE   + " TEXT, " +
                MovieEntry.COLUMN_RATING + " REAL, " +
                MovieEntry.COLUMN_OVERVIEW + " TEXT, " +
                MovieEntry.COLUMN_TRAILER_IDS + " TEXT, " +
                MovieEntry.COLUMN_REVIEWS    + " TEXT );";

        db.execSQL(CREATE_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
