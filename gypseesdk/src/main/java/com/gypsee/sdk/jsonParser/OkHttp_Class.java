package com.gypsee.sdk.jsonParser;

import android.util.Log;

import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;


public class OkHttp_Class {


    private static String TAG = OkHttp_Class.class.getSimpleName();

    public static String callServer(String jsonInput, String requestParameter, String url, String logTagName) {
        try {

            OkHttpClient.Builder client1 = new OkHttpClient.Builder();

            OkHttpClient client = client1.connectTimeout(60, TimeUnit.SECONDS)
                    .writeTimeout(60, TimeUnit.SECONDS)
                    .readTimeout(60, TimeUnit.SECONDS)
                    //.retryOnConnectionFailure(true)
                    .build();
            MediaType mediaType = MediaType.parse("application/json");

            JSONObject jsonObject = new JSONObject(jsonInput);

            String actoken = "";

            if (jsonObject.has("authorization"))
            {
                actoken  = jsonObject.getString("authorization");
                jsonObject.remove("authorization");
                jsonInput = jsonObject.toString();
            }
            RequestBody body = RequestBody.create(mediaType, jsonInput);


         Log.e(TAG, logTagName + " URL : " + url);
         Log.e(TAG, logTagName + " Input : " + jsonInput);
         Log.e(TAG, logTagName + " Authorization : " + actoken);
            Request request = new Request.Builder()
                    .url(url)
                    .method("POST", body)
                    .addHeader("Content-Type", "application/json")
                    .addHeader("authorization",actoken)
                    .build();
            Response responseObject = client.newCall(request).execute();

            String jsonString;
            if (responseObject.body() != null) {
                jsonString = responseObject.body().string();
            } else {
                return "SSLHandshakeException";
            }
            responseObject.close();
         Log.e(TAG, logTagName + " Response Normal: " + jsonString);
            return jsonString;
        } catch (Exception e) {
            e.printStackTrace();
            if (e instanceof IOException) {
                return "SSLHandshakeException";

            } else {
                return "UnknownHostException";
            }


        }


    }


}
