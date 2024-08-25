package com.gypsee.sdk.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TripRecordsDao {

    @Insert
    void insertTripRecord(TripRecords tripRecord);

    @Query("SELECT * FROM trip_records WHERE tripId = :tripId")
    List<TripRecords> getTripRecordsByTripId(String tripId);
}