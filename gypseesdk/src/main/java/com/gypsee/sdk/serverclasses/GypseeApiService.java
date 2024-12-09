package com.gypsee.sdk.serverclasses;

import com.gypsee.sdk.models.GypseeThresholdValues;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GypseeApiService {
    @GET("privacy/gypseeThresholdValues.json")
    Call<GypseeThresholdValues> getThresholdValues();
}