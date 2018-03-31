package com.example.ostap.popularmovie.utils;

import com.example.ostap.popularmovie.model.Movie;
import com.example.ostap.popularmovie.model.Review;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ostap on 25/02/2018.
 */

public class MovieJsonUtils {

    private static final String ARRAY_RESULTS = "results";
    private static final String MOVIE_IMAGE = "poster_path";
    private static final String MOVIE_OVERVIEW = "overview";
    private static final String MOVIE_RATING = "vote_average";
    private static final String MOVIE_ID = "id";
    private static final String MOVIE_TITLE = "original_title";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    private static final String MOVIE_IMAGE_URL = "http://image.tmdb.org/t/p/w780/";
    private static final String MOVIE_TRAILER_KEY = "key";
    private static final String MOVIE_REVIEW_AUTHOR = "author";
    private static final String MOVIE_REVIEW_CONTENT = "content";
    private static final String MOVIE_REVIEW_URL = "url";

    /**
     * Creates Movie objects based on the json response string.
     *
     * @param jsonResponse JSON response string.
     * @return Array list of Movie objects.
     */
    public static List<Movie> getMoviesFromJson(String jsonResponse){

        List<Movie> movies = new ArrayList<>();

        try {

            JSONObject moviesJson = new JSONObject(jsonResponse);

            JSONArray moviesArray = moviesJson.getJSONArray(ARRAY_RESULTS);

            for (int i = 0; i < moviesArray.length(); i++) {

                JSONObject movie = moviesArray.getJSONObject(i);
                String id = movie.optString(MOVIE_ID);
                String title = movie.optString(MOVIE_TITLE);
                String image = movie.optString(MOVIE_IMAGE);
                image = MOVIE_IMAGE_URL + image;
                String overview = movie.optString(MOVIE_OVERVIEW);
                String rating = movie.optString(MOVIE_RATING);
                String releaseDate = movie.optString(MOVIE_RELEASE_DATE);

                movies.add(new Movie(id, title, image, overview, rating, releaseDate));

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return movies;
    }

    /**
     * Extracts trailer urls from JSON response string.
     *
     * @param jsonResponse JSON response string.
     * @return Array list of urls.
     */
    public static List<String> getTrailerKeysFromJson(String jsonResponse) {

        List<String> trailers = new ArrayList<>();

        try {
            JSONObject trailerJson = new JSONObject(jsonResponse);
            JSONArray trailersArray = trailerJson.getJSONArray(ARRAY_RESULTS);

            for (int i = 0; i < trailersArray.length(); i++) {
                JSONObject trailer = trailersArray.getJSONObject(i);
                trailers.add(Movie.MOVIE_TRAILER_BASE_URL + trailer.optString(MOVIE_TRAILER_KEY));
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return trailers;
    }

    /**
     * Creates Review object from the JSON response string.
     *
     * @param jsonResponse JSON response string.
     * @return Array list of Review objects.
     */
    public static List<Review> getReviewsFromJson(String jsonResponse) {

        List<Review> reviews = new ArrayList<>();

        try {
            JSONObject reviewsJson = new JSONObject(jsonResponse);
            JSONArray reviewsArray = reviewsJson.getJSONArray(ARRAY_RESULTS);

            for (int i = 0; i < reviewsArray.length(); i++) {
                JSONObject review = reviewsArray.getJSONObject(i);
                Review rv = new Review(review.optString(MOVIE_REVIEW_AUTHOR),
                        review.optString(MOVIE_REVIEW_CONTENT),
                        review.optString(MOVIE_REVIEW_URL));
                reviews.add(rv);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return reviews;
    }

}
