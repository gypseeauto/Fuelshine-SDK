package com.gypsee.sdk.threads;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.UUID;

import com.gypsee.sdk.interfaces.BlueToothConnectionInterface;

public class ConnectThread extends Thread {
    private BluetoothSocket mmSocket;
    private final BluetoothDevice mmDevice;
    BlueToothConnectionInterface blueToothConnectionInterface;
    Context context;

    String TAG = ConnectThread.class.getSimpleName();
    private static final UUID MY_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");

    public ConnectThread(BluetoothDevice device, BlueToothConnectionInterface blueToothConnectionInterface, Context context) {
        // Use a temporary object that is later assigned to mmSocket
        // because mmSocket is final.
        BluetoothSocket tmp = null;
        mmDevice = device;
        this.blueToothConnectionInterface = blueToothConnectionInterface;
        this.context = context;

        try {
            // Get a BluetoothSocket to connect with the given BluetoothDevice.
            // MY_UUID is the app's UUID string, also used in the server code.
            tmp = device.createRfcommSocketToServiceRecord(MY_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Socket's create() method failed", e);
        }
        mmSocket = tmp;
    }

    public void run() {

        //Cancel discovery will speed up the connection.
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        btAdapter.cancelDiscovery();

        try {
            // Connect to the remote device through the socket. This call blocks
            // until it succeeds or throws an exception.
            mmSocket.connect();
            intimateOnMainThread(true);

        } catch (IOException connectException) {

            // Unable to connect; close the socket and return.
            Class<?> clazz = mmSocket.getRemoteDevice().getClass();
            Class<?>[] paramTypes = new Class<?>[]{Integer.TYPE};
            try {
                Method m = clazz.getMethod("createRfcommSocket", paramTypes);
                Object[] params = new Object[]{Integer.valueOf(1)};
                BluetoothSocket sockFallback = (BluetoothSocket) m.invoke(mmSocket.getRemoteDevice(), params);
                sockFallback.connect();
                mmSocket = sockFallback;
                intimateOnMainThread(true);
            } catch (Exception e2) {
                Log.e(TAG, "Couldn't fallback while establishing Bluetooth connection.", e2);
                e2.printStackTrace();
                try {
                    mmSocket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                intimateOnMainThread(false);

            }
        }


    }

    private void intimateOnMainThread(final boolean isConnected) {


        // TO exceute on the main thread, we need to use context. getMainLooper

        // Get a handler that can be used to post to the main thread
        Handler mainHandler = new Handler(context.getMainLooper());

        Runnable myRunnable = new Runnable() {
            @Override
            public void run() {

                if (isConnected) {
                    blueToothConnectionInterface.onBluetoothConnected(mmSocket);

                } else {
                    blueToothConnectionInterface.obBluetoothDisconnected();

                }
            } // This is your code
        };
        mainHandler.post(myRunnable);


    }

    // Closes the client socket and causes the thread to finish.
    public void cancel() {
        try {
            mmSocket.close();
        } catch (IOException e) {
            Log.e(TAG, "Could not close the client socket", e);
        }
    }
}