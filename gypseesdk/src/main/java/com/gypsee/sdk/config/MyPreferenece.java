package com.gypsee.sdk.config;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.gypsee.sdk.models.SubscriptionModel;
import com.gypsee.sdk.models.User;

public class MyPreferenece {

    public static String AUTO_START = "AUTO_START";
    public static final String DRIVING_MODE = "DRIVING_MODE";
    private SharedPreferences sharedPreferences;
    private static final String LAT_LNG = "lat_lng";
    private static final String VEHICLE_REG_NO = "vehicle_reg_no";
    private static final String VEHICLE_MODEL = "vehicle_model";
    private static final String VEHICLE_MAKE = "vehicle_make";
    private static final String VEHICLE_PURCHASE_DATE = "vehicle_purchase_date";
    private static final String CUSTOMER_NAME = "customer_name";
    private static final String CUSTOMER_PHONE = "customer_phone";
    public static final String FCM_TOKEN = "fcm_token";
    public static final String isFCmTOkenSaved = "isFCmtokenSaved";
    public static final String lastConnectedVehicle = "LastConnectVehicle";
    public static final String GYPSEE_PREFERENCES = "Gypsee_preferncess";

    public static final String isConnecting = "isConnecting";
    public static final String isTripRunning = "isTripRunning";

    public static final String NEW_INSTALL = "new_install";

    public static final String initCommandApi = "init_command_api";
    public static final String obdExistForUser = "obdExistForUser";

    public static final String lastTripVhsScore = "lastTripVhsScore";
    public static final String lastTripMileage = "lastTripMileage";

    public static final String installReferrer = "installReferrer";

    public static final String deviceCategoryIndex = "deviceCategoryIndex";

    public static final String connectedDeviceMac = "connectedDeviceMac";

    public static final String lastLocationCache = "lastLocationCache";

    public static final String driveModePagerPosition = "driveModePagerPosition";

    public static final String allowReadNotification = "allowReadNotification";

   // public static final String backgroundLocationPriority = "backgroundLocationPriority";

    public static final String availableSubscriptionId = "availableSubscriptionId";

    public static final String accessibilityPermission = "accessibilityPermission";

    public static final String queryAllPackagesPermission = "queryAllPackagesPermission";

    public static final String connectedToCar = "connectedToCar";
    public static final String mute = "mute";
    public static final String speedSwitch = "speedSwitch";
    public static final String harshBrakeSwitch = "harshBrakeSwitch";
    public static final String harshAccelarationSwitch = "harshAccelarationSwitch";
    public static final String textAndDriveSwitch = "textAndDriveSwitch";
    public static final String serviceLogs = "ServiceLogs";
    public static final String tripLogs = "tripLogs";

    private static final String LANG = "language";

    private static final String LOGS = "logs";


    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    public MyPreferenece(String preferenceName, Context context) {
        sharedPreferences = context.getSharedPreferences(GYPSEE_PREFERENCES, Context.MODE_PRIVATE);
    }

    public String getAvailableSubscriptionId(){return sharedPreferences.getString(availableSubscriptionId, "");}

    public void setAvailableSubscriptionId(String id){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(availableSubscriptionId, id);
        editor.apply();
    }

    public String getLastLatLng() {
        return sharedPreferences.getString(LAT_LNG, "0/0");
    }

    public boolean getIsConnecting(){
        return sharedPreferences.getBoolean(isConnecting, true);
    }

    public boolean getIsTripRunning() {
        return sharedPreferences.getBoolean(isTripRunning, true);
    }



    public void setLastTripVhsScore(double vhsScore){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(lastTripVhsScore, String.valueOf(vhsScore));
        edit.apply();
    }

    public void setAllowReadNotification(boolean flag){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(allowReadNotification, false);
        editor.apply();
    }

    public boolean getAllowReadNotification(){
        return sharedPreferences.getBoolean(allowReadNotification, true);
    }

    public void setConnectedToCar(Boolean value){
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(connectedToCar , value);
        editor.apply();
    }

    public void setSpeedSwitch(Boolean value){
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(speedSwitch , value);
        editor.apply();
    }

    public void setHarshBrakeSwitch(Boolean value){
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(harshBrakeSwitch , value);
        editor.apply();
    }

    public void setHarshAccelarationSwitch(Boolean value){
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(harshAccelarationSwitch , value);
        editor.apply();
    }

    public void setTextAndDriveSwitch(Boolean value){
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(textAndDriveSwitch , value);
        editor.apply();
    }

    public void setMute(Boolean value){
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(mute , value);
        editor.apply();
    }

    public Boolean getMute() {
        return sharedPreferences.getBoolean(mute, false);
    }

