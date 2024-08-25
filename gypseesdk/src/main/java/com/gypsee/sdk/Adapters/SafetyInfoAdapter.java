package com.gypsee.sdk.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gypsee.sdk.R;

public class SafetyInfoAdapter extends RecyclerView.Adapter<SafetyInfoAdapter.ViewHolder>{


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.sos_info_card, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        switch (position){

            case 0:
                holder.infoImage.setImageResource(R.drawable.ic_contacts_new);
                holder.infoTitle.setText("Members");
                holder.infoMessage.setText("During emegency the people who are mostly concerned about you are your loved ones.");
                break;

            case 1:
                holder.infoImage.setImageResource(R.drawable.ic_how_it_works_new);
                holder.infoTitle.setText("How it Works");
                holder.infoMessage.setText("Explore the benefits of “Safety Center” and learn How does it works.");
                break;

            case 2:
                holder.infoImage.setImageResource(R.drawable.ic_emergency_new);
                holder.infoTitle.setText("Emergency Directory");
                holder.infoMessage.setText("Get easy access to emergency helpline numbers, click out the Gypsee directory.");
                break;

            case 3:
                holder.infoImage.setImageResource(R.drawable.ic_emergency_service);
                holder.infoTitle.setText("Emergency Services");
                holder.infoMessage.setText("Have you ever encountered with a fear of Car breakdown than don’t worry just Tap it over here.");
                break;


        }

    }

    @Override
    public int getItemCount() {
        return 4;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView infoTitle, infoMessage;
        ImageView infoImage;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            infoTitle = itemView.findViewById(R.id.info_title);
            infoMessage = itemView.findViewById(R.id.info_message);
            infoImage = itemView.findViewById(R.id.info_image);
        }
    }

}
