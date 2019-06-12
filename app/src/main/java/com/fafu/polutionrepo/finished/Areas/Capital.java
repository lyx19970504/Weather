package com.fafu.polutionrepo.finished.Areas;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class Capital extends LitePalSupport implements Serializable {

    private String cityName;
    private double latitude;

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
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

    private double longitude;
}
