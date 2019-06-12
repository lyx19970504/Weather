package com.fafu.polutionrepo.finished.Beans;

import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName("imageId")
    private String imageId;
    @SerializedName("title")
    private String title;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }
}
