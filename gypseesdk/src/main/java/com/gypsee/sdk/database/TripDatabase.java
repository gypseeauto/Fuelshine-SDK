package com.gypsee.sdk.database;


import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;


@Database(entities = {TripLatLong.class, TripAlert.class, TripRecords.class}, version = 7, exportSchema = false)
public abstract class TripDatabase extends RoomDatabase {

    public abstract TripDao tripDao();
    public abstract TripAlertDao tripAlertDao();
    public abstract TripRecordsDao tripRecordsDao();

    private static volatile TripDatabase INSTANCE;

    public static TripDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (TripDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    TripDatabase.class, "TripDatabase")
                            .allowMainThreadQueries()
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}