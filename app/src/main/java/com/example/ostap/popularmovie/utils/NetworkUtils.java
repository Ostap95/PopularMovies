package com.example.ostap.popularmovie.utils;

import android.net.Uri;
import android.util.Log;

import com.example.ostap.popularmovie.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import javax.net.ssl.HttpsURLConnection;

/**
 * Created by ostap on 26/02/2018.
 * This class handles all the network connections to fetch data from the API.
 */

public class NetworkUtils {

    public static final String MOVIE_POPULAR_RANKING = "popular";
    public static final String MOVIE_TOP_RATED_RANKING = "top_rated";
    private static final String TAG = NetworkUtils.class.getSimpleName();
    private static final String BASE_MOVIE_URL =
            "https://api.themoviedb.org/3/movie";
    private static final String API_KEY = "api_key";
    private static final String API_KEY_VALUE = BuildConfig.API_KEY;
    private static final String MOVIE_VIDEOS_PATH = "videos";
    private static final String MOVIE_REVIEWS_PATH = "reviews";


    /**
     * Builds a URL based on the desired ranking
     * @param typeRanking should be in the form of #MOVIE_POPULAR_RANKING or #MOVIE_TOP_RATED_RANKING
     * @return built url
     */
    public static URL buildUrl(String typeRanking) {

        Uri buildUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(typeRanking)
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Couldn't create URL: " + buildUri.toString());
        }

        return url;
    }

    /**
     * Builds a video URL based on the movie id
     *
     * @param movieId ID of the movie that we want to get the trailer
     * @return Built url
     */
    public static URL buildVideosUrl(String movieId) {

        Uri buildUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(MOVIE_VIDEOS_PATH)
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Couldn't create video URL: " + buildUri.toString());
        }
        return url;
    }

    /**
     * Builds reviews URL based on the movie id
     *
     * @param movieId ID of the movie that we want to get the reviews
     * @return Built url.
     */
    public static URL buildReviewsUrl(String movieId) {

        Uri buildUri = Uri.parse(BASE_MOVIE_URL).buildUpon()
                .appendPath(movieId)
                .appendPath(MOVIE_REVIEWS_PATH)
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();

        URL url = null;
        try {
            url = new URL(buildUri.toString());
        } catch (MalformedURLException e) {
            Log.e(TAG, "Couldn't create reviews URL: " + buildUri.toString());
        }
        return url;
    }


    /**
     * Returns the result from the HTTP response.
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading.
     */
    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpsURLConnection urlConnection = (HttpsURLConnection) url.openConnection();
        try {
            InputStream inputStream = urlConnection.getInputStream();

            Scanner scanner = new Scanner(inputStream);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();

            if (hasInput) return scanner.next();
            else return null;

        } finally {
            urlConnection.disconnect();
        }
    }
}
