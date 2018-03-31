package com.example.ostap.popularmovie.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.example.ostap.popularmovie.data.FavoriteMoviesContract.MovieEntry;

/**
 * Created by ostap on 16/03/2018.
 */

public class MovieProvider extends ContentProvider {

    private static final int MOVIE = 100;
    private static final int MOVIE_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoriteMoviesDbHelper mOpenHelper;

    @Override
    public boolean onCreate() {
        mOpenHelper = FavoriteMoviesDbHelper.getInstance(getContext());
        return true;
    }

    /**
     * Builds a UriMatcher that is used to determine witch database request is being made.
     */
    public static UriMatcher buildUriMatcher() {
        String content = FavoriteMoviesContract.CONTENT_AUTHORITY;

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(content, FavoriteMoviesContract.PATH_MOVIE, MOVIE);
        matcher.addURI(content, FavoriteMoviesContract.PATH_MOVIE + "/#", MOVIE_ID);

        return matcher;
    }

    /**
     * @param uri The URI (table) that should be queried.
     * @param projection A string array of columns that will be returned in the result set.
     * @param selection A string defining the criteria for results to be returned.
     * @param selectionArgs Arguments to the above criteria that rows will be checked against.
     * @param sortOrder A string of the column(s) and order to sort the result set by.
     * @return
     */
    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Cursor retCursor;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                retCursor = db.query(MovieEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            case MOVIE_ID:
                long _id = ContentUris.parseId(uri);
                retCursor = db.query(MovieEntry.TABLE_NAME,
                        projection,
                        MovieEntry._ID + " = ?",
                        new String[]{String.valueOf(_id)},
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        /*
        Set the notification URI for the cursor to the one passed into the function. This causes
        the cursor to register a content observer to watch for changes that happen to this URI and
        any of it's descendants. By descendants, we mean any URI that begins with this path.
         */
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return retCursor;
    }

    /*
    This method is used to find MIME type of the results, either a directory of multiple results
    or an individual item
     */
    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                return MovieEntry.CONTENT_TYPE;
            case MOVIE_ID:
                return MovieEntry.CONTENT_ITEM_TYPE;
            default:
                throw new UnsupportedOperationException("Unknown uri: " +  uri);
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        long _id;
        Uri returnUri;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                _id = db.insert(MovieEntry.TABLE_NAME, null, values);
                if(_id > 0){
                    returnUri = MovieEntry.buildMovieUri(_id);
                } else{
                    throw new UnsupportedOperationException("Unable to insert rows into: " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);

        }
        // Use this on the URI passed into the function to notify any observers that the uri has
        // changed.
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;

    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows; // number of rows affected

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                rows = db.delete(MovieEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }

        // Because null could delete all rows
        if (selection == null || rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues,
                      @Nullable String selection, @Nullable String[] selectionArgs) {

        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int rows;

        switch (sUriMatcher.match(uri)) {
            case MOVIE:
                rows = db.update(MovieEntry.TABLE_NAME, contentValues, selection, selectionArgs);
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri:" + uri);
        }

        if (rows != 0) getContext().getContentResolver().notifyChange(uri, null);
        return rows;
    }
}
