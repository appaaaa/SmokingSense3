package com.smokingsense.smokingsense3;

/**
 * Created by appaaaa on 2017-02-08.
 */

public class SmokingLocation {
    private String title;
    private double latitude;
    private double longitude;
    // private List<String> Photo;

    public SmokingLocation(){

    }
    public SmokingLocation(String title, double latitude, double longitude){
        this.title = title;
        this.latitude = latitude;
        this.longitude = longitude;
    }
    public String getTitle(){ return title; }
    public void setTitle(String title){ this.title = title; }

    public double getLatitude(){ return latitude; }
    public void setLatitude(double latitude){ this.latitude = latitude; }

    public double getLongitude(){ return longitude; }
    public void setLongitude(double longitude){ this.longitude = longitude; }

}
