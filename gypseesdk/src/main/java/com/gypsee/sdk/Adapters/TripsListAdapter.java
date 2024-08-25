package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.TripItemLayoutBinding;
import com.gypsee.sdk.models.UserTrips;

public class TripsListAdapter extends RecyclerView.Adapter<TripsListAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<UserTrips> userTripsArrayList;

    public TripsListAdapter(Context context, ArrayList<UserTrips> offersModelArrayList) {
        this.context = context;
        this.userTripsArrayList = offersModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        TripItemLayoutBinding offerItemLayoutBinding =
                DataBindingUtil.inflate(LayoutInflater.from(context),
                        R.layout.trip_item_layout, parent, false);
        return new MyViewHolder(offerItemLayoutBinding);


    }

    private String TAG = TripsListAdapter.class.getSimpleName();

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        UserTrips userTrip = userTripsArrayList.get(position);
        holder.tripItemLayoutBinding.setUsertrip(userTrip);


    }

    @Override
    public int getItemCount() {
        return userTripsArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TripItemLayoutBinding tripItemLayoutBinding;

        private MyViewHolder(@NonNull TripItemLayoutBinding tripItemLayoutBinding) {
            super(tripItemLayoutBinding.getRoot());

            this.tripItemLayoutBinding = tripItemLayoutBinding;

        }
    }


}
