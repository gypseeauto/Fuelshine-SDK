package com.gypsee.sdk.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.models.MemberModel;

public class MembersAdapter extends RecyclerView.Adapter<MembersAdapter.ViewHolder>{

    ArrayList<MemberModel> membersList;

    public MembersAdapter(ArrayList<MemberModel> membersList) {
        this.membersList = membersList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.member_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        MemberModel currentMember = membersList.get(position);
        holder.memberImage.setImageResource(R.drawable.ic_person_one);
        holder.memberName.setText(currentMember.getName() + " (" + currentMember.getRelation() + ")");
        holder.memberDetails.setText(currentMember.getMobileNo() + "\n" + currentMember.getCity());
    }

    @Override
    public int getItemCount() {
        return membersList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView memberName, memberDetails;
        ImageView memberImage;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            memberName = itemView.findViewById(R.id.member_name);
            memberDetails = itemView.findViewById(R.id.member_details);
            memberImage = itemView.findViewById(R.id.person_image);
        }
    }

}
