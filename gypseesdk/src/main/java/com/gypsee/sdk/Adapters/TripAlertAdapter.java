package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import com.gypsee.sdk.R;
import com.gypsee.sdk.trips.TripAlert;

import org.joda.time.DateTimeComparator;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class TripAlertAdapter extends RecyclerView.Adapter<TripAlertAdapter.MyViewHolder> {

    private final List<TripAlert> records;
    Context context;

    public TripAlertAdapter(List<TripAlert> records, Context context) {
        this.records = records;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.drive_details_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        TripAlert record = records.get(position);
        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM");


        Date date1 = new Date(record.getTimeStamp());

        Date date2;
        holder.dateTime.setVisibility(View.GONE);
        if (position > 0) {
            TripAlert recordPre = records.get(position - 1);
            date2 = new Date(recordPre.getTimeStamp());
            int retVal = DateTimeComparator.getDateOnlyInstance().compare(date1, date2);
            if (retVal != 0) {
                holder.dateTime.setVisibility(View.VISIBLE);
                holder.dateTime.setText(format.format(Objects.requireNonNull(date1)));
            }
        }
        if (position == 0) {
            holder.dateTime.setVisibility(View.VISIBLE);
            holder.dateTime.setText(format.format(Objects.requireNonNull(date1)));
        }


        holder.title.setText(record.getAlertType());


        holder.desc.setText(new StringBuilder().append("Interval: ").append(record.getTimeInterval() + ", Value : ").append(record.getAlertValue()).toString()+ "\n"+String.format("Time : %s", new SimpleDateFormat("yyyy-MM-dd hh:mm").format(date1)));

        checkAlertType(record, holder);


    }

    private void checkAlertType(TripAlert record, MyViewHolder holder) {

        Drawable drawable = context.getDrawable(R.drawable.icon_high_rpm);
        switch (record.getAlertType()) {
            case "High RPM":
                break;

            case "Harsh Braking":
                drawable = context.getDrawable(R.drawable.icon_harsh_braking);
                break;
            case "Harsh Accelaration":
                drawable = context.getDrawable(R.drawable.icon_high_acceleration);
                break;
            case "Overspeed":
                drawable = context.getDrawable(R.drawable.icon_high_overspeed);
                break;
        }



        Glide
                .with(context)
                .load(drawable)
                .centerInside()
                .into(holder.alertImage);
    }

    String TAG = TripAlertAdapter.class.getSimpleName();

    @Override
    public int getItemCount() {
        return records.size();
    }


    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView title, desc,dateTime;
        ImageView alertImage;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            dateTime = itemView.findViewById(R.id.dateTime);
            desc = itemView.findViewById(R.id.description);
            // tvTimeStamp = itemView.findViewById(R.id.tv_time);
            alertImage = itemView.findViewById(R.id.alertImage);
        }
    }


}
