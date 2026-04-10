package com.learn.base3.model;

public class Seat {
    private String name;
    private boolean isSelected;
    private boolean isBooked;

    public Seat(String name) {
        this.name = name;
        this.isSelected = false;
        this.isBooked = false;
    }

    public String getName() { return name; }
    public boolean isSelected() { return isSelected; }
    public void setSelected(boolean selected) { isSelected = selected; }
    public boolean isBooked() { return isBooked; }
    public void setBooked(boolean booked) { isBooked = booked; }
}