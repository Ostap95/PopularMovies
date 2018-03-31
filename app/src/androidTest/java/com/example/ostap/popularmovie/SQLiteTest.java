package com.example.ostap.popularmovie;

import android.support.test.InstrumentationRegistry;
import android.support.test.runner.AndroidJUnit4;

import com.example.ostap.popularmovie.data.FavoriteMoviesContract;
import com.example.ostap.popularmovie.data.FavoriteMoviesDbHelper;
import com.example.ostap.popularmovie.model.Movie;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.List;


/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class SQLiteTest {

    FavoriteMoviesDbHelper dbHelper;
    Movie movie1, movie2;

    @Before
    public void setUp() {
        dbHelper = FavoriteMoviesDbHelper.getInstance(InstrumentationRegistry.getTargetContext());
        dbHelper.getWritableDatabase().delete(FavoriteMoviesContract.MovieEntry.TABLE_NAME, null, null);
        movie1 = new Movie(
                "Movie Title",
                "Movie Image",
                "Movie Overview",
                "10",
                "15-11-95"
        );

        movie2 = new Movie(
                "Movie Title 2",
                "Movie Image",
                "Movie Overview",
                "10",
                "15-11-95"
        );
    }

    public void aAddMovie_isSuccessful() {

        dbHelper.addOrUpdateMovie(movie1);
        dbHelper.addOrUpdateMovie(movie2);

        List<Movie> movies = dbHelper.getAllMovies();
        Assert.assertEquals(2, movies.size());
        Assert.assertEquals("Movie Title", movies.get(0).getTitle());
        Assert.assertEquals("Movie Image", movies.get(0).getImage());
        Assert.assertEquals("Movie Overview", movies.get(0).getOverview());
        Assert.assertEquals("10", movies.get(0).getRating());
        Assert.assertEquals("15-11-95", movies.get(0).getReleaseDate());

        Assert.assertEquals("Movie Title 2", movies.get(1).getTitle());
    }

    @Test
    public void removeMovie_isSuccessful() {
        dbHelper.addOrUpdateMovie(movie1);
        dbHelper.addOrUpdateMovie(movie2);
        dbHelper.removeMovie(movie2.getTitle());
        List<Movie> movies = dbHelper.getAllMovies();
        Assert.assertEquals(1, movies.size());
    }


    @After
    public void removeTable() {
        dbHelper.getWritableDatabase().delete(FavoriteMoviesContract.MovieEntry.TABLE_NAME, null, null);
    }
}