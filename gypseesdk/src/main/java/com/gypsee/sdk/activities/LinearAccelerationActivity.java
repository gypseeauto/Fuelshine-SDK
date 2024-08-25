package com.gypsee.sdk.activities;

import android.os.Bundle;

import com.gypsee.sdk.databinding.ActivityLinearAccelerationBinding;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class LinearAccelerationActivity extends Activity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor linearAccelerationSensor;

    private boolean isCalibrating = true;
    private float[] calibrationOffsets = {0.0f, 0.0f, 0.0f};

    // Set your threshold values for harsh acceleration and deceleration in m/s²
    private float harshAccelerationThreshold = 5.0f;
    private float harshDecelerationThreshold = -5.0f;

    // Calibration duration in milliseconds (e.g., 5000 milliseconds or 5 seconds)
    private long calibrationDuration = 5000;

    ActivityLinearAccelerationBinding activityLinearAccelerationBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activityLinearAccelerationBinding = ActivityLinearAccelerationBinding.inflate(getLayoutInflater());

        setContentView(activityLinearAccelerationBinding.getRoot());


        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        linearAccelerationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION);

        calibrateLinearAcceleration();
    }

    private void calibrateLinearAcceleration() {
        sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Delay for calibration duration
        new android.os.Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                sensorManager.unregisterListener(LinearAccelerationActivity.this);

                // Calculate average offsets
                for (int i = 0; i < calibrationOffsets.length; i++) {
                    calibrationOffsets[i] /= calibrationDuration / 1000; // Convert to seconds
                }

                // Calibration complete, start monitoring linear acceleration
                isCalibrating = false;
                detectHarshAccelerationAndDeceleration();
            }
        }, calibrationDuration);
    }

    private void detectHarshAccelerationAndDeceleration() {
        sensorManager.registerListener(this, linearAccelerationSensor, SensorManager.SENSOR_DELAY_NORMAL);

        // Implement logic for detecting harsh acceleration and deceleration
        // ...
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {
            if (isCalibrating) {
                // Accumulate accelerometer values during calibration
                for (int i = 0; i < event.values.length; i++) {
                    calibrationOffsets[i] += event.values[i];
                }
            } else {
                // Subtract calibrated offsets to get accurate linear acceleration
                float linearAccelerationX = event.values[0] - calibrationOffsets[0];
                float linearAccelerationY = event.values[1] - calibrationOffsets[1];
                float linearAccelerationZ = event.values[2] - calibrationOffsets[2];

                // Calculate the magnitude of linear acceleration
                float linearAccelerationMagnitude =
                        (float) Math.sqrt(linearAccelerationX * linearAccelerationX +
                                linearAccelerationY * linearAccelerationY +
                                linearAccelerationZ * linearAccelerationZ);

                // Detect harsh acceleration
                if (linearAccelerationMagnitude > harshAccelerationThreshold) {
                    // You can take further actions here.
                    activityLinearAccelerationBinding.accelarationvalue.setText("Harsh Accelation :\n"+linearAccelerationMagnitude);
                }

                // Detect harsh deceleration
                if (linearAccelerationMagnitude < harshDecelerationThreshold) {
                    // You can take further actions here.
                    activityLinearAccelerationBinding.accelarationvalue.setText("Harsh Deceleration :\n"+linearAccelerationMagnitude);

                }
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // Handle accuracy changes if needed
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the sensor listener to avoid memory leaks
        sensorManager.unregisterListener(this);
    }
}