package com.example.ostap.popularmovie.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.example.ostap.popularmovie.utils.NetworkUtils;

/**
 * Created by ostap on 02/03/2018.
 */

public final class PopularMoviePreferences {

    private static final String PREF_DISPLAY_FILTER = "display_filter";
    public static final String MOVIE_FAVORITE_RANKING = "favorites";


    /**
     * Method that sets filter option in Preferences
     * @param context Context used to get the SharedPreferences
     * @param filter The way we want our movies list displayed (POPULAR or TOP RATED)
     */
    public static void setFilter(Context context, String filter) {

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.putString(PREF_DISPLAY_FILTER, filter);
        editor.apply();
    }

    /**
     * Resets the filter option stored in SharedPreferences
     * @param context Context used to get the SharedPreferences
     */
    public static void resetFilter(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sp.edit();

        editor.remove(PREF_DISPLAY_FILTER);
        editor.apply();
    }


    /**
     * Returns the filter currently set in Preferences. The default filter this method returns
     * is "MOVIE_POPULAR_RANKING".
     * @param context Context used to get the SharedPreferences
     * @return Returns the filter that the user has set in SharedPreferences
     */
    public static String getPreferredFilter(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        return sp.getString(PREF_DISPLAY_FILTER, NetworkUtils.MOVIE_POPULAR_RANKING);
    }




}
