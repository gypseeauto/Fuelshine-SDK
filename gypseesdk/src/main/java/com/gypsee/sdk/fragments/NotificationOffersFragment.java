package com.gypsee.sdk.fragments;


import static com.gypsee.sdk.utils.Constants.goToDashboard;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;

import com.gypsee.sdk.Adapters.OffersRecyclerAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.activities.SecondActivity;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.FragmentNotificationOffersBinding;
import com.gypsee.sdk.models.OffersModel;
import com.gypsee.sdk.utils.RecyclerItemClickListener;

/**
 * A simple {@link Fragment} subclass.
 */
public class NotificationOffersFragment extends Fragment {


    private FragmentNotificationOffersBinding fragmentNotificationOffersBinding;


    public NotificationOffersFragment() {
        // Required empty public constructor
    }

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentNotificationOffersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_notification_offers, container, false);
        context = getContext();
        View view = fragmentNotificationOffersBinding.getRoot();
        registerBroadcastReceivers();
        setUpRecyclerView();
        return view;
    }

    private String TAG = NotificationOffersFragment.class.getSimpleName();

    private void setUpRecyclerView() {

        final DatabaseHelper databaseHelper = new DatabaseHelper(context);
        offersModelArrayList = databaseHelper.fetchAllNotification();
        Collections.reverse(offersModelArrayList);
        int notificationCount = offersModelArrayList.size();
        Log.e(TAG, "Notification size  :" + notificationCount);

        fragmentNotificationOffersBinding.notificationLabel.setText(notificationCount + " Notification(s)");

        final OffersRecyclerAdapter offersRecyclerAdapter = new OffersRecyclerAdapter(context, offersModelArrayList);

       /* SwipeableRecyclerView offersRecycleriew = fragmentNotificationOffersBinding.offersRecycleriew;
        offersRecycleriew.setAdapter(offersRecyclerAdapter);

        offersRecycleriew.setListener(new SwipeLeftRightCallback.Listener() {

            @Override
            public void onSwipedLeft(int position) {
                // mList.remove(position);
                // mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onSwipedRight(int position) {
                offersRecyclerAdapter.notifyItemRemoved(position);
                databaseHelper.deleteNotification(offersModelArrayList.get(position).getId());
                offersModelArrayList.remove(position);
                sendBroadcastToHomeFragment();

                if (offersModelArrayList.size() == 0) {
                    Toast.makeText(context, "All notifications are cleared", Toast.LENGTH_LONG).show();
                    goback();
                }
            }
        });

        offersRecycleriew.addOnItemTouchListener(
                new RecyclerItemClickListener(context, offersRecycleriew, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        //Send brand name back
                        if (offersModelArrayList.get(position).getNotificationType().equals("TripEnd")) {
                            LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("ShowTrips"));
                            requireActivity().finish();
                            Intent in = new Intent(getActivity(), GypseeMainActivity.class);
                            in.putExtra("action", goToDashboard);
                            startActivity(in);

                        } else if (offersModelArrayList.get(position).getNotificationType().equals("ServiceReminderNotification")) {
                            //Navigate to offers page of gypsee.ai. User will get discounts on this.
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gypsee.ai/offers/"));
                            startActivity(browserIntent);
                        }else if(offersModelArrayList.get(position).getNotificationType().equals("general")){
                            if(offersModelArrayList.get(position).getDescription().toLowerCase().contains("sos")){
                                startActivity(new Intent(getActivity(), SecondActivity.class)
                                        .putExtra("TAG", "SafetyCenterFragment"));
                            }else if(offersModelArrayList.get(position).getDescription().toLowerCase().contains("bluetooth")){
                                startActivity(new Intent(getActivity(), SecondActivity.class)
                                        .putExtra("TAG", "MyDevicesFragment"));
                            }else{
                                startActivity(new Intent(getActivity(), SecondActivity.class)
                                        .putExtra("TAG", "MyCarsListFragment"));
                            }
                        }

                        goback();

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );*/

    }

    private void sendBroadcastToHomeFragment() {
        //Send broadcast to home fragment regarding the ntoification count increase.
        Intent intent = new Intent("NotificationCount");
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }

    private void goback() {
        ((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();
    }

    private ArrayList<OffersModel> offersModelArrayList = new ArrayList<>();

    private void registerBroadcastReceivers() {

        //THis is to receive the obd commands from the device . Regarding the vehcile


        IntentFilter filter = new IntentFilter();
        filter.addAction("NotificationCount");

        //this is for receiving the notification count etc
        LocalBroadcastManager.getInstance(context).registerReceiver(
                notificationCountBroadcastReceiver, filter);
    }

    private BroadcastReceiver notificationCountBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("NotificationCount"))
                setUpRecyclerView();

        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(notificationCountBroadcastReceiver);
    }
}
