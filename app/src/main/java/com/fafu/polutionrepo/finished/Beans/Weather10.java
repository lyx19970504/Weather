package com.fafu.polutionrepo.finished.Beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather10 {


    @SerializedName("daily_forecast")
    public List<Forecast> forecasts;

    public String status;

    public Basic basic;
}
