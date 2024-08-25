package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.DrivingTipLayoutBinding;
import com.gypsee.sdk.databinding.EmergencyRowlayoutBinding;
import com.gypsee.sdk.models.KeyvalueModelClass;

public class EmergencyContactsRecyclerAdapter extends RecyclerView.Adapter<EmergencyContactsRecyclerAdapter.ViewHolder> {

    private Context mContext;
   String TAG = EmergencyContactsRecyclerAdapter.class.getSimpleName();

    ArrayList<KeyvalueModelClass> keyvalueModelClasses;

    public EmergencyContactsRecyclerAdapter(Context mContext, ArrayList<KeyvalueModelClass> keyvalueModelClasses) {
        this.mContext = mContext;
        this.keyvalueModelClasses = keyvalueModelClasses;
        Log.e(TAG, "Size of recycler Adapter : "+ keyvalueModelClasses.size() );
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        EmergencyRowlayoutBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.emergency_rowlayout, parent, false);
        return new ViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.emergencyRowlayoutBinding.setTitle(keyvalueModelClasses.get(position).getKey());
        holder.emergencyRowlayoutBinding.setNumber(keyvalueModelClasses.get(position).getDescription());

        Glide
                .with(mContext)
                .load(keyvalueModelClasses.get(position).getValue())
                .centerInside()
                .into( holder.emergencyRowlayoutBinding.emergencyIcon);

        holder.emergencyRowlayoutBinding.getRoot().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // do whatever
                //Send brand name back
                Intent intent = new Intent(Intent.ACTION_DIAL);
                intent.setData(Uri.parse("tel:" + keyvalueModelClasses.get(position).getDescription()));
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return keyvalueModelClasses.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public EmergencyRowlayoutBinding emergencyRowlayoutBinding;

        public ViewHolder(EmergencyRowlayoutBinding drivingTipLayoutBinding) {
            super(drivingTipLayoutBinding.getRoot());
            this.emergencyRowlayoutBinding = drivingTipLayoutBinding;

        }
    }

}
