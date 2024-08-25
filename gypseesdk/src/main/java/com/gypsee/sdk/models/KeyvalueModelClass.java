package com.gypsee.sdk.models;

import android.graphics.drawable.Drawable;

public class KeyvalueModelClass {

    String key,description;

    Drawable value;

    public KeyvalueModelClass(String key, String description, Drawable value) {
        this.key = key;
        this.description = description;
        this.value = value;
    }

    public KeyvalueModelClass(String key, Drawable value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Drawable getValue() {
        return value;
    }

    public String getDescription() {
        return description;
    }
}
