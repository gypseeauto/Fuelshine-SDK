package com.gypsee.sdk.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.gypsee.sdk.R;

public class RTOInfoAdapter extends RecyclerView.Adapter<RTOInfoAdapter.ViewHolder>{

    String mvTaxUpto, vehicleClass, insurance, pollution, fitness, insuranceStatus;


    public RTOInfoAdapter(String mvTaxUpto,
                          String vehicleClass,
                          String insurance,
                          String pollution,
                          String fitness
    ){

        if(fitness.equalsIgnoreCase("null")){
            this.fitness = "";
        } else {
            this.fitness = fitness;
        }
        if(insurance.equalsIgnoreCase("null")){
            this.insurance = "";
            this.insuranceStatus = "";
        } else {
            this.insurance = insurance;
            Date date = getDateFromString(insurance);
            if(date == null){
                this.insuranceStatus = "";
            } else{
                this.insuranceStatus = getInsuranceStatus(date);
            }
        }
        if(pollution.equalsIgnoreCase("null")){
            this.pollution = "";
        } else {
            this.pollution = pollution;
        }
        if(mvTaxUpto.equalsIgnoreCase("null")){
            this.mvTaxUpto = "";
        } else {
            this.mvTaxUpto = mvTaxUpto;
        }
        if(vehicleClass.equalsIgnoreCase("null")){
            this.vehicleClass = "";
        } else {
            this.vehicleClass = vehicleClass;
        }

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.rto_info_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return  viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.valueEdit.setVisibility(View.GONE);

        switch (position){

            case 0:
                holder.heading.setText("Vehicle Class");
                if(vehicleClass.equals("")){
                    holder.value.setText("NA");
                } else {
                    holder.value.setText(vehicleClass);
                }

                holder.valueType.setVisibility(View.GONE);

                holder.mvTaxUptoView.setVisibility(View.VISIBLE);
                if(mvTaxUpto.equals("")){
                    holder.mvTaxUpto.setText("NA");
                } else {
                    holder.mvTaxUpto.setText(mvTaxUpto);
                }

                holder.action.setVisibility(View.GONE);
                break;

            case 1:
                holder.heading.setText("Expiry");
                holder.valueType.setVisibility(View.VISIBLE);
                holder.valueType.setText("Insurance");
                holder.value.setVisibility(View.VISIBLE);
                if(insurance.equals("")){
                    holder.value.setText("NA");
                } else {
                    holder.value.setText(insurance);
                }


                holder.mvTaxUptoView.setVisibility(View.GONE);
                holder.action.setVisibility(View.VISIBLE);
                holder.action.setText(insuranceStatus);

                break;

            case 2:
                holder.heading.setText("Expiry");
                holder.valueType.setVisibility(View.VISIBLE);
                holder.valueType.setText("Pollution");
                holder.value.setVisibility(View.VISIBLE);
                if(pollution.equals("")){
                    holder.value.setText("NA");
                } else {
                    holder.value.setText(pollution);
                }


                holder.mvTaxUptoView.setVisibility(View.GONE);
                holder.action.setVisibility(View.VISIBLE);
                holder.action.setText("Add Expiry Date");

                break;

            case 3:
                holder.heading.setText("Expiry");
                holder.valueType.setVisibility(View.VISIBLE);
                holder.valueType.setText("Fitness");
                holder.value.setVisibility(View.VISIBLE);
                if(fitness.equals("")){
                    holder.value.setText("NA");
                } else {
                    holder.value.setText(fitness);
                }


                holder.mvTaxUptoView.setVisibility(View.GONE);
                holder.action.setVisibility(View.VISIBLE);
                holder.action.setText("Add Expiry Date");
                break;


        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    private Date getDateFromString(String date){
        try {
            return new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String getInsuranceStatus(Date date){
        Calendar calendar = Calendar.getInstance();
        Date now = calendar.getTime();
        if (now.after(date)){
            return "Add Expiry Date";
        } else return "Valid";
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView heading, valueType, value, mvTaxUpto, action;
        LinearLayout mvTaxUptoView; //rc validity is replaced with mv_tax_utpo. rename when necessary
        View itemView;
        EditText valueEdit;

        public ViewHolder(View itemView) {
            super(itemView);
            heading = itemView.findViewById(R.id.rto_heading);
            valueType = itemView.findViewById(R.id.rto_valuetype);
            value = itemView.findViewById(R.id.rto_value);
            mvTaxUpto = itemView.findViewById(R.id.mv_tax_upto);
            action = itemView.findViewById(R.id.rto_action);
            mvTaxUptoView = itemView.findViewById(R.id.mv_tax_upto_view);
            valueEdit = itemView.findViewById(R.id.value_edit);

            this.itemView = itemView;


        }



    }


}
