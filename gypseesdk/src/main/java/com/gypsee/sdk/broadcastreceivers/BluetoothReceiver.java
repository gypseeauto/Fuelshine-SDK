package com.gypsee.sdk.broadcastreceivers;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class BluetoothReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_CONNECTED)){
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BluetoothConnectionChange"));
        }
        if (intent.getAction().equals(BluetoothDevice.ACTION_ACL_DISCONNECTED)){
            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("BluetoothDisconnected"));
        }

    }
}
