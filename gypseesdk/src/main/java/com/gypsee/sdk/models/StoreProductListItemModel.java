package com.gypsee.sdk.models;

public class StoreProductListItemModel {

    private String currencyCode, currencyNumericCode, currencySymbol
            , imageThumbnail, sku, url, name;

    public StoreProductListItemModel(String currencyCode, String currencyNumericCode, String currencySymbol, String imageThumbnail, String sku, String url, String name) {
        this.currencyCode = currencyCode;
        this.currencyNumericCode = currencyNumericCode;
        this.currencySymbol = currencySymbol;
        this.imageThumbnail = imageThumbnail;
        this.sku = sku;
        this.url = url;
        this.name = name;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrencyNumericCode() {
        return currencyNumericCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public String getImageThumbnail() {
        return imageThumbnail;
    }

    public String getSku() {
        return sku;
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }
}
