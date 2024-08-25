package com.gypsee.sdk.Adapters;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.models.BluetoothDeviceModel;

public class MyDevicesAdapter extends RecyclerView.Adapter<MyDevicesAdapter.ViewHolder>{

    ArrayList<BluetoothDeviceModel> deviceList = new ArrayList<>();
    String TAG = getClass().getSimpleName();

    public MyDevicesAdapter(ArrayList<BluetoothDeviceModel> deviceList){
        this.deviceList = deviceList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.device_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (position == deviceList.size()){ //change to last position
            holder.imageView.setVisibility(View.GONE);
            holder.textLayout.setVisibility(View.GONE);
            holder.addDeviceLayout.setVisibility(View.VISIBLE);
            holder.greenDot.setVisibility(View.GONE);
            holder.deviceName.setVisibility(View.GONE);
        } else {
            Log.e(TAG, "green dot: " + position + " " +deviceList.get(position).isNowConnected());
            if (deviceList.get(position).isNowConnected()){
                holder.greenDot.setVisibility(View.VISIBLE);
            } else {
                holder.greenDot.setVisibility(View.INVISIBLE);
            }
            holder.deviceName.setVisibility(View.VISIBLE);
            holder.imageView.setVisibility(View.VISIBLE);
            holder.textLayout.setVisibility(View.VISIBLE);
            holder.addDeviceLayout.setVisibility(View.GONE);
            holder.title.setText(deviceList.get(position).getDeviceName());
            holder.deviceName.setText(deviceList.get(position).getBluetoothName());
            holder.subTitle.setText("Last Connected\n" + deviceList.get(position).getLastConnectedTime());
            Picasso.get()
                    .load(deviceList.get(position).getImageUrl())
                    .placeholder(R.drawable.gypsee_theme_logo)
                    .into(holder.imageView);
        }
    }

    @Override
    public int getItemCount() {
        return deviceList.size() + 1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        TextView title, subTitle, deviceName;
        LinearLayout textLayout, addDeviceLayout;
        View greenDot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.device_image);
            title = itemView.findViewById(R.id.device_title);
            subTitle = itemView.findViewById(R.id.device_subtitle);
            textLayout = itemView.findViewById(R.id.device_info_layout);
            addDeviceLayout = itemView.findViewById(R.id.device_add_layout);
            greenDot = itemView.findViewById(R.id.device_online_dot);
            deviceName = itemView.findViewById(R.id.device_name);
        }
    }

}
