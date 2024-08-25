package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.models.StoreProductListItemModel;


public class DemoStoreListAdapter extends RecyclerView.Adapter<DemoStoreListAdapter.ViewHolder>{

    private ArrayList<StoreProductListItemModel> productList;
    private Context context;

    public DemoStoreListAdapter(ArrayList<StoreProductListItemModel> productList, Context context) {
        this.productList = productList;
        this.context = context;
    }

    @NonNull
    @NotNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.store_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull DemoStoreListAdapter.ViewHolder holder, int position) {
        holder.productTitle.setText(productList.get(position).getName());
        Glide.with(context)
                .load(context.getDrawable(R.drawable.ic_subscribe_now_icon))
                .placeholder(R.drawable.ic_image_placeholder_new)
                .error(R.drawable.ic_broken_image_placeholder_new)
                .into(holder.productImage);
    }

    @Override
    public int getItemCount() {
        return productList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView productImage;
        TextView productTitle;

        public ViewHolder(@NonNull @NotNull View itemView) {
            super(itemView);
            productImage = itemView.findViewById(R.id.product_image);
            productTitle = itemView.findViewById(R.id.product_title);
        }
    }


}

