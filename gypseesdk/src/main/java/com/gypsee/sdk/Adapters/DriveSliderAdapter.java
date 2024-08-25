package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.models.DriveSliderItem;

public class DriveSliderAdapter extends RecyclerView.Adapter<DriveSliderAdapter.ViewHolder>{

    private ArrayList<DriveSliderItem> sliderItems;
    private int pagePosition;
    private ViewPager2 viewPager2;
    private Context context;
    private Location currentLocation;


    public DriveSliderAdapter(ArrayList<DriveSliderItem> sliderItems, ViewPager2 viewPager2, Context context) {
        this.sliderItems = sliderItems;
        this.pagePosition = 1;
        this.viewPager2 = viewPager2;
        this.context = context;
    }

    public void setCurrentLocation(Location location){
        this.currentLocation = location;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.drive_scroll_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setImage(sliderItems.get(position));

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                switch (holder.getAdapterPosition()){

                    case 0:
                        //to open dialer
                        Intent intent = new Intent(Intent.ACTION_DIAL);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(intent);
                        break;

                    case 1:
                        //navigation
                        Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.google.com/maps/@?api=1&map_action=map&parameters"));
                        mapsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        context.startActivity(mapsIntent);
                        break;

                    case 2:
                        //music
                        try {
                            Intent musicIntent = Intent.makeMainSelectorActivity(Intent.ACTION_MAIN, Intent.CATEGORY_APP_MUSIC);
                            musicIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(musicIntent);
                        } catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(context, "No music app installed.", Toast.LENGTH_SHORT).show();
                        }
                        break;

                    case 3:
                        //find parkings
                        if (currentLocation != null){
                            Uri intentUri = Uri.parse("geo:" + currentLocation.getLatitude() +
                                    "," + currentLocation.getLongitude() + "?q=Parking");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (mapIntent.resolveActivity(context.getPackageManager()) != null){
                                context.startActivity(mapIntent);
                            }
                        } else {
                            Intent mapIntent2 = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://www.google.com/maps/search/?api=1&query=Parking"));
                            mapIntent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(mapIntent2);
                        }
                        break;

                    case 4:
                        //share location
                        if (currentLocation != null){
                            Intent shareLocationIntent = new Intent(Intent.ACTION_SEND);
                            shareLocationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            shareLocationIntent.putExtra(Intent.EXTRA_TEXT, "https://www.google.com/maps/search/?api=1&query="
                                    + currentLocation.getLatitude() + "%2C" + currentLocation.getLongitude());
                            shareLocationIntent.setType("text/plain");
                            context.startActivity(shareLocationIntent);
                        }
                        break;
                    case 5:
                        //nearby gas stations
                        if (currentLocation != null){
                            Uri intentUri = Uri.parse("geo:" + currentLocation.getLatitude() +
                                    "," + currentLocation.getLongitude() + "?q=fuel_station");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (mapIntent.resolveActivity(context.getPackageManager()) != null){
                                context.startActivity(mapIntent);
                            }
                        } else {
                            Intent mapIntent2 = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://www.google.com/maps/search/?api=1&query=fuel_station"));
                            mapIntent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(mapIntent2);
                        }
                        break;
                    case 6:
                        //nearby ev charging stations
                        if (currentLocation != null){
                            Uri intentUri = Uri.parse("geo:" + currentLocation.getLatitude() +
                                    "," + currentLocation.getLongitude() + "?q=ev_charger");
                            Intent mapIntent = new Intent(Intent.ACTION_VIEW, intentUri);
                            mapIntent.setPackage("com.google.android.apps.maps");
                            mapIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            if (mapIntent.resolveActivity(context.getPackageManager()) != null){
                                context.startActivity(mapIntent);
                            }
                        } else {
                            Intent mapIntent2 = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://www.google.com/maps/search/?api=1&query=ev_charger"));
                            mapIntent2.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(mapIntent2);
                        }
                        break;

                }

            }
        });
    }

    public void setSelectedPagePosition(int pagePosition){
        int oldPosition = this.pagePosition;
        viewPager2.post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(oldPosition);
            }
        });

        this.pagePosition = pagePosition;

        viewPager2.post(new Runnable() {
            @Override
            public void run() {
                notifyItemChanged(pagePosition);
            }
        });



        /*notifyItemChanged(this.pagePosition);
        this.pagePosition = pagePosition;
        notifyItemChanged(this.pagePosition);*/
    }

    @Override
    public int getItemCount() {
        return sliderItems.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        private ImageView imageView;
        private RelativeLayout cardView;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.scroll_image);
            cardView = itemView.findViewById(R.id.scroll_card);
        }

        void setImage(DriveSliderItem driveSliderItem){
            imageView.setImageResource(driveSliderItem.getImage());
        }

    }

}
