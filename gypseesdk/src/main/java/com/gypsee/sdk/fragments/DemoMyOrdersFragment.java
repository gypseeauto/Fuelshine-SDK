package com.gypsee.sdk.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import com.gypsee.sdk.Adapters.DemoOrderListAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentMyOrdersBinding;
import com.gypsee.sdk.models.MyOrderModel;

public class DemoMyOrdersFragment extends Fragment {

    private Context context;
    private FragmentMyOrdersBinding fragmentMyOrdersBinding;


    private String TAG = DemoMyOrdersFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentMyOrdersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_orders, container, false);


        initToolbar();

        initStaticView();



        return fragmentMyOrdersBinding.getRoot();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    private void initToolbar(){

        Toolbar toolbar = fragmentMyOrdersBinding.toolBarLayout.toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);

        fragmentMyOrdersBinding.toolBarLayout.setTitle("My Orders");
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }


    private void goBack() {
        ((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();
    }


    private DemoOrderListAdapter orderListAdapter;

    private void initStaticView(){


        fragmentMyOrdersBinding.noOrders.setVisibility(View.GONE);

        myOrderArrayList.add(new MyOrderModel(
                "10% Discount on drivemate subscription",
                "",
                "COMPLETE",
                "DEMO CODE",
                "12-10-2022",
                "12345",
                "12345",
                null,
                "",
                "",
                true,
                "1",
                "100",
                "12345"
        ));

        orderListAdapter = new DemoOrderListAdapter(myOrderArrayList, context, getActivity());

        fragmentMyOrdersBinding.orderList.setAdapter(orderListAdapter);
        fragmentMyOrdersBinding.orderList.setLayoutManager(new LinearLayoutManager(context));

        fragmentMyOrdersBinding.swipeLayout.setColorSchemeResources(R.color.colorPrimary);


    }



    private ArrayList<MyOrderModel> myOrderArrayList = new ArrayList<>();















}
