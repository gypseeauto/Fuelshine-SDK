package com.gypsee.sdk.models;

public class AlertsArray {
    private String alertName,minutesOfSpeeding,hoursOfTravelling,kmsTravelled,eventsCount;

    public String getAlertName() {
        return alertName;
    }

    public void setAlertName(String alertName) {
        this.alertName = alertName;
    }

    public String getMinutesOfSpeeding() {
        return minutesOfSpeeding;
    }

    public void setMinutesOfSpeeding(String minutesOfSpeeding) {
        this.minutesOfSpeeding = minutesOfSpeeding;
    }

    public String getHoursOfTravelling() {
        return hoursOfTravelling;
    }

    public void setHoursOfTravelling(String hoursOfTravelling) {
        this.hoursOfTravelling = hoursOfTravelling;
    }

    public String getKmsTravelled() {
        return kmsTravelled;
    }

    public void setKmsTravelled(String kmsTravelled) {
        this.kmsTravelled = kmsTravelled;
    }

    public String getEventsCount() {
        return eventsCount;
    }

    public void setEventsCount(String eventsCount) {
        this.eventsCount = eventsCount;
    }

    public AlertsArray(String alertName, String minutesOfSpeeding, String hoursOfTravelling, String kmsTravelled, String eventsCount ) {
        this.alertName = alertName;
        this.minutesOfSpeeding = minutesOfSpeeding;
        this.hoursOfTravelling = hoursOfTravelling;
        this.kmsTravelled = kmsTravelled;
        this.eventsCount = eventsCount;
    }
}
