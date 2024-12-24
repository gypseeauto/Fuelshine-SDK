package com.gypsee.sdk.models;

import com.google.gson.annotations.SerializedName;

import java.util.Map;

//public class GypseeThresholdValues {
//
//    @SerializedName("alerts")
//    private Alerts alerts;
//
//    public Alerts getAlerts() {
//        return alerts;
//    }
//
//    public void setAlerts(Alerts alerts) {
//        this.alerts = alerts;
//    }
//
//    public static class Alerts {
//        @SerializedName("Harsh accelarion")
//        private double harshAcceleration;
//
//        @SerializedName("Harsh braking")
//        private double harshBraking;
//
//        @SerializedName("overspeed")
//        private int overspeed;
//
//        // Getters and Setters
//        public double getHarshAcceleration() {
//            return harshAcceleration;
//        }
//
//        public void setHarshAcceleration(double harshAcceleration) {
//            this.harshAcceleration = harshAcceleration;
//        }
//
//        public double getHarshBraking() {
//            return harshBraking;
//        }
//
//        public void setHarshBraking(double harshBraking) {
//            this.harshBraking = harshBraking;
//        }
//
//        public int getOverspeed() {
//            return overspeed;
//        }
//
//        public void setOverspeed(int overspeed) {
//            this.overspeed = overspeed;
//        }
//    }
//}


public class GypseeThresholdValues {
    private Map<String, Alert> alerts;

    public Map<String, Alert> getAlerts() {
        return alerts;
    }

    public static class Alert {
        private double harshBraking;
        private double HarshAccelaration;
        private double HarshCornering;
        private double overspeeding;

        public double getHarshBraking() {
            return harshBraking;
        }

        public double getHarshAccelaration() {
            return HarshAccelaration;
        }

        public double getHarshCornering() {
            return HarshCornering;
        }

        public double getOverspeeding() {
            return overspeeding;
        }
    }
}

