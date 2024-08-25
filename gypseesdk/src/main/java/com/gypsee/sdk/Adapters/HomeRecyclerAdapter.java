package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.Emergencyactivity;
import com.gypsee.sdk.activities.MyCarDetailActivity;
import com.gypsee.sdk.activities.SecondActivity;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.ToolsRowLayoutBinding;
import com.gypsee.sdk.demoaccount.activities.DemoMainActivity;
import com.gypsee.sdk.fragments.ZoopAddCarDialogFragment;
import com.gypsee.sdk.models.ToolModelClass;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.utils.RecyclerItemClickListener;

public class HomeRecyclerAdapter extends RecyclerView.Adapter<HomeRecyclerAdapter.MyViewHolder> {

    ArrayList<ArrayList<ToolModelClass>> homeData;
    Context context;
    FragmentManager fragmentManager;




    public HomeRecyclerAdapter(ArrayList<ArrayList<ToolModelClass>> homeData, Context context, FragmentManager fragmentManager){
        this.homeData = homeData;
        this.context = context;
        this.fragmentManager = fragmentManager;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.home_recyclerview_list_item, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.textView.setText(homeData.get(position).get(0).getCategory());

        if (homeData.get(position).get(0).getCategory().equalsIgnoreCase("Gypsee Stories")){

            holder.recyclerView.setAdapter(new GypseeStoriesRecyclerAdapter(context, homeData.get(position)));

            holder.recyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, holder.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                @Override
                public void onItemClick(View view, int innerPosition) {
                    // do whatever
                    //Send brand name back
                    if (homeData.get(position).size() <= innerPosition) {
                        Toast.makeText(context, "Please Contact Customer care for gypsee Stories", Toast.LENGTH_LONG).show();

                    } else {
                        Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homeData.get(position).get(innerPosition).getRedirectUrl()));
                        context.startActivity(browserIntent);
                    }
                }

                @Override
                public void onLongItemClick(View view, int position) {
                    // do whatever
                }
            }));

        } else {

            holder.recyclerView.setAdapter(new ToolsListRecyclerAdapter(homeData.get(position), context));
            holder.recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(context, holder.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int innerPosition) {
                            // do whatever
                            //Send brand name back
                            if (homeData.get(position).size() <= innerPosition) {
                                Toast.makeText(context, "Please Contact Customer care for gypsee driver training program", Toast.LENGTH_LONG).show();

                            } else {

                                switch (homeData.get(position).get(innerPosition).getTitle().trim()){

                                    case "RTO Vehicle Information":
                                        ArrayList<Vehiclemodel> vehiclemodels = new DatabaseHelper(context).fetchAllVehicles();

                                        if(vehiclemodels.size() == 0){
                                            showZoopAddCarDialog();
                                        } else {
                                            Intent carDetailsIntent = new Intent(context, MyCarDetailActivity.class);
                                            carDetailsIntent.putExtra(Vehiclemodel.class.getName(), vehiclemodels.get(0));
                                            context.startActivity(carDetailsIntent);
                                        }
                                        break;

                                    case "Vehicle Maintenance Reminder":
                                        context.startActivity(new Intent(context, SecondActivity.class)
                                                .putExtra("TAG", "MyCarsListFragment")
                                                .putExtra("Reason", "Mycars")
                                        );
                                        break;


                                    case "Emergency Contact":
                                        context.startActivity(new Intent(context, Emergencyactivity.class));
                                        break;

                                    /*case "Car Health":
                                        context.startActivity(new Intent(context, SecondActivity.class)
                                                .putExtra("TAG", "PerformanceFragment"));
                                        break;*/

                                    case "Car Scan":
                                        context.startActivity(new Intent(context, SecondActivity.class)
                                                .putExtra("TAG", "CarScanFragment"));
                                        break;

                                    case "Car Scanner Live Demo":
                                        context.startActivity(new Intent(context, DemoMainActivity.class));
                                        break;

                                    default:
                                        String redirectURL = homeData.get(position).get(innerPosition).getRedirectUrl();
                                        if (redirectURL.contains("http")) {
                                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homeData.get(position).get(innerPosition).getRedirectUrl()));
                                            context.startActivity(browserIntent);
                                        }

                                }

                                /*
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homeData.get(position).get(innerPosition).getRedirectUrl()));
                                context.startActivity(browserIntent);*/
                            }
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            // do whatever
                        }
                    })
            );

        }

