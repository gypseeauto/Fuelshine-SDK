package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.DriveGoalsRowLayoutBinding;
import com.gypsee.sdk.models.DailyTripAlertCountModel;

public class DrivingGoalsRecyclerAdapter extends RecyclerView.Adapter<DrivingGoalsRecyclerAdapter.MyViewHolder> {


    private ArrayList<DailyTripAlertCountModel> dailyTripAlertCountModelArrayList;
    Context context;

    public DrivingGoalsRecyclerAdapter(ArrayList<DailyTripAlertCountModel> dailyTripAlertCountModelArrayList, Context context) {
        this.dailyTripAlertCountModelArrayList = dailyTripAlertCountModelArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DriveGoalsRowLayoutBinding singleDayTripRowLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.drive_goals_row_layout, parent, false);
        return new MyViewHolder(singleDayTripRowLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        DriveGoalsRowLayoutBinding singleDayTripRowLayoutBinding = holder.driveGoalsRowLayoutBinding;

        DailyTripAlertCountModel dailyTripAlertCountModel = dailyTripAlertCountModelArrayList.get(position);

        singleDayTripRowLayoutBinding.tvDay.setText(dailyTripAlertCountModel.getDayName());
        int totalAlertCount = dailyTripAlertCountModel.getTotalHarshAccelerationAlerts() + dailyTripAlertCountModel.getTotalHarshBreakingAlerts()
                + dailyTripAlertCountModel.getTotalHighRPMAlerts() + dailyTripAlertCountModel.getTotalOverSpeedAlerts();

        Drawable drawable;


        if (dailyTripAlertCountModel.isEnabled()) {
            if (dailyTripAlertCountModel.isDataLoading()) {
                //Need to toast the user, we are checking your data, please check after sometime.
                drawable = context.getDrawable(R.drawable.ic_alert_came_day);

            } else {
                //Show the same data.
                if (dailyTripAlertCountModel.getTotalTrips() == 0) {
                    drawable = context.getDrawable(R.drawable.ic_no_driving_day);

                } else if (totalAlertCount == 0) {
                    drawable = context.getDrawable(R.drawable.ic_safe_driving_day);
                } else {
                    drawable = context.getDrawable(R.drawable.ic_alert_came_day);
                }
            }
        } else {
            //it means trip is not created for that day.
            drawable = context.getDrawable(R.drawable.ic_no_driving_day);
        }
        singleDayTripRowLayoutBinding.goalsImage.setImageDrawable(drawable);

    }

    @Override
    public int getItemCount() {
        return dailyTripAlertCountModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        DriveGoalsRowLayoutBinding driveGoalsRowLayoutBinding;

        public MyViewHolder(@NonNull DriveGoalsRowLayoutBinding driveGoalsRowLayoutBinding) {
            super(driveGoalsRowLayoutBinding.getRoot());
            this.driveGoalsRowLayoutBinding = driveGoalsRowLayoutBinding;
        }
    }

    String TAG = DrivingGoalsRecyclerAdapter.class.getSimpleName();


}

    

