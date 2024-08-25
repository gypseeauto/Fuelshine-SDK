package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gypsee.sdk.R;

public class SingleTextviewAdapter extends RecyclerView.Adapter<SingleTextviewAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<String> stringArrayList;

    public SingleTextviewAdapter(Context context, ArrayList<String> driveDetailsModelClasses) {
        this.context = context;
        this.stringArrayList = driveDetailsModelClasses;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.textview_layout, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        String troubleCode = stringArrayList.get(position);


        SpannableString ss = new SpannableString(troubleCode);
        ClickableSpan clickableSpan = new ClickableSpan() {
            @Override
            public void onClick(View textView) {

             Log.e(SingleTextviewAdapter.class.getSimpleName(), "Clicked Position : " + position);
                //startActivity(new Intent(MyActivity.this, NextActivity.class));
            }

            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setUnderlineText(false);
            }
        };
        ss.setSpan(clickableSpan, troubleCode.length()-5, troubleCode.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        holder.sampleTextview.setText(ss);
        holder.sampleTextview.setMovementMethod(LinkMovementMethod.getInstance());
        holder.sampleTextview.setHighlightColor(Color.TRANSPARENT);


        if (position != 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 10, 0, 10);
            holder.sampleTextview.setLayoutParams(params);
        }


    }

    @Override
    public int getItemCount() {
        return stringArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView sampleTextview, desc;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            sampleTextview = itemView.findViewById(R.id.sampleTextview);
        }
    }


}
