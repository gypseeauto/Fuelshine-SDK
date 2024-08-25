package com.gypsee.sdk.Adapters;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.CardSpeedLayoutBinding;
import com.gypsee.sdk.models.AlertsArray;


public class AlertsArrayAdapter extends RecyclerView.Adapter<AlertsArrayAdapter.MyViewHolder> {

    private ArrayList<AlertsArray> alertsArrayList;
    private Context context;

    public AlertsArrayAdapter(ArrayList<AlertsArray> alertsArrayList, Context context) {
        this.alertsArrayList = alertsArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        CardSpeedLayoutBinding cardSpeedLayoutBinding = CardSpeedLayoutBinding.inflate(layoutInflater, parent, false);
        return new MyViewHolder(cardSpeedLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
        if (!alertsArrayList.isEmpty()) {
            AlertsArray alertsArray = alertsArrayList.get(position);

            // Only inflate the layout if the alert name is not "AboveEcoSpeed", "BelowEcoSpeed", or "EcoSpeed"
            if (!isSpecialAlert(alertsArray.getAlertName())) {
                CardSpeedLayoutBinding cardSpeedLayoutBinding = holder.cardSpeedLayoutBinding;

                // Set appropriate data to the views based on the alert type
                switch (alertsArray.getAlertName()) {
                    case "Overspeed":
                        cardSpeedLayoutBinding.img.setBackground(context.getResources().getDrawable(R.drawable.fs_speeding_icon));
                        cardSpeedLayoutBinding.speedingText.setText(R.string.speeding_txt);
                        cardSpeedLayoutBinding.minutesOf.setText(R.string.minutes_of_speeding);
                        cardSpeedLayoutBinding.hoursOf.setText(R.string.hours_of_travel);
                        cardSpeedLayoutBinding.value1.setText(String.valueOf(alertsArray.getMinutesOfSpeeding()));
                        cardSpeedLayoutBinding.value2.setText(String.valueOf(alertsArray.getHoursOfTravelling()));
                        break;
                    case "Harsh Accelaration":
                        cardSpeedLayoutBinding.img.setBackground(context.getResources().getDrawable(R.drawable.fs_harsh_acc_icon));
                        cardSpeedLayoutBinding.speedingText.setText(R.string.harsh_acceleration);
                        cardSpeedLayoutBinding.minutesOf.setText(R.string.harsh_accelaration_events);
                        cardSpeedLayoutBinding.hoursOf.setText(R.string.kilometers_travelled);
                        cardSpeedLayoutBinding.value1.setText(String.valueOf(alertsArray.getEventsCount()));
                        cardSpeedLayoutBinding.value2.setText(String.valueOf(alertsArray.getKmsTravelled()));
                        break;
                    case "Harsh Braking":
                        cardSpeedLayoutBinding.img.setBackground(context.getResources().getDrawable(R.drawable.fs_harsh_brake_icon));
                        cardSpeedLayoutBinding.speedingText.setText(R.string.harsh_breaking);
                        cardSpeedLayoutBinding.minutesOf.setText(R.string.harsh_braking_events);
                        cardSpeedLayoutBinding.hoursOf.setText(R.string.kilometers_travelled);
                        cardSpeedLayoutBinding.value1.setText(String.valueOf(alertsArray.getEventsCount()));
                        cardSpeedLayoutBinding.value2.setText(String.valueOf(alertsArray.getKmsTravelled()));
                        break;
                    case "TextAndDrive":
                        cardSpeedLayoutBinding.img.setBackground(context.getResources().getDrawable(R.drawable.fs_text_driving_icon));
                        cardSpeedLayoutBinding.speedingText.setText(R.string.text_amp_driving);
                        cardSpeedLayoutBinding.minutesOf.setText(R.string.minutes_of_texting_while_driving);
                        cardSpeedLayoutBinding.hoursOf.setText(R.string.hours_of_travel);
                        cardSpeedLayoutBinding.value1.setText(String.valueOf(alertsArray.getMinutesOfSpeeding()));
                        cardSpeedLayoutBinding.value2.setText(String.valueOf(alertsArray.getHoursOfTravelling()));
                        break;
                    default:
                        // Handle other alert types if necessary
                        break;
                }

                holder.cardSpeedLayoutBinding.getRoot().setVisibility(View.VISIBLE);
                ViewGroup.LayoutParams params = holder.cardSpeedLayoutBinding.getRoot().getLayoutParams();
                params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
                holder.cardSpeedLayoutBinding.getRoot().setLayoutParams(params);
            } else {
                // Hide the layout if it's one of the special alert types
                holder.cardSpeedLayoutBinding.getRoot().setVisibility(View.GONE);
                ViewGroup.LayoutParams params = holder.cardSpeedLayoutBinding.getRoot().getLayoutParams();
                params.height = 0;
                holder.cardSpeedLayoutBinding.getRoot().setLayoutParams(params);
            }
        }
    }

    private boolean isSpecialAlert(String alertName) {
        return alertName.equals("AboveEcoSpeed") || alertName.equals("BelowEcoSpeed") || alertName.equals("EcoSpeed");
    }

    @Override
    public int getItemCount() {
        return alertsArrayList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {
        CardSpeedLayoutBinding cardSpeedLayoutBinding;

        public MyViewHolder(@NonNull CardSpeedLayoutBinding cardSpeedLayoutBinding) {
            super(cardSpeedLayoutBinding.getRoot());
            this.cardSpeedLayoutBinding = cardSpeedLayoutBinding;
        }
    }
}

