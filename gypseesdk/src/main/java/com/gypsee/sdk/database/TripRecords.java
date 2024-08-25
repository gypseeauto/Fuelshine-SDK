package com.gypsee.sdk.database;


import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "trip_records")
public class TripRecords {
    @PrimaryKey(autoGenerate = true)
    private int id;

    private long timeInMillis;

    private String tripId;

    public TripRecords(String tripId,long timeInMillis){
        this.tripId = tripId;
        this.timeInMillis = timeInMillis;
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTimeInMillis() {
        return timeInMillis;
    }

    public void setTimeInMillis(long timeInMillis) {
        this.timeInMillis = timeInMillis;
    }

    public String getTripId() {
        return tripId;
    }

    public void setTripId(String tripId) {
        this.tripId = tripId;
    }
}
