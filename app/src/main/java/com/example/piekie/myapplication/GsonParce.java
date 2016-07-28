package com.example.piekie.myapplication;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by piekie on 7/28/16.
 */

public class GsonParce {

    @SerializedName("timestamp")
    private String timestamp;

    @SerializedName("images")
    private List<Img> images;

    public String getTimestamp() {
        return timestamp;
    }

    public List<Img> getImages() {
        return images;
    }
}