//-------------------------------------------------------------------------------------------------------
            /*if (homeData.get(position).get(0).getCategory().equalsIgnoreCase("Driver Traning Program")){

            holder.recyclerView.setAdapter(new ToolsListRecyclerAdapter(homeData.get(position), context));
            holder.recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(context, holder.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int innerPosition) {
                            // do whatever
                            //Send brand name back
                            if (homeData.get(position).size() <= innerPosition) {
                                Toast.makeText(context, "Please Contact Customer care for gypsee driver training program", Toast.LENGTH_LONG).show();

                            } else {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homeData.get(position).get(innerPosition).getRedirectUrl()));
                                context.startActivity(browserIntent);
                            }
                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            // do whatever
                        }
                    })
            );


        } else if (homeData.get(position).get(0).getCategory().equalsIgnoreCase("Vehicle Information Manager")){

            holder.recyclerView.setAdapter(new ToolsListRecyclerAdapter(homeData.get(position), context));

            holder.recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(context, holder.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int innerPosition) {
                            // do whatever
                            //Send brand name back

                            switch (homeData.get(position).get(innerPosition).getTitle().trim()){

                                case "RTO Vehicle Details":
                                    ArrayList<Vehiclemodel> vehiclemodels = new DatabaseHelper(context).fetchAllVehicles();

                                    if(vehiclemodels.size() == 0){
                                        showZoopAddCarDialog();
                                    } else {
                                        Intent carDetailsIntent = new Intent(context, MyCarDetailActivity.class);
                                        carDetailsIntent.putExtra(Vehiclemodel.class.getName(), vehiclemodels.get(0));
                                        context.startActivity(carDetailsIntent);


                                    }
                                    break;

                                case "Maintenance Reminders":
                                    context.startActivity(new Intent(context, SecondActivity.class)
                                            .putExtra("TAG", "MyCarsListFragment")
                                            .putExtra("Reason", "Mycars")
                                    );
                                    break;


                                case "Emergency Contacts":
                                    context.startActivity(new Intent(context, Emergencyactivity.class));
                                    break;

                                case "Car Health":
                                    context.startActivity(new Intent(context, SecondActivity.class)
                                            .putExtra("TAG", "PerformanceFragment"));
                                    break;

                                case "Car Scan":
                                    context.startActivity(new Intent(context, SecondActivity.class)
                                            .putExtra("TAG", "CarScanFragment"));
                                    break;


                            }

                            *//*if (homeData.get(position).get(innerPosition).getRedirectUrl().contains("http")) {

                            *//**//*Intent in = new Intent(context, SampleWebviewActivity.class);
                            in.putExtra("Url", localtoolsModelClasses.get(position1).getRedirectUrl());
                            in.putExtra("Title", localtoolsModelClasses.get(position1).getTitle());
                            context.startActivity(in);*//**//*

                            *//**//*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(toolsModelClasses.get(position).getRedirectUrl()));
                            context.startActivity(browserIntent);*//**//*

                                ArrayList<Vehiclemodel> vehiclemodels = new DatabaseHelper(context).fetchAllVehicles();

                                if(vehiclemodels.size() == 0){
                                    showZoopAddCarDialog();
                                } else {
                                    Intent carDetailsIntent = new Intent(context, MyCarDetailActivity.class);
                                    carDetailsIntent.putExtra(Vehiclemodel.class.getName(), vehiclemodels.get(0));
                                    context.startActivity(carDetailsIntent);
                                }



                            } else {

                                if (homeData.get(position).get(innerPosition).getTitle().equalsIgnoreCase("Emergency Contacts")) {
                                    context.startActivity(new Intent(context, Emergencyactivity.class));

                                } else if (homeData.get(position).get(innerPosition).getTitle().equalsIgnoreCase("Maintenance Reminders")) {
                                    context.startActivity(new Intent(context, SecondActivity.class)
                                            .putExtra("TAG", "MyCarsListFragment")
                                            .putExtra("Reason", "Mycars")
                                    );
                                }
                            }*//*

                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            // do whatever
                        }
                    })
            );



        } else { //use offers layout for any extra row added

            holder.recyclerView.setAdapter(new ToolsListRecyclerAdapter(homeData.get(position), context));

            holder.recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(context, holder.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int innerPosition) {
                            // do whatever
                            //Send brand name back
                            Log.e(TAG, "Redirect URL : " + gypseeOffersModels.get(position).getRedirectUrl());
                             if (homeData.get(position).size() <= innerPosition) {
                                Toast.makeText(context, "Please Contact Customer care for gypsee offers", Toast.LENGTH_LONG).show();
                            } else if (homeData.get(position).get(innerPosition).getTitle().equalsIgnoreCase("Product Demo")){
                                 context.startActivity(new Intent(context, DemoMainActivity.class));
                             } else {
                                String redirectURL = homeData.get(position).get(innerPosition).getRedirectUrl();
                                if (redirectURL.contains("http")) {
                                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(homeData.get(position).get(innerPosition).getRedirectUrl()));
                                    context.startActivity(browserIntent);
                                }
                            }

                        }

                        @Override
                        public void onLongItemClick(View view, int position) {
                            // do whatever
                        }
                    })
            );

        }*/

    }

    ZoopAddCarDialogFragment zoopAddCarDialogFragment;

    private void showZoopAddCarDialog() {

        if (zoopAddCarDialogFragment != null && zoopAddCarDialogFragment.isAdded()) {

        } else {
            zoopAddCarDialogFragment = new ZoopAddCarDialogFragment();
            zoopAddCarDialogFragment.setCancelable(false);
            zoopAddCarDialogFragment.show(fragmentManager, ZoopAddCarDialogFragment.class.getSimpleName());
        }
    }

    @Override
    public int getItemCount() {
        return homeData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
       RecyclerView recyclerView;
       TextView textView;

        public MyViewHolder(View view) {
            super(view);
            recyclerView = (RecyclerView) view.findViewById(R.id.home_recyclerview_tile_recyclerview);
            textView = (TextView) view.findViewById(R.id.home_recyclerview_tile_heading);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);


        }
    }

}
