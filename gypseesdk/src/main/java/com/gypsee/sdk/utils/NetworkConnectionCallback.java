package com.gypsee.sdk.utils;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkInfo;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import com.gypsee.sdk.services.ForegroundService;


public class NetworkConnectionCallback extends ConnectivityManager.NetworkCallback {
    private final ConnectivityManager connectivityManager;
    ForegroundService foregroundService;
    public boolean isNetWorkAvailable = true;
    public NetworkConnectionCallback(ConnectivityManager connectivityManager, ForegroundService foregroundService) {
        this.connectivityManager = connectivityManager;
        this.foregroundService = foregroundService;
    }

    @Override
    public void onAvailable(Network network) {
        super.onAvailable(network);
        // Handle network available
        Log.e("Network", "onAvailable: " );
      checkOnlineOrNot();
    }

    private void checkOnlineOrNot() {

        if (isOnline()) {
            // Internet is available
            isNetWorkAvailable = true;
            foregroundService.checkNeedToEndTrip();

        }else{
            isNetWorkAvailable = false;

            //Retry on Connection not available every 5 sec.
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    checkOnlineOrNot();
                }
            },5000);
        }

    }

    @Override
    public void onLost(Network network) {
        super.onLost(network);
        Log.e("Network", "onLost: " );
        // Handle network lost
        checkOnlineOrNot();

    }

    private boolean isInternetAvailable() {
        Network network = connectivityManager.getActiveNetwork();
        if (network == null) {
            return false;
        }
        NetworkCapabilities capabilities = connectivityManager.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);
    }

    public boolean isOnline() {
        if (connectivityManager != null) {
            NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
            if (activeNetwork != null && activeNetwork.isConnected()) {
                try {
                    // Make an HTTP request to verify internet connectivity
                    URL url = new URL("https://www.google.com");
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.setRequestProperty("User-Agent", "test");
                    urlConnection.setRequestProperty("Connection", "close");
                    urlConnection.setConnectTimeout(1000); // 1 second timeout
                    urlConnection.connect();
                    return (urlConnection.getResponseCode() == 200);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }
}

