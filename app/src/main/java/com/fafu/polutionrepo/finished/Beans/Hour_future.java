package com.fafu.polutionrepo.finished.Beans;

import com.google.gson.annotations.SerializedName;

public class Hour_future {

    @SerializedName("cond_txt")
    public String condition;    //天气情况

    @SerializedName("hum")
    public String humidity;    //湿度

    @SerializedName("pop")
    public String pop;      //降水概率

    @SerializedName("pres")
    public String pres;   //大气压强

    @SerializedName("time")
    public String time;     //时间

    @SerializedName("tmp")
    public String temperature;   //温度

    @SerializedName("wind_dir")
    public String wind_drection;   //风向

    @SerializedName("wind_sc")
    public String wind_sc;         //风力

    @SerializedName("wind_spd")
    public String wind_spd;         //风速
}
