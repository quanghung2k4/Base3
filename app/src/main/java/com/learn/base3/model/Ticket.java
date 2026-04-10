package com.learn.base3.model;

import java.io.Serializable;

public class Ticket implements Serializable {
    private String id;
    private String userId;
    private String movieId;
    private String movieTitle;
    private String theaterName;
    private String showtime;
    private double price;
    private String seat;

    public Ticket() {}

    public Ticket(String id, String userId, String movieId, String movieTitle, String theaterName, String showtime, double price, String seat) {
        this.id = id;
        this.userId = userId;
        this.movieId = movieId;
        this.movieTitle = movieTitle;
        this.theaterName = theaterName;
        this.showtime = showtime;
        this.price = price;
        this.seat = seat;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getMovieId() { return movieId; }
    public void setMovieId(String movieId) { this.movieId = movieId; }
    public String getMovieTitle() { return movieTitle; }
    public void setMovieTitle(String movieTitle) { this.movieTitle = movieTitle; }
    public String getTheaterName() { return theaterName; }
    public void setTheaterName(String theaterName) { this.theaterName = theaterName; }
    public String getShowtime() { return showtime; }
    public void setShowtime(String showtime) { this.showtime = showtime; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }
    public String getSeat() { return seat; }
    public void setSeat(String seat) { this.seat = seat; }
}