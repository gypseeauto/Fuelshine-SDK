package com.gypsee.sdk.serverclasses;

import android.content.Context;
import android.util.Log;

import java.util.concurrent.TimeUnit;

import com.gypsee.sdk.R;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {

    private static String TAG = ApiClient.class.getSimpleName();

    public static Retrofit getClient(String TAG, String purpose, String Url) {


       Log.e(TAG, purpose + " Url is : " + Url);

        OkHttpClient.Builder client = new OkHttpClient.Builder();

        OkHttpClient timeWaitClient = client
                .connectTimeout(150, TimeUnit.SECONDS)
                .readTimeout(150, TimeUnit.SECONDS).build();

        return new Retrofit.Builder()
                .baseUrl(Url)
                .addConverterFactory(GsonConverterFactory.create())

                .client(timeWaitClient)
                .build();
    }


    private static Retrofit retrofit;
    //private static final String BASE_URL = "http://appadmin.gypsee.in/gypsee/";

    public static Retrofit getRetrofitInstance(Context context) {
        OkHttpClient.Builder client = new OkHttpClient.Builder();

        OkHttpClient timeWaitClient = client
                .connectTimeout(150, TimeUnit.SECONDS)
                .readTimeout(150, TimeUnit.SECONDS).build();


        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(context.getResources().getString(R.string.base_url))
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(timeWaitClient)
                    .build();
        }
        return retrofit;
    }


}