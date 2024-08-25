package com.gypsee.sdk.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gypsee.sdk.R;

public class OfferPointsAdapter extends RecyclerView.Adapter<OfferPointsAdapter.ViewHolder>{

    ArrayList<String> bulletPoints;

    public OfferPointsAdapter(ArrayList<String> bulletPoints) {
        this.bulletPoints = bulletPoints;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.text_bullet_point_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.textView.setText(bulletPoints.get(position));
    }

    @Override
    public int getItemCount() {
        return bulletPoints.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.bullet_point);
        }
    }

}
