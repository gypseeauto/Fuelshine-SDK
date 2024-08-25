package com.gypsee.sdk.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Arrays;

import com.gypsee.sdk.models.BluetoothDeviceModel;
import com.gypsee.sdk.models.DeviceInformationModel;
import com.gypsee.sdk.models.MemberModel;
import com.gypsee.sdk.models.OffersModel;
import com.gypsee.sdk.models.ToolModelClass;
import com.gypsee.sdk.models.TripFileNameModel;
import com.gypsee.sdk.models.VehicleHealthDataModel;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.trips.TripAlert;
import com.gypsee.sdk.trips.TripRecord;

public class DatabaseHelper extends SQLiteOpenHelper {


    // Database Information
    private static final String DB_NAME = "GypseeAutomative.DB";

    // database version
    private static final int DB_VERSION = 51;

    // Notification Table Name
    public static final String NOTIFICATIONS_TABLE = "Notifications_Table";
    //Notification Table columns
    private static final String _ID = "_id";
    private static final String Notification_Title = "Notification_Title";
    private static final String Notification_Message = "Notification_Message";
    private static final String Notification_Image = "Notification_Image";
    private static final String Notification_Time = "Notification_Time";
    private static final String Notification_Type = "Notification_Type";
    private static final String Notification_Data = "Notification_Data";
    private SQLiteDatabase database;

    //Vehciles Table
    public static final String VEHICLES_TABLE = "Vehicles_table";
    private static final String TRIP_ALERTS_TABLE = "Trip_Alerts_Table";
    private static final String TRIP_OFFLINE_DATA_TABLE_PACKETS = "trip_offline_datapackets_table";


    //vehcle table columns
    public static final String userVehicleId = "userVehicleId", vehicleName = "vehicleName",
            regNumber = "regNumber", purchaseDate = "purchaseDate", vehicleBrand = "vehicleBrand", vehicleModel = "vehicleModel",
            latitude = "latitude", longitude = "longitude", vehicleAlerts = "vehicleAlerts",
            createdOn = "createdOn", lastUpdatedOn = "lastUpdatedOn", category = "category",
            odoMeterRdg = "odoMeterRdg", distancePostCC = "distancePostCC", serviceReminderkm = "serviceReminderkm", insuranceReminderDate = "insuranceReminderDate", pollutionReminderDate = "pollutionReminderDate",
            servicereminderduedate = "servicereminderduedate", vin = "Vin", approved = "approved", vinAvl = "vinAvl", newServiceReminderRemainingKm = "newServiceReminderRemainingKm", newServiceReminderRequiredKm = "newServiceReminderRequiredKm", rsaCouponCode = "rsaCouponCode", fuelType = "fuelType", chassisNo = "chassisNo", engineNo = "engineNo", customerName = "customerName",
            vehicleClass = "vehicleClass", mvTaxUpto = "mvTaxUpto", fitness = "fitness";
    public static final String epaArAiMileage ="epaArAiMileage",ecoSpeedStartRange = "ecoSpeedStartRange",ecoSpeedEndRange = "ecoSpeedEndRange";

    //Trip_Alert_Table Columns
    private static final String alertType = "alertType";
    private static final String alertValue = "alertValue";
    private static final String timeInterval = "timeInterval";
    private static final String gForce = "gForce";
    private static final String timeStamp = "timeStamp";
    private static final String lat = "lat";
    private static final String lng = "lng";
    private static final String impact = "impact";
    private static final String TRIP_ID = "TRIP_ID";
    private static final String LAST_UPDATED_ON = "LAST_UPDATED_ON";


    // Trip_upload from CSV TABLE
    private static final String TRIP_CSV_FILENAME = "tripcsv_fileName";

    //File name
    private static final String UploadEmpyValuesVehicleTable = "UploadEmpyValuesVehicleTable";
    private static final String VEHICLE_ID = "VEHICLE_ID";

    // Notification Table Name
    public static final String VEHICLEMAKE_TABLE = "VEHICLEMakeTable";
    public static final String VEHICLEMAKE_ID = "VEHICLEMAKE_ID";
    public static final String VEHICLEMAKE = "vehicleMake";


    public static final String TRIPRECORD_TABLE = "TRIPRECORD_TABLE";
    /// column names for RECORDS_TABLE

    private static final String SAFE_KM = "SAFE_KM";
    private static final String TRIP_SAVED_AMOUNT = "TRIP_SAVED_AMOUNT";
        private static final String TRIP_SAVINGS_COMISSION = "TRIP_SAVINGS_COMISSION";
    private static final String RECORD_START_DATE = "startDate";
    private static final String RECORD_END_DATE = "endDate";
    private static final String RECORD_RPM_MAX = "rmpMax";
    private static final String RECORD_SPEED_MAX = "speedMax";

    private static final String RECORD_ENGINE_RUNTIME = "engineRuntime";
    private static final String DISTANCE_COVERED = "distance_covered";
    private static final String ALERT_COUNT = "alert_count";

    private static final String START_LAT = "start_laitude";
    private static final String START_LANG = "start_longitude";
    private static final String END_LAT = "end_latitude";
    private static final String END_LANG = "end_longitude";
    private static final String START_LOCATION_NAME = "START_LOCATION_NAME";
    private static final String END_LOCATION_NAME = "END_LOCATION_NAME";

    private static final String VEHICLE_HEALTH_SCORE = "VEHICLE_HEALTH_SCORE";
    private static final String MILEAGE = "MILEAGE";
    private static final String TRIP_DURATION = "TRIP_DURATION";

    public static final String DRIVING_TOOLS_TABLE = "DRIVING_TOOLS_TABLE";

    private static final String tool_id = "id";
    private static final String tool_title = "title";
    private static final String tool_description = "description";
    private static final String tool_imageUrl = "imageUrl";
    private static final String tool_ColorCode = "tool_ColorCode";
    private static final String tool_Type = "tool_Type";
    private static final String tool_redirectUrl = "redirectUrl";


    public static final String PERFORMANCE_MEASURES_TABLE = "PERFORMANCE_MEASURES_TABLE";
    public static final String FAULT_CODES_TABLE = "FAULT_CODES_TABLE";
    public static final String CRITICAL_FAULTS_TABLE = "CRITICAL_FAULTS_TABLE";

    private static final String CODE_HEALTH = "CODE_KEY";
    private static final String TITLE_HEALTH = "TITLE_HEALTH";
    private static final String VALUE_HEALTH = "VALUE_HEALTH";
    private static final String DATA_NOT_SUPPORTED = "DATA_NOT_SUPPORTED";
    private static final String THRESHOLD_VALUE_HEALTH = "THRESHOLD_VALUE_HEALTH";
    private static final String ERROR_HEALTH = "ERROR_HEALTH";



