package com.gypsee.sdk.models;

import java.util.ArrayList;

public class DeviceInformationModel {

    private String id, imageUrl, name;
    private ArrayList<String> instructions;

    public DeviceInformationModel(String id,
                                  String imageUrl,
                                  String name,
                                  ArrayList<String> instructions) {
        this.id = id;
        this.imageUrl = imageUrl;
        this.name = name;
        this.instructions = instructions;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getInstructions() {
        return instructions;
    }

    public void setInstructions(ArrayList<String> instructions) {
        this.instructions = instructions;
    }
}
