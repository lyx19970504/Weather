package com.fafu.polutionrepo.finished.Beans;

import org.litepal.crud.LitePalSupport;

public class City_selected extends LitePalSupport {

    private String location;
    private String temperature;
    private String weather;

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }
}
