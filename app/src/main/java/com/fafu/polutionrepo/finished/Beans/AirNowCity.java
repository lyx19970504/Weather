package com.fafu.polutionrepo.finished.Beans;

import com.google.gson.annotations.SerializedName;

public class AirNowCity {
    @SerializedName("aqi")
    public String aqi;

    @SerializedName("pm25")
    public String pm25;

    @SerializedName("qlty")
    public String air_quality;

    @SerializedName("pm10")
    public String pm10;

    @SerializedName("no2")
    public String no2;

    @SerializedName("so2")
    public String so2;

    @SerializedName("co")
    public String co;

    @SerializedName("o3")
    public String o3;
}
