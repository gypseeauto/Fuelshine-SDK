package com.gypsee.sdk.models;

import android.graphics.drawable.Drawable;

public class ToolsModelClass {
    String title, description, clickableLink;
    Drawable image;

    public ToolsModelClass(String title, String description, String clickableLink, Drawable image) {
        this.title = title;
        this.description = description;
        this.clickableLink = clickableLink;
        this.image = image;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getClickableLink() {
        return clickableLink;
    }

    public void setClickableLink(String clickableLink) {
        this.clickableLink = clickableLink;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }
}
