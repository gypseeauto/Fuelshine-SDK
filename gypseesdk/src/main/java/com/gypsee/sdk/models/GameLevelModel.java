package com.gypsee.sdk.models;

public class GameLevelModel {
    String level, totalSafePercent,totalSafeKm;

    public GameLevelModel(String level, String totalSafePercent,String totalSafeKm) {


        this.level = level;
        this.totalSafePercent = totalSafePercent;
        this.totalSafeKm = totalSafeKm;
    }

    public String getLevel() {
        return level;
    }

    public String getTotalSafePercent() {
        return totalSafePercent;
    }
    public String getTotalSafeKm(){
        return totalSafeKm;
    }
}
