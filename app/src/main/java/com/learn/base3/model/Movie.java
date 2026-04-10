package com.learn.base3.model;

import java.io.Serializable;

public class Movie implements Serializable {
    private String id;
    private String title;
    private String imageUrl;
    private String description;
    private double rating;
    private String genre;

    public Movie() {}

    public Movie(String id, String title, String imageUrl, String description, double rating, String genre) {
        this.id = id;
        this.title = title;
        this.imageUrl = imageUrl;
        this.description = description;
        this.rating = rating;
        this.genre = genre;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public double getRating() { return rating; }
    public void setRating(double rating) { this.rating = rating; }
    public String getGenre() { return genre; }
    public void setGenre(String genre) { this.genre = genre; }
}