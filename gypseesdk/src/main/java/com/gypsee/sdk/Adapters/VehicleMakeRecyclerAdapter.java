package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;

import java.util.ArrayList;
import java.util.Locale;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.VehicleMakeRowLayoutBinding;
import com.gypsee.sdk.models.VehicleBrandImageModel;

public class VehicleMakeRecyclerAdapter extends RecyclerView.Adapter<VehicleMakeRecyclerAdapter.MyViewHolder> {
    private ArrayList<VehicleBrandImageModel> vehicleMakeArraylist;
    private ArrayList<VehicleBrandImageModel> vehicleMakeArraylistDuplicate = new ArrayList<>();

    private Context context;

    public VehicleMakeRecyclerAdapter(ArrayList<VehicleBrandImageModel> vehicleMakeArraylist, Context context) {
        this.vehicleMakeArraylist = vehicleMakeArraylist;

        vehicleMakeArraylistDuplicate.addAll(vehicleMakeArraylist);
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        VehicleMakeRowLayoutBinding viewScanItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(context),
                        R.layout.vehicle_make_row_layout, parent, false);
        return new MyViewHolder(viewScanItemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        VehicleBrandImageModel vehicleBrandImageModel = vehicleMakeArraylist.get(position);
        holder.vehicleMakeRowLayoutBinding.setBrandName(vehicleBrandImageModel.getBrand());

        if (vehicleBrandImageModel.getImage().equals(""))
        {
            holder.vehicleMakeRowLayoutBinding.setFirstLetter(vehicleMakeArraylist.get(position).getBrand().substring(0, 1));
            holder.vehicleMakeRowLayoutBinding.number.setVisibility(View.VISIBLE);
            holder.vehicleMakeRowLayoutBinding.brandImage.setVisibility(View.GONE);
        }
        else
        {
            holder.vehicleMakeRowLayoutBinding.number.setVisibility(View.GONE);
            holder.vehicleMakeRowLayoutBinding.brandImage.setVisibility(View.VISIBLE);
            Glide
                    .with(context)

                    .load(vehicleMakeArraylist.get(position).getImage())
                    .placeholder(context.getResources().getDrawable(R.mipmap.ic_launcher))
                    .addListener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            return false;
                        }
                    })
                    .centerInside()
                    .into(holder.vehicleMakeRowLayoutBinding.brandImage);
        }




    }

    @Override
    public int getItemCount() {
        return vehicleMakeArraylist.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        VehicleMakeRowLayoutBinding vehicleMakeRowLayoutBinding;


        private MyViewHolder(@NonNull VehicleMakeRowLayoutBinding viewScanItemBinding) {
            super(viewScanItemBinding.getRoot());
            this.vehicleMakeRowLayoutBinding = viewScanItemBinding;

        }
    }

    public void filter(String charText, TextView emptyLabel) {

        charText = charText.toLowerCase(Locale.getDefault());
        vehicleMakeArraylist.clear();
      Log.e("Size of the filter", String.valueOf(vehicleMakeArraylistDuplicate.size()));
        if (charText.equalsIgnoreCase("")) {
            vehicleMakeArraylist.addAll(vehicleMakeArraylistDuplicate);
        } else {
            for (VehicleBrandImageModel vehicleMake : vehicleMakeArraylistDuplicate) {
                if (vehicleMake.getBrand().toLowerCase(Locale.getDefault()).startsWith(charText)) {
                    vehicleMakeArraylist.add(vehicleMake);
                }
            }
        }

        emptyLabel.setVisibility(View.GONE);

        if (vehicleMakeArraylist.size() == 0) {
            emptyLabel.setText("There are no brands with the mentioned name - " + charText);
            emptyLabel.setVisibility(View.VISIBLE);
        }
        notifyDataSetChanged();
    }

}