    /*//category table
    private static final String DEVICE_CATEGORY_TABLE = "DEVICE_CATEGORY_TABLE";
    private static final String CATEGORY_ID = "CATEGORY_ID";
    private static final String CATEGORY_NAME = "CATEGORY_NAME";
    private static final String CATEGORY_DESCRIPTION = "CATEGORY_DESCRIPTION";


    String categoryTable = "create table " + DEVICE_CATEGORY_TABLE + " ( " +
            CATEGORY_ID + " TEXT NOT NULL UNIQUE, " +
            CATEGORY_NAME + " text not null, " +
            CATEGORY_DESCRIPTION + " text " +
            ");";*/

    //category devices table
    public static final String ALL_DEVICES_TABLE = "ALL_DEVICES_TABLE";
    private static final String ALL_DEVICE_ID = "ALL_DEVICE_ID";
    private static final String ALL_DEVICE_CATEGORY = "ALL_DEVICE_CATEGORY";
    private static final String ALL_DEVICE_NAME = "ALL_DEVICE_NAME";
    private static final String ALL_DEVICE_IMAGE = "ALL_DEVICE_IMAGE";
    private static final String ALL_DEVICE_INSTRUCTION_IMAGE = "ALL_DEVICE_INSTRUCTION_IMAGE";
    private static final String ALL_DEVICE_INSTRUCTIONS = "ALL_DEVICE_INSTRUCTIONS";
    private static final String ALL_DEVICE_INSTRUCTIONS_ID = "ALL_DEVICE_INSTRUCTIONS_ID";



    //registered device table
    public static final String REGISTERED_DEVICE_TABLE = "REGISTERED_DEVICE_TABLE";
    private static final String REGISTERED_DEVICE_ID = "REGISTERED_DEVICE_ID";
    private static final String REGISTERED_DEVICE_NAME = "REGISTERED_DEVICE_NAME";
    private static final String REGISTERED_DEVICE_BLUETOOTH_NAME = "REGISTERED_DEVICE_BLUETOOTH_NAME";
    private static final String REGISTERED_DEVICE_BLUETOOTH_TYPE = "REGISTERED_DEVICE_BLUETOOTH_TYPE";
    private static final String REGISTERED_DEVICE_PROFILE_SUPPORTED = "REGISTERED_DEVICE_PROFILE_SUPPORTED";
    private static final String REGISTERED_DEVICE_MAC = "REGISTERED_DEVICE_MAC";
    private static final String REGISTERED_DEVICE_IMAGE = "REGISTERED_DEVICE_IMAGE";
    private static final String REGISTERED_DEVICE_LAST_CONNECTED = "REGISTERED_DEVICE_LAST_CONNECTED";
    private static final String REGISTERED_DEVICE_CREATED_ON = "REGISTERED_DEVICE_CREATED_ON";
    private static final String REGISTERED_DEVICE_LAST_UPDATED = "REGISTERED_DEVICE_LAST_UPDATED";
    private static final String REGISTERED_DEVICE_NOW_CONNECTED = "REGISTERED_DEVICE_NOW_CONNECTED";


    //emergency contact table
    public static final String EMERGENCY_CONTACT_TABLE = "EMERGENCY_CONTACT_TABLE";
    private static final String MEMBER_ID = "MEMBER_ID";
    private static final String MEMBER_NAME = "MEMBER_NAME";
    private static final String MEMBER_CITY = "MEMBER_CITY";
    private static final String MEMBER_MOBILE_NUMBER = "MEMBER_MOBILE_NUMBER";
    private static final String MEMBER_EMAIL = "MEMBER_EMAIL";
    private static final String MEMBER_RELATION = "MEMBER_RELATION";





