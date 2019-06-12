package com.fafu.polutionrepo.finished.Beans;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class LifeStyleIndex {

    @SerializedName("lifestyle")
    public List<Lifestyle> lifestyles;

    public String status;

    public Basic basic;
}
