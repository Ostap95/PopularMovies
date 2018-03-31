package com.example.ostap.popularmovie;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.ostap.popularmovie.adapter.ReviewListAdapter;
import com.example.ostap.popularmovie.adapter.TrailerListAdapter;
import com.example.ostap.popularmovie.data.FavoriteMoviesContract.MovieEntry;
import com.example.ostap.popularmovie.data.FavoriteMoviesDbHelper;
import com.example.ostap.popularmovie.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by ostap on 02/03/2018.
 */

public class DetailActivity extends AppCompatActivity {

    public static final String INTENT_EXTRA_MOVIE_CLICKED = "movie_clicked";
    private static final String TAG = DetailActivity.class.getSimpleName();

    private TextView titleTextView, releaseDateTextView, overviewTextView, movieRatingTextView;
    private TextView trailerTextView, reviewsTextView;
    private ImageView movieImageView;
    private ToggleButton favoriteTb;
    private RecyclerView trailerRecyclerView, reviewsRecyclerView;

    private Movie mMovie = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        if (getIntent() != null) {
            mMovie = getIntent().getParcelableExtra(INTENT_EXTRA_MOVIE_CLICKED);
        }

        titleTextView = findViewById(R.id.movie_title_tv);
        releaseDateTextView = findViewById(R.id.release_date_tv);
        overviewTextView = findViewById(R.id.overview_tv);
        movieImageView = findViewById(R.id.movie_image_iv);
        movieRatingTextView = findViewById(R.id.movie_rating_tv);
        trailerTextView = findViewById(R.id.trailer_tv);
        reviewsTextView = findViewById(R.id.reviews_tv);
        trailerRecyclerView = findViewById(R.id.trailer_rv);
        reviewsRecyclerView = findViewById(R.id.reviews_rv);

        getSupportActionBar().setTitle(mMovie.getTitle());
        setUiElements();

        TrailerListAdapter trailerListAdapter = new TrailerListAdapter(this, mMovie.getTrailersUrl());
        ReviewListAdapter reviewListAdapter = new ReviewListAdapter(this, mMovie.getReviews());

        trailerRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        trailerRecyclerView.setHasFixedSize(true);
        trailerRecyclerView.setAdapter(trailerListAdapter);

        reviewsRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewsRecyclerView.setHasFixedSize(true);
        reviewsRecyclerView.setAdapter(reviewListAdapter);

        if (mMovie.getReviews().size() == 0) reviewsTextView.setVisibility(View.INVISIBLE);
        if (mMovie.getTrailersUrl().size() == 0) trailerTextView.setVisibility(View.INVISIBLE);
    }


    private void setUiElements() {
        Picasso.with(this).load(mMovie.getImage()).into(movieImageView);
        titleTextView.setText(mMovie.getTitle());
        releaseDateTextView.setText(mMovie.getReleaseDate());
        overviewTextView.setText(mMovie.getOverview());
        movieRatingTextView.setText(mMovie.getRating());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.detail_activity_menu, menu);


        /* Toggle button represents the favorite button. When checked, the movies is added
        * to the database */
        favoriteTb = menu.findItem(R.id.action_favorite).
                getActionView().findViewById(R.id.btnFavoriteAction);


        ArrayList<String> moviesTitle = FavoriteMoviesDbHelper.getInstance(getApplicationContext())
                .getMoviesTitle();

        if (moviesTitle.contains(mMovie.getTitle())) favoriteTb.setChecked(true);
        else favoriteTb.setChecked(false);

        favoriteTb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                if (checked) {
                    insertMovieIntoDatabase();
                    Toast.makeText(DetailActivity.this,
                            "Added to Favorites", Toast.LENGTH_SHORT).show();
                } else {
                    removeMovieFromDatabase();
                    Toast.makeText(DetailActivity.this,
                            "Removed from Favorites", Toast.LENGTH_SHORT).show();

                }
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie.getTrailerToShare());
                startActivity(Intent.createChooser(shareIntent, "Share with"));
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    /**
     * Adds the current movie to the database
     * @return Returns URI of the inserted movie
     */
    private Uri insertMovieIntoDatabase() {
        Uri returnedUri;

        ContentValues values = new ContentValues();
        values.put(MovieEntry.COLUMN_MOVIE_TITLE, mMovie.getTitle());
        values.put(MovieEntry.COLUMN_MOVIE_IMAGE_URL, mMovie.getImage());
        values.put(MovieEntry.COLUMN_MOVIE_OVERVIEW, mMovie.getOverview());
        values.put(MovieEntry.COLUMN_MOVIE_RATING, mMovie.getRating());
        values.put(MovieEntry.COLUMN_MOVIE_RELEASE_DATE, mMovie.getReleaseDate());

        returnedUri = getContentResolver().insert(MovieEntry.CONTENT_URI, values);

        return returnedUri;
    }

    /**
     * Removes current movie from database
     * @return Returns the number of rows deleted
     */
    private int removeMovieFromDatabase() {
        int rowsDeleted;

        String selection = MovieEntry.COLUMN_MOVIE_TITLE + " LIKE ?";
        String[] selectionArgs = {mMovie.getTitle()};

        rowsDeleted = getContentResolver().delete(MovieEntry.CONTENT_URI,
                selection, selectionArgs);

        return rowsDeleted;
    }
}
