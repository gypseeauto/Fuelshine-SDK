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
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.Emergencyactivity;
import com.gypsee.sdk.demoaccount.activities.DemoMyCarDetailActivity;
import com.gypsee.sdk.demoaccount.activities.DemoSecondActivity;
import com.gypsee.sdk.models.ToolModelClass;
import com.gypsee.sdk.utils.RecyclerItemClickListener;

public class DemoHomeRecyclerAdapter extends RecyclerView.Adapter<DemoHomeRecyclerAdapter.MyViewHolder>{

    ArrayList<ArrayList<ToolModelClass>> homeData;
    Context context;

    public DemoHomeRecyclerAdapter(ArrayList<ArrayList<ToolModelClass>> homeData, Context context){
        this.homeData = homeData;
        this.context = context;
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

        if (homeData.get(position).get(0).getCategory().equalsIgnoreCase("Stories")){

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

                                    case "RTO Vehicle Details":
                                        context.startActivity(new Intent(context, DemoMyCarDetailActivity.class));
                                        break;

                                    case "Maintenance Reminders":
                                        context.startActivity(new Intent(context, DemoSecondActivity.class) //create demo second activity
                                                .putExtra("TAG", "MyCarsListFragment"));
                                        break;


                                    case "Emergency Contacts":
                                        context.startActivity(new Intent(context, Emergencyactivity.class));
                                        break;

                                    /*case "Car Health":
                                        context.startActivity(new Intent(context, SecondActivity.class)
                                                .putExtra("TAG", "PerformanceFragment"));
                                        break;*/

                                    case "Car Scan":
                                        context.startActivity(new Intent(context, DemoSecondActivity.class)
                                                .putExtra("TAG", "DemoCarScanFragment"));
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

        //------------------------
        /*if (homeData.get(position).get(0).getCategory().equalsIgnoreCase("Driver training program")){

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


        } else if (homeData.get(position).get(0).getCategory().equalsIgnoreCase("Tools")){

            holder.recyclerView.setAdapter(new ToolsListRecyclerAdapter(homeData.get(position), context));

            holder.recyclerView.addOnItemTouchListener(
                    new RecyclerItemClickListener(context, holder.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int innerPosition) {
                            // do whatever
                            //Send brand name back
                            if (homeData.get(position).get(innerPosition).getRedirectUrl().contains("http")) {

                            *//*Intent in = new Intent(context, SampleWebviewActivity.class);
                            in.putExtra("Url", localtoolsModelClasses.get(position1).getRedirectUrl());
                            in.putExtra("Title", localtoolsModelClasses.get(position1).getTitle());
                            context.startActivity(in);*//*

                            *//*Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(toolsModelClasses.get(position).getRedirectUrl()));
                            context.startActivity(browserIntent);*//*

                                context.startActivity(new Intent(context, DemoMyCarDetailActivity.class));



                            } else {

                                if (homeData.get(position).get(innerPosition).getTitle().equalsIgnoreCase("Emergency Contacts")) {
                                    context.startActivity(new Intent(context, Emergencyactivity.class));

                                } else if (homeData.get(position).get(innerPosition).getTitle().equalsIgnoreCase("Maintenance Reminders")) {
                                    context.startActivity(new Intent(context, DemoSecondActivity.class) //create demo second activity
                                            .putExtra("TAG", "MyCarsListFragment")
                                    );
                                }
                            }

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

    @Override
    public int getItemCount() {
        return homeData.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;
        TextView textView;

        public MyViewHolder(View view) {
            super(view);
            recyclerView = view.findViewById(R.id.home_recyclerview_tile_recyclerview);
            textView = view.findViewById(R.id.home_recyclerview_tile_heading);
            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
            linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
            recyclerView.setLayoutManager(linearLayoutManager);
        }
    }


}
