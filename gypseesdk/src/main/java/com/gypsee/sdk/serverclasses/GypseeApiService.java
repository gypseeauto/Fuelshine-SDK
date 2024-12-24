package com.gypsee.sdk.serverclasses;

import com.gypsee.sdk.models.GypseeThresholdValues;

import retrofit2.Call;
import retrofit2.http.GET;

public interface GypseeApiService {
    @GET("privacy/gypseeconfigv2.json")
    Call<GypseeThresholdValues> getThresholdValues();
}