package com.gypsee.sdk.interfaces;

import android.bluetooth.BluetoothSocket;

public interface BlueToothConnectionInterface {
   void onBluetoothConnected(BluetoothSocket bluetoothSocket);
   void obBluetoothDisconnected();

}
