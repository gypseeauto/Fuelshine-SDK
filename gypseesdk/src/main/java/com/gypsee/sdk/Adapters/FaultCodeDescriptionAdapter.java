package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.SecondActivity;
import com.gypsee.sdk.databinding.FaultCodeDescriptionRowLayoutBinding;

public class FaultCodeDescriptionAdapter extends RecyclerView.Adapter<FaultCodeDescriptionAdapter.MyViewHolder> {

    private final int color;
    Context context;
    ArrayList<String> keys, values;

    public FaultCodeDescriptionAdapter(ArrayList<String> keys, ArrayList<String> values, Context context, int color) {
        this.keys = keys;
        this.values = values;
        this.context = context;
        this.color = color;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        FaultCodeDescriptionRowLayoutBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fault_code_description_row_layout, parent, false);
        return new MyViewHolder(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        FaultCodeDescriptionRowLayoutBinding faultCodeDescriptionRowLayoutBinding = holder.faultCodeDescriptionRowLayoutBinding;

        faultCodeDescriptionRowLayoutBinding.troubleCodeTv.setText(keys.get(position));
        faultCodeDescriptionRowLayoutBinding.descriptionTv.setText(values.get(position));
        faultCodeDescriptionRowLayoutBinding.searchforMoreInfoTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AppCompatActivity) context).startActivity(new Intent(context, SecondActivity.class)
                        .putExtra("TAG", "TroubleCodeDescriptionFragment")
                        .putExtra("Troublecode", keys.get(position)));
            }
        });

    }

    @Override
    public int getItemCount() {
        return keys.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        FaultCodeDescriptionRowLayoutBinding faultCodeDescriptionRowLayoutBinding;

        public MyViewHolder(@NonNull FaultCodeDescriptionRowLayoutBinding faultCodeDescriptionRowLayoutBinding) {
            super(faultCodeDescriptionRowLayoutBinding.getRoot());
            this.faultCodeDescriptionRowLayoutBinding = faultCodeDescriptionRowLayoutBinding;
            changeBackgrundColor(faultCodeDescriptionRowLayoutBinding.relativeLayout, color);

        }
    }

    String TAG = FaultCodeDescriptionAdapter.class.getSimpleName();

    private void changeBackgrundColor(View textView, int color) {
        //Changing the background color
        Drawable background = textView.getBackground();
        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            Log.e(TAG, "Background ShapeDrawable");
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            Log.e(TAG, "Background GradientDrawable");

            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(color);
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            Log.e(TAG, "Background ColorDrawable");

            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(color);
        }
    }

}

    

