package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.GypseeStoriesRowLayoutBinding;
import com.gypsee.sdk.models.ToolModelClass;

public class GypseeStoriesRecyclerAdapter extends RecyclerView.Adapter<GypseeStoriesRecyclerAdapter.ViewHolder> {

    private Context mContext;

    ArrayList<ToolModelClass> gypseeStoriesModels;

    public GypseeStoriesRecyclerAdapter(Context mContext, ArrayList<ToolModelClass> drivingTipModels) {
        this.mContext = mContext;
        this.gypseeStoriesModels = drivingTipModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        GypseeStoriesRowLayoutBinding gypseeStoriesRowLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.gypsee_stories_row_layout, parent, false);
        return new ViewHolder(gypseeStoriesRowLayoutBinding);
    }


    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        ToolModelClass toolsModelClass = gypseeStoriesModels.get(position);

        if (toolsModelClass.getImageUrl().contains("http"))
            Glide
                    .with(mContext)
                    .load(toolsModelClass.getImageUrl())
                    .placeholder(R.drawable.ic_profile)
                    .centerInside()
                    .into(holder.gypseeStoriesRowLayoutBinding.imageView);
        holder.gypseeStoriesRowLayoutBinding.titleTV.setText(toolsModelClass.getTitle());
    }

    @Override
    public int getItemCount() {
        return gypseeStoriesModels.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        public GypseeStoriesRowLayoutBinding gypseeStoriesRowLayoutBinding;

        public ViewHolder(GypseeStoriesRowLayoutBinding drivingTipLayoutBinding) {
            super(drivingTipLayoutBinding.getRoot());
            this.gypseeStoriesRowLayoutBinding = drivingTipLayoutBinding;

        }
    }

}
