package com.gypsee.sdk.models;

import android.graphics.Bitmap;

import java.io.File;

public class DocumentTypes {

    private String id, name, description, imagelink;

    private Bitmap bitmap;

    private File file;

    public DocumentTypes(String id, String name, String description, String imagelink, Bitmap bitmap, File file) {

        this.id = id;
        this.name = name;
        this.description = description;
        this.imagelink = imagelink;
        this.bitmap = bitmap;
        this.file = file;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImagelink() {
        return imagelink;
    }

    public void setImagelink(String imagelink) {
        this.imagelink = imagelink;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }
}