    public Boolean getConnectedToCar() {
        return sharedPreferences.getBoolean(connectedToCar, false);
    }

    public Boolean getHarshBrakeSwitch() {
        return sharedPreferences.getBoolean(harshBrakeSwitch, true);
    }

    public Boolean getHarshAccelarationSwitch() {
        return sharedPreferences.getBoolean(harshAccelarationSwitch, true);
    }
    public Boolean getSpeedingSwitch() {
        return sharedPreferences.getBoolean(speedSwitch, true);
    }

    public Boolean getTextAndDriveSwitch() {
        return sharedPreferences.getBoolean(textAndDriveSwitch, true);
    }



    public String getLastLocationCache(){
        return sharedPreferences.getString(lastLocationCache, "0/0");
    }

    public void setLastLocationCache(String lastLocation){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(lastLocationCache, lastLocation);
        edit.apply();
    }

    public void setConnectedDeviceMac(String mac){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(connectedDeviceMac, mac);
        editor.apply();
    }

    public String getConnectedDeviceMac(){
        return sharedPreferences.getString(connectedDeviceMac, "");
    }

    public double getLastTripVhsScore() {
        return Double.parseDouble(sharedPreferences.getString(lastTripVhsScore, "0"));
    }

    public void setLastTripMileage(double mileage){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(lastTripMileage, String.valueOf(mileage));
        edit.apply();
    }

    public double getLastTripMileage() {
        return Double.parseDouble(sharedPreferences.getString(lastTripMileage, "0"));
    }

    public void setDeviceCategoryIndex(int index){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putInt(MyPreferenece.deviceCategoryIndex, index);
        edit.apply();
    }

    public int getDeviceCategoryIndex() {
        return sharedPreferences.getInt(MyPreferenece.deviceCategoryIndex, 0);
    }




    public void setIsConnecting(boolean value){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(isConnecting, value);
        edit.apply();
    }

    public void setIsTripRunning(boolean value){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(isTripRunning, value);
        edit.apply();
    }

    public void setInitApi(boolean initApi){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(initCommandApi, initApi);
        edit.apply();
    }

    public boolean getInitApi(){
        return sharedPreferences.getBoolean(initCommandApi, true);
    }


    public String getLang() {
        return sharedPreferences.getString(LANG, "en");
    }

