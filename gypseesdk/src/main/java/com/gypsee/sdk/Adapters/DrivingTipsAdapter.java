package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.DrivingTipLayoutBinding;
import com.gypsee.sdk.databinding.FaultCodeDescriptionRowLayoutBinding;
import com.gypsee.sdk.models.DrivingTipModel;

public class DrivingTipsAdapter extends RecyclerView.Adapter<DrivingTipsAdapter.ViewHolder> {

    private Context mContext;
    String date;
    String slipNumber;

    ArrayList<DrivingTipModel> drivingTipModels;

    public DrivingTipsAdapter(Context mContext, ArrayList<DrivingTipModel> drivingTipModels) {
        this.mContext = mContext;
        this.drivingTipModels = drivingTipModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        DrivingTipLayoutBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.driving_tip_layout, parent, false);
        return new ViewHolder(itemBinding);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.drivingTipLayoutBinding.tipTitleTv.setText(drivingTipModels.get(position).getTitle());
        holder.drivingTipLayoutBinding.tipDecription.setText(drivingTipModels.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return drivingTipModels.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public DrivingTipLayoutBinding drivingTipLayoutBinding;

        public ViewHolder(DrivingTipLayoutBinding drivingTipLayoutBinding) {
            super(drivingTipLayoutBinding.getRoot());
            this.drivingTipLayoutBinding = drivingTipLayoutBinding;

        }
    }

}
