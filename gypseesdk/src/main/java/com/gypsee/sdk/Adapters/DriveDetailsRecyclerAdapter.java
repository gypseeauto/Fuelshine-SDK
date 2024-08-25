package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.models.DriveDetailsModelClass;

public class DriveDetailsRecyclerAdapter extends RecyclerView.Adapter<DriveDetailsRecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<DriveDetailsModelClass> driveDetailsModelClasses;

    public DriveDetailsRecyclerAdapter(Context context, ArrayList<DriveDetailsModelClass> driveDetailsModelClasses) {
        this.context = context;
        this.driveDetailsModelClasses = driveDetailsModelClasses;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.drive_details_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    private String TAG = DriveDetailsRecyclerAdapter.class.getSimpleName();

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        DriveDetailsModelClass driveDetailsModelClass = driveDetailsModelClasses.get(position);
        holder.title.setText(driveDetailsModelClass.getTitle());
        holder.desc.setText(driveDetailsModelClass.getDescription());
        Log.e(TAG, " Position : " + position);

        Glide
                .with(context)
                .load(driveDetailsModelClass.getDrawable())
                .centerInside()
                .into(holder.alertImage);


    }

    @Override
    public int getItemCount() {
        return driveDetailsModelClasses.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView title, desc;
        ImageView alertImage;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            desc = itemView.findViewById(R.id.description);
            alertImage = itemView.findViewById(R.id.alertImage);
        }
    }


}
