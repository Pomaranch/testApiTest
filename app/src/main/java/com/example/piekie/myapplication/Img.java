package com.example.piekie.myapplication;

import com.google.gson.annotations.SerializedName;

/**
 * Created by piekie on 7/28/16.
 */

public class Img {

    @SerializedName(value =  "title")
    private String title;

    @SerializedName(value = "id")
    private Integer id;

    public String getTitle() {
        return title;
    }

    public Integer getId() {
        return id;
    }
}
