package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.Emergencyactivity;
import com.gypsee.sdk.activities.SecondActivity;
import com.gypsee.sdk.databinding.ToolsRecyclerviewLayoutBinding;
import com.gypsee.sdk.databinding.ToolsRowLayoutBinding;
import com.gypsee.sdk.models.ToolModelClass;
import com.gypsee.sdk.utils.RecyclerItemClickListener;
import com.gypsee.sdk.utils.SpacesItemDecoration;

public class ToolsNestedRecyclerAdapter extends RecyclerView.Adapter<ToolsNestedRecyclerAdapter.MyViewHolder> {


    private ArrayList<ArrayList<ToolModelClass>> toolsModelClassesList = new ArrayList<>();
    Context context;

    public ToolsNestedRecyclerAdapter(ArrayList<ToolModelClass> toolsModelClasses, Context context) {
        this.context = context;

       /* ArrayList<ToolModelClass> modelClasses = new ArrayList<>();
        for (ToolModelClass toolsModelClass :
                toolsModelClasses) {

            modelClasses.add(toolsModelClass);

            if (modelClasses.size() == 2) {
                toolsModelClassesList.add(modelClasses);
                modelClasses = new ArrayList<>();
            }

        }

        if (modelClasses.size() > 0) {
            toolsModelClassesList.add(modelClasses);
        }*/
        toolsModelClassesList.add(toolsModelClasses);
        Log.e(TAG, "Size of the array list is : " + toolsModelClassesList.size());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());


        ToolsRecyclerviewLayoutBinding toolsRecyclerviewLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.tools_recyclerview_layout, parent, false);
        return new MyViewHolder(toolsRecyclerviewLayoutBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        ArrayList<ToolModelClass> localtoolsModelClasses = toolsModelClassesList.get(position);
        holder.toolsRecyclerviewLayoutBinding.recyclerView.addItemDecoration(new SpacesItemDecoration(20));
        holder.toolsRecyclerviewLayoutBinding.recyclerView.setAdapter(new ToolsListRecyclerAdapter(localtoolsModelClasses, context));

        holder.toolsRecyclerviewLayoutBinding.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, holder.toolsRecyclerviewLayoutBinding.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position1) {
                        // do whatever
                        //Send brand name back
                        if (localtoolsModelClasses.get(position1).getRedirectUrl().contains("http")) {

                            /*Intent in = new Intent(context, SampleWebviewActivity.class);
                            in.putExtra("Url", localtoolsModelClasses.get(position1).getRedirectUrl());
                            in.putExtra("Title", localtoolsModelClasses.get(position1).getTitle());
                            context.startActivity(in);*/

                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(localtoolsModelClasses.get(position1).getRedirectUrl()));
                            context.startActivity(browserIntent);

                        } else {

                            if (localtoolsModelClasses.get(position1).getTitle().equalsIgnoreCase("Emergency Contacts")) {
                                context.startActivity(new Intent(context, Emergencyactivity.class));

                            } else if (localtoolsModelClasses.get(position1).getTitle().equalsIgnoreCase("Maintenance Reminders")) {
                                context.startActivity(new Intent(context, SecondActivity.class)
                                        .putExtra("TAG", "MyCarsListFragment")
                                );
                            }
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }

    @Override
    public int getItemCount() {
        return toolsModelClassesList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        ToolsRecyclerviewLayoutBinding toolsRecyclerviewLayoutBinding;

        public MyViewHolder(@NonNull ToolsRecyclerviewLayoutBinding toolsRecyclerviewLayoutBinding) {
            super(toolsRecyclerviewLayoutBinding.getRoot());
            this.toolsRecyclerviewLayoutBinding = toolsRecyclerviewLayoutBinding;
        }
    }

    String TAG = ToolsNestedRecyclerAdapter.class.getSimpleName();
}

    

