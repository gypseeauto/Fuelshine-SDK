package com.gypsee.sdk.models;

public class DrivingTipModel {

    String title, description;

    public DrivingTipModel(String title, String description) {
        this.title = title;
        this.description = description;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }
}
