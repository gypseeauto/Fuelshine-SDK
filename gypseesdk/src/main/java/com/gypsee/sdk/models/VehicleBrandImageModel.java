package com.gypsee.sdk.models;

public class VehicleBrandImageModel {
    String brand, image;

    public VehicleBrandImageModel(String brand, String image) {
        this.brand = brand;
        this.image = image;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
