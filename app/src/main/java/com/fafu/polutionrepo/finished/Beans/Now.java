package com.fafu.polutionrepo.finished.Beans;

import com.google.gson.annotations.SerializedName;

public class Now {

    @SerializedName("cond_txt")
    public String weather_condition;     //天气情况

    @SerializedName("fl")
    public String feel_temp;    //体感温度

    @SerializedName("hum")
    public String humidity;      //湿度

    @SerializedName("pcpn")
    public String pcpn;             //降水量

    @SerializedName("pres")
    public String pres;            //大气压强

    @SerializedName("tmp")
    public String tmp;              //温度

    @SerializedName("vis")
    public String visible;         //能见度

    @SerializedName("wind_dir")
    public String wind_direction;    //风向

    @SerializedName("wind_sc")
    public String wind_sc;         //风力

    @SerializedName("wind_spd")
    public String wind_spd;         //风速

}
