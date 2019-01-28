package edu.uci.ics.fabflixmobile;

import android.os.Parcelable;

import java.io.Serializable;

public class Movie implements Serializable {
    private String id;
    public String title;
    private Integer year;
    private String director;
    private String genres;
    private String stars;

    public Movie(String id, String title, Integer year, String director, String genres, String stars) {
        this.id = id;
        this.title = title;
        this.year = year;
        this.director = director;
        this.genres = genres;
        this.stars = stars;
    }

    public String getId(){
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Integer getYear() {
        return year;
    }

    public String getDirector() {
        return director;
    }

    public String getGenres() {
        return genres;
    }

    public String getStars() {
        return stars;
    }
}
