package com.gypsee.sdk.demoaccount.adapters;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import com.gypsee.sdk.R;
import com.gypsee.sdk.demoaccount.activities.DemoMapsActivity;
import com.gypsee.sdk.trips.TripRecord;
import com.gypsee.sdk.utils.RecyclerItemClickListener;

public class DemoTripListAdapter extends RecyclerView.Adapter<DemoTripListAdapter.MyViewHolder> {

    private List<TripRecord> tripRecords;

    String TAG = DemoTripListAdapter.class.getName();

    Context context;

    public DemoTripListAdapter(List<TripRecord> tripRecords, Context context) {
        this.tripRecords = tripRecords;
        this.context = context;
        parseRecords();
    }

    ArrayList<ArrayList<TripRecord>> recordsInnerList = new ArrayList<>();

    //THis is for making recyclerview inside another recyclerview
    private void parseRecords() {
        ArrayList<TripRecord> records = new ArrayList<>();
        Log.e(TAG, "Records size : " + tripRecords.size());

        for (int i = 0; i < tripRecords.size(); i++) {
            TripRecord tripRecord = tripRecords.get(i);

            if (i == 0 || tripRecord.getStartDate().toString().substring(0, 10).equals(tripRecords.get(i - 1).getStartDate().toString().substring(0, 10))) {
                records.add(tripRecord);
            } else {
                recordsInnerList.add(records);
                Log.e(TAG, "Size of the list is : " + recordsInnerList.size());
                records = new ArrayList<>();
                records.add(tripRecord);

            }

            Log.e(TAG, "Outside Size of the list is : " + records.size());

        }
        recordsInnerList.add(records);

        Log.e(TAG, "Size of the inner list is : " + recordsInnerList.size());
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.view_trip_log, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        final ArrayList<TripRecord> records = recordsInnerList.get(position);

        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM");

        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(records.get(0).getStartDate());
            holder.dateTime.setVisibility(View.VISIBLE);
            holder.dateTime.setText(format.format(Objects.requireNonNull(date1)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "OnBind size : " + records.size());

//        holder.recyclerView.setAdapter(new SingleDayTripAdapter(records, context));


        holder.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, holder.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        //Send brand name back

                            Intent browserIntent = new Intent(context, DemoMapsActivity.class);
                            browserIntent.putExtra(TripRecord.class.getSimpleName(), records.get(position));
                            //browserIntent.putExtra(DemoTripListAdapter.class.getName(), records.);

                            context.startActivity(browserIntent);

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

    }

    @Override
    public int getItemCount() {

        return recordsInnerList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView dateTime;
        RecyclerView recyclerView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            dateTime = itemView.findViewById(R.id.tv_date);
            recyclerView = itemView.findViewById(R.id.singleDayTripsRecyclerview);

        }
    }



}
