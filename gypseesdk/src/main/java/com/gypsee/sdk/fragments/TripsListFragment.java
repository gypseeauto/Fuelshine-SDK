package com.gypsee.sdk.fragments;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import com.gypsee.sdk.Adapters.TripsListAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentTripsBinding;
import com.gypsee.sdk.models.TripListResponse;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.UserTrips;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.util.ArrayList;

public class TripsListFragment extends Fragment {


    private Context context;
    private RecyclerView tripList;
    TextView empty;

    private FragmentTripsBinding fragmentTripsBinding;
    private User user;
    private TripsListAdapter tripsListAdapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        fragmentTripsBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_trips, container, false);
        context = getContext();
        user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser();
        initView();
        callServer(getResources().getString(R.string.tripListUrl) .replace("userId", user.getUserId()), "Fetch trips", 0);
        return fragmentTripsBinding.getRoot();
    }

    private void initView() {
        fragmentTripsBinding.toolBarLayout.setTitle("My Trips");
        fragmentTripsBinding.toolBarLayout.toolbar.setNavigationIcon(R.drawable.back_button);
        fragmentTripsBinding.toolBarLayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((AppCompatActivity) context).getSupportFragmentManager().popBackStack();

            }
        });
        final LinearLayoutManager layoutManager = new LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false);
        fragmentTripsBinding.tripListRecyclerview.setLayoutManager(layoutManager);

        RecyclerView.OnScrollListener recyclerViewOnScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {
                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount
                            && firstVisibleItemPosition >= 0
                            && totalItemCount >= 10) {

                        callServer(getResources().getString(R.string.tripListUrl).replace("userId", user.getUserId()), "Fetch trips", 0);


                    }
                }
            }
        };

        tripsListAdapter = new TripsListAdapter(context, userTripsArrayList);
        fragmentTripsBinding.tripListRecyclerview.setOnScrollListener(recyclerViewOnScrollListener);
        fragmentTripsBinding.tripListRecyclerview.setAdapter(tripsListAdapter);
    }

    private String TAG = TripsListFragment.class.getSimpleName();

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    int pageNo;
    boolean isLastPage = false, isLoading = true;
    ArrayList<UserTrips> userTripsArrayList = new ArrayList<>();

    private void callServer(String url, final String purpose, final int value) {

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);

        Call<TripListResponse> call;
        JsonObject jsonObject = new JsonObject();

        switch (value) {
            default:
                //llProgress.setVisibility(View.VISIBLE);
                isLoading = true;
                fragmentTripsBinding.progressBar.setVisibility(View.VISIBLE);
                call = apiService.getTripList(user.getUserAccessToken(), 10, pageNo);
                break;
        }


        call.enqueue(new Callback<TripListResponse>() {
            @Override
            public void onResponse(Call<TripListResponse> call, Response<TripListResponse> response) {
                // llProgress.setVisibility(View.GONE);

                isLoading = false;
                fragmentTripsBinding.progressBar.setVisibility(View.GONE);
                try {

                    if (response.isSuccessful()) {
                      Log.e(TAG, "Response is success");
                        TripListResponse tripListResponse = response.body();
                        String responseStr = tripListResponse.toString();
                      Log.e(TAG, purpose + " Response :" + responseStr);

                        switch (value) {

                            case 0:
                                userTripsArrayList.addAll(tripListResponse.getUserTrips());
                                pageNo++;
                                if (tripListResponse.getUserTrips().size() < 10)
                                    isLastPage = true;
                                else {
                                    tripsListAdapter.notifyDataSetChanged();
                                }

                                if (userTripsArrayList.size() == 0) {
                                    fragmentTripsBinding.tvEmpty.setVisibility(View.VISIBLE);
                                } else {
                                    fragmentTripsBinding.tvEmpty.setVisibility(View.GONE);

                                }
                                //parseTripsList(responseStr);

                              Log.e(TAG, "List size : " + userTripsArrayList.size());
                                break;

                        }

                    } else {
                      Log.e(TAG, "Response is not succesfull");

                      Log.e("Error code 400", response.errorBody().string());

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

            }


            @Override
            public void onFailure(Call<TripListResponse> call, Throwable t) {
                //llProgress.setVisibility(View.GONE);
                //Logger.ErrorLog(TAG, "error here since request failed");
                if (call.isCanceled()) {

                } else {

                }
            }
        });
    }

}