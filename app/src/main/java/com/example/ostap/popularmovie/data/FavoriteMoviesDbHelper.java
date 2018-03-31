package com.example.ostap.popularmovie.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.ostap.popularmovie.data.FavoriteMoviesContract.MovieEntry;
import com.example.ostap.popularmovie.model.Movie;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ostap on 06/03/2018.
 */

public class FavoriteMoviesDbHelper extends SQLiteOpenHelper {

    private static final String TAG = FavoriteMoviesDbHelper.class.getSimpleName();

    // Database info
    private static final String DATABASE_NAME = "favoriteMovies.db";
    private static final int DATABASE_VERSION = 1;

    private static FavoriteMoviesDbHelper sInstance;


    /**
     * This method ensures that only one instance of FavoriteMoviesDbHelper will ever exist at any
     * given time. By using singleton pattern in this way, we avoid memory leaks and unnecessary
     * reallocations.
     * @param context The context from which the request was sent
     * @return Returns an instance of FavoriteMoviesDbHelper
     */
    public static synchronized FavoriteMoviesDbHelper getInstance(Context context) {
        if (sInstance ==  null) {
            sInstance = new FavoriteMoviesDbHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    /**
     * Constructor should be private to prevent direct instantiation.
     * Make a call to the static method "getInstance()" instead.
     */
    private FavoriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_FAVORITE_MOVIES_TABLE = "CREATE TABLE " + MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieEntry.COLUMN_MOVIE_IMAGE_URL + " TEXT NOT NULL," +
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL," +
                MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL," +
                MovieEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL," +
                MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT NOT NULL);";

        sqLiteDatabase.execSQL(CREATE_FAVORITE_MOVIES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // TODO in production change to ALTER
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    /**
     * Insert or update a movie in the database.
     * @param movie The movie we want to insert or update.
     * @return Returns the ID of the inserted or updated movie
     */
    public long addOrUpdateMovie(Movie movie) {
        SQLiteDatabase db = getWritableDatabase();
        long movieId = -1;

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(MovieEntry.COLUMN_MOVIE_TITLE, movie.getTitle());
            values.put(MovieEntry.COLUMN_MOVIE_IMAGE_URL, movie.getImage());
            values.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, movie.getOverview());
            values.put(MovieEntry.COLUMN_MOVIE_RATING, movie.getRating());
            values.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, movie.getReleaseDate());

            db.insertOrThrow(MovieEntry.TABLE_NAME, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add or update movie");
            Log.d(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }

        return movieId;
    }


    /**
     * Returns all movies stored in database.
     * @return Returns a list of Movie objects.
     */
    public List<Movie> getAllMovies() {
        List<Movie> movies = new ArrayList<>();

        String MOVIES_SELECT_QUERY = String.format("SELECT * FROM %s", MovieEntry.TABLE_NAME);

        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery(MOVIES_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    Movie movie = new Movie();
                    movie.setTitle(cursor.getString(cursor.getColumnIndex(
                            MovieEntry.COLUMN_MOVIE_TITLE)));

                    movie.setImage(cursor.getString(cursor.getColumnIndex(
                            MovieEntry.COLUMN_MOVIE_IMAGE_URL)));

                    movie.setOverview(cursor.getString(cursor.getColumnIndex(
                            MovieEntry.COLUMN_MOVIE_OVERVIEW)));

                    movie.setRating(cursor.getString(cursor.getColumnIndex(
                            MovieEntry.COLUMN_MOVIE_RATING)));

                    movie.setReleaseDate(cursor.getString(cursor.getColumnIndex(
                            MovieEntry.COLUMN_MOVIE_RELEASE_DATE)));

                    movies.add(movie);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get movies from database");
        } finally {
            if (cursor != null & !cursor.isClosed()) cursor.close();
        }

        return movies;
    }


    /**
     * Removes a given movie from the database.
     * @param title The title of the movie that should be removed from database.
     */
    public void removeMovie(String title) {
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {

            int deletedRows = db.delete(MovieEntry.TABLE_NAME, MovieEntry.COLUMN_MOVIE_TITLE + " LIKE ?",
                    new String[]{title});
            Log.d(TAG, Integer.toString(deletedRows));
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to remove " + title + " from the database");
            Log.d(TAG, e.getMessage());
        } finally {
            db.endTransaction();
        }
    }


    /**
     * Returns titles of the movies that are stores in the database
     */
    public ArrayList<String> getMoviesTitle() {
        ArrayList<String> moviesTitle = new ArrayList<>();

        SQLiteDatabase db = getWritableDatabase();

        String MOVIES_SELECT_QUERY = String.format("SELECT * FROM %s", MovieEntry.TABLE_NAME);

        Cursor cursor = db.rawQuery(MOVIES_SELECT_QUERY, null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    moviesTitle.add(cursor.getString(cursor.getColumnIndex(
                            MovieEntry.COLUMN_MOVIE_TITLE)));
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "Error while trying to get movies title from the database");
        }

        return moviesTitle;
    }
}
