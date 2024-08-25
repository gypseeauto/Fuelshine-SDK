package com.gypsee.sdk.models;

import android.graphics.drawable.Drawable;

public class GypseeStoriesModel {

    String title, urlLInk;
    Drawable image;

    public GypseeStoriesModel(String title, String urlLInk, Drawable image) {
        this.title = title;
        this.urlLInk = urlLInk;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public String getUrlLInk() {
        return urlLInk;
    }

    public Drawable getImage() {
        return image;
    }
}