    public void setLang(String name) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(LANG, name);
        edit.apply();
    }


    public String getLogs() {
        return sharedPreferences.getString(LOGS, "");
    }

    public void setLogs(String name) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(LOGS, name);
        edit.apply();
    }




    public void clearAllSharesPreferences() {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear();
        edit.apply();
    }

    public void setNewInstall(boolean newInstall){
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(NEW_INSTALL, newInstall);
        edit.apply();
    }

    public boolean getNewInstall(){
        return sharedPreferences.getBoolean(NEW_INSTALL, true);
    }


    public void setLatLng(String name) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(LAT_LNG, name);
        edit.apply();
    }

    public void saveBoolean(String key, boolean value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putBoolean(key, value);
        edit.apply();
    }

    public void setCustomerName(String name) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(CUSTOMER_NAME, name);
        edit.apply();
    }

    public void saveStringData(String key, String value) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(key, value);
        edit.apply();
    }

    public String getStringData(String key) {
        return sharedPreferences.getString(key, null);
    }


    public void setVehicleModel(String name) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(VEHICLE_MODEL, name);
        edit.apply();
    }

    public void setVehicleMake(String name) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(VEHICLE_MAKE, name);
        edit.apply();
    }

    public void setPurchaseDate(String name) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(VEHICLE_PURCHASE_DATE, name);
        edit.apply();
    }

    public void setCustomerPhone(String name) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(CUSTOMER_PHONE, name);
        edit.apply();
    }

    public void setVehicleRegNo(String name) {
        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.putString(VEHICLE_REG_NO, name);
        edit.apply();
    }

    public void setAutostart(Boolean value){
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(AUTO_START , value);
        editor.apply();
    }

    public Boolean getAutostart(){
        return sharedPreferences.getBoolean(AUTO_START, false);
    }

    public void setAccessibilityPermission(Boolean value){
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(accessibilityPermission , value);
        editor.apply();
    }

    public void setQueryAllPackagesPermission(Boolean value){
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(queryAllPackagesPermission , value);
        editor.apply();
    }

    public Boolean getIfAccessibilityPermissionGranted() {
        return sharedPreferences.getBoolean(accessibilityPermission, false);
    }

    public Boolean getIfQueryAllPackagesPermissionGranted() {
        return sharedPreferences.getBoolean(queryAllPackagesPermission, false);
    }

    public String getCustomerName() {
        return sharedPreferences.getString(CUSTOMER_NAME, null);
    }

    public String getCustomerPhone() {
        return sharedPreferences.getString(CUSTOMER_PHONE, null);
    }

    public String getVehicleModel() {
        return sharedPreferences.getString(VEHICLE_MODEL, null);
    }

    public String getVehicleMake() {
        return sharedPreferences.getString(VEHICLE_MAKE, null);
    }

    public String getPurchaseDate() {
        return sharedPreferences.getString(VEHICLE_PURCHASE_DATE, null);
    }

    public String getVehicleRegNo() {
        return sharedPreferences.getString(VEHICLE_REG_NO, null);
    }


    public void clearData() {

        SharedPreferences.Editor edit = sharedPreferences.edit();
        edit.clear();
        edit.apply();
        edit.commit();

    }

    private static final String UserID = "UserID";
    private static final String userName = "userName";
    private static final String userFullName = "userFullName";
    private static final String userEmail = "userEmail";
    private static final String userPhoneNumber = "userPhoneNumber";
    private static final String userAccessToken = "userAccessToken";
    private static final String fcmToken = "fcmToken";
    private static final String userImg = "userImg";
    private static final String userDeviceMac = "userDeviceMac";
    private static final String userTypes = "userTypes";
    private static final String referCode = "referCode";
    public static final String createdOn = "createdOn";
    public static final String lastUpdatedOn = "lastUpdatedOn";
    private static final String userAddresses = "userAddresses";
    private static final String approved = "user_approved";
    private static final String locked = "locked";
    private static final String signUpBonusCredited = "signUpBonusCredited";
    private static final String referCodeApplied = "referCodeApplied";
    private static final String inTrainingMode = "inTrainingMode";
    private static final String walletAmount = "walletAmount";
    private static final String subscriptionCouponCode = "subscriptionCouponCode";
    private static final String subscriptionActive = "subscriptionActive";
    private static final String subscriptionCreatedOn = "subscriptionCreatedOn";
    private static final String subscriptionEndDate = "subscriptionEndDate";
    private static final String subscriptionId = "subscriptionId";
    private static final String subscriptionLastUpdatedOn = "subscriptionLastUpdatedOn";
    private static final String subscriptionStartDate = "subscriptionStartDate";
    private static final String subscriptionDiscountAmount = "subscriptionDiscountAmount";
    private static final String subscriptionAmount = "subscriptionAmount";
    private static final String subscriptionPaidAmount = "subscriptionPaidAmount";


    //Save Login User Data
    public void storeUser(User user) {

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(UserID, user.getUserId());
        editor.putString(userName, user.getUserName());
        editor.putString(userFullName, user.getUserFullName());
        editor.putString(userEmail, user.getUserEmail());
        editor.putString(userPhoneNumber, user.getUserPhoneNumber());
        editor.putString(userAccessToken, user.getUserAccessToken());
        editor.putString(fcmToken, user.getFcmToken());
        editor.putString(userImg, user.getUserImg());
        editor.putString(userDeviceMac, user.getUserDeviceMac());
        editor.putString(userTypes, user.getUserTypes());
        editor.putString(referCode, user.getReferCode());
        editor.putString(createdOn, user.getCreatedOn());
        editor.putString(lastUpdatedOn, user.getLastUpdatedOn());
        editor.putBoolean(approved, user.isApproved());
        editor.putBoolean(locked, user.isLocked());
        editor.putBoolean(signUpBonusCredited, user.isSignUpBonusCredited());
        editor.putBoolean(referCodeApplied, user.isReferCodeApplied());
        editor.putBoolean(inTrainingMode, user.isInTrainingMode());
        editor.putString(walletAmount, user.getWalletAmount());

        if (user.getUserSubscriptions() != null){
        editor.putString(subscriptionCouponCode, user.getUserSubscriptions().getCouponCode());
        editor.putBoolean(subscriptionActive, user.getUserSubscriptions().isActive());
        editor.putString(subscriptionCreatedOn, user.getUserSubscriptions().getCreatedOn());
        editor.putString(subscriptionEndDate, user.getUserSubscriptions().getEndDate());
        editor.putString(subscriptionId, user.getUserSubscriptions().getId());
        editor.putString(subscriptionLastUpdatedOn, user.getUserSubscriptions().getLastUpdatedOn());
        editor.putString(subscriptionStartDate, user.getUserSubscriptions().getStartDate());
        editor.putString(subscriptionAmount, String.valueOf(user.getUserSubscriptions().getSubscriptionAmount()));
        editor.putString(subscriptionDiscountAmount, String.valueOf(user.getUserSubscriptions().getDiscountAmount()));
        editor.putString(subscriptionPaidAmount, String.valueOf(user.getUserSubscriptions().getPaidAmount()));
        }



        editor.apply();

    }

    //get current or saved user
    public User getUser() {

        String userId, userName, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken, userImg, userDeviceMac,
                userTypes, referCode, createdOn, lastUpdatedOn, walletAmount,
                subscriptionCouponCode, subscriptionCreatedOn, subscriptionEndDate, subscriptionId,
                subscriptionLastUpdatedOn, subscriptionStartDate, subscriptionAmount, subscriptionDiscountAmount, subscriptionPaidAmount;

        boolean approved, locked, signUpBonusCredited,referCodeApplied, inTrainingMode, subscriptionActive;
        userId = sharedPreferences.getString(UserID, null);

        if (userId != null) {

            userName = sharedPreferences.getString(MyPreferenece.userName, null);
            userFullName = sharedPreferences.getString(MyPreferenece.userFullName, null);
            userEmail = sharedPreferences.getString(MyPreferenece.userEmail, null);
            userPhoneNumber = sharedPreferences.getString(MyPreferenece.userPhoneNumber, "");
            userAccessToken = sharedPreferences.getString(MyPreferenece.userAccessToken, "");
            fcmToken = sharedPreferences.getString(MyPreferenece.fcmToken, "");

            userImg = sharedPreferences.getString(MyPreferenece.userImg, "");
            userDeviceMac = sharedPreferences.getString(MyPreferenece.userDeviceMac, "");
            userTypes = sharedPreferences.getString(MyPreferenece.userTypes, "");
            referCode = sharedPreferences.getString(MyPreferenece.referCode, "");

            createdOn = sharedPreferences.getString(MyPreferenece.createdOn, "");
            lastUpdatedOn = sharedPreferences.getString(MyPreferenece.lastUpdatedOn, "");

            approved = sharedPreferences.getBoolean(MyPreferenece.approved, false);
            locked = sharedPreferences.getBoolean(MyPreferenece.locked, false);
            signUpBonusCredited = sharedPreferences.getBoolean(MyPreferenece.signUpBonusCredited, false);
            referCodeApplied = sharedPreferences.getBoolean(MyPreferenece.referCodeApplied, false);
            inTrainingMode = sharedPreferences.getBoolean(MyPreferenece.inTrainingMode, false);
            walletAmount = sharedPreferences.getString(MyPreferenece.walletAmount, "0");

            SubscriptionModel subscriptionModel = null;

            subscriptionActive = sharedPreferences.getBoolean(MyPreferenece.subscriptionActive, false);
            subscriptionId = sharedPreferences.getString(MyPreferenece.subscriptionId, "");
            if (!subscriptionId.equals("")){
                subscriptionCouponCode = sharedPreferences.getString(MyPreferenece.subscriptionCouponCode, "");
                subscriptionCreatedOn = sharedPreferences.getString(MyPreferenece.subscriptionCreatedOn, "");
                subscriptionEndDate = sharedPreferences.getString(MyPreferenece.subscriptionEndDate, "");
                subscriptionLastUpdatedOn = sharedPreferences.getString(MyPreferenece.subscriptionLastUpdatedOn, "");
                subscriptionStartDate = sharedPreferences.getString(MyPreferenece.subscriptionStartDate, "");
                subscriptionAmount = sharedPreferences.getString(MyPreferenece.subscriptionAmount, "");
                subscriptionDiscountAmount = sharedPreferences.getString(MyPreferenece.subscriptionDiscountAmount, "");
                subscriptionPaidAmount = sharedPreferences.getString(MyPreferenece.subscriptionPaidAmount, "");
                subscriptionModel = new SubscriptionModel(
                        subscriptionActive,
                        subscriptionCouponCode,
                        subscriptionCreatedOn,
                        subscriptionEndDate,
                        subscriptionId,
                        subscriptionLastUpdatedOn,
                        subscriptionStartDate,
                        Double.parseDouble(subscriptionDiscountAmount),
                        Double.parseDouble(subscriptionAmount),
                        Double.parseDouble(subscriptionPaidAmount)
                );
            }

            User user = new User(userId, userName, userFullName, userEmail, userPhoneNumber, userAccessToken, fcmToken, userImg, userDeviceMac,
                    userTypes, referCode, createdOn, lastUpdatedOn, approved, locked, signUpBonusCredited,referCodeApplied, inTrainingMode, walletAmount);
            user.setUserSubscriptions(subscriptionModel);



            return user;
        }
        return null;
    }

}
