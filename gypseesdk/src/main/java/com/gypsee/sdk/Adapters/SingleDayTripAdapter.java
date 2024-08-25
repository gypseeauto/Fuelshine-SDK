package com.gypsee.sdk.Adapters;

import android.content.Context;

import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.gypsee.sdk.R;

import com.gypsee.sdk.databinding.SingleDayTripRowLayoutBinding;
import com.gypsee.sdk.fragments.MapsActivity;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.models.GameLevelModel;
import com.gypsee.sdk.trips.TripRecord;

public class SingleDayTripAdapter extends RecyclerView.Adapter<SingleDayTripAdapter.MyViewHolder> {


    private List<TripRecord> tripRecords;
    Context context;
    private ArrayList<GameLevelModel> gamelevelArray;

    public SingleDayTripAdapter(List<TripRecord> tripRecords, Context context, ArrayList<GameLevelModel> gamelevelArray) {
        this.tripRecords = tripRecords;
        this.context = context;
        this.gamelevelArray = gamelevelArray;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        SingleDayTripRowLayoutBinding singleDayTripRowLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.single_day_trip_row_layout, parent, false);
        return new MyViewHolder(singleDayTripRowLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        SingleDayTripRowLayoutBinding singleDayTripRowLayoutBinding = holder.singleDayTripRowLayoutBinding;

        TripRecord tripRecord = tripRecords.get(position);

        Log.e("TripDate",tripRecord.toString());

        singleDayTripRowLayoutBinding.tripBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (tripRecord.getEndDate() == null) {
                    BluetoothHelperClass.showSinpleWarningDialog(context, ((AppCompatActivity) context).getLayoutInflater(), "Hello!", "Please check The trip is not completed yet.");
                } else {
                    Intent browserIntent = new Intent(context, MapsActivity.class);
                    browserIntent.putExtra(TripRecord.class.getSimpleName(), tripRecord);
                    browserIntent.putExtra("level", gamelevelArray);
                    context.startActivity(browserIntent);

                }
            }
        });


        Double distancecovered = BluetoothHelperClass.round(Double.valueOf(tripRecord.getDistanceCovered()), 2);


        String source = tripRecord.getStartLocationName().length() <= 60 ? tripRecord.getStartLocationName() : tripRecord.getStartLocationName().substring(0, 60);
        try {
            if (tripRecord.getEndDate() == null) {

                String[] sourceParts = source.split(", ");
                String sourceAddress = String.join(", ", Arrays.copyOfRange(sourceParts, 0, 2));

                singleDayTripRowLayoutBinding.sourceDestinationTv.setText(sourceAddress);
//                singleDayTripRowLayoutBinding.tripDescriptionTv.setText(TimeUtils.parseDatehm(tripRecord.getStartDate()) +
//                        " • Distance : " + distancecovered + "km" +
//                        " • Driving Alerts : " + tripRecord.getAlertCount());
                singleDayTripRowLayoutBinding.tripDescriptionTv.setText(
                        ""
                    );


                String dateString = tripRecord.getStartDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = dateFormat.parse(dateString);
                    dateFormat.applyPattern("MMMM dd");
                    String convertedDate = dateFormat.format(date);
                    singleDayTripRowLayoutBinding.date.setText(convertedDate);
                } catch (Exception e) {
                    Log.e("Error occurred: " , e.getMessage());
                }



               singleDayTripRowLayoutBinding.tripDistance.setText("");
               singleDayTripRowLayoutBinding.time.setText("");

            } else {
                String destination = tripRecord.getDestinationName().length() <= 60 ? tripRecord.getDestinationName() : tripRecord.getDestinationName().substring(0, 60);
                double mileage = tripRecord.getMileage();
                String mileageString = mileage > 0 ? " • Mileage : " + mileage + " kmpl" : "" ;

//                String address = "Spencers compound, FR8J+6CP, near apsrtc bustand, Kadapa, Andhra Pradesh 516001, India";
                String[] sourceParts = source.split(", ");
                String shortAddress = String.join(", ", Arrays.copyOfRange(sourceParts, 0, 2));

                String[] destinationParts = destination.split(", ");
                String destinationAddress = String.join(", ", Arrays.copyOfRange(destinationParts, 0, 2));



                Log.e("destination names",destinationAddress);
                String safeKm = String.valueOf(tripRecord.getTripSavedAmount());


//                singleDayTripRowLayoutBinding.sourceDestinationTv.setText(source + " ➥ " + destination);
                singleDayTripRowLayoutBinding.sourceDestinationTv.setText(shortAddress);
                singleDayTripRowLayoutBinding.tripDescriptionTv.setText(destinationAddress);
//                singleDayTripRowLayoutBinding.tripDescriptionTv.setText(tripRecords.get(position).getDestinationName());

                String dateString = tripRecord.getStartDate();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                try {
                    Date date = dateFormat.parse(dateString);
                    dateFormat.applyPattern("MMMM dd");
                    String convertedDate = dateFormat.format(date);
                    singleDayTripRowLayoutBinding.date.setText(convertedDate  + "/");
                } catch (Exception e) {
                    System.out.println("Error occurred: " + e.getMessage());
                }

                String tripTiming = String.valueOf(tripRecord.getTripDuration());
//                singleDayTripRowLayoutBinding.date.setText(TimeUtils.parseDatehm(tripRecord.getStartDate()) + "/");

                singleDayTripRowLayoutBinding.tripDistance.setText(distancecovered.toString() + "km");
//                singleDayTripRowLayoutBinding.time.setText(TimeUtils.getTimeIndhms(tripRecord.getTripDuration()*60));
                singleDayTripRowLayoutBinding.time.setText(tripTiming + " Min");

//                if (tripRecord.getSafeKm() < 0){
//                    singleDayTripRowLayoutBinding.data.setBackground(context.getResources().getDrawable(R.drawable.data_red));
//                    singleDayTripRowLayoutBinding.amount.setTextColor(context.getColor(R.color.white));
//                    singleDayTripRowLayoutBinding.amount.setText("+ ₹" + safeKm+".00");
//                }
//                else if (tripRecord.getSafeKm() == 0) {
//                    singleDayTripRowLayoutBinding.amount.setText("+ ₹" + safeKm+".00");
//                } else {
//                    singleDayTripRowLayoutBinding.amount.setText("+ ₹" + safeKm+".00");
//                }

                float amount = Float.parseFloat(tripRecord.getTripSavedAmount());

                if (amount < 0){
                    singleDayTripRowLayoutBinding.data.setBackground(context.getResources().getDrawable(R.drawable.data_red));
                    singleDayTripRowLayoutBinding.amount.setTextColor(context.getColor(R.color.white));
                    singleDayTripRowLayoutBinding.amount.setText(" ₹" + tripRecord.getTripSavedAmount() );
                }else if (amount == 0){
                    singleDayTripRowLayoutBinding.data.setBackground(context.getResources().getDrawable(R.drawable.data));
                    singleDayTripRowLayoutBinding.amount.setText("+ ₹" +  tripRecord.getTripSavedAmount() );
                }
                else {
                    singleDayTripRowLayoutBinding.data.setBackground(context.getResources().getDrawable(R.drawable.data));
                    singleDayTripRowLayoutBinding.amount.setText( "+ ₹" +  tripRecord.getTripSavedAmount() );
                }



//                singleDayTripRowLayoutBinding.tripDescriptionTv.setText(TimeUtils.parseDatehm(tripRecord.getStartDate()) +
//                        " • Distance : " + distancecovered + "km" +
//                        " • Trip duration : " + TimeUtils.getTimeIndhms(tripRecord.getTripDuration()*60) +
//                        " • Driving Alerts : " + tripRecord.getAlertCount()  +
//                        " • Safe Km : " + tripRecord.getSafeKm() + "Km" +
//                        mileageString
////                        " • Mileage : " + tripRecord.getMileage() + " kmpl"
//                );


//                        +" • Last Updated on: "+TimeUtils.parseDatehm(tripRecord.getLastUpdatedOn()));

            }
        } catch (/*ParseException e*/ Exception e) {
            e.printStackTrace();
        }

        //For the last item we are hiding the lineview
//        if (position == tripRecords.size() - 1)
//            singleDayTripRowLayoutBinding.lineView.setVisibility(View.GONE);

    }

    @Override
    public int getItemCount() {
        return tripRecords.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        SingleDayTripRowLayoutBinding singleDayTripRowLayoutBinding;

        public MyViewHolder(@NonNull SingleDayTripRowLayoutBinding singleDayTripRowLayoutBinding) {
            super(singleDayTripRowLayoutBinding.getRoot());
            this.singleDayTripRowLayoutBinding = singleDayTripRowLayoutBinding;
        }
    }

    String TAG = SingleDayTripAdapter.class.getSimpleName();


}

    

