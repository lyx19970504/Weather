package com.fafu.polutionrepo.finished.Beans;

import com.google.gson.annotations.SerializedName;

import org.litepal.crud.LitePalSupport;

import java.util.List;

public class Weather extends LitePalSupport {

    public String status;

    @SerializedName("daily_forecast")
    public List<Forecast> forecasts;

    @SerializedName("hourly")
    public List<Hour_future> hourFutures;

    public Basic basic;


    public Now now;
}
