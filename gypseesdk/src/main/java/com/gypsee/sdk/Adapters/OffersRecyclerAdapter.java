package com.gypsee.sdk.Adapters;

import android.content.Context;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;


import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.OfferItemLayoutBinding;
import com.gypsee.sdk.dialogs.ServiceReminderConfirmationDialog;
import com.gypsee.sdk.models.OffersModel;
import com.gypsee.sdk.models.Vehiclemodel;

public class OffersRecyclerAdapter extends RecyclerView.Adapter<OffersRecyclerAdapter.MyViewHolder> {

    private Context context;
    private ArrayList<OffersModel> offersModelArrayList;

    public OffersRecyclerAdapter(Context context, ArrayList<OffersModel> offersModelArrayList) {
        this.context = context;
        this.offersModelArrayList = offersModelArrayList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        OfferItemLayoutBinding offerItemLayoutBinding =
                DataBindingUtil.inflate(LayoutInflater.from(context),
                        R.layout.offer_item_layout, parent, false);
        return new MyViewHolder(offerItemLayoutBinding);


    }

    private String TAG = OffersRecyclerAdapter.class.getSimpleName();

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int position) {

        final OffersModel offersModel = offersModelArrayList.get(position);
        holder.offerItemLayoutBinding.setOffer(offersModel);

        if(!offersModel.getImageLink().isEmpty()) {
            Glide.with(context)
                    .load(offersModel.getImageLink())
                    .centerInside()
                    .into(holder.offerItemLayoutBinding.offerIcon);
        }else if(offersModel.getTitle().toLowerCase().contains("coin")){
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.drawable.ic_coin_front);
            holder.offerItemLayoutBinding.offerIcon.setImageBitmap(icon);
        }else{
            Bitmap icon = BitmapFactory.decodeResource(context.getResources(),
                    R.mipmap.ic_launcher);
            holder.offerItemLayoutBinding.offerIcon.setImageBitmap(icon);
        }

        holder.offerItemLayoutBinding.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (offersModel.getNotificationType().equals("serviceReminderConfirmationNotification")) {
                    checkNotificationIntentValues(offersModel);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return offersModelArrayList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        OfferItemLayoutBinding offerItemLayoutBinding;

        private MyViewHolder(@NonNull OfferItemLayoutBinding offerItemLayoutBinding) {
            super(offerItemLayoutBinding.getRoot());

            this.offerItemLayoutBinding = offerItemLayoutBinding;

        }
    }

    // This is to check the notification intent values
    private void checkNotificationIntentValues(OffersModel offersModel) {

        String notificationType = offersModel.getNotificationType();
       Log.e(TAG, "notificationType : " + notificationType);
        switch (notificationType) {
            case "serviceReminderConfirmationNotification":
                String dataObject = offersModel.getNotificationData();

                try {
                    JSONObject jsonObject = new JSONObject(dataObject);
                    String vehicleId = jsonObject.getString("vehicleId");
                    ArrayList<Vehiclemodel> vehiclemodelArrayList = new DatabaseHelper(context).fetchAllVehicles();
                    for (Vehiclemodel vehiclemodel : vehiclemodelArrayList) {
                       Log.e(TAG, "VehicleID" + vehicleId);
                       Log.e(TAG, "Vehicle ID : " + vehiclemodel.getUserVehicleId());

                        if (vehiclemodel.getUserVehicleId().equals(vehicleId)) {

                            //Show dialog to update the ServiceReminderConfirmationNotification status to true.
                            ServiceReminderConfirmationDialog serviceReminderConfirmationDialog = new ServiceReminderConfirmationDialog(context, vehiclemodel, dataObject);
                            serviceReminderConfirmationDialog.setCanceledOnTouchOutside(false);
                            serviceReminderConfirmationDialog.setCancelable(false);
                            serviceReminderConfirmationDialog.show();

                            Window callDialogWindow = serviceReminderConfirmationDialog.getWindow();
                            // TO set the
                            WindowManager.LayoutParams params = new WindowManager.LayoutParams();
                            callDialogWindow.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.copyFrom(serviceReminderConfirmationDialog.getWindow().getAttributes());
                            params.gravity = Gravity.CENTER;
                            serviceReminderConfirmationDialog.getWindow().setAttributes(params);


                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                break;
        }
    }

}
