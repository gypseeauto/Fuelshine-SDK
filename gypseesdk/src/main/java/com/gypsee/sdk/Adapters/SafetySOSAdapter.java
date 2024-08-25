package com.gypsee.sdk.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gypsee.sdk.R;

public class SafetySOSAdapter extends RecyclerView.Adapter<SafetySOSAdapter.ViewHolder>{


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.safety_sos_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        switch (position){

            case 0:
                holder.sosImage.setImageResource(R.drawable.ic_crash);
                holder.sosTitle.setText("Accident");
                holder.sosMessage.setText("In the case of accident, you should inform your loved ones.");
                break;

            case 2:
                holder.sosImage.setImageResource(R.drawable.ic_vehicle_breakdown);
                holder.infoImage.setImageResource(R.mipmap.ic_green_call);
                holder.sosTitle.setText("Vehicle Breakdown");
                holder.sosMessage.setText("Breakdown could be a daunting task for you, so why not ask for help.");
                break;

            case 3:
                holder.sosImage.setImageResource(R.drawable.ic_roadrage);
                holder.sosTitle.setText("Road Rage");
                holder.sosMessage.setText("Road rage is aggressive or angry behavior exhibited by motorists.");
                break;

            case 1:
                //holder.sosImage.setImageResource(R.drawable.ic_confused);
                holder.sosImage.setImageResource(R.drawable.ic_preferred_services);
                holder.sosTitle.setText("Preferred service assistance");
                holder.sosMessage.setText("Contact your preferred service center for on demand roadside assistance and emergency services.");
                break;

        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView sosTitle, sosMessage;
        ImageView sosImage, infoImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            sosImage = itemView.findViewById(R.id.sos_type);
            sosTitle = itemView.findViewById(R.id.sos_title);
            sosMessage = itemView.findViewById(R.id.sos_message);
            infoImage = itemView.findViewById(R.id.info_image);
        }
    }


}
