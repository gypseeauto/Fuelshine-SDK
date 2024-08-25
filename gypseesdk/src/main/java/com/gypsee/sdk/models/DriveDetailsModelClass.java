package com.gypsee.sdk.models;

import android.graphics.drawable.Drawable;

public class DriveDetailsModelClass {

    String title, description;
    Drawable drawable;

    public DriveDetailsModelClass(String title, String description,Drawable drawable) {

        this.title = title;
        this.description = description;
        this.drawable = drawable;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Drawable getDrawable() {
        return drawable;
    }
}
