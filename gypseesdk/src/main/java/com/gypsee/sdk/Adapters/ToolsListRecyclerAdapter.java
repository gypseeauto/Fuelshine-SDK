package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.ToolsRowLayoutBinding;
import com.gypsee.sdk.models.ToolModelClass;

public class ToolsListRecyclerAdapter extends RecyclerView.Adapter<ToolsListRecyclerAdapter.MyViewHolder> {


    private List<ToolModelClass> toolsModelClasses;
    Context context;

    public ToolsListRecyclerAdapter(ArrayList<ToolModelClass> tripRecords, Context context) {
        this.toolsModelClasses = tripRecords;
        this.context = context;
        Log.e(TAG, "Size of the array list is : " + toolsModelClasses.size());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());


        ToolsRowLayoutBinding singleDayTripRowLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.tools_row_layout, parent, false);

       /* View itemView = singleDayTripRowLayoutBinding.getRoot();

        ViewGroup.LayoutParams layoutParams = itemView.getLayoutParams();
        layoutParams.width = (int) (parent.getWidth() * 0.7);
        itemView.setLayoutParams(layoutParams);*/
        return new MyViewHolder(singleDayTripRowLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        ToolsRowLayoutBinding singleDayTripRowLayoutBinding = holder.toolsRowLayoutBinding;

        ToolModelClass toolsModelClass = toolsModelClasses.get(position);

        singleDayTripRowLayoutBinding.setTitle(toolsModelClass.getTitle());
        singleDayTripRowLayoutBinding.setDescription(toolsModelClass.getDescription());
        if (toolsModelClass.getColourCode().equals("") || toolsModelClass.getColourCode().equals("'")) {

        } else {
            singleDayTripRowLayoutBinding.toolcarView.setCardBackgroundColor(Color.parseColor( toolsModelClass.getColourCode()));
        }

        if (toolsModelClass.getImageUrl().contains("http"))
            Glide
                    .with(context)
                    .load(toolsModelClass.getImageUrl())
                    .placeholder(R.drawable.ic_profile)
                    .centerInside()
                    .into(singleDayTripRowLayoutBinding.imageView);

        //singleDayTripRowLayoutBinding.imageView.setImageDrawable(toolsModelClass.getImage());

    }

    @Override
    public int getItemCount() {
        return toolsModelClasses.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ToolsRowLayoutBinding toolsRowLayoutBinding;

        public MyViewHolder(@NonNull ToolsRowLayoutBinding toolsRowLayoutBinding) {
            super(toolsRowLayoutBinding.getRoot());
            this.toolsRowLayoutBinding = toolsRowLayoutBinding;


            // This code is used to get the screen dimensions of the user's device
            DisplayMetrics displayMetrics = new DisplayMetrics();
            ((AppCompatActivity) context).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
            int width = displayMetrics.widthPixels;
            int height = displayMetrics.heightPixels;

            RecyclerView.LayoutParams params = new RecyclerView.LayoutParams((int) (width / 1.5), RecyclerView.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 15, 0);
            // Set the ViewHolder width to be a third of the screen size, and height to wrap content
            itemView.setLayoutParams(params);


        }
    }

    String TAG = ToolsListRecyclerAdapter.class.getSimpleName();
}

    

