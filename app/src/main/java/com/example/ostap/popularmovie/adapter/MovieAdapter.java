package com.example.ostap.popularmovie.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ostap.popularmovie.R;
import com.example.ostap.popularmovie.model.Movie;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ostap on 25/02/2018.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.ViewHolder> {

    /* List of movies from the API */
    private static List<Movie> mMovies;

    private final MovieAdapterOnClickHandler mClickHandler;

    private Context mContext;

    public MovieAdapter(Context context, MovieAdapterOnClickHandler clickHandler) {
        mMovies = new ArrayList<>();
        mContext = context;
        mClickHandler = clickHandler;
    }

    /**
     * Returns movie object based on its position
     *
     * @param position Movie position within the list
     * @return Movie located in desired position
     */
    public static Movie getMovieAtPosition(int position) {
        return mMovies.get(position);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the movie_poster_item layout
        View posterView = inflater.inflate(R.layout.movie_poster_item, parent, false);

        // Return the new holder instance
        return new ViewHolder(posterView);
    }

    /**
     * Populates movie data into the item
     */
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Movie movie = mMovies.get(position);
        Picasso.with(mContext).load(movie.getImage()).into(holder.posterImageView);

    }

    @Override
    public int getItemCount() {
        if (mMovies == null) return 0;
        return mMovies.size();
    }

    /**
     * This method is used to set movies data if we've already created the adapter.
     *
     * @param data New movies data.
     */
    public void setMoviesData(List<Movie> data) {
        mMovies = data;
        notifyDataSetChanged();
    }


    public interface MovieAdapterOnClickHandler {
        void onClick(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        public ImageView posterImageView;

        public ViewHolder(View itemView) {
            super(itemView);

            posterImageView = itemView.findViewById(R.id.poster_iv);

            itemView.setOnClickListener(this);
        }

        /**
         * This method is called by the child views during a click
         * @param view The view that was clicked
         */
        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            mClickHandler.onClick(adapterPosition);
        }
    }
}
