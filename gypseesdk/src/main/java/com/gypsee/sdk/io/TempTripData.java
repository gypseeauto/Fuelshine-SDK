package com.gypsee.sdk.io;

import android.content.Context;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;


public class TempTripData {

    private String TAG = TempTripData.class.getName();
    private Context context;
    private static TempTripData tempTripData;

    /*
    * JSON structure:
    * {
    *   tripID: String,
    *   data: [
    *       {
    *           latitude: double
    *           longitude: double
    *           createdOn: String
    *       }
    *   ]
    * */



    private TempTripData(){}

    private TempTripData(Context context){
        this.context = context;
    }

    public static TempTripData getInstance(Context context){
        if (tempTripData != null){
            return tempTripData;
        }
        tempTripData = new TempTripData(context);
        return tempTripData;
    }

    private String getFileName(){
        String suffix;
        suffix = "-prod";

        return "gypsee-temp" + suffix + ".json";
    }


    public void writeFile(JSONObject jsonObject) throws IOException {

        File file = new File(this.context.getFilesDir(), getFileName());
        if (file.createNewFile()){
            Log.e(TAG, "File created!");
        } else {
            Log.e(TAG, "File already exist");
        }

        FileWriter fileWriter = new FileWriter(file, false);
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(jsonObject.toString());
        bufferedWriter.close();
        fileWriter.close();
    }

    public JSONObject readFile() throws IOException, JSONException {

        File file = new File(this.context.getFilesDir(), getFileName());
        if (file.createNewFile()){
            Log.e(TAG, "New empty file created for read operation.");
            return new JSONObject();
        }
        FileReader fileReader = new FileReader(file);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        StringBuilder stringBuilder = new StringBuilder();
        String line;

        while ((line = bufferedReader.readLine()) != null){
            stringBuilder.append(line);
        }
        bufferedReader.close();
        fileReader.close();

        return new JSONObject(stringBuilder.toString());

    }

    public void deleteFile(){

        File file = new File(this.context.getFilesDir(), getFileName());
        if (file.exists()){
            Log.e(TAG, "gps file exist");
            if (file.delete()){
                Log.e(TAG, "GPS File deleted");
            } else{
                Log.e(TAG, "GPS file not deleted");
            }
        } else {
            Log.e(TAG, "GPS file not exist");
        }

    }


}
