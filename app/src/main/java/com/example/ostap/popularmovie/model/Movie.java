package com.example.ostap.popularmovie.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ostap on 25/02/2018.
 */

public class Movie implements Parcelable{

    public static final String MOVIE_TRAILER_BASE_URL = "https://www.youtube.com/watch?v=";
    public static final Creator<Movie> CREATOR = new Creator<Movie>() {
        @Override
        public Movie createFromParcel(Parcel in) {
            return new Movie(in);
        }

        @Override
        public Movie[] newArray(int size) {
            return new Movie[size];
        }
    };
    private String title;
    private String image;
    private String overview;
    private String rating;
    private String releaseDate;
    private String id;
    private List<String> trailersUrl = new ArrayList<>();
    private List<Review> reviews = new ArrayList<>();

    public Movie() {}

    public Movie(String id, String title, String image, String overview, String rating, String releaseDate) {
        this.title = title;
        this.image = image;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.id = id;
    }

    protected Movie(Parcel in) {
        id = in.readString();
        title = in.readString();
        image = in.readString();
        overview = in.readString();
        rating = in.readString();
        releaseDate = in.readString();
        in.readStringList(trailersUrl);
        in.readTypedList(reviews, Review.CREATOR);
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<String> getTrailersUrl() {
        return trailersUrl;
    }

    public void setTrailersUrl(List<String> trailers) {
        trailersUrl = trailers;
    }

    public List<Review> getReviews() {
        return reviews;
    }

    public void setReviews(List<Review> reviews) {
        this.reviews = reviews;
    }

    public String getTrailerToShare() {
        return trailersUrl.get(0);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Here we write the values we want to save to the Parcel
     */
    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(title);
        parcel.writeString(image);
        parcel.writeString(overview);
        parcel.writeString(rating);
        parcel.writeString(releaseDate);
        parcel.writeStringList(trailersUrl);
        parcel.writeTypedList(reviews);
    }
}
