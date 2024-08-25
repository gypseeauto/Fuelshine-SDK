package com.gypsee.sdk.utils;

import android.location.Location;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.Vehiclemodel;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FleetSocketConnection{

    private String TAG = FleetSocketConnection.class.getSimpleName();
    private Socket socket;
    private User user;
    private Vehiclemodel selectedVehicleModel;
    private Location endLocation;
    private String url = "http://qa.gypsee.in:5500/";
    private FleetSocketConnection(){}

    public FleetSocketConnection(User user, Vehiclemodel selectedVehicleModel) throws Exception {
        this.user = user;
        this.selectedVehicleModel = selectedVehicleModel;
        attemptConnection(url);
    }

    private void attemptConnection(String url) throws URISyntaxException {

        Log.e(TAG, "Attempt socket connection");

        Map<String, List<String>> map = new HashMap<>();
        map.put("token", Arrays.asList(user.getUserAccessToken()));
        map.put("userId", Arrays.asList(user.getUserId()));
        map.put("vehicleId", Arrays.asList(selectedVehicleModel.getUserVehicleId()));
        map.put("type", Arrays.asList("SENDER"));

        IO.Options options = IO.Options.builder().build();
        options.extraHeaders = map;

        this.socket = IO.socket(url, options);
        this.socket.on("message", listener);
        this.socket.connect();
    }



    public boolean isConnected(){
        if (this.socket == null){
            Log.e(TAG, "socket null");
            return false;
        }
        return this.socket.connected();
    }


    public void setEndLocation(Location endLocation){
        this.endLocation = endLocation;
    }


    public void closeConnection(){
        if (this.socket == null){
            return;
        }
        if (timer != null){
            timer.cancel();
            timer.purge();
            timer = null;
        }
        this.socket.disconnect();
    }



    private Emitter.Listener listener = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            Log.e(TAG, "listner call: " + Arrays.toString(args));

            //echo: connection successful

            try {
                setLocationTask();
            } catch (JSONException e) {
                e.printStackTrace();
            }


        }
    };

    private Timer timer;

    private void setLocationTask() throws JSONException {

        timer = new Timer();

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                if (isConnected()){
                    sendLocationData();
                } else {
                    try {
                        attemptConnection(url);
                    } catch (URISyntaxException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, 100, 1000);


    }


    private void sendLocationData(){
        if (endLocation != null /*&& user != null */&& selectedVehicleModel != null){
            JSONObject jsonObject = new JSONObject();
            try {
                //jsonObject.put("lat", "13.0793928");
                //jsonObject.put("lng", "77.505849");
                jsonObject.put("lat", endLocation.getLatitude());
                jsonObject.put("lng", endLocation.getLongitude());
                jsonObject.put("speed", endLocation.getSpeed());
                jsonObject.put("bearing", endLocation.getBearing());
                jsonObject.put("timestamp", new Date().getTime());
                jsonObject.put("vehicleId", selectedVehicleModel.getUserVehicleId());
            } catch (JSONException e) {
                e.printStackTrace();
            }
            socket.send(jsonObject);
        }
    }





}
