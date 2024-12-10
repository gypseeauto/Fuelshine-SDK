package com.sdk.gypsee.sampleapp

import android.app.Application
import android.content.Context
import android.util.Log
import androidx.multidex.MultiDex
import com.gypsee.sdk.models.GypseeThresholdValues
import com.gypsee.sdk.network.RetrofitClient
import com.gypsee.sdk.serverclasses.GypseeApiService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SampleAppApplication:Application() {

    override fun attachBaseContext(base: Context?) {
        super.attachBaseContext(base)
        MultiDex.install(base)

        fetchThresholdValues()
    }

    private fun fetchThresholdValues() {
        val apiService = RetrofitClient.getRetrofitInstance().create(GypseeApiService::class.java)
        val call = apiService.getThresholdValues()

        call.enqueue(object : Callback<GypseeThresholdValues> {
            override fun onResponse(call: Call<GypseeThresholdValues>, response: Response<GypseeThresholdValues>) {
                if (response.isSuccessful && response.body() != null) {
                    val values = response.body()
                    val alerts = values?.alerts

                    Log.d("Config Values", "Harsh Acceleration: ${alerts?.harshAcceleration}")
                    Log.d("Config Values", "Harsh Braking: ${alerts?.harshBraking}")
                    Log.d("Config Values", "Overspeed: ${alerts?.overspeed}")

                    // Store double values as Strings in SharedPreferences
                    val sharedPreferences = getSharedPreferences("ThresholdPrefs", MODE_PRIVATE)
                    val editor = sharedPreferences.edit()
                    editor.putString("harsh_acceleration", alerts?.harshAcceleration.toString())
                    editor.putString("harsh_braking", alerts?.harshBraking.toString())
                    editor.putString("overspeed", alerts?.overspeed.toString())
                    editor.apply() // Save changes
                } else {
                    Log.e("Config Values", "Threshold Response was not successful")
                }
            }

            override fun onFailure(call: Call<GypseeThresholdValues>, t: Throwable) {
                Log.e("Config Values", "Failed to fetch threshold values", t)
            }
        })
    }



}