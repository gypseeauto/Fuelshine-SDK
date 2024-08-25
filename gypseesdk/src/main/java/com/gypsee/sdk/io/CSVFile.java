package com.gypsee.sdk.io;

import android.os.Environment;
import android.util.Log;

import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CSVFile {
    private static final String TAG = CSVFile.class.getSimpleName();
    InputStream inputStream;

    public CSVFile(InputStream inputStream) {
        this.inputStream = inputStream;
    }

    public List read() {
        List resultList = new ArrayList();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        try {
            String csvLine;
            while ((csvLine = reader.readLine()) != null) {
                String[] row = csvLine.split(",");
                resultList.add(row);
              Log.e("VariableTag", row[0].toString());
            }
        } catch (IOException ex) {
            throw new RuntimeException("Error in reading CSV file: " + ex);
        } finally {
            try {
                inputStream.close();
            } catch (IOException e) {
                throw new RuntimeException("Error while closing input stream: " + e);
            }
        }
        return resultList;
    }

    public static void prepArray(String fileName) {

        // /storage/emulated/0/GypseeVehicleLogs/Log_28_05_2020_15_45_46.csv

        int i = 0;
        try {

          Log.e(TAG, "file path is : " + Environment.getExternalStorageDirectory().getAbsolutePath() + "/GypseeVehicleLogs/" + fileName);
            File csvfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/GypseeVehicleLogs/" + fileName);
            CSVReader reader = new CSVReader(new FileReader(csvfile));
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                // nextLine[] is an array of values from the line
                System.out.println(nextLine.length);
                if (nextLine.length > 10) {
                  Log.e(TAG, i++ + "First string :" + nextLine[0]);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
          Log.e(TAG, "The specified file was not found");

        }
    }

}
