package com.example.ostap.popularmovie;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.ostap.popularmovie.adapter.MovieAdapter;
import com.example.ostap.popularmovie.data.FavoriteMoviesContract.MovieEntry;
import com.example.ostap.popularmovie.data.PopularMoviePreferences;
import com.example.ostap.popularmovie.model.Movie;
import com.example.ostap.popularmovie.utils.MovieJsonUtils;
import com.example.ostap.popularmovie.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements
        LoaderCallbacks, MovieAdapter.MovieAdapterOnClickHandler {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int NUMBER_COLUMNS = 2;
    private static final int MOVIES_LOADER_ID = 0;
    private static final int DATABASE_LOADER_ID = 1;

    private ProgressBar mLoadingIndicator;
    private RecyclerView mPosterRecyclerView;
    private TextView mErrorMessageTextView;
    private ImageView mErrorImageView;

    private MovieAdapter mMovieAdapter = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mErrorImageView = findViewById(R.id.error_iv);

        mErrorMessageTextView = findViewById(R.id.error_tv);

        mPosterRecyclerView = findViewById(R.id.posters_rv);
        mPosterRecyclerView.setLayoutManager(new GridLayoutManager(this, NUMBER_COLUMNS));
        mPosterRecyclerView.setHasFixedSize(true);

        mMovieAdapter = new MovieAdapter(this, this);
        mPosterRecyclerView.setAdapter(mMovieAdapter);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        /*
        The variable callback is passed to initLoader below. This means that whenever the
        loaderManager has something to notify us og, it will do so through this callback.
         */
        LoaderCallbacks callback = MainActivity.this;

        /* First check for the internet connection, before sending request to the API */
        ConnectivityManager cm =
                (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnectedToInternet = activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();

        if (PopularMoviePreferences.getPreferredFilter(this)
                .equals(PopularMoviePreferences.MOVIE_FAVORITE_RANKING)) {

            invalidateData();
            showMovieDataView();
            PopularMoviePreferences.setFilter(this, PopularMoviePreferences.MOVIE_FAVORITE_RANKING);
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            getSupportLoaderManager().restartLoader(DATABASE_LOADER_ID, null, this);
        } else if (isConnectedToInternet) {
            /*
            Ensures a loader is initialized and active. If the loader doesn't already exist, one is
            created and (if the activity/fragment is currently started) starts the loader. Otherwise
            the last created loader is re-used.
             */
            getSupportLoaderManager().initLoader(MOVIES_LOADER_ID, null, callback);
        } else
            showErrorMessage();

    }

    /**
     * Instantiate and return a new Loader for the given ID
     * @param id of the loader to be created
     * @param args to pass to the loader
     * @return loader instance that is ready to start loading
     */
    @Override
    public Loader onCreateLoader(int id, final Bundle args) {

        switch (id) {
            case MOVIES_LOADER_ID:
                return new AsyncTaskLoader<List<Movie>>(this) {

                    /* This string holds and helps to cache the movies data */
                    List<Movie> mMoviesData = null;

                    @Override
                    protected void onStartLoading() {
                        super.onStartLoading();

                        if (mMoviesData != null) {
                            deliverResult(mMoviesData);
                        } else {
                            mLoadingIndicator.setVisibility(View.VISIBLE);
                            forceLoad();
                        }

                    }

                    /**
                     * This is the method of the AsyncTaskLoader that will load and parse the JSON data
                     * in the background.
                     *
                     * @return movie data from the API
                     */
                    @Override
                    public List<Movie> loadInBackground() {

                        List<Movie> movies = null;
                        String jsonResponse;

                        String filter = PopularMoviePreferences.getPreferredFilter(getContext());

                        URL moviesRequestUrl = NetworkUtils.buildUrl(filter);

                        try {
                            jsonResponse = NetworkUtils.getResponseFromHttpUrl(moviesRequestUrl);
                            movies = MovieJsonUtils.getMoviesFromJson(jsonResponse);
                            for (Movie movie : movies) {
                                String trailerJsonResponse = NetworkUtils.getResponseFromHttpUrl(
                                        NetworkUtils.buildVideosUrl(movie.getId()));

                                movie.setTrailersUrl(MovieJsonUtils.getTrailerKeysFromJson(trailerJsonResponse));

                                String reviewsJsonResponse = NetworkUtils.getResponseFromHttpUrl(
                                        NetworkUtils.buildReviewsUrl(movie.getId()));

                                movie.setReviews(MovieJsonUtils.getReviewsFromJson(reviewsJsonResponse));

                            }

                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        return movies;
                    }

                    /**
                     * Sends the result of the load to the registered listener
                     *
                     * @param data the result of the load
                     */
                    @Override
                    public void deliverResult(List<Movie> data) {
                        super.deliverResult(data);
                        mMoviesData = data;
                    }
                };
            case DATABASE_LOADER_ID:
                String[] projection = {
                        MovieEntry.COLUMN_MOVIE_IMAGE_URL,
                        MovieEntry.COLUMN_MOVIE_TITLE,
                        MovieEntry.COLUMN_MOVIE_OVERVIEW,
                        MovieEntry.COLUMN_MOVIE_RATING,
                        MovieEntry.COLUMN_MOVIE_RELEASE_DATE
                };
                return new CursorLoader(this, MovieEntry.CONTENT_URI, projection, null,
                        null, null);
            default:
                return null;
        }
    }

    /**
     * Extracts movies from database and adds them to the movie adapter
     *
     * @param cursor Database cursor
     */
    private void extractMoviesFromDatabase(Cursor cursor) {
        List<Movie> mMoviesDatabase = new ArrayList<>();

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

                    mMoviesDatabase.add(movie);

                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get movies from database");
        } finally {
            if (cursor != null & !cursor.isClosed()) cursor.close();
        }
        mMovieAdapter.setMoviesData(mMoviesDatabase);
    }

    /**
     * Called when previously created load has finished its load.
     * @param loader The loader that has finished.
     * @param data The data generated by the loader.
     */
    @Override
    public void onLoadFinished(Loader loader, Object data) {

        mLoadingIndicator.setVisibility(View.INVISIBLE);

        switch (loader.getId()) {
            case MOVIES_LOADER_ID:
                if (data != null) {
                    mMovieAdapter.setMoviesData((List<Movie>) data);
                    showMovieDataView();
                } else {
                    showErrorMessage();
                }
                break;
            case DATABASE_LOADER_ID:
                extractMoviesFromDatabase((Cursor) data);
                break;
        }

    }

    @Override
    public void onLoaderReset(Loader loader) {

    }

    /**
     * This method makes the view for the movie data visible, and hides the error message.
     */
    private void showMovieDataView() {
        mPosterRecyclerView.setVisibility(View.VISIBLE);
        mErrorImageView.setVisibility(View.INVISIBLE);
        mErrorMessageTextView.setVisibility(View.INVISIBLE);
    }

    /**
     * This method makes the view for the movie data invisible, and shows the error message.
     */
    private void showErrorMessage() {
        mPosterRecyclerView.setVisibility(View.INVISIBLE);
        mLoadingIndicator.setVisibility(View.INVISIBLE);
        mErrorImageView.setVisibility(View.VISIBLE);
        mErrorMessageTextView.setVisibility(View.VISIBLE);
    }


    /**
     * This method responds to the item list click
     * @param position Position of the item that was clicked
     */
    @Override
    public void onClick(int position) {
        Intent intent = new Intent(this, DetailActivity.class);
        Movie movie = MovieAdapter.getMovieAtPosition(position);
        intent.putExtra(DetailActivity.INTENT_EXTRA_MOVIE_CLICKED, movie);
        startActivity(intent);
    }

    /**
     * This method is used when we are resetting data, so that at one point in time during a
     * refresh of our data, you can see that there is no data showing.
     */
    private void invalidateData() {
        mMovieAdapter.setMoviesData(null);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_refresh:
                invalidateData();
                showMovieDataView();
                getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
                return true;

            case R.id.action_filter_popular:
                invalidateData();
                showMovieDataView();
                PopularMoviePreferences.setFilter(this, NetworkUtils.MOVIE_POPULAR_RANKING);
                getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
                return true;

            case R.id.action_filter_top_rated:
                invalidateData();
                showMovieDataView();
                PopularMoviePreferences.setFilter(this, NetworkUtils.MOVIE_TOP_RATED_RANKING);
                getSupportLoaderManager().restartLoader(MOVIES_LOADER_ID, null, this);
                return true;

            case R.id.action_filter_favorite:
                invalidateData();
                showMovieDataView();
                PopularMoviePreferences.setFilter(this, PopularMoviePreferences.MOVIE_FAVORITE_RANKING);
                mLoadingIndicator.setVisibility(View.INVISIBLE);
                getSupportLoaderManager().restartLoader(DATABASE_LOADER_ID, null, this);

        }

        return super.onOptionsItemSelected(item);

    }
}
