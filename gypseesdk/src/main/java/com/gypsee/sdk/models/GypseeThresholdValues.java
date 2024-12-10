package com.gypsee.sdk.models;

import com.google.gson.annotations.SerializedName;

public class GypseeThresholdValues {

    @SerializedName("alerts")
    private Alerts alerts;

    public Alerts getAlerts() {
        return alerts;
    }

    public void setAlerts(Alerts alerts) {
        this.alerts = alerts;
    }

    public static class Alerts {
        @SerializedName("Harsh accelarion")
        private double harshAcceleration;

        @SerializedName("Harsh braking")
        private double harshBraking;

        @SerializedName("overspeed")
        private int overspeed;

        // Getters and Setters
        public double getHarshAcceleration() {
            return harshAcceleration;
        }

        public void setHarshAcceleration(double harshAcceleration) {
            this.harshAcceleration = harshAcceleration;
        }

        public double getHarshBraking() {
            return harshBraking;
        }

        public void setHarshBraking(double harshBraking) {
            this.harshBraking = harshBraking;
        }

        public int getOverspeed() {
            return overspeed;
        }

        public void setOverspeed(int overspeed) {
            this.overspeed = overspeed;
        }
    }
}



