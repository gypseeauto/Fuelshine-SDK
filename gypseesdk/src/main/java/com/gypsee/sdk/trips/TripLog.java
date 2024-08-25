package com.gypsee.sdk.trips;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

/**
 * Some code taken from https://github.com/wdkapps/FillUp
 */
public class TripLog {

    /// the database version number
    public static final int DATABASE_VERSION = 5;
    /// the name of the database
    public static final String DATABASE_NAME = "tripslog.db";
    /// a tag string for debug logging (the name of this class)
    private static final String TAG = TripLog.class.getName();
    /// database table names
    public static final String RECORDS_TABLE = "Records";
    private static final String ALERT_TABLE = "Alerts";
    /// column names for RECORDS_TABLE
    private static final String RECORD_ID = "id";
    private static final String RECORD_FK_ID = "record_id";
    private static final String ALERT_ID = "id";
    private static final String RECORD_START_DATE = "startDate";
    private static final String RECORD_END_DATE = "endDate";
    private static final String RECORD_RPM_MAX = "rmpMax";
    private static final String RECORD_SPEED_MAX = "speedMax";
    private static final String ALERT_MAX_VALUE = "maxValue";
    private static final String ALERT_TYPE = "alertType";
    private static final String RECORD_ENGINE_RUNTIME = "engineRuntime";
    private static final String DISTANCE_COVERED = "distance_covered";
    private static final String ALERT_COUNT = "alert_count";
    /// SQL commands to create the database
    public static final String[] DATABASE_CREATE = new String[]{
            "create table " + RECORDS_TABLE + " ( " +
                    RECORD_ID + " integer primary key autoincrement, " +
                    RECORD_START_DATE + " integer not null, " +
                    RECORD_END_DATE + " integer, " +
                    RECORD_SPEED_MAX + " integer, " +
                    ALERT_COUNT + " integer, " +
                    RECORD_RPM_MAX + " integer, " +
                    RECORD_ENGINE_RUNTIME + " text, " +
                    DISTANCE_COVERED + " text" +
                    ");"
    };

    public static final String[] DATABASE_CREATE_ALERT = new String[]{
            "create table " + ALERT_TABLE + " ( " +
                    ALERT_ID + " integer primary key autoincrement, " +
                    RECORD_FK_ID + " integer not null, " +
                    RECORD_START_DATE + " integer not null, " +
                    RECORD_END_DATE + " integer, " +
                    ALERT_MAX_VALUE + " integer, " +
                    ALERT_TYPE + " integer" +
                    ");"
    };

    /// array of all column names for RECORDS_TABLE
    private static final String[] RECORDS_TABLE_COLUMNS = new String[]{
            RECORD_ID,
            RECORD_START_DATE,
            RECORD_END_DATE,
            RECORD_SPEED_MAX,
            RECORD_ENGINE_RUNTIME,
            RECORD_RPM_MAX,
            DISTANCE_COVERED,
            ALERT_COUNT

    };

    /// a helper instance used to open and close the database
    private final TripLogOpenHelper helper;
    /// the database
    private final SQLiteDatabase db;

    private TripLog(Context context) {
        /// context of the instance creator
        this.helper = new TripLogOpenHelper(context);
        this.db = helper.getWritableDatabase();
    }

    public static TripLog getInstance(Context context) {
        /// singleton instance
        return new TripLog(context);
    }

}
