package com.gypsee.sdk.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gypsee.sdk.R;

public class BasicDetailListAdapter extends RecyclerView.Adapter<BasicDetailListAdapter.ViewHolder> {

    String customerName, vehicleRegNo, vehicleMakeModel;
    String[] basicDetailHeadings = {"Customer Name", "Vehicle Registration Number", "Vehicle Make & Model"};

    public BasicDetailListAdapter(String customerName, String vehicleRegNo, String vehicleMakeModel){
        if(customerName == null){
            this.customerName = "";
        } else {
            this.customerName = customerName;
        }
        if(vehicleRegNo == null){
            this.vehicleRegNo = "";
        } else{
            this.vehicleRegNo = vehicleRegNo;
        }
        if (vehicleMakeModel == null){
            this.vehicleMakeModel = "";
        }else{
            this.vehicleMakeModel = vehicleMakeModel;
        }
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.detail_list_item, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {


        switch (position){

            case 0:
                holder.heading.setText("Customer Name");
                holder.value.setText(customerName);
                break;

            case 1:
                holder.heading.setText("Vehicle Registration Number");
                holder.value.setText(vehicleRegNo);
                break;

            case 2:
                holder.heading.setText("Vehicle Make & Model");
                holder.value.setText(vehicleMakeModel);
                break;

        }

    }

    @Override
    public int getItemCount() {
        return basicDetailHeadings.length;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView heading;
        public TextView value;
        public ViewHolder(View itemView) {
            super(itemView);

            this.heading = itemView.findViewById(R.id.detail_item_heading);
            this.value = itemView.findViewById(R.id.detail_item_detail);
        }
    }


}
