package com.fafu.polutionrepo.finished.Beans;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Pollution implements Serializable {

    @SerializedName("pollutionId")
    private int id;

    private String CityName;
    private String Date;
    @SerializedName("severe")
    private String PollutionLevel;
    @SerializedName("pollutionType")
    private String PollutionType;
    @SerializedName("latitude")
    private double latitude;
    @SerializedName("longtitude")
    private double longitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getCityName() {
        return CityName;
    }

    public void setCityName(String cityName) {
        CityName = cityName;
    }

    public String getDate() {
        return Date;
    }

    public void setDate(String date) {
        Date = date;
    }

    public String getPollutionLevel() {
        return PollutionLevel;
    }

    public void setPollutionLevel(String pollutionLevel) {
        PollutionLevel = pollutionLevel;
    }

    public String getPollutionType() {
        return PollutionType;
    }

    public void setPollutionType(String pollutionType) {
        PollutionType = pollutionType;
    }
}
