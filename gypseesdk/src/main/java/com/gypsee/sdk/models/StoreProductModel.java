package com.gypsee.sdk.models;

public class StoreProductModel {

    private String id, sku, name, brandName, description, currencyCode, currencySymbol, numericCode,
            type, baseImage, thumbnailImage, tncContent, tncURL, expiry, amount, perceivedValue;

    public StoreProductModel(String id, String sku, String name, String brandName, String description, String currencyCode, String currencySymbol, String numericCode, String type, String baseImage, String thumbnailImage, String tncContent, String tncURL, String expiry, String amount, String perceivedValue) {
        this.id = id;
        this.sku = sku;
        this.name = name;
        this.brandName = brandName;
        this.description = description;
        this.currencyCode = currencyCode;
        this.currencySymbol = currencySymbol;
        this.numericCode = numericCode;
        this.type = type;
        this.baseImage = baseImage;
        this.tncContent = tncContent;
        this.tncURL = tncURL;
        this.expiry = expiry;
        this.thumbnailImage = thumbnailImage;
        this.amount = amount;
        this.perceivedValue = perceivedValue;
    }

    public String getPerceivedValue() {
        return perceivedValue;
    }

    public void setPerceivedValue(String perceivedValue) {
        this.perceivedValue = perceivedValue;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public String getThumbnailImage() {
        return thumbnailImage;
    }

    public String getSku() {
        return sku;
    }

    public String getName() {
        return name;
    }

    public String getBrandName() {
        return brandName;
    }

    public String getDescription() {
        return description;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public String getNumericCode() {
        return numericCode;
    }

    public String getType() {
        return type;
    }

    public String getBaseImage() {
        return baseImage;
    }

    public String getTncContent() {
        return tncContent;
    }

    public String getTncURL() {
        return tncURL;
    }

    public String getExpiry() {
        return expiry;
    }
}
