package com.gypsee.sdk.Adapters;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.fragments.UpdateCarFragment;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.helpers.DialogHelper;
import com.gypsee.sdk.jsonParser.ResponseFromServer;
import com.gypsee.sdk.models.Vehiclemodel;

import java.util.List;


public class CarListRecyclerViewAdapter extends RecyclerView.Adapter<CarListRecyclerViewAdapter.ViewHolder> {

    private final List<Vehiclemodel> mValues;
    Context context;
    boolean enableOnclickItem;

    boolean connected;
    String lastConnectedVehicleID;
    MyPreferenece myPreferenece;

    public CarListRecyclerViewAdapter(List<Vehiclemodel> items, Context context, boolean enableOnclickItem) {
        mValues = items;
        this.context = context;
        this.enableOnclickItem = enableOnclickItem;
        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
        lastConnectedVehicleID = myPreferenece.getStringData(MyPreferenece.lastConnectedVehicle);
    }

    ResponseFromServer responseFromServer;

    public CarListRecyclerViewAdapter(List<Vehiclemodel> items, Context context, boolean enableOnclickItem, ResponseFromServer responseFromServer,boolean connected) {
        mValues = items;
        this.context = context;
        this.enableOnclickItem = enableOnclickItem;
        this.responseFromServer = responseFromServer;
        this.connected = connected;
        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
        lastConnectedVehicleID = myPreferenece.getStringData(MyPreferenece.lastConnectedVehicle);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_list_item, parent, false);
        return new ViewHolder(view);
    }

    String TAG = CarListRecyclerViewAdapter.class.getSimpleName();

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Vehiclemodel mItem = mValues.get(position);
        holder.brandName.setText(mItem.getVehicleBrand());
        holder.vehicleName.setText(mItem.getVehicleModel());
        holder.carRegNo.setText(mItem.getRegNumber());
        holder.vinNumber.setText("VIN:" + mItem.getVin());
//        holder.serviceReminderkm.setText("SEVICE DUE IN:" + mItem.getServiceReminderkm() + " KM");

        //we need to check whether the OBD is connected. If connected, we need to show user that the car is connected to IBD 2.
//        if (mItem.isObdConnected()) {
//            BluetoothHelperClass.changeBackgrundColor(holder.carConnectedImage, ContextCompat.getColor(context, R.color.green), TAG);
//        } else {
//            BluetoothHelperClass.changeBackgrundColor(holder.carConnectedImage, ContextCompat.getColor(context, R.color.grey), TAG);
//        }

        // CHeck the previously connected vehcile


        if (responseFromServer != null) {
            holder.mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
////                     implement item click listeners.
////                     OBD connected, we do not let the user to edit the details.
//                    if (mValues.get(position).isObdConnected()) {
//                        //Show dialog to not edit the details of vehicle
//                        DialogHelper.showCustomDialog(false, context, (AppCompatActivity) context, "Alert!", "The car " + mValues.get(position).getVehicleModel() + " is Connected to OBD II. Once the trip is ended, you can edit the car details.");
//                    } else {
//                        ((AppCompatActivity) context).getSupportFragmentManager()
//                                .beginTransaction()
//                                .add(R.id.mainFrameLayout, UpdateCarFragment.newInstance(mValues.get(position)), UpdateCarFragment.class.getSimpleName())
//                                .addToBackStack(UpdateCarFragment.class.getSimpleName())
//                                .commit();
//                    }
//                     mValues.get(position).isObdConnected();

                    if (connected) {
                        //Show dialog to not edit the details of vehicle
                        DialogHelper.showCustomDialog(false, context, (AppCompatActivity) context, "Alert!", "The car " + mValues.get(position).getVehicleModel() + " is Connected to your car. Once the trip is ended, you can edit the car details.");
                    } else {
                        ((AppCompatActivity) context).getSupportFragmentManager()
                                .beginTransaction()
                                .add(R.id.mainFrameLayout, UpdateCarFragment.newInstance(mValues.get(position)), UpdateCarFragment.class.getSimpleName())
                                .addToBackStack(UpdateCarFragment.class.getSimpleName())
                                .commit();
                    }

                }
            });
        }
        holder.deleteCarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BluetoothHelperClass.showTripEndDialog(context, ((AppCompatActivity) context).getLayoutInflater(), "", "Are you sure you want to Delete the Car?", responseFromServer, position);
            }
        });

        //IF response from server is not null means we are in view vehicles list page.
        //If the car is deleting, we will hide the delete image.
        if (responseFromServer != null) {
            if (!myPreferenece.getUser().isInTrainingMode()) {
                if (mItem.isDeleting()) {
                    holder.deleteCarImg.setVisibility(View.GONE);
//                    holder.deleteCarTv.setVisibility(View.VISIBLE);
                } else {
                    holder.deleteCarImg.setVisibility(View.VISIBLE);
//                    holder.deleteCarTv.setVisibility(View.GONE);
                }
            } else {
                holder.deleteCarImg.setVisibility(View.GONE);
            }
        }

    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;


        private TextView brandName, vehicleName, carRegNo, vinNumber, serviceReminderkm, viewCarDetails, deleteCarTv;

        private TextView carConnectedImage;
        LinearLayout vinServiceLayout;
        View lineView;
        ImageView deleteCarImg;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            brandName = (TextView) view.findViewById(R.id.brandname);
            vehicleName = (TextView) view.findViewById(R.id.vehModel);
            carRegNo = (TextView) view.findViewById(R.id.carRegNo);
            vinNumber = (TextView) view.findViewById(R.id.vinNumber);
            serviceReminderkm = (TextView) view.findViewById(R.id.serviceReminderkm);
//            carConnectedImage = view.findViewById(R.id.carConnectedImage);
//            vinServiceLayout = view.findViewById(R.id.vinServiceLayout);
            lineView = view.findViewById(R.id.lineView);

//            viewCarDetails = view.findViewById(R.id.viewCarDetails);
//            deleteCarTv = view.findViewById(R.id.deleteCarTv);
            deleteCarImg = view.findViewById(R.id.deleteCarImg);


            //Not clickable condition
            if (responseFromServer == null) {
                vinServiceLayout.setVisibility(View.GONE);
                lineView.setVisibility(View.GONE);
            } else {
                // viewCarDetails.setVisibility(View.VISIBLE);
                // deleteCarImg.setVisibility(View.VISIBLE);
            }

        }
    }
}
