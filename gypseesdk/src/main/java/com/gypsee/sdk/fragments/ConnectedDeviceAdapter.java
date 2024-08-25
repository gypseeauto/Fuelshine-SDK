package com.gypsee.sdk.fragments;


import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ArrayAdapter;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.models.BluetoothDeviceModel;

public class ConnectedDeviceAdapter extends ArrayAdapter<BluetoothDeviceModel> {


    private ArrayList<BluetoothDeviceModel> connectedDevices;
    private LayoutInflater inflater;
    private Context context;

    public ConnectedDeviceAdapter(Context context, ArrayList<BluetoothDeviceModel> connectedDevices) {
        super(context, 0, connectedDevices);
        this.connectedDevices = connectedDevices;
        this.inflater = LayoutInflater.from(context);
        this.context = context;
    }

    @Override
    public int getCount() {
        return connectedDevices.size();
    }

    @Override
    public BluetoothDeviceModel getItem(int position) {
        return connectedDevices.get(position);
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.bluetooth_dropdown_item, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.deviceNameTextView = convertView.findViewById(R.id.device_name);
            viewHolder.connectedImg = convertView.findViewById(R.id.device_icon);
            viewHolder.device = convertView.findViewById(R.id.addDevice);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        BluetoothDeviceModel device = connectedDevices.get(position);
        viewHolder.deviceNameTextView.setText(device.getDeviceName());

        viewHolder.device.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Settings.ACTION_BLUETOOTH_SETTINGS);
                context.startActivity(intent);
            }
        });

        // Check if the device at the current position is connected
        if (connectedDevices.get(position).isNowConnected()) {

            // If the device is connected, display the connected icon
            viewHolder.connectedImg.setImageResource(R.drawable.current_linked_device);
        } else {
            // If the device is not connected, display the default icon
            viewHolder.connectedImg.setImageResource(R.drawable.link_bluetooth);
        }

        return convertView;
    }


    private static class ViewHolder {
        TextView deviceNameTextView;
        ImageView connectedImg;
        RelativeLayout device;
    }
}
