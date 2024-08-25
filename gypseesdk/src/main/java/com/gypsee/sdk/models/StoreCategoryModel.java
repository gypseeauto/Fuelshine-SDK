package com.gypsee.sdk.models;

public class StoreCategoryModel {

    private String description, id, image, thumbnail, name, url;

    public StoreCategoryModel(String description, String id, String image, String thumbnail, String name, String url) {
        this.description = description;
        this.id = id;
        this.image = image;
        this.thumbnail = thumbnail;
        this.name = name;
        this.url = url;
    }

    public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }
}
