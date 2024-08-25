package com.gypsee.sdk.demoaccount.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import com.gypsee.sdk.Adapters.MembersAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentSosInfoDetailBinding;
import com.gypsee.sdk.models.MemberModel;

public class DemoSosInfoDetailFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public DemoSosInfoDetailFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SosInfoDetailFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DemoSosInfoDetailFragment newInstance(String param1, String param2) {
        DemoSosInfoDetailFragment fragment = new DemoSosInfoDetailFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    Context context;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    private FragmentSosInfoDetailBinding fragmentSosInfoDetailBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentSosInfoDetailBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_sos_info_detail, container, false);
        initView();

        return fragmentSosInfoDetailBinding.getRoot();
    }

    @Override
    public void onResume() {
        super.onResume();
        initView();
    }

    private void initView(){

        fragmentSosInfoDetailBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();
            }
        });

        if (mParam1 != null){
            switch (mParam1){

                case "MEMBERS":
                    setupMemberList();
                    break;

                case "HOW_IT_WORKS":
                    setupHowItWorks();
                    break;


            }


            fragmentSosInfoDetailBinding.addMemberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DemoAddMemberDialogFragment addMemberDialogFragment = new DemoAddMemberDialogFragment();
                    addMemberDialogFragment.setCancelable(true);
                    addMemberDialogFragment.show(getChildFragmentManager(), "TAG");
                }
            });



        }
    }

    private void setupMemberList(){
        fragmentSosInfoDetailBinding.sosRecyclerView.setVisibility(View.VISIBLE);
        fragmentSosInfoDetailBinding.howItWorksScrollView.setVisibility(View.GONE);

        MemberModel model1 = new MemberModel("1", "Rick", "Bangalore", "9876543210", "abc@xyz.com", "Brother");
        MemberModel model2 = new MemberModel("1", "Ajay", "Gurgaon", "9876543210", "abc@xyz.com","Brother");
        MemberModel model3 = new MemberModel("1", "David", "Bangalore", "9876543210", "abc@xyz.com", "Brother");

        ArrayList<MemberModel> memberModelArrayList = new ArrayList<>();
        memberModelArrayList.add(model1);
        memberModelArrayList.add(model2);
        memberModelArrayList.add(model3);

        if (memberModelArrayList.size() > 0){
            fragmentSosInfoDetailBinding.noMembersText.setVisibility(View.GONE);
            fragmentSosInfoDetailBinding.sosRecyclerView.setAdapter(new MembersAdapter(memberModelArrayList));
            fragmentSosInfoDetailBinding.sosRecyclerView.setLayoutManager(new LinearLayoutManager(context));
        } else {
            fragmentSosInfoDetailBinding.noMembersText.setVisibility(View.VISIBLE);

        }



    }


    private void setupHowItWorks(){
        fragmentSosInfoDetailBinding.howItWorksScrollView.setVisibility(View.VISIBLE);
        fragmentSosInfoDetailBinding.sosRecyclerView.setVisibility(View.GONE);
    }




}
