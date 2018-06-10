package com.example.android.popularmovies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by Lore on 5/7/2018.
 */

public class JsonUtils {

    private static final String POSTER_BASE_URL = "http://image.tmdb.org/t/p/";

    //takes json string and returns array of image/id pair
    public static Movie[] parseJSON(String rawJSON ) {

        Movie[] moviesArray = new Movie[20];
    //(int id, String title, String poster, String overview, float rating, String date)
        try {
            //create JSONobject from jsonString
            JSONObject jsonResponse = new JSONObject(rawJSON);
            //get array of movies
            JSONArray moviesJSONArray = jsonResponse.getJSONArray("results");

            //get details for each movie
            for (int i = 0; i < moviesJSONArray.length(); i++) {
                JSONObject movie = moviesJSONArray.getJSONObject(i);
                int id = movie.getInt("id");
                String title = movie.getString("title");
                String posterPath = movie.getString("poster_path");
                String overview = movie.getString("overview");
                float rating = movie.getLong("vote_average");
                String date = movie.getString("release_date");

                //add base URL and size to posterPath
                String size = "w185";
                String poster = POSTER_BASE_URL + size + posterPath;

                //create movie and add to the Movie array
                Movie movieInstance = new Movie(id, title, poster, overview, rating, date);
                moviesArray[i] = movieInstance;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return moviesArray;
    }

    public static ArrayList<String> parseVideosJSON (String rawJSON){

        ArrayList<String> videoArrayList = new ArrayList<String>();
        try {
            //create JSONobject from jsonString
            JSONObject videosJSON = new JSONObject(rawJSON);
            //get array of videos
            JSONArray videosJSONArray = videosJSON.getJSONArray("results");

            //get key for each video
            for (int i = 0; i < videosJSONArray.length(); i++) {
                JSONObject video = videosJSONArray.getJSONObject(i);
                String key = video.getString("key");

                //add to the videos arrayList
                videoArrayList.add(key);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return videoArrayList;
    }

    public static ArrayList<String[]> parseReviewsJSON (String rawJSON){

        ArrayList<String[]> reviewsArrayList = new ArrayList<String[]>();
        try {
            //create JSONobject from jsonString
            JSONObject reviewsJSON = new JSONObject(rawJSON);
            //get array of reviews
            JSONArray reviewsJSONArray = reviewsJSON.getJSONArray("results");

            //get author and content for each review
            for (int i = 0; i < reviewsJSONArray.length(); i++) {
                JSONObject video = reviewsJSONArray.getJSONObject(i);
                String author = video.getString("author");
                String content = video.getString("content");
                //add to the reviews arrayList
                reviewsArrayList.add(new String[]{author, content});
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reviewsArrayList;
    }
}


