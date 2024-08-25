package com.gypsee.sdk.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface TripDao {

    @Insert
    void insertTrip(TripLatLong tripLatLong);

    @Query("SELECT * FROM TripLatLongTable")
    List<TripLatLong> getAllTrips();

    @Query("SELECT * FROM TripLatLongTable WHERE tripId = :tripId")
    List<TripLatLong> getTripsLatLongByTripId(String tripId);

    @Query("DELETE FROM TripLatLongTable")
    void deleteAllTrips();

    @Query("DELETE FROM TripLatLongTable WHERE tripId = :tripId")
    void deleteTripsByTripId(String tripId);

}