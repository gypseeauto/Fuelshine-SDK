package com.gypsee.sdk.demoaccount.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import com.gypsee.sdk.R;
import com.gypsee.sdk.demoaccount.fragments.DemoUpdateCarFragment;
import com.gypsee.sdk.models.Vehiclemodel;

public class DemoCarListRecyclerViewAdapter extends RecyclerView.Adapter<DemoCarListRecyclerViewAdapter.ViewHolder>{

    private final List<Vehiclemodel> mValues;
    Context context;
    String TAG = DemoCarListRecyclerViewAdapter.class.getName();

    String lastConnectedVehicleID;

    public DemoCarListRecyclerViewAdapter(List<Vehiclemodel> items, Context context) {
        mValues = items;
        this.context = context;
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.car_list_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        Vehiclemodel mItem = mValues.get(position);
        holder.brandName.setText(mItem.getVehicleBrand());
        holder.vehicleName.setText(mItem.getVehicleModel());
        holder.carRegNo.setText(mItem.getRegNumber());
        holder.vinNumber.setText("VIN:" + mItem.getVin());
        holder.serviceReminderkm.setText("SEVICE DUE IN:" + mItem.getServiceReminderkm() + " KM");

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    ((AppCompatActivity) context).getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.mainFrameLayout, DemoUpdateCarFragment.newInstance(mValues.get(position)), DemoUpdateCarFragment.class.getSimpleName())
                            .addToBackStack(DemoUpdateCarFragment.class.getSimpleName())
                            .commit();

            }
        });

        holder.deleteCarImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Please register your account to avail this service", Toast.LENGTH_LONG).show();
            }
        });


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


        }
    }





}
