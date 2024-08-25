package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.jsonParser.RefreshDataFromOtherclass;
import com.gypsee.sdk.models.DocumentTypes;
import com.gypsee.sdk.models.VehicleDocsModel;
import com.gypsee.sdk.models.Vehiclemodel;

public class VehicleDocrecyclerAdapter extends RecyclerView.Adapter<VehicleDocrecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<DocumentTypes> documentTypes;
    private ArrayList<VehicleDocsModel> vehicleDocsModelArrayList;
    private RefreshDataFromOtherclass refreshDataFromOtherclass;
    private Vehiclemodel vehiclemodel;

    boolean isAddVehicle;

    public VehicleDocrecyclerAdapter(Context context, ArrayList<DocumentTypes> documentTypes, boolean isAddVehicle) {
        this.context = context;
        this.documentTypes = documentTypes;
        this.isAddVehicle = isAddVehicle;
    }

    public VehicleDocrecyclerAdapter(Context context, ArrayList<VehicleDocsModel> vehicleDocsModelArrayList, boolean isAddVehicle, int x) {

        this.context = context;
        this.vehicleDocsModelArrayList = vehicleDocsModelArrayList;
        this.isAddVehicle = isAddVehicle;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View itemView;
        if (isAddVehicle)
            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.rc_image_layout, parent, false);
        else {

            itemView = LayoutInflater.from(context)
                    .inflate(R.layout.rc_image_layout, parent, false);
        }
        return new MyViewHolder(itemView);
    }

    String TAG = VehicleDocrecyclerAdapter.class.getSimpleName();

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        if (isAddVehicle) {
            DocumentTypes documentType = documentTypes.get(position);
            holder.rcTitle.setText(documentType.getName());

            File imageFile = documentType.getFile();
            if (imageFile != null) {
                Glide
                        .with(context)
                        .load(documentType.getFile())
                        .placeholder(R.drawable.ic_camera)
                        .centerInside()
                        .into(holder.rcImage);

            }
        } else {

            VehicleDocsModel vehicleDocsModel = vehicleDocsModelArrayList.get(position);
            holder.rcTitle.setText(vehicleDocsModel.getDocName());

            if (vehicleDocsModel.getDocumentLink().equals("")) {
                Glide
                        .with(context)
                        .load(vehicleDocsModel.getFile())
                        .placeholder(R.drawable.ic_camera)
                        .centerInside()
                        .into(holder.rcImage);
            } else {
                Glide
                        .with(context)
                        .load(vehicleDocsModel.getDocumentLink())
                        .placeholder(R.drawable.ic_camera)
                        .centerInside()
                        .into(holder.rcImage);
            }

/*
            Drawable drawable = vehicleDocsModel.isVerified() ? context.getDrawable(R.drawable.ic_approved) : context.getDrawable(R.drawable.ic_pending);
            Glide
                    .with(context)
                    .load(drawable)
                    .placeholder(R.drawable.ic_camera)
                    .into(holder.docStatusImageView);

            FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, 20, 20);
            holder.vehicleStatusLayout.setLayoutParams(params);*/

        }


    }

    @Override
    public int getItemCount() {
        if (isAddVehicle)
            return documentTypes.size();
        else
            return vehicleDocsModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        TextView rcTitle;
        ImageView rcImage, docStatusImageView;
        RelativeLayout rcRootLayout;

        RelativeLayout vehicleStatusLayout;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            rcTitle = itemView.findViewById(R.id.rcTitle);
            rcRootLayout = itemView.findViewById(R.id.rcRootLayout);

            if (isAddVehicle) {
                rcImage = itemView.findViewById(R.id.rcImage);

            } else {
                {
                    rcImage = itemView.findViewById(R.id.rcImage);
                    vehicleStatusLayout = itemView.findViewById(R.id.vehicleStatusLayout);
                    docStatusImageView = itemView.findViewById(R.id.docStatusImageView);
                }

            }

        }
    }


}
