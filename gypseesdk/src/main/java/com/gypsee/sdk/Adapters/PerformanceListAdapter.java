package com.gypsee.sdk.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gypsee.sdk.R;

import java.util.ArrayList;

public class PerformanceListAdapter extends RecyclerView.Adapter<PerformanceListAdapter.MyViewHolder> {
    public ArrayList<String> data;

    public PerformanceListAdapter() {
        data = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.performanace_view, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        String[] eachCode=data.get(position).split(":");

        if (eachCode.length == 2) {
            holder.problemID.setText(eachCode[0]);

            holder.desc.setText(eachCode[1]);
        }
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView problemID, desc;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            problemID = itemView.findViewById(R.id.tv_title);
            desc = itemView.findViewById(R.id.tv_value);
        }
    }

}
