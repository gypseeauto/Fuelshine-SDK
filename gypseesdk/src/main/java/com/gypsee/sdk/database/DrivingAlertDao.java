package com.gypsee.sdk.database;


import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface DrivingAlertDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertDrivingAlert(DrivingAlert drivingAlert);

    @Query("SELECT * FROM driving_alerts")
    List<DrivingAlert> getAllDrivingAlerts();

    @Query("SELECT * FROM driving_alerts WHERE tripId = :tripId")
    List<DrivingAlert> getDrivingAlertsByTripId(String tripId);

    @Query("DELETE FROM driving_alerts WHERE tripId = :tripId")
    void deleteDrivingAlertByTripId(String tripId);

}
