package com.example.android.popularmovies;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Lore on 5/7/2018.
 * Parcelable with help from https://stackoverflow.com/questions/7181526/how-can-i-make-my-custom-objects-parcelable
 * and http://www.parcelabler.com/
 */

public class Movie implements Parcelable{
    private int mID;
    private String mTitle;
    private String mPoster;
    private String mOverview;
    private float mRating;
    private String mDate;

    //constructor
    public Movie(int id, String title, String poster, String overview, float rating, String date) {

        this.mID = id;
        this.mTitle = title;
        this.mPoster = poster;
        this.mOverview = overview;
        this.mRating = rating;
        this.mDate = date;
    }

    //get methods
    public int getId() {
        return mID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getPoster() { return mPoster; }

    public String getOverview() {
        return mOverview;
    }

    public float getRating() {
        return mRating;
    }

    public String getDate() {
        return mDate;
    }

    //readable version
    @Override
    public String toString() {

        return "id: " + this.mID + ", title: " + this.mTitle + ", poster: " + this.mPoster
                + ", overview: " + this.mOverview + ", rating: " + this.mRating + ", date: " + this.mDate;

    }

    public Movie(Parcel in){
        mID = in.readInt();
        mTitle = in.readString();
        mPoster = in.readString();
        mOverview = in.readString();
        mRating = in.readFloat();
        mDate = in.readString();
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mID);
        parcel.writeString(mTitle);
        parcel.writeString(mPoster);
        parcel.writeString(mOverview);
        parcel.writeFloat(mRating);
        parcel.writeString(mDate);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Movie> CREATOR = new Parcelable.Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
}
