package com.fafu.polutionrepo.finished.Beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Weather_industrial {

    public String status;
    @SerializedName("hourly")
    public List<Hour_future> hours_24;

    public Basic basic;
}
