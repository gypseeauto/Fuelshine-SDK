package com.gypsee.sdk.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gypsee.sdk.R;

public class DrivePagerDotsAdapter extends RecyclerView.Adapter<DrivePagerDotsAdapter.ViewHolder>{

    private int pagePosition;
    private RecyclerView recyclerView;

    public DrivePagerDotsAdapter(RecyclerView recyclerView){
        this.pagePosition = 0;
        this.recyclerView = recyclerView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.drive_pager_dots, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ViewGroup.LayoutParams params = holder.view.getLayoutParams();
        if (position == this.pagePosition){
            params.width = 50;
            params.height = 8;
            holder.view.setLayoutParams(params);
        } else {
            params.width = 8;
            params.height = 8;
            holder.view.setLayoutParams(params);
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }

    public void setPagePosition(int pagePosition){
        int oldPosition = this.pagePosition;
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(oldPosition);
            }
        });

        this.pagePosition = pagePosition;
        recyclerView.post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(pagePosition);
            }
        });


    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        View view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            view = itemView.findViewById(R.id.drive_dot);
        }
    }


}
