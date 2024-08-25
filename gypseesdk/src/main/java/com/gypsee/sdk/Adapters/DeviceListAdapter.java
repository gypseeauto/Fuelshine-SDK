package com.gypsee.sdk.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.models.BluetoothDeviceModel;

public class DeviceListAdapter extends RecyclerView.Adapter<DeviceListAdapter.ViewHolder>{

    ArrayList<BluetoothDeviceModel> devices;
    String TAG = getClass().getSimpleName();

    public DeviceListAdapter(ArrayList<BluetoothDeviceModel> devices){
        this.devices = devices;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.select_device_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Picasso.get()
                .load(devices.get(position).getImageUrl())
                .placeholder(R.drawable.gypsee_theme_logo)
                .into(holder.imageView);
        holder.textView.setText(devices.get(position).getDeviceName());
//        Log.e(TAG, devices.get(position).getDeviceName() + "\n" +devices.get(position).getImageUrl());
    }

    @Override
    public int getItemCount() {
        return devices.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.device_select_image);
            textView = itemView.findViewById(R.id.device_select_title);
        }
    }

}
