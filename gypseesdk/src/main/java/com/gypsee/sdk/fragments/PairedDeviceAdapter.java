package com.gypsee.sdk.fragments;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.models.BluetoothDeviceModel;


public class PairedDeviceAdapter extends ArrayAdapter<BluetoothDevice> {
    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    private Context mContext;
    private ArrayList<BluetoothDevice> mDevices;
    private BluetoothDevice mPairedDevice;
    private OnItemClickListener mListener; // Declare mListener variable
    private ArrayList<BluetoothDeviceModel> connectedDevice;

    public PairedDeviceAdapter(Context context, ArrayList<BluetoothDevice> devices, BluetoothDevice pairedDevice, OnItemClickListener listener, ArrayList<BluetoothDeviceModel> currentConnectedDevice) {
        super(context, R.layout.bluetooth_dropdown_item, devices);
        mContext = context;
        mDevices = devices;
        mPairedDevice = pairedDevice;
        mListener = listener;
        connectedDevice = currentConnectedDevice;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View itemView = convertView;
        if (itemView == null) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            itemView = inflater.inflate(R.layout.bluetooth_dropdown_item, parent, false);
        }

        ImageView deviceIcon = itemView.findViewById(R.id.device_icon);
        TextView deviceName = itemView.findViewById(R.id.device_name);
        RelativeLayout addDevice = itemView.findViewById(R.id.addDevice);

        BluetoothDevice device = mDevices.get(position);



        // Set device name
        if (position < connectedDevice.size()) {
            deviceName.setText(connectedDevice.get(position).getBluetoothName());
            if (connectedDevice.get(position).isNowConnected()) {
                deviceIcon.setImageResource(R.drawable.connected_bluetooth_icon);
            } else {
                deviceIcon.setImageResource(R.drawable.bluetooth_icon);
            }
        }

        addDevice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mListener != null) {
                    mListener.onItemClick(position);
                }
            }
        });

        return itemView;
    }


    private boolean isPaired(BluetoothDevice device) {
        return device.equals(mPairedDevice);
    }
}
