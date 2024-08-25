package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.Map;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.PerformanceRowLayoutBinding;
import com.gypsee.sdk.models.PerformanceModel;
import com.gypsee.sdk.models.VehicleHealthDataModel;

public class NewperformanceListAdapter extends RecyclerView.Adapter<NewperformanceListAdapter.MyViewHolder> {

    public ArrayList<PerformanceModel> performanceModelArrayList;
    Context context;
    Map<String, VehicleHealthDataModel> vehicleHealthDataModels;

    public NewperformanceListAdapter(ArrayList<PerformanceModel> performanceModelArrayList, Context context, Map<String, VehicleHealthDataModel> vehicleHealthDataModels) {
        this.performanceModelArrayList = performanceModelArrayList;
        this.context = context;
        this.vehicleHealthDataModels = vehicleHealthDataModels;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {


        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        PerformanceRowLayoutBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.performance_row_layout, parent, false);
        return new MyViewHolder(itemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {


        PerformanceRowLayoutBinding performanceRowLayoutBinding = holder.performanceRowLayoutBinding;

        PerformanceModel performanceModel = performanceModelArrayList.get(position);
        performanceRowLayoutBinding.descriptionTV.setText(performanceModel.getDescription());
        performanceRowLayoutBinding.titleTV.setText(performanceModel.getTitle());

        Glide
                .with(context)
                .load(performanceModel.getDrawable())
                .centerInside()
                .into(performanceRowLayoutBinding.performanceImage);


        /*if (circleImage.size() <= position){
            performanceRowLayoutBinding.circleImage.setBackground(ContextCompat.getDrawable(context, R.drawable.green_circle_drawable));
        } else {
            if (circleImage.get(position)) {
                performanceRowLayoutBinding.circleImage.setBackground(ContextCompat.getDrawable(context, R.drawable.green_circle_drawable));
            } else {
                performanceRowLayoutBinding.circleImage.setBackground(ContextCompat.getDrawable(context, R.drawable.red_circle));
            }
        }
*/

        VehicleHealthDataModel currentModel = null;
        if (performanceModel.getTitle().contains("Engine Idling : ")){
            currentModel = vehicleHealthDataModels.get("engineIdlingTime");
            setCircleDrawable(currentModel, performanceRowLayoutBinding);
        } else if (performanceModel.getTitle().contains("Battery: ")){
            currentModel = vehicleHealthDataModels.get("batteryVoltage");
            setCircleDrawable(currentModel, performanceRowLayoutBinding);
        } else if (performanceModel.getTitle().contains("Coolant: ")){
            currentModel = vehicleHealthDataModels.get("engineCoolantTemp");
            setCircleDrawable(currentModel, performanceRowLayoutBinding);
        } else if (performanceModel.getTitle().contains("Engine Load: ")){
            currentModel = vehicleHealthDataModels.get("engineLoad");
            setCircleDrawable(currentModel, performanceRowLayoutBinding);
        } else if (performanceModel.getTitle().contains("Engine : ")){
            currentModel = vehicleHealthDataModels.get("engine");
            setCircleDrawable(currentModel, performanceRowLayoutBinding);
        } else if (performanceModel.getTitle().contains("Emission : ")){
            currentModel = vehicleHealthDataModels.get("emission");
            setCircleDrawable(currentModel, performanceRowLayoutBinding);
        } else if (performanceModel.getTitle().contains("Transmission : ")){
            currentModel = vehicleHealthDataModels.get("transmission");
            setCircleDrawable(currentModel, performanceRowLayoutBinding);
        } else if (performanceModel.getTitle().contains("Control system : ")){
            currentModel = vehicleHealthDataModels.get("Control System");
            setCircleDrawable(currentModel, performanceRowLayoutBinding);
        } else if (performanceModel.getTitle().contains("Charging System : ")){
            currentModel = vehicleHealthDataModels.get("chargingSystem");
            setCircleDrawable(currentModel, performanceRowLayoutBinding);
        } else if (performanceModel.getTitle().contains("Engine Temperature : ")){
            currentModel = vehicleHealthDataModels.get("engineTemperature");
            setCircleDrawable(currentModel, performanceRowLayoutBinding);
        } else if (performanceModel.getTitle().contains("Oil Pressure : ")){
            currentModel = vehicleHealthDataModels.get("oilPressure");
            setCircleDrawable(currentModel, performanceRowLayoutBinding);
        }



    }

    private void setCircleDrawable(VehicleHealthDataModel currentModel, PerformanceRowLayoutBinding performanceRowLayoutBinding){
        if (currentModel != null){
            if (currentModel.getDataNotSupported()){
                performanceRowLayoutBinding.circleImage.setBackground(ContextCompat.getDrawable(context, R.drawable.orange_circle));
            } else if (currentModel.getError()){
                performanceRowLayoutBinding.circleImage.setBackground(ContextCompat.getDrawable(context, R.drawable.red_circle));
            } else {
                performanceRowLayoutBinding.circleImage.setBackground(ContextCompat.getDrawable(context, R.drawable.green_circle_drawable));
            }
        } else {
            performanceRowLayoutBinding.circleImage.setBackground(ContextCompat.getDrawable(context, R.drawable.orange_circle));
        }
    }

    @Override
    public int getItemCount() {
        return performanceModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        PerformanceRowLayoutBinding performanceRowLayoutBinding;

        public MyViewHolder(@NonNull PerformanceRowLayoutBinding performanceRowLayoutBinding) {
            super(performanceRowLayoutBinding.getRoot());
            this.performanceRowLayoutBinding = performanceRowLayoutBinding;

        }
    }

}

    

