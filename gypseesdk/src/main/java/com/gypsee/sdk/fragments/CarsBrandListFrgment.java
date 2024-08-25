package com.gypsee.sdk.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import com.gypsee.sdk.Adapters.VehicleMakeRecyclerAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentCarsBrandListFrgmentBinding;

import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.VehicleBrandImageModel;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.RecyclerItemClickListener;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarsBrandListFrgment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarsBrandListFrgment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private FragmentCarsBrandListFrgmentBinding fragmentCarsBrandListFrgmentBinding;

    public CarsBrandListFrgment() {
        // Required empty public constructor
    }

    private ArrayList<VehicleBrandImageModel> vehicleModels = new ArrayList<>();

    // TODO: Rename and change types and number of parameters
    public static CarsBrandListFrgment newInstance() {
        CarsBrandListFrgment fragment = new CarsBrandListFrgment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    private Context context;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentCarsBrandListFrgmentBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_cars_brand_list_frgment, container, false);
        context = getContext();

        initToolbar();
        callServer(getResources().getString(R.string.fetchVehicleMakesUrl), "Fetch Vehicle makes ", 0);

        return fragmentCarsBrandListFrgmentBinding.getRoot();
    }

    private void checkVehicleModelsAfterSomeTime() {
        //vehicleModels = new DatabaseHelper(context).fetchAllVehicleMakes();
        fragmentCarsBrandListFrgmentBinding.progressBar.setVisibility(View.GONE);
        if (vehicleModels.size() == 0) {
            fragmentCarsBrandListFrgmentBinding.emptyLabel.setVisibility(View.VISIBLE);

        } else {
            loadRecyclerview();
        }
    }

    private void loadRecyclerview() {
        fragmentCarsBrandListFrgmentBinding.recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context, RecyclerView.VERTICAL, false);
        fragmentCarsBrandListFrgmentBinding.recyclerView.setLayoutManager(linearLayoutManager);

        final VehicleMakeRecyclerAdapter vehicleMakeRecyclerAdapter = new VehicleMakeRecyclerAdapter(vehicleModels, context);
        fragmentCarsBrandListFrgmentBinding.recyclerView.setAdapter(vehicleMakeRecyclerAdapter);

        fragmentCarsBrandListFrgmentBinding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                vehicleMakeRecyclerAdapter.filter(fragmentCarsBrandListFrgmentBinding.searchEditText.getText().toString(), fragmentCarsBrandListFrgmentBinding.emptyLabel);

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        fragmentCarsBrandListFrgmentBinding.recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(context, fragmentCarsBrandListFrgmentBinding.recyclerView, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        //Send brand name back
                        Intent intent = new Intent("VehicleName");
                        intent.putExtra("brandName", vehicleModels.get(position).getBrand());
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                        goBack();
                    }
                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }


    private void initToolbar() {


        fragmentCarsBrandListFrgmentBinding.toolBarLayout.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);

        fragmentCarsBrandListFrgmentBinding.toolBarLayout.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });


    }

    private void goBack() {
        ((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();
    }

    private String TAG = CarsBrandListFrgment.class.getSimpleName();

    private void callServer(String url, final String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        User user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser();

        Call<ResponseBody> call;
        call = apiService.getDocTypes(user.getUserAccessToken(), false);


        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                try {
                    if (response.isSuccessful()) {
                       Log.e(TAG, "Response is successful");
                        String responseStr = response.body().string();
                       Log.e(TAG, purpose + " Response : " + responseStr);

                        parseVehicleMakes(responseStr);

                    } else {
                       Log.e(TAG, purpose + " Response is not Success : " + response.errorBody().string());
                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(context);
                            return;
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
               Log.e(TAG, "error here since request failed");
                if (call.isCanceled()) {

                } else {

                }
            }
        });
    }

    private void parseVehicleMakes(String responseStr) {
        vehicleModels.clear();
        try {
            JSONObject jsonObject = new JSONObject(responseStr);

            JSONArray jsonArray = jsonObject.getJSONArray("vehicleMakes");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject1 = jsonArray.getJSONObject(i);
                String vehicleMake = jsonObject1.getString("vehicleMake");
                String imageUrl = jsonObject1.has("imageUrl") ?jsonObject1.getString("imageUrl"):"";
                vehicleModels.add(new VehicleBrandImageModel(vehicleMake,imageUrl));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        checkVehicleModelsAfterSomeTime();

    }


}
