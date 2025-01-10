package com.gypsee.sdk.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;


@Dao
public interface TripAlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertTripAlert(TripAlert tripAlert);

    @Query("SELECT * FROM trip_alerts")
    List<TripAlert> getAllTripAlerts();

    @Query("SELECT * FROM trip_alerts WHERE tripId = :tripId")
    List<TripAlert> getTripAlertsByTripId(String tripId);

    @Query("DELETE FROM trip_alerts WHERE tripId = :tripId")
    void deleteByTripId(String tripId);

}
