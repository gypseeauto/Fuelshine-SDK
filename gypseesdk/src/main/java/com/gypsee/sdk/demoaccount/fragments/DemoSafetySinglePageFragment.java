package com.gypsee.sdk.demoaccount.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.gypsee.sdk.Adapters.SafetyInfoAdapter;
import com.gypsee.sdk.Adapters.SafetySOSAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.Emergencyactivity;
import com.gypsee.sdk.databinding.FragmentSafetySinglePageBinding;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.utils.RecyclerItemClickListener;

public class DemoSafetySinglePageFragment extends Fragment {

    public static String ARG_POSITION = "ARG_POSITION";

    //LocationManager locationManager;

    public static DemoSafetySinglePageFragment newInstance(int position) {

        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);

        DemoSafetySinglePageFragment fragment = new DemoSafetySinglePageFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private int position = 0;
    private FragmentSafetySinglePageBinding fragmentSafetySinglePageBinding;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            position = getArguments().getInt(ARG_POSITION);
        }
    }

    Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentSafetySinglePageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_safety_single_page, container, false);

        //locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);


        fragmentSafetySinglePageBinding.sosRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        if (position == 0) {
            fragmentSafetySinglePageBinding.sosRecyclerView.setAdapter(new SafetySOSAdapter());
            sosButtonListener();
        } else {
            fragmentSafetySinglePageBinding.sosRecyclerView.setAdapter(new SafetyInfoAdapter());
            setupInfoListOnTouchListener();
        }


        return fragmentSafetySinglePageBinding.getRoot();
    }

    private String sosType = "Accident";

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void sosButtonListener() {

        Log.e(TAG, "sos listener");

        fragmentSafetySinglePageBinding.sosRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, fragmentSafetySinglePageBinding.sosRecyclerView
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position == 1){
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.readyassist.in/")));
                } else {

                    BluetoothHelperClass.showOkCancelDialog(context, getLayoutInflater(), "Confirmation", "Are you sure want to use SOS?", (Response, className, value) -> {
                        if (value == 1) {

                            Toast.makeText(context, "SOS message sent", Toast.LENGTH_SHORT).show();
                        }
                    }, 1);

                }
            }


            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));

    }


    private void setupInfoListOnTouchListener(){
        fragmentSafetySinglePageBinding.sosRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(context, fragmentSafetySinglePageBinding.sosRecyclerView
                , new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                switch (position){

                    case 0:
                        Fragment fragmentOne = DemoSosInfoDetailFragment.newInstance("MEMBERS", "");
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mainFrameLayout, fragmentOne, DemoSosInfoDetailFragment.class.getSimpleName())
                                .addToBackStack(DemoSafetySinglePageFragment.class.getSimpleName())
                                .commit();
                        break;

                    case 1:
                        Fragment fragmentTwo = DemoSosInfoDetailFragment.newInstance("HOW_IT_WORKS", "");
                        getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.mainFrameLayout, fragmentTwo, DemoSosInfoDetailFragment.class.getSimpleName())
                                .addToBackStack(DemoSafetySinglePageFragment.class.getSimpleName())
                                .commit();
                        break;

                    case 2:
                        startActivity(new Intent(context, Emergencyactivity.class));
                        break;

                    case 3:
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.readyassist.in/")));
                        break;

                }

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));
    }

    private String TAG = DemoSafetySinglePageFragment.class.getSimpleName();





}

