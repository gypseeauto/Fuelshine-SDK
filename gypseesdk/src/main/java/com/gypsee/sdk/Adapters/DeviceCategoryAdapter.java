package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;

public class DeviceCategoryAdapter extends RecyclerView.Adapter<DeviceCategoryAdapter.ViewHolder> {

    Context context;
    ArrayList<String> categoryNames;

    public DeviceCategoryAdapter(Context context, ArrayList<String> categoryNames){
        this.context = context;
        this.categoryNames = categoryNames;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View view = layoutInflater.inflate(R.layout.device_category_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.categoryText.setText(categoryNames.get(position));
        if (new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getDeviceCategoryIndex() == position){
            holder.categoryText.setBackgroundResource(R.drawable.selected_item_background);
            holder.categoryText.setTextColor(context.getResources().getColor(R.color.white));
        } else {
            holder.categoryText.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.categoryText.setTextColor(context.getResources().getColor(R.color.text_black));
        }
    }

    @Override
    public int getItemCount() {
        return categoryNames.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        TextView categoryText;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            categoryText = itemView.findViewById(R.id.category_text);
        }
    }

}
