package com.gypsee.sdk.models;

public class OffersModel {

    private int id;
    private String imageLink, title, description, notificationType, notificationData, dateTime;

    public OffersModel(int id, String imageLink, String title, String description, String notificationType, String notificationData, String dateTime) {
        this.id = id;
        this.imageLink = imageLink;
        this.title = title;
        this.description = description;
        this.notificationType = notificationType;
        this.notificationData = notificationData;
        this.dateTime = dateTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImageLink() {
        return imageLink;
    }

    public void setImageLink(String imageLink) {
        this.imageLink = imageLink;
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

    public String getNotificationType() {
        return notificationType;
    }

    public void setNotificationType(String notificationType) {
        this.notificationType = notificationType;
    }

    public String getNotificationData() {
        return notificationData;
    }

    public void setNotificationData(String notificationData) {
        this.notificationData = notificationData;
    }

    public String getDateTime() {
        return dateTime;
    }

    public void setDateTime(String dateTime) {
        this.dateTime = dateTime;
    }
}
