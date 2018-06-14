package com.example.android.popularmovies;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Lore on 5/5/2018.
 * Some code copied from Udacity Sunshine exercise
 */

public class NetworkUtils {

    private static final String API_BASE_URL = "http://api.themoviedb.org/";
    private static final String API_KEY_PARAM = "api_key";
    private static String API_KEY = BuildConfig.TMDB_API_KEY;
    private static final String API_MOVIE_PATH = "3/movie/";

    //build query URL depending on sort mode
    public static URL buildQueryUrl(String sort) {
        Uri movieQueryUri = Uri.parse(API_BASE_URL).buildUpon()
                .path(sort)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        try {
            URL movieQueryUrl = new URL(movieQueryUri.toString());
            return movieQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }

    }

    //build videos and reviews query URL for selected movie
    public static URL buildVideosReviewsUrl(int id, String mode) {
        Uri videosQueryUri = Uri.parse(API_BASE_URL).buildUpon()
                .path(API_MOVIE_PATH + String.valueOf(id) + mode)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        try {
            URL videosQueryUrl = new URL(videosQueryUri.toString());
            return videosQueryUrl;
        } catch (MalformedURLException e) {
            e.printStackTrace();
            return null;
        }
    }


    //make query and return JSON as string
    public static String getHttpResponse(URL url) throws IOException {

        //open url connection
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        // set the connection and read timeouts
        //with help from https://eventuallyconsistent.net/2011/08/02/working-with-urlconnection-and-timeouts/
        urlConnection.setConnectTimeout(10000);
        urlConnection.setReadTimeout(10000);
        try {
            //get response(input stream)
            InputStream in = urlConnection.getInputStream();
            //read input stream
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A"); //A:“beginning of the input boundary” gets only one token for the entire contents of the stream.

            //if response is not empty, save it and return it
            boolean hasInput = scanner.hasNext();
            String response = null;
            if (hasInput) {
                response = scanner.next();
            }
            scanner.close();
            return response;
        } finally {
            //close connection
            urlConnection.disconnect();
        }
    }

    //check internet connection from https://stackoverflow.com/questions/1560788/how-to-check-internet-access-on-android-inetaddress-never-times-out
    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

}
