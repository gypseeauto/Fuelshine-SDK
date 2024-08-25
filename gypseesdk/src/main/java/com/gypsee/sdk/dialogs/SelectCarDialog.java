package com.gypsee.sdk.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.Collections;

import com.gypsee.sdk.Adapters.CarListRecyclerViewAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.FragmentSelectCarDialogBinding;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.utils.RecyclerItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class SelectCarDialog extends Dialog {


    public SelectCarDialog(@NonNull Context context) {
        super(context);
        this.context = context;
    }
    
    FragmentSelectCarDialogBinding fragmentSelectCarDialogBinding;
    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentSelectCarDialogBinding = DataBindingUtil.inflate(LayoutInflater.from(context), R.layout.fragment_select_car_dialog, null, false);
        setContentView(fragmentSelectCarDialogBinding.getRoot());
        loadRecyclerView();
        setLayoutParams();

    }

    private void setLayoutParams() {
        ViewGroup.LayoutParams params = getWindow().getAttributes();
        params.width = ViewGroup.LayoutParams.MATCH_PARENT;
        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
        getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);

        // THis is for making the dialog to make it transparant
        getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);

        setOnKeyListener(new OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, android.view.KeyEvent event) {

                if ((keyCode == android.view.KeyEvent.KEYCODE_BACK)) {
                    //Hide your keyboard here!!!
                    return true; // pretend we've processed it
                } else
                    return false; // pass on to be processed as normal
            }
        });

        setCanceledOnTouchOutside(false);

    }





    // Methiod to load the recyclerview(list of cars)

    ArrayList<Vehiclemodel> vehiclemodelArrayList = new ArrayList<>();

    private void loadRecyclerView() {
        vehiclemodelArrayList.clear();
        for(Vehiclemodel vehiclemodel :
                new DatabaseHelper(context).fetchAllVehicles()) {

            if (!vehiclemodel.isVinAvl())
                vehiclemodelArrayList.add(vehiclemodel);

        }

        Collections.reverse(vehiclemodelArrayList);
        CarListRecyclerViewAdapter carListRecyclerViewAdapter = new CarListRecyclerViewAdapter(vehiclemodelArrayList, context, false);
        fragmentSelectCarDialogBinding.carsListRecyclerview.setAdapter(carListRecyclerViewAdapter);

        fragmentSelectCarDialogBinding.carsListRecyclerview.addOnItemTouchListener(
                new RecyclerItemClickListener(context, fragmentSelectCarDialogBinding.carsListRecyclerview, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        //Send brand name back
                        Intent intent = new Intent("selectedvehiclemodel");
                        intent.putExtra(Vehiclemodel.class.getSimpleName(), vehiclemodelArrayList.get(position));
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        dismiss();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }





}
