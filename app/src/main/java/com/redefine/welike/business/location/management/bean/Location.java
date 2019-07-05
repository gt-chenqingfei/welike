package com.redefine.welike.business.location.management.bean;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by liubin on 2018/3/21.
 */

public class Location implements Serializable {
    private static final long serialVersionUID = -4885959084065561837L;
    @SerializedName("lon")
    private double longitude;
    @SerializedName("lat")
    private double latitude;
    private String placeId;
    private String place;
    private boolean isClickable = true;

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlace() {
        return place;
    }

    public void setPlace(String place) {
        this.place = place;
    }

    public boolean isClickable() {
        return isClickable;
    }

    public void setClickable(boolean clickable) {
        this.isClickable = clickable;
    }
}