    public DatabaseHelper(Context context) {

        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        // Creating table query
        String CREATE_TABLE = "create table " + NOTIFICATIONS_TABLE + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                Notification_Title + " TEXT NOT NULL, " +
                Notification_Image + " TEXT NOT NULL, " +
                Notification_Time + " TEXT NOT NULL, " +
                Notification_Type + " TEXT NOT NULL, " +
                Notification_Data + " TEXT NOT NULL, " +
                Notification_Message + " TEXT NOT NULL);";

        db.execSQL(CREATE_TABLE);


        String CREATE__VEHICLE_TABLE = "create table " + VEHICLES_TABLE + "(" +
                userVehicleId + " TEXT NOT NULL UNIQUE, " +
                vehicleName + " TEXT NOT NULL, " +
                regNumber + " TEXT NOT NULL, " +
                purchaseDate + " TEXT, " +

                vehicleBrand + " TEXT NOT NULL, " +
                vehicleModel + " TEXT NOT NULL, " +
                latitude + " TEXT , " +

                longitude + " TEXT , " +
                vehicleAlerts + " TEXT, " +
                createdOn + " TEXT NOT NULL, " +

                lastUpdatedOn + " TEXT NOT NULL, " +
                odoMeterRdg + " TEXT NOT NULL, " +
                distancePostCC + " TEXT NOT NULL, " +
                serviceReminderkm + " TEXT NOT NULL, " +
                insuranceReminderDate + " TEXT NOT NULL, " +
                pollutionReminderDate + " TEXT NOT NULL, " +
                servicereminderduedate + " TEXT NOT NULL, " +

                newServiceReminderRemainingKm + " TEXT NOT NULL, " +
                newServiceReminderRequiredKm + " TEXT NOT NULL, " +
                rsaCouponCode + " TEXT NOT NULL, " +
                vin + " TEXT NOT NULL, " +
                vinAvl + " TEXT NOT NULL, " +
                fuelType + " TEXT NOT NULL, " +

                approved + " TEXT NOT NULL, " +

                chassisNo + " TEXT NOT NULL, " +
                engineNo + " TEXT NOT NULL, " +
                customerName + " TEXT NOT NULL, " +
                vehicleClass + " TEXT NOT NULL, " +
                mvTaxUpto + " TEXT NOT NULL, " +
                ecoSpeedStartRange + " INTEGER, " +
                epaArAiMileage + " INTEGER, " +
                ecoSpeedEndRange + " INTEGER, " +
                fitness + " TEXT NOT NULL);";
        db.execSQL(CREATE__VEHICLE_TABLE);

        String CREATE__TRIP_ALERTS_TABLE = "create table " + TRIP_ALERTS_TABLE + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                alertType + " TEXT NOT NULL, " +
                alertValue + " TEXT, " +
                timeInterval + " TEXT, " +
                gForce + " TEXT NOT NULL, " +
                timeStamp + " LONG, " +
                lat + " DOUBLE, " +
                lng + " DOUBLE, " +
                impact + " TEXT NOT NULL);";
        db.execSQL(CREATE__TRIP_ALERTS_TABLE);


        String CREATE_OFFLINE_TRIP_PACKETS = "create table " + TRIP_OFFLINE_DATA_TABLE_PACKETS + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                TRIP_ID + " TEXT NOT NULL, " +
                TRIP_CSV_FILENAME + " TEXT NOT NULL);";


        db.execSQL(CREATE_OFFLINE_TRIP_PACKETS);


        String CREATE_VEHICLEMAKE_TABLE = "create table " + VEHICLEMAKE_TABLE + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VEHICLEMAKE + " TEXT NOT NULL);";
        db.execSQL(CREATE_VEHICLEMAKE_TABLE);

        String EmptyValuesUplodVehTable = "create table " + UploadEmpyValuesVehicleTable + "(" +
                _ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                VEHICLE_ID + " TEXT NOT NULL);";

        db.execSQL(EmptyValuesUplodVehTable);


        String TripRecordTable = "create table " + TRIPRECORD_TABLE + " ( " +
                TRIP_ID + " TEXT NOT NULL UNIQUE, " +
                RECORD_START_DATE + " text not null, " +
                RECORD_END_DATE + " text," +
                RECORD_RPM_MAX + " integer, " +
                RECORD_ENGINE_RUNTIME + " text, " +
                RECORD_SPEED_MAX + " integer, " +
                DISTANCE_COVERED + " text," +
                START_LAT + " text," +
                START_LANG + " text," +
                END_LAT + " text," +
                END_LANG + " text," +
                START_LOCATION_NAME + " text," +
                END_LOCATION_NAME + " text," +
                ALERT_COUNT + " integer," +
                VEHICLE_HEALTH_SCORE + " text, " +
                MILEAGE + " text, " +
                TRIP_DURATION + " integer, " +
                SAFE_KM + " integer, " +
                LAST_UPDATED_ON + " text," +
                TRIP_SAVED_AMOUNT + " text," +
                TRIP_SAVINGS_COMISSION + " text" +
                ");";


        db.execSQL(TripRecordTable);

        String drivingToolsTable = "create table " + DRIVING_TOOLS_TABLE + " ( " +
                tool_id + " TEXT NOT NULL UNIQUE, " +
                tool_title + " text not null, " +
                tool_description + " text," +
                tool_imageUrl + " text, " +
                tool_redirectUrl + " text, " +
                tool_Type + " text, " +
                tool_ColorCode + " text, " +
                createdOn + " text," +
                category + " text" +
                ");";

        db.execSQL(drivingToolsTable);

        String perfromanceMeasuresTable = "create table " + PERFORMANCE_MEASURES_TABLE + " ( " +
                CODE_HEALTH + " TEXT NOT NULL UNIQUE, " +
                TITLE_HEALTH + " text not null, " +
                VALUE_HEALTH + " text," +
                THRESHOLD_VALUE_HEALTH + " text, " +
                DATA_NOT_SUPPORTED + " text, " +
                ERROR_HEALTH + " text " +
                ");";

        db.execSQL(perfromanceMeasuresTable);


        String faultCodesTable = "create table " + FAULT_CODES_TABLE + " ( " +
                CODE_HEALTH + " TEXT NOT NULL UNIQUE, " +
                TITLE_HEALTH + " text not null, " +
                VALUE_HEALTH + " text," +
                THRESHOLD_VALUE_HEALTH + " text, " +
                DATA_NOT_SUPPORTED + " text, " +
                ERROR_HEALTH + " text " +
                ");";

        db.execSQL(faultCodesTable);


        String criticalFaultsTable = "create table " + CRITICAL_FAULTS_TABLE + " ( " +
                CODE_HEALTH + " TEXT NOT NULL UNIQUE, " +
                TITLE_HEALTH + " text not null, " +
                VALUE_HEALTH + " text," +
                THRESHOLD_VALUE_HEALTH + " text, " +
                DATA_NOT_SUPPORTED + " text, " +
                ERROR_HEALTH + " text " +
                ");";

        db.execSQL(criticalFaultsTable);


        String categoryDevicesTable = "create table " + ALL_DEVICES_TABLE + " ( " +
                ALL_DEVICE_ID + " TEXT NOT NULL UNIQUE, " +
                ALL_DEVICE_CATEGORY + " text not null, " +
                ALL_DEVICE_NAME + " text, " +
                ALL_DEVICE_IMAGE + " text, " +
                ALL_DEVICE_INSTRUCTION_IMAGE + " text, " +
                ALL_DEVICE_INSTRUCTIONS + " text, " +
                ALL_DEVICE_INSTRUCTIONS_ID + " text " +
                ");";

        db.execSQL(categoryDevicesTable);


        String registeredDevicesTable = "create table " + REGISTERED_DEVICE_TABLE + " ( " +
                REGISTERED_DEVICE_ID + " TEXT NOT NULL UNIQUE, " +
                REGISTERED_DEVICE_NAME + " text not null, " +
                ALL_DEVICE_NAME + " text, " +
                REGISTERED_DEVICE_BLUETOOTH_NAME + " text, " +
                REGISTERED_DEVICE_BLUETOOTH_TYPE + " text, " +
                REGISTERED_DEVICE_PROFILE_SUPPORTED + " text, " +
                REGISTERED_DEVICE_MAC + " text, " +
                REGISTERED_DEVICE_IMAGE + " text, " +
                REGISTERED_DEVICE_LAST_CONNECTED + " text, " +
                REGISTERED_DEVICE_CREATED_ON + " text, " +
                REGISTERED_DEVICE_LAST_UPDATED + " text, " +
                REGISTERED_DEVICE_NOW_CONNECTED + " text " +
                ");";


        db.execSQL(registeredDevicesTable);


        String emergencyContactsTable = "create table " + EMERGENCY_CONTACT_TABLE + " ( " +
                MEMBER_ID + " TEXT NOT NULL UNIQUE, " +
                MEMBER_NAME + " text not null, " +
                MEMBER_CITY + " text, " +
                MEMBER_MOBILE_NUMBER + " text, " +
                MEMBER_EMAIL + " text, " +
                MEMBER_RELATION + " text " +
                ");";


        db.execSQL(emergencyContactsTable);



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + NOTIFICATIONS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + VEHICLES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TRIP_ALERTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + TRIP_OFFLINE_DATA_TABLE_PACKETS);
        db.execSQL("DROP TABLE IF EXISTS " + VEHICLEMAKE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + UploadEmpyValuesVehicleTable);
        db.execSQL("DROP TABLE IF EXISTS " + TRIPRECORD_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + DRIVING_TOOLS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + PERFORMANCE_MEASURES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + FAULT_CODES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + CRITICAL_FAULTS_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + ALL_DEVICES_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + REGISTERED_DEVICE_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + EMERGENCY_CONTACT_TABLE);
        onCreate(db);
    }

    public void close() {
        close();
    }

    public void InsertNotification(String title, String image, String message, String notificationType, String dataObject, String time) {
        database = getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(Notification_Title, title);
        contentValue.put(Notification_Image, image);
        contentValue.put(Notification_Message, message);
        contentValue.put(Notification_Time, time);
        contentValue.put(Notification_Type, notificationType);
        contentValue.put(Notification_Data, dataObject);
        database.insert(NOTIFICATIONS_TABLE, null, contentValue);
        database.close();
    }

    public ArrayList<OffersModel> fetchAllNotification() {

        ArrayList<OffersModel> offersModelArrayList = new ArrayList<>();
        database = getReadableDatabase();

        Cursor cursor = database.rawQuery("select * from " + NOTIFICATIONS_TABLE, null);


      /*  String[] columns = new String[]{_ID, Notification_Title, Notification_Image, Notification_Message};
        Cursor cursor = database.query(NOTIFICATIONS_TABLE, columns, null, null, null, null, null);
        */


        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    offersModelArrayList.add(new OffersModel(cursor.getInt(cursor.getColumnIndex(_ID)),
                            cursor.getString(cursor.getColumnIndex(Notification_Image)),
                            cursor.getString(cursor.getColumnIndex(Notification_Title)),
                            cursor.getString(cursor.getColumnIndex(Notification_Message)),
                            cursor.getString(cursor.getColumnIndex(Notification_Type)),
                            cursor.getString(cursor.getColumnIndex(Notification_Data)),
                            cursor.getString(cursor.getColumnIndex(Notification_Time))));
                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        database.close();
        return offersModelArrayList;


    }



    public ArrayList<BluetoothDeviceModel> fetchCategoryDevices(){
        ArrayList<BluetoothDeviceModel> categoryDevices = new ArrayList<>();
        database = getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + ALL_DEVICES_TABLE, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    categoryDevices.add(new BluetoothDeviceModel(
                            cursor.getString(cursor.getColumnIndex(ALL_DEVICE_ID)),
                            cursor.getString(cursor.getColumnIndex(ALL_DEVICE_NAME)),
                            cursor.getString(cursor.getColumnIndex(ALL_DEVICE_CATEGORY)),
                            "",
                            "",
                            "",
                            "",
                            cursor.getString(cursor.getColumnIndex(ALL_DEVICE_IMAGE)),
                            "",
                            "",
                            "",
                            false,
                            new DeviceInformationModel(
                                    cursor.getString(cursor.getColumnIndex(ALL_DEVICE_INSTRUCTIONS_ID)),
                                    cursor.getString(cursor.getColumnIndex(ALL_DEVICE_INSTRUCTION_IMAGE)),
                                    cursor.getString(cursor.getColumnIndex(ALL_DEVICE_NAME)),
                                    convertStringToArray(cursor.getString(cursor.getColumnIndex(ALL_DEVICE_INSTRUCTIONS)))
                            )

                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        database.close();
        return categoryDevices;
    }



    public void insertCategoryDevices(ArrayList<BluetoothDeviceModel> devices){
        database = getWritableDatabase();
        for (BluetoothDeviceModel deviceModel: devices){
            ContentValues contentValues = new ContentValues();
            contentValues.put(ALL_DEVICE_ID, deviceModel.getId());
            contentValues.put(ALL_DEVICE_CATEGORY, deviceModel.getCategory());
            contentValues.put(ALL_DEVICE_NAME, deviceModel.getDeviceName());
            contentValues.put(ALL_DEVICE_IMAGE, deviceModel.getImageUrl());
            contentValues.put(ALL_DEVICE_INSTRUCTION_IMAGE, deviceModel.getInformationModel().getImageUrl());
            contentValues.put(ALL_DEVICE_INSTRUCTIONS, convertArrayToString(deviceModel.getInformationModel().getInstructions()));
            contentValues.put(ALL_DEVICE_INSTRUCTIONS_ID, deviceModel.getInformationModel().getId());
            database.insert(ALL_DEVICES_TABLE, null, contentValues);
        }
        database.close();
    }


    private String convertArrayToString(ArrayList<String> array){
        String strSeparator = "__,__";
        StringBuilder str = new StringBuilder("");
        for (int i = 0;i<array.size(); i++) {
            str.append(array.get(i));
            // Do not append comma at the end of last element
            if(i<array.size()-1){
                str.append(strSeparator);
            }
        }
        return str.toString();
    }

    private ArrayList<String> convertStringToArray(String str){
        String strSeparator = "__,__";
        String[] arr = str.split(strSeparator);
        return new ArrayList<String>(Arrays.asList(arr));
    }


    public void insertRegisteredDevices(ArrayList<BluetoothDeviceModel> devices){
        database = getWritableDatabase();
        for (BluetoothDeviceModel deviceModel: devices){
            ContentValues contentValues = new ContentValues();
            contentValues.put(REGISTERED_DEVICE_ID, deviceModel.getId());
            contentValues.put(REGISTERED_DEVICE_NAME, deviceModel.getDeviceName());
            contentValues.put(REGISTERED_DEVICE_BLUETOOTH_NAME, deviceModel.getBluetoothName());
            contentValues.put(REGISTERED_DEVICE_BLUETOOTH_TYPE, deviceModel.getBluetoothType());
            contentValues.put(REGISTERED_DEVICE_PROFILE_SUPPORTED, deviceModel.getBluetoothProfileSupported());
            contentValues.put(REGISTERED_DEVICE_MAC, deviceModel.getMacAddress());
            contentValues.put(REGISTERED_DEVICE_IMAGE, deviceModel.getImageUrl());
            contentValues.put(REGISTERED_DEVICE_LAST_CONNECTED, deviceModel.getLastConnectedTime());
            contentValues.put(REGISTERED_DEVICE_CREATED_ON, deviceModel.getCreatedOn());
            contentValues.put(REGISTERED_DEVICE_LAST_UPDATED, deviceModel.getLastUpdatedOn());
            contentValues.put(REGISTERED_DEVICE_NOW_CONNECTED, String.valueOf(deviceModel.isNowConnected()));
            database.insert(REGISTERED_DEVICE_TABLE, null, contentValues);
        }
        database.close();
    }


    public ArrayList<BluetoothDeviceModel> fetchRegisteredDevices(){
        ArrayList<BluetoothDeviceModel> registeredDevices = new ArrayList<>();
        database = getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + REGISTERED_DEVICE_TABLE, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    registeredDevices.add(new BluetoothDeviceModel(
                            cursor.getString(cursor.getColumnIndex(REGISTERED_DEVICE_ID)),
                            cursor.getString(cursor.getColumnIndex(REGISTERED_DEVICE_NAME)),
                            "",
                            cursor.getString(cursor.getColumnIndex(REGISTERED_DEVICE_BLUETOOTH_NAME)),
                            cursor.getString(cursor.getColumnIndex(REGISTERED_DEVICE_BLUETOOTH_TYPE)),
                            cursor.getString(cursor.getColumnIndex(REGISTERED_DEVICE_PROFILE_SUPPORTED)),
                            cursor.getString(cursor.getColumnIndex(REGISTERED_DEVICE_MAC)),
                            cursor.getString(cursor.getColumnIndex(REGISTERED_DEVICE_IMAGE)),
                            cursor.getString(cursor.getColumnIndex(REGISTERED_DEVICE_LAST_CONNECTED)),
                            cursor.getString(cursor.getColumnIndex(REGISTERED_DEVICE_CREATED_ON)),
                            cursor.getString(cursor.getColumnIndex(REGISTERED_DEVICE_LAST_UPDATED)),
                            Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(REGISTERED_DEVICE_NOW_CONNECTED))),
                            null
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        database.close();
        return registeredDevices;
    }




    public ArrayList<MemberModel> fetchEmergencyContacts(){
        ArrayList<MemberModel> contacts = new ArrayList<>();
        database = getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + EMERGENCY_CONTACT_TABLE, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    contacts.add(new MemberModel(
                            cursor.getString(cursor.getColumnIndex(MEMBER_ID)),
                            cursor.getString(cursor.getColumnIndex(MEMBER_NAME)),
                            cursor.getString(cursor.getColumnIndex(MEMBER_CITY)),
                            cursor.getString(cursor.getColumnIndex(MEMBER_MOBILE_NUMBER)),
                            cursor.getString(cursor.getColumnIndex(MEMBER_EMAIL)),
                            cursor.getString(cursor.getColumnIndex(MEMBER_RELATION))
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        database.close();
        return contacts;
    }


    public void insertEmergencyContacts(ArrayList<MemberModel> emergencyContacts){
        database = getWritableDatabase();
        for (MemberModel member: emergencyContacts){
            ContentValues contentValues = new ContentValues();
            contentValues.put(MEMBER_ID, member.getId());
            contentValues.put(MEMBER_NAME, member.getName());
            contentValues.put(MEMBER_CITY, member.getCity());
            contentValues.put(MEMBER_MOBILE_NUMBER, member.getMobileNo());
            contentValues.put(MEMBER_EMAIL, member.getEmail());
            contentValues.put(MEMBER_RELATION, member.getRelation());
            database.insert(EMERGENCY_CONTACT_TABLE, null, contentValues);
        }
        database.close();
    }






    public int updateNotification(long _id, String title, String image, String message, String time) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(Notification_Title, title);
        contentValues.put(Notification_Image, image);
        contentValues.put(Notification_Message, message);
        contentValues.put(Notification_Time, time);
        int i = database.update(NOTIFICATIONS_TABLE, contentValues, _ID + " = " + _id, null);
        return i;
    }

    public void deleteNotification(long _id) {
        database = getWritableDatabase();
        database.delete(NOTIFICATIONS_TABLE, _ID + "=" + _id, null);
        database.close();
    }

    //If we are removing the string, we need to ecode with single quote. For int single quotes are not required.

    public void deleteVehicle(String id) {
        database = getWritableDatabase();

        database.delete(VEHICLES_TABLE, userVehicleId + "='" + id + "'", null);
        database.close();
    }

    public void deleteTable(String tableName) {
        database = getWritableDatabase();
        database.execSQL("delete from " + tableName);
        database.close();
    }


    public void InsertAllVehicles(ArrayList<Vehiclemodel> vehiclemodelArrayList) {
        database = getWritableDatabase();
        for (Vehiclemodel vehiclemodel :
                vehiclemodelArrayList) {

            ContentValues contentValue = new ContentValues();
            contentValue.put(userVehicleId, vehiclemodel.getUserVehicleId());
            contentValue.put(vehicleName, vehiclemodel.getVehicleName());
            contentValue.put(regNumber, vehiclemodel.getRegNumber());
            contentValue.put(purchaseDate, vehiclemodel.getPurchaseDate());
            contentValue.put(vehicleBrand, vehiclemodel.getVehicleBrand());
            contentValue.put(vehicleModel, vehiclemodel.getVehicleModel());
            contentValue.put(latitude, vehiclemodel.getLatitude());
            contentValue.put(longitude, vehiclemodel.getLongitude());

            contentValue.put(vehicleAlerts, vehiclemodel.getVehicleAlerts());
            contentValue.put(createdOn, vehiclemodel.getCreatedOn());
            contentValue.put(lastUpdatedOn, vehiclemodel.getLastUpdatedOn());
            contentValue.put(approved, String.valueOf(vehiclemodel.isApproved()));

            contentValue.put(odoMeterRdg, String.valueOf(vehiclemodel.getOdoMeterRdg()));
            contentValue.put(distancePostCC, String.valueOf(vehiclemodel.getDistancePostCC()));
            contentValue.put(serviceReminderkm, String.valueOf(vehiclemodel.getServiceReminderkm()));
            contentValue.put(insuranceReminderDate, String.valueOf(vehiclemodel.getInsuranceReminderDate()));
            contentValue.put(pollutionReminderDate, String.valueOf(vehiclemodel.getPollutionReminderDate()));
            contentValue.put(servicereminderduedate, String.valueOf(vehiclemodel.getServicereminderduedate()));

            contentValue.put(newServiceReminderRemainingKm, String.valueOf(vehiclemodel.getNewServiceReminderRemainingKm()));
            contentValue.put(newServiceReminderRequiredKm, String.valueOf(vehiclemodel.getNewServiceReminderRequiredKm()));
            contentValue.put(rsaCouponCode, String.valueOf(vehiclemodel.getRsaCouponCode()));
            contentValue.put(vinAvl, String.valueOf(vehiclemodel.isVinAvl()));
            contentValue.put(vin, String.valueOf(vehiclemodel.getVin()));
            contentValue.put(fuelType, String.valueOf(vehiclemodel.getFuelType()));
            contentValue.put(chassisNo, String.valueOf(vehiclemodel.getChassisNo()));
            contentValue.put(engineNo, String.valueOf(vehiclemodel.getEngineNo()));
            contentValue.put(customerName, String.valueOf(vehiclemodel.getcustomerName()));
            contentValue.put(vehicleClass, String.valueOf(vehiclemodel.getVehicleClass()));
            contentValue.put(mvTaxUpto, String.valueOf(vehiclemodel.getMvTaxUpto()));
            contentValue.put(fitness, String.valueOf(vehiclemodel.getFitness()));

            contentValue.put(epaArAiMileage, (Math.round(vehiclemodel.getEpaArAiMileage())));
            contentValue.put(ecoSpeedStartRange,(vehiclemodel.getEcoSpeedStartRange()));
            contentValue.put(ecoSpeedEndRange, (vehiclemodel.getEcoSpeedEndRange()));
            database.insert(VEHICLES_TABLE, null, contentValue);
        }

        database.close();
    }

    public void insertPerformanceMeasures(ArrayList<VehicleHealthDataModel> performanceMeasures){
        database = getWritableDatabase();
        for (VehicleHealthDataModel model : performanceMeasures){
            ContentValues contentValues = new ContentValues();
            contentValues.put(CODE_HEALTH, model.getCode());
            contentValues.put(TITLE_HEALTH, model.getTitle());
            contentValues.put(VALUE_HEALTH, model.getValue());
            contentValues.put(THRESHOLD_VALUE_HEALTH, model.getThresholdValue());
            contentValues.put(DATA_NOT_SUPPORTED, String.valueOf(model.getDataNotSupported()));
            contentValues.put(ERROR_HEALTH, String.valueOf(model.getError()));
            database.insert(PERFORMANCE_MEASURES_TABLE, null, contentValues);
        }
        database.close();
    }

    public void insertFaultCodes(ArrayList<VehicleHealthDataModel> faultCodes){
        database = getWritableDatabase();
        for (VehicleHealthDataModel model : faultCodes){
            ContentValues contentValues = new ContentValues();
            contentValues.put(CODE_HEALTH, model.getCode());
            contentValues.put(TITLE_HEALTH, model.getTitle());
            contentValues.put(VALUE_HEALTH, model.getValue());
            contentValues.put(THRESHOLD_VALUE_HEALTH, model.getThresholdValue());
            contentValues.put(DATA_NOT_SUPPORTED, String.valueOf(model.getDataNotSupported()));
            contentValues.put(ERROR_HEALTH, String.valueOf(model.getError()));
            database.insert(FAULT_CODES_TABLE, null, contentValues);
        }
        database.close();
    }

    public void insertCriticalFaults(ArrayList<VehicleHealthDataModel> criticalFaults){
        database = getWritableDatabase();
        for (VehicleHealthDataModel model : criticalFaults){
            ContentValues contentValues = new ContentValues();
            contentValues.put(CODE_HEALTH, model.getCode());
            contentValues.put(TITLE_HEALTH, model.getTitle());
            contentValues.put(VALUE_HEALTH, model.getValue());
            contentValues.put(THRESHOLD_VALUE_HEALTH, model.getThresholdValue());
            contentValues.put(DATA_NOT_SUPPORTED, String.valueOf(model.getDataNotSupported()));
            contentValues.put(ERROR_HEALTH, String.valueOf(model.getError()));
            database.insert(CRITICAL_FAULTS_TABLE, null, contentValues);
        }
        database.close();
    }

    public ArrayList<VehicleHealthDataModel> fetchPerformanceMeasures(){
        ArrayList<VehicleHealthDataModel> performanceMeasures = new ArrayList<>();
        database = getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + PERFORMANCE_MEASURES_TABLE, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    performanceMeasures.add(new VehicleHealthDataModel(
                            cursor.getString(cursor.getColumnIndex(CODE_HEALTH)),
                            cursor.getString(cursor.getColumnIndex(TITLE_HEALTH)),
                            cursor.getString(cursor.getColumnIndex(VALUE_HEALTH)),
                            cursor.getString(cursor.getColumnIndex(THRESHOLD_VALUE_HEALTH)),
                            Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DATA_NOT_SUPPORTED))),
                            Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(ERROR_HEALTH)))
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        database.close();
        return performanceMeasures;
    }

    public ArrayList<VehicleHealthDataModel> fetchFaultCodes(){
        ArrayList<VehicleHealthDataModel> faultCodes = new ArrayList<>();
        database = getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + FAULT_CODES_TABLE, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    faultCodes.add(new VehicleHealthDataModel(
                            cursor.getString(cursor.getColumnIndex(CODE_HEALTH)),
                            cursor.getString(cursor.getColumnIndex(TITLE_HEALTH)),
                            cursor.getString(cursor.getColumnIndex(VALUE_HEALTH)),
                            cursor.getString(cursor.getColumnIndex(THRESHOLD_VALUE_HEALTH)),
                            Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DATA_NOT_SUPPORTED))),
                            Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(ERROR_HEALTH)))
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        database.close();
        return faultCodes;
    }

    public ArrayList<VehicleHealthDataModel> fetchCriticalFaults(){
        ArrayList<VehicleHealthDataModel> criticalFaults = new ArrayList<>();
        database = getReadableDatabase();
        Cursor cursor = database.rawQuery("select * from " + CRITICAL_FAULTS_TABLE, null);
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    criticalFaults.add(new VehicleHealthDataModel(
                            cursor.getString(cursor.getColumnIndex(CODE_HEALTH)),
                            cursor.getString(cursor.getColumnIndex(TITLE_HEALTH)),
                            cursor.getString(cursor.getColumnIndex(VALUE_HEALTH)),
                            cursor.getString(cursor.getColumnIndex(THRESHOLD_VALUE_HEALTH)),
                            Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(DATA_NOT_SUPPORTED))),
                            Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(ERROR_HEALTH)))
                    ));
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        database.close();
        return criticalFaults;
    }








    public ArrayList<Vehiclemodel> fetchAllVehicles() {

        ArrayList<Vehiclemodel> vehiclemodelArrayList = new ArrayList<>();
        database = getReadableDatabase();

        Cursor cursor = database.rawQuery("select * from " + VEHICLES_TABLE, null);

      /*  String[] columns = new String[]{_ID, Notification_Title, Notification_Image, Notification_Message};
        Cursor cursor = database.query(NOTIFICATIONS_TABLE, columns, null, null, null, null, null);
        */

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    vehiclemodelArrayList.add(new Vehiclemodel(cursor.getString(cursor.getColumnIndex(userVehicleId)),
                            cursor.getString(cursor.getColumnIndex(vehicleName)),
                            cursor.getString(cursor.getColumnIndex(regNumber)),
                            cursor.getString(cursor.getColumnIndex(purchaseDate)),
                            cursor.getString(cursor.getColumnIndex(vehicleBrand)),
                            cursor.getString(cursor.getColumnIndex(vehicleModel)),
                            cursor.getString(cursor.getColumnIndex(latitude)),
                            cursor.getString(cursor.getColumnIndex(longitude)),
                            cursor.getString(cursor.getColumnIndex(vehicleAlerts)),
                            cursor.getString(cursor.getColumnIndex(createdOn)),
                            cursor.getString(cursor.getColumnIndex(lastUpdatedOn)),
                            cursor.getString(cursor.getColumnIndex(odoMeterRdg)),
                            cursor.getString(cursor.getColumnIndex(distancePostCC)),
                            cursor.getString(cursor.getColumnIndex(serviceReminderkm)),
                            cursor.getString(cursor.getColumnIndex(insuranceReminderDate)),
                            cursor.getString(cursor.getColumnIndex(pollutionReminderDate)),
                            cursor.getString(cursor.getColumnIndex(servicereminderduedate)),
                            cursor.getString(cursor.getColumnIndex(vin)),
                            Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(approved))),
                            Boolean.parseBoolean(cursor.getString(cursor.getColumnIndex(vinAvl))),
                            cursor.getString(cursor.getColumnIndex(newServiceReminderRemainingKm)),
                            cursor.getString(cursor.getColumnIndex(newServiceReminderRequiredKm)),
                            cursor.getString(cursor.getColumnIndex(rsaCouponCode)),
                            cursor.getString(cursor.getColumnIndex(fuelType)),
                            cursor.getString(cursor.getColumnIndex(chassisNo)),
                            cursor.getString(cursor.getColumnIndex(engineNo)),
                            cursor.getString(cursor.getColumnIndex(customerName)),
                            cursor.getString(cursor.getColumnIndex(vehicleClass)),
                            cursor.getString(cursor.getColumnIndex(mvTaxUpto)),
                            cursor.getString(cursor.getColumnIndex(fitness)),
                            cursor.getInt(cursor.getColumnIndex(epaArAiMileage)),
                            cursor.getInt(cursor.getColumnIndex(ecoSpeedStartRange)),
                            cursor.getInt(cursor.getColumnIndex(ecoSpeedEndRange))
                    ));
                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        database.close();
        return vehiclemodelArrayList;
    }

    public long insertTripAlert(TripAlert tripAlert) {
        database = getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(alertType, tripAlert.getAlertType());
        contentValue.put(alertValue, tripAlert.getAlertValue());
        contentValue.put(timeInterval, tripAlert.getTimeInterval());
        contentValue.put(gForce, tripAlert.getgForce());
        contentValue.put(timeStamp, tripAlert.getTimeStamp());
        contentValue.put(lat, tripAlert.getLat());
        contentValue.put(lng, tripAlert.getLng());
        contentValue.put(impact, tripAlert.getImpact());
        long val = database.insert(TRIP_ALERTS_TABLE, null, contentValue);
        database.close();
        return val;
    }




    private String TAG = DatabaseHelper.class.getSimpleName();

    public ArrayList<TripAlert> fetchAllTripAlerts() {

        ArrayList<TripAlert> vehiclemodelArrayList = new ArrayList<>();
        database = getReadableDatabase();

        Cursor cursor = database.rawQuery("select * from " + TRIP_ALERTS_TABLE, null);

      /*  String[] columns = new String[]{_ID, Notification_Title, Notification_Image, Notification_Message};
        Cursor cursor = database.query(NOTIFICATIONS_TABLE, columns, null, null, null, null, null);
        */

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    vehiclemodelArrayList.add(new TripAlert(
                            cursor.getString(cursor.getColumnIndex(alertType)),
                            cursor.getString(cursor.getColumnIndex(alertValue)),
                            cursor.getString(cursor.getColumnIndex(timeInterval)),
                            cursor.getString(cursor.getColumnIndex(gForce)),
                            cursor.getLong(cursor.getColumnIndex(timeStamp)),
                            cursor.getDouble(cursor.getColumnIndex(lat)),
                            cursor.getDouble(cursor.getColumnIndex(lng)),
                            cursor.getString(cursor.getColumnIndex(impact))));
                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        database.close();
        return vehiclemodelArrayList;
    }


    public long insertTripAlert(TripFileNameModel tripFileNameModel) {
        database = getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(TRIP_ID, tripFileNameModel.getTripId());
        contentValue.put(TRIP_CSV_FILENAME, tripFileNameModel.getCsvFileName());
        long val = database.insert(TRIP_OFFLINE_DATA_TABLE_PACKETS, null, contentValue);
        database.close();
        return val;
    }

    public long insertEmptyVehicleObdDetails(String vehicleId) {
        database = getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(VEHICLE_ID, vehicleId);
        long val  = database.insert(UploadEmpyValuesVehicleTable, null, contentValue);
        database.close();
        return val;
    }

    public ArrayList<String> fetchEmptyVehicleObdDetail() {

        ArrayList<String> vehiclemodelArrayList = new ArrayList<>();
        database = getReadableDatabase();

        Cursor cursor = database.rawQuery("select * from " + UploadEmpyValuesVehicleTable, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    vehiclemodelArrayList.add(
                            cursor.getString(cursor.getColumnIndex(VEHICLE_ID)));
                } while (cursor.moveToNext());

            }
            cursor.close();
        }
        database.close();
        return vehiclemodelArrayList;
    }


    public ArrayList<TripFileNameModel> fetchAllcsvFilesNames() {

        ArrayList<TripFileNameModel> vehiclemodelArrayList = new ArrayList<>();
        database = getReadableDatabase();

        Cursor cursor = database.rawQuery("select * from " + TRIP_OFFLINE_DATA_TABLE_PACKETS, null);

      /*  String[] columns = new String[]{_ID, Notification_Title, Notification_Image, Notification_Message};
        Cursor cursor = database.query(NOTIFICATIONS_TABLE, columns, null, null, null, null, null);
        */

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    vehiclemodelArrayList.add(new TripFileNameModel(cursor.getLong(cursor.getColumnIndex(_ID)),
                            cursor.getString(cursor.getColumnIndex(TRIP_ID)),
                            cursor.getString(cursor.getColumnIndex(TRIP_CSV_FILENAME))
                    ));
                } while (cursor.moveToNext());

            }
            cursor.close();
        }
        database.close();
        return vehiclemodelArrayList;
    }

    public void deleteTripCSvEntry(long tripId) {
        database = getWritableDatabase();

        try {
            database.delete(TRIP_OFFLINE_DATA_TABLE_PACKETS, _ID + "=" + tripId, null);
        } catch (Exception e) {
            e.printStackTrace();
        }
        database.close();

    }

    public void insertAllVehicleMakes(ArrayList<String> vehicleMakes) {
        for (String brandName :
                vehicleMakes) {
            database = getWritableDatabase();
            ContentValues contentValue = new ContentValues();
            contentValue.put(VEHICLEMAKE, brandName);
            database.insert(VEHICLEMAKE_TABLE, null, contentValue);
        }
        database.close();
    }

    public ArrayList<String> fetchAllVehicleMakes() {

        ArrayList<String> vehiclemodelArrayList = new ArrayList<>();
        database = getReadableDatabase();

        Cursor cursor = database.rawQuery("select * from " + VEHICLEMAKE_TABLE, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    vehiclemodelArrayList.add(
                            cursor.getString(cursor.getColumnIndex(VEHICLEMAKE)));
                } while (cursor.moveToNext());

            }
            cursor.close();
        }
        database.close();
        return vehiclemodelArrayList;
    }

    public void deleteTotalDatabase(Context context) {
        context.deleteDatabase(DB_NAME);
    }

    public long insertTripRecord(TripRecord tripRecord) {
        database = getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(TRIP_ID, tripRecord.getId());
        contentValue.put(RECORD_START_DATE, tripRecord.getStartDate());
        contentValue.put(RECORD_END_DATE, tripRecord.getEndDate());
        contentValue.put(RECORD_RPM_MAX, tripRecord.getEngineRpmMax());
        contentValue.put(RECORD_ENGINE_RUNTIME, tripRecord.getEngineRuntime());
        contentValue.put(RECORD_SPEED_MAX, tripRecord.getSpeed());
        contentValue.put(DISTANCE_COVERED, tripRecord.getDistanceCovered());
        contentValue.put(START_LAT, tripRecord.getStartLat());
        contentValue.put(START_LANG, tripRecord.getStartLong());
        contentValue.put(END_LAT, tripRecord.getEndLat());
        contentValue.put(END_LANG, tripRecord.getEndLong());
        contentValue.put(ALERT_COUNT, tripRecord.getAlertCount());

        contentValue.put(START_LOCATION_NAME, tripRecord.getStartLocationName());
        contentValue.put(END_LOCATION_NAME, tripRecord.getDestinationName());
        contentValue.put(VEHICLE_HEALTH_SCORE, String.valueOf(tripRecord.getVhsScore()));
        contentValue.put(MILEAGE, String.valueOf(tripRecord.getMileage()));
        contentValue.put(TRIP_DURATION, tripRecord.getTripDuration());
        contentValue.put(SAFE_KM, tripRecord.getSafeKm());
        contentValue.put(TRIP_SAVED_AMOUNT, tripRecord.getTripSavedAmount());
        contentValue.put(TRIP_SAVINGS_COMISSION, tripRecord.getTripSavingsCommission());
        contentValue.put(LAST_UPDATED_ON, tripRecord.getLastUpdatedOn());
        long val = database.insert(TRIPRECORD_TABLE, null, contentValue);
        database.close();
        return val;


    }


    public ArrayList<TripRecord> fetchAllTripRecords() {

        ArrayList<TripRecord> vehiclemodelArrayList = new ArrayList<>();
        database = getReadableDatabase();

        Cursor cursor = database.rawQuery("select * from " + TRIPRECORD_TABLE, null);

      /*  String[] columns = new String[]{_ID, Notification_Title, Notification_Image, Notification_Message};
        Cursor cursor = database.query(NOTIFICATIONS_TABLE, columns, null, null, null, null, null);
        */

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    vehiclemodelArrayList.add(new TripRecord(
                            cursor.getString(cursor.getColumnIndex(TRIP_ID)),
                            cursor.getString(cursor.getColumnIndex(RECORD_START_DATE)),
                            cursor.getString(cursor.getColumnIndex(RECORD_END_DATE)),
                            cursor.getInt(cursor.getColumnIndex(RECORD_RPM_MAX)),
                            cursor.getInt(cursor.getColumnIndex(RECORD_SPEED_MAX)),
                            cursor.getString(cursor.getColumnIndex(RECORD_ENGINE_RUNTIME)),
                            cursor.getString(cursor.getColumnIndex(DISTANCE_COVERED)),
                            cursor.getInt(cursor.getColumnIndex(ALERT_COUNT)),
                            cursor.getString(cursor.getColumnIndex(START_LAT)),
                            cursor.getString(cursor.getColumnIndex(START_LANG)),
                            cursor.getString(cursor.getColumnIndex(END_LAT)),
                            cursor.getString(cursor.getColumnIndex(END_LANG)),
                            cursor.getString(cursor.getColumnIndex(START_LOCATION_NAME)),
                            cursor.getString(cursor.getColumnIndex(END_LOCATION_NAME)),
                            Double.parseDouble(cursor.getString(cursor.getColumnIndex(VEHICLE_HEALTH_SCORE))),
                            Double.parseDouble(cursor.getString(cursor.getColumnIndex(MILEAGE))),
                            cursor.getLong(cursor.getColumnIndex(TRIP_DURATION)),
                            cursor.getInt(cursor.getColumnIndex(SAFE_KM)),
                            cursor.getString(cursor.getColumnIndex(LAST_UPDATED_ON)),
                            cursor.getString(cursor.getColumnIndex(TRIP_SAVED_AMOUNT)),
                            cursor.getString(cursor.getColumnIndex(TRIP_SAVINGS_COMISSION))));


                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        database.close();
        return vehiclemodelArrayList;
    }

    public long insertDrivingTool(ToolModelClass toolModelClass) {
        database = getWritableDatabase();
        ContentValues contentValue = new ContentValues();
        contentValue.put(tool_id, toolModelClass.getId());
        contentValue.put(tool_title, toolModelClass.getTitle());
        contentValue.put(tool_description, toolModelClass.getDescription());
        contentValue.put(tool_imageUrl, toolModelClass.getImageUrl());

        contentValue.put(tool_ColorCode, toolModelClass.getColourCode());
        contentValue.put(tool_Type, toolModelClass.getType());

        contentValue.put(tool_redirectUrl, toolModelClass.getRedirectUrl());
        contentValue.put(createdOn, toolModelClass.getCreatedOn());
        contentValue.put(category, toolModelClass.getCategory());
        long val = database.insert(DRIVING_TOOLS_TABLE, null, contentValue);
        database.close();
        return val;


    }

    public ArrayList<ToolModelClass> fetchDrivingTools() {

        ArrayList<ToolModelClass> vehiclemodelArrayList = new ArrayList<>();
        database = getReadableDatabase();

        Cursor cursor = database.rawQuery("select * from " + DRIVING_TOOLS_TABLE, null);

      /*  String[] columns = new String[]{_ID, Notification_Title, Notification_Image, Notification_Message};
        Cursor cursor = database.query(NOTIFICATIONS_TABLE, columns, null, null, null, null, null);
        */

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {

                    vehiclemodelArrayList.add(new ToolModelClass(
                            cursor.getString(cursor.getColumnIndex(tool_id)),
                            cursor.getString(cursor.getColumnIndex(tool_title)),
                            cursor.getString(cursor.getColumnIndex(tool_description)),
                            cursor.getString(cursor.getColumnIndex(tool_imageUrl)),
                            cursor.getString(cursor.getColumnIndex(tool_redirectUrl)),
                            cursor.getString(cursor.getColumnIndex(tool_Type)),
                            cursor.getString(cursor.getColumnIndex(tool_ColorCode)),
                            cursor.getString(cursor.getColumnIndex(createdOn)),
                            cursor.getString(cursor.getColumnIndex(category))));


                } while (cursor.moveToNext());

            }
            cursor.close();
        }

        database.close();
        return vehiclemodelArrayList;
    }









}