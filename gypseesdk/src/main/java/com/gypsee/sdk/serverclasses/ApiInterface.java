package com.gypsee.sdk.serverclasses;


import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONObject;

import java.util.HashMap;

import com.gypsee.sdk.models.TripListResponse;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;

public interface ApiInterface {


    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @POST(".")
    Call<ResponseBody> getLogin(@Body JsonObject jsonInput);


    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @POST(".")
    Call<ResponseBody> getRegister(@Body JsonObject jsonInput);


    @GET("version")
    Call<ResponseBody> getAppVersion(@Header("authorization") String authorization);


    @GET(".")
    Call<ResponseBody> getDocTypes(@Header("authorization") String authorization, @Query("isVehicleType") boolean isVehicleType);


    @GET(".")
    Call<ResponseBody> getUser(@Header("authorization") String authorization, @Query("isWallet") boolean isWallet);


    @GET(".")
    Call<ResponseBody> getVehicleAlerts(@Header("authorization") String authorization, @QueryMap HashMap<String, Object> values);


    //Get documents or get vehicle alerts

    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @POST(".")
    Call<ResponseBody> getDocVehicleAlerts(@Header("authorization") String authorization, @Query("isDocuments") boolean isDocuments,
                                           @Query("isAlerts") boolean isAlerts);

    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @POST(".")
    Call<ResponseBody> uploadVehDetails(@Header("authorization") String authorization, @Body JsonObject jsonInput);

    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @POST(".")
    Call<ResponseBody> endTrip(@Header("authorization") String authorization, @Body JsonObject jsonInput, @Query("isGenerateVHSReport") boolean isGenerateVHSReport);

    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @POST(".")
    Call<ResponseBody> uploadVehDetails(@Header("authorization") String authorization, @Body JSONObject jsonInput);


    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @POST(".")
    Call<ResponseBody> uploadDTCerrorcodes(@Header("authorization") String authorization, @Body JsonArray jsonArray);


    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @PUT(".")
    Call<ResponseBody> uploadFCMToken(@Header("authorization") String authorization, @Body JsonObject jsonInput);


    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @PUT("user/{userid}/refer/code/add/")
    Call<ResponseBody> uploadReferralCode(@Header("authorization") String authorization, @Body JsonObject jsonInput, @Path("userid") String userId);


    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @PUT("user/{userid}/fcmtoken/update")
    Call<ResponseBody> updateFcmToken(@Header("authorization") String authorization, @Body JsonObject jsonInput, @Path("userid") String userid);


    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @PUT(".")
    Call<ResponseBody> clearAlertCode(@Header("authorization") String authorization, @Body JsonObject jsonInput);


    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @GET(".")
    Call<TripListResponse> getTripList(@Header("authorization") String authorization, @Query("size") int size, @Query("page") int page);

    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @GET(".")
    Call<ResponseBody> getTripListBasic(@Header("authorization") String authorization, @Query("size") int size, @Query("page") int page, @Query("isBasic") boolean isBasic );

    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @GET(".")
    Call<ResponseBody> getTodayTripListBasic(@Header("authorization") String authorization, @Query("size") int size, @Query("page") int page, @Query("isBasic") boolean isBasic,@Query("periodFrom") String periodFrom,@Query("periodTo") String periodTo);



    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @GET(".")
    Call<ResponseBody> getTripDrivingAlerts(@Header("authorization") String authorization, @Query("size") int size, @Query("page") int page, @Query("userId") String userId,@Query("tripId") String tripId);

    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @GET(".")
    Call<ResponseBody> getDrivingAlerts(@Header("authorization") String authorization, @Query("size") int size, @Query("page") int page, @Query("userId") String userId);

    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @DELETE(".")
    Call<ResponseBody> deleteCar(@Header("authorization") String authorization);

    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @POST(".")
    Call<ResponseBody> uploadObd(@Header("authorization") String authorization, @Body JsonObject jsonInput);

    @POST(".")
    Call<ResponseBody> uploadDeviceDetails(@Header("authorization") String authorization, @Body JsonObject jsonInput);

    @GET(".")
    Call<ResponseBody> getGameLevel(@Header("authorization") String authorization, @Query("periodFrom") String periodFrom, @Query("periodTo") String periodTo);


    @GET(".")
    Call<ResponseBody> getFuelSavings(@Header("authorization") String authorization, @Query("periodFrom") String periodFrom, @Query("periodTo") String periodTo);

    @GET(".")
    Call<ResponseBody> getWallet(@Header("authorization") String authorization,  @Query("includeTransactions") boolean includeTransactions, @Query("periodFrom") String periodFrom, @Query("periodTo") String periodTo);


    @GET(".")
    Call<ResponseBody> getPerformance(@Header("authorization") String authorization,  @Query("periodFrom") String periodFrom, @Query("periodTo") String periodTo);


    @GET(".")
    Call<ResponseBody> getFuelPrice(@Header("authorization") String authorization );



    @GET(".")
    Call<ResponseBody> getObdDevice(@Header("authorization") String authorization);

    @POST(".")
    Call<ResponseBody> getDistanceRecalculation(@Header("authorization") String authorization, @Query("tripId") String userId);

    @GET(".")
    Call<ResponseBody> getVehicleAlertsForTrip(@Header("authorization") String authorization);

    @GET(".")
    Call<ResponseBody> getVehicleHealthData(@Header("authorization") String authorization);

    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @POST(".")
    Call<ResponseBody> exitTrainingMode(@Header("authorization") String authorization);


    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @PUT(".")
    Call<ResponseBody> updateRegisteredDevices(@Header("authorization") String authorization, @Body JsonArray jsonInput);

    @GET(".")
    Call<ResponseBody> getEmergencyContacts(@Header("authorization") String authorization, @Query("userId") String userId);

    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @POST(".")
    Call<ResponseBody> updateTripConnectedDevice(@Header("authorization") String authorization, @Body JsonArray arrayInput);


    @GET("products")
    Call<ResponseBody> fetchProductList(@Header("authorization") String authorization, @Query("offset") int offset, @Query("limit") int limit);


    @Headers({
            "Content-Type: application/json",
            "accept: */*"
    })
    @POST(".")
    Call<ResponseBody> createOrder(@Header("authorization") String authorization, @Body JsonObject jsonInput, @Query("userId") String userId);



    @GET(".")
    Call<ResponseBody> getUserSubscription(@Header("authorization") String authorization, @Query("isWallet") boolean isWallet, @Query("isSubscription") boolean isSubscription);





}

