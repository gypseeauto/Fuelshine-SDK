package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.BarGraphLayoutBinding;
import com.gypsee.sdk.models.DailyTripAlertCountModel;

public class WeeklyOverspeedCountAdapter extends RecyclerView.Adapter<WeeklyOverspeedCountAdapter.MyViewHolder> {


    private ArrayList<DailyTripAlertCountModel> dailyTripAlertCountModelArrayList;
    Context context;
    int maxHeight = 0;
    boolean isLeadFootSyndrome;

    public WeeklyOverspeedCountAdapter(ArrayList<DailyTripAlertCountModel> dailyTripAlertCountModelArrayList, Context context, boolean isLeadFootSyndrome) {
        this.dailyTripAlertCountModelArrayList = dailyTripAlertCountModelArrayList;
        this.context = context;
        this.isLeadFootSyndrome = isLeadFootSyndrome;

        for (DailyTripAlertCountModel dailyTripAlertCountModel :
                dailyTripAlertCountModelArrayList) {

            int currentHeight;
            if (isLeadFootSyndrome) {
                currentHeight = dailyTripAlertCountModel.getTotalHarshBreakingAlerts() + dailyTripAlertCountModel.getTotalHarshAccelerationAlerts() + dailyTripAlertCountModel.getTotalHighRPMAlerts();
            } else {
                currentHeight = dailyTripAlertCountModel.getTotalOverSpeedAlerts();

            }
            if (maxHeight < currentHeight) {
                maxHeight = currentHeight;
            }

        }
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        BarGraphLayoutBinding barGraphLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.bar_graph_layout, parent, false);
        return new MyViewHolder(barGraphLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        BarGraphLayoutBinding singleDayTripRowLayoutBinding = holder.barGraphLayoutBinding;

        DailyTripAlertCountModel dailyTripAlertCountModel = dailyTripAlertCountModelArrayList.get(position);


        if (isLeadFootSyndrome) {
            showVerticalLineView(singleDayTripRowLayoutBinding.harshBreakingLineView, dailyTripAlertCountModel.getTotalHarshBreakingAlerts(), singleDayTripRowLayoutBinding, position);
            showVerticalLineView(singleDayTripRowLayoutBinding.harshAccelarationLineView, dailyTripAlertCountModel.getTotalHarshAccelerationAlerts(), singleDayTripRowLayoutBinding, position);
            showVerticalLineView(singleDayTripRowLayoutBinding.highRPMLineView, dailyTripAlertCountModel.getTotalHighRPMAlerts(), singleDayTripRowLayoutBinding, position);
        } else {
            if (dailyTripAlertCountModel.getTotalOverSpeedAlerts() == 0) {
            }
            showVerticalLineView(singleDayTripRowLayoutBinding.harshBreakingLineView, dailyTripAlertCountModel.getTotalOverSpeedAlerts(), singleDayTripRowLayoutBinding, position);

        }

        if (isLeadFootSyndrome) {
            int totalAlerts = dailyTripAlertCountModel.getTotalHarshBreakingAlerts() + dailyTripAlertCountModel.getTotalHarshAccelerationAlerts() + dailyTripAlertCountModel.getTotalHighRPMAlerts();
            singleDayTripRowLayoutBinding.tickMarkIcon.setVisibility(totalAlerts == 0 ? View.VISIBLE : View.GONE);
        } else {

            singleDayTripRowLayoutBinding.tickMarkIcon.setVisibility(dailyTripAlertCountModel.getTotalOverSpeedAlerts() == 0 ? View.VISIBLE : View.GONE);

        }

        singleDayTripRowLayoutBinding.tickMarkIcon.setLayoutParams(new LinearLayout.LayoutParams(25, 25));


        //we will put the current day with text white and bold
        singleDayTripRowLayoutBinding.tvDay.setText(dailyTripAlertCountModel.getDayName());
        if (position == 0) {
            singleDayTripRowLayoutBinding.tvDay.setTypeface(null, Typeface.BOLD);
            singleDayTripRowLayoutBinding.tvDay.setTextColor(context.getResources().getColor(R.color.white));

        } else {
            singleDayTripRowLayoutBinding.tvDay.setTextColor(context.getResources().getColor(R.color.disabled_text_white));
            singleDayTripRowLayoutBinding.tvDay.setTypeface(null, Typeface.NORMAL);
        }

    }

    private void showVerticalLineView(View harshBreakingLineView, int totalOverSpeedAlerts, BarGraphLayoutBinding singleDayTripRowLayoutBinding, int position) {

        //We will put the width of the bar as 25 and height as 6 for each alert.
        // we will add on 6 pixels height to the bar  for each alert.
        //
        int height = 5;
        int normalHeight = 6;
        int width = 40;
        singleDayTripRowLayoutBinding.alertCoun.setText(totalOverSpeedAlerts + "");
        if (harshBreakingLineView.getId() == R.id.harshBreakingLineView) {
            harshBreakingLineView.setLayoutParams(new LinearLayout.LayoutParams(width, normalHeight + height * totalOverSpeedAlerts));
        } else {
            harshBreakingLineView.setVisibility(totalOverSpeedAlerts == 0 ? View.GONE : View.VISIBLE);
            harshBreakingLineView.setLayoutParams(new LinearLayout.LayoutParams(width, height * totalOverSpeedAlerts));
        }

        //If not a lead foot syndrome, we will not show alert count.
        if (!isLeadFootSyndrome) {
            singleDayTripRowLayoutBinding.alertCoun.setVisibility(totalOverSpeedAlerts == 0 ? View.GONE : View.VISIBLE);
        }


    }

    @Override
    public int getItemCount() {
        return dailyTripAlertCountModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        BarGraphLayoutBinding barGraphLayoutBinding;

        public MyViewHolder(@NonNull BarGraphLayoutBinding barGraphLayoutBinding) {
            super(barGraphLayoutBinding.getRoot());
            this.barGraphLayoutBinding = barGraphLayoutBinding;

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, 80 + maxHeight * 6);
            params.setMargins(0, 0, 20, 0);
            barGraphLayoutBinding.getRoot().setLayoutParams(params);

        }
    }

    String TAG = WeeklyOverspeedCountAdapter.class.getSimpleName();


}

    

