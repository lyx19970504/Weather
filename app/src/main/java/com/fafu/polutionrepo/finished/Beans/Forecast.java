package com.fafu.polutionrepo.finished.Beans;

import com.google.gson.annotations.SerializedName;

public class Forecast {

    @SerializedName("date")
    public String date;                  //日期

    @SerializedName("tmp_max")
    public String tmp_max;          //最高温度

    @SerializedName("tmp_min")
    public String tmp_min;      //最低温度

    @SerializedName("hum")
    public String humidity;    //湿度

    @SerializedName("cond_txt_d")
    public String dayConf;   //白天天气情况

    @SerializedName("cond_txt_n")
    public String nightConf;      //夜晚天气情况

    @SerializedName("wind_dir")
    public String wind_direction;      //风向

    @SerializedName("wind_sc")
    public String wind_sc;       //风力

    @SerializedName("wind_spd")
    public String wnid_speed;      //风速

    @SerializedName("uv_index")
    public String uv_index;        //紫外线强度指数
}
