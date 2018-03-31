package com.example.ostap.popularmovie.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ostap on 06/03/2018.
 */

public class FavoriteMoviesContract {

    /* Name for the entire content provider */
    public static final String CONTENT_AUTHORITY = "com.example.ostap.popularmovie";

    /**
     * The content authority is used to create the base of all URIs which apps will use to
     * contact this content provider.
     */
    private static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /* path that will be appended to the base URI for each of the different tables */
    public static final String PATH_MOVIE = "movie";

    // We define the constructor as private to prevent its instances
    private FavoriteMoviesContract(){}

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE)
                .build();

        public static final String CONTENT_TYPE =
                "vnd.android.cursor.dir/" +  CONTENT_URI + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                "vnd.android.cursor.item/" + CONTENT_URI + "/" + PATH_MOVIE;

        public static final String TABLE_NAME = "favorite_movies";
        public static final String COLUMN_MOVIE_TITLE = "movie_title";
        public static final String COLUMN_MOVIE_IMAGE_URL = "movie_image_url";
        public static final String COLUMN_MOVIE_OVERVIEW = "movie_overview";
        public static final String COLUMN_MOVIE_RATING = "movie_rating";
        public static final String COLUMN_MOVIE_RELEASE_DATE = "movie_release_date";

        public static Uri buildMovieUri(long id) {
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
