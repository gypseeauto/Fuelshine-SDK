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

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.SecondActivity;
import com.gypsee.sdk.databinding.ViewScanItemBinding;
import com.gypsee.sdk.models.DtcModelClass;

import java.util.ArrayList;

public class DTCAdapter extends RecyclerView.Adapter<DTCAdapter.MyViewHolder> {
    private ArrayList<DtcModelClass> dtcModelClassArrayList;

    private Context context;

    public DTCAdapter(ArrayList<DtcModelClass> dtcModelClassArrayList, Context context) {
        this.dtcModelClassArrayList = dtcModelClassArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        ViewScanItemBinding viewScanItemBinding =
                DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()),
                        R.layout.view_scan_item, parent, false);
        return new MyViewHolder(viewScanItemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {
      Log.e("DTC Adapter", "Called DTC");
        holder.viewScanItemBinding.setDtcModelClass(dtcModelClassArrayList.get(position));
        if (position == (dtcModelClassArrayList.size() - 1)) {
            holder.viewScanItemBinding.lineDivider.setVisibility(View.GONE);
        } else {
            holder.viewScanItemBinding.lineDivider.setVisibility(View.GONE);

        }

        holder.viewScanItemBinding.descriptionTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTroubleCodeDescriptionPage(position);
            }
        });
        holder.viewScanItemBinding.troubleCodeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoTroubleCodeDescriptionPage(position);

            }
        });

    }

    private void gotoTroubleCodeDescriptionPage(int position) {


        ((AppCompatActivity) context).startActivity(new Intent(context, SecondActivity.class)
                .putExtra("TAG", "TroubleCodeDescriptionFragment")
        .putExtra("Troublecode",dtcModelClassArrayList.get(position).getToubleCode()));



    }

    @Override
    public int getItemCount() {
        return dtcModelClassArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        ViewScanItemBinding viewScanItemBinding;


        private MyViewHolder(@NonNull ViewScanItemBinding viewScanItemBinding) {
            super(viewScanItemBinding.getRoot());
            this.viewScanItemBinding = viewScanItemBinding;

        }
    }

}
