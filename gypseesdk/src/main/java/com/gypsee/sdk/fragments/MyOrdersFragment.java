package com.gypsee.sdk.fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.gypsee.sdk.Adapters.OrderListAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentMyOrdersBinding;
import com.gypsee.sdk.models.MyOrderModel;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.UserOrderModel;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyOrdersFragment extends Fragment {

    private Context context;
    private FragmentMyOrdersBinding fragmentMyOrdersBinding;
    MyPreferenece myPreferenece;

    private String TAG = MyOrdersFragment.class.getSimpleName();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentMyOrdersBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_my_orders, container, false);

        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);

        initToolbar();

        initStaticView();

        fetchUserOrders();



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
        if(!((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate()){
            context.startActivity(new Intent(context, GypseeMainActivity.class));
            ((AppCompatActivity) context).finish(); //deep linking purpose
        }
    }


    private OrderListAdapter orderListAdapter;

    private void initStaticView(){

        currentOrderIndex = 0;

        fragmentMyOrdersBinding.noOrders.setVisibility(View.GONE);

        orderListAdapter = new OrderListAdapter(myOrderArrayList, context, getActivity());

        fragmentMyOrdersBinding.orderList.setAdapter(orderListAdapter);
        fragmentMyOrdersBinding.orderList.setLayoutManager(new LinearLayoutManager(context));

        fragmentMyOrdersBinding.orderList.setVisibility(View.GONE);
        fragmentMyOrdersBinding.progressBar.setVisibility(View.VISIBLE);

        fragmentMyOrdersBinding.swipeLayout.setColorSchemeResources(R.color.colorPrimary);

        fragmentMyOrdersBinding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshPage();
            }
        });

    }


    private void refreshPage(){

        currentOrderIndex = 0;
        myOrderArrayList.clear();
        orderIDs.clear();
        fragmentMyOrdersBinding.noOrders.setVisibility(View.GONE);

        fragmentMyOrdersBinding.orderList.setVisibility(View.GONE);
        fragmentMyOrdersBinding.progressBar.setVisibility(View.VISIBLE);

        orderListAdapter.notifyDataSetChanged();

        fetchUserOrders();

    }


    /*
    * 1. fetch order IDs from backend
    * 3. if status processing or failed:
    *       call order details API
    *    else
    *       call activated cards API
    * 4. populate the recyclerview at the end (for now)
    * */



    private ArrayList<MyOrderModel> myOrderArrayList = new ArrayList<>();

    private ArrayList<UserOrderModel> orderIDs = new ArrayList<>();

    private int currentOrderIndex = 0;



    private void callOrderFetch(){

        Log.e(TAG, "coming in callorderfetch");

        if (orderIDs.get(currentOrderIndex).getStatus().equalsIgnoreCase("COMPLETE")
                && orderIDs.get(currentOrderIndex).getOrderId() != null){

            Log.e(TAG, "coming in fetch activated cards");
            //call activated cards API
            callServer(getString(R.string.storeActivatedCards).replace("id", orderIDs.get(currentOrderIndex).getOrderId()), "Fetch Activated Cards", 3);


        } else {

            Log.e(TAG, "coming in call product api");

            if (orderIDs.get(currentOrderIndex).getSku() == null) {

                String rawStatus = orderIDs.get(currentOrderIndex).getStatus();
                String status;
                if (
                        rawStatus.equalsIgnoreCase("CANCELLED")
                                || rawStatus.equalsIgnoreCase("CANCELED")
                ) {
                    status = "<span style=\"color: red\">CANCELLED</span>";
                } else {
                    status = rawStatus;
                }


                myOrderArrayList.add(new MyOrderModel(
                        "Order",
                        "",
                        status,
                        null,
                        null,
                        null,
                        null,
                        orderIDs.get(currentOrderIndex).getErrorInfo(),
                        null,
                        null,
                        true,
                        null,
                        orderIDs.get(currentOrderIndex).getAmount(),
                        orderIDs.get(currentOrderIndex).getRefNo()
                ));

                incrementOrderIndex();
                checkIfOrderIdNull();

            } else {

                //call Product API
                callServer(getString(R.string.storeFetchProduct).replace("sku", orderIDs.get(currentOrderIndex).getSku()), "Fetch Product", 2);

            }


        }


    }


    private void checkIfOrderIdNull(){

        /*
        * If orderId is coming null (due to timeout error)
        * call status API uing refno to fetch orderId
        * */

        if (currentOrderIndex >= orderIDs.size()){

            orderListAdapter.notifyDataSetChanged();
            fragmentMyOrdersBinding.progressBar.setVisibility(View.GONE);
            fragmentMyOrdersBinding.noOrders.setVisibility(View.GONE);
            fragmentMyOrdersBinding.orderList.setVisibility(View.VISIBLE);

            if (fragmentMyOrdersBinding.swipeLayout.isRefreshing()){
                fragmentMyOrdersBinding.swipeLayout.setRefreshing(false);
            }


            return;

        }

        if (orderIDs.get(currentOrderIndex).getStatus().equals("PROCESSING")
                || orderIDs.get(currentOrderIndex).getStatus().equals("PENDING")
                || orderIDs.get(currentOrderIndex).getOrderId() == null){
            //call status API
            callServer(getString(R.string.storeOrderStatus).replace("refno", orderIDs.get(currentOrderIndex).getRefNo()), "Fetch Order Status", 4);
        } else {
            callOrderFetch();
        }

    }


    private void parseFetchOrderStatus(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);

        if (jsonObject.has("status")){
            orderIDs.get(currentOrderIndex).setStatus(jsonObject.getString("status"));
        }
        String orderID = jsonObject.has("orderId") ? jsonObject.getString("orderId") : "";
        if (orderID.equalsIgnoreCase("null") || orderID.equals("")){
            orderID = null;
        }
        orderIDs.get(currentOrderIndex).setOrderId(orderID);



//        if (orderIDs.get(currentOrderIndex).getOrderId().equals("")){


            callOrderFetch();

            /*if (orderIDs.get(currentOrderIndex).getSku() == null) {

                String rawStatus = orderIDs.get(currentOrderIndex).getStatus();
                String status;
                if (
                        rawStatus.equalsIgnoreCase("CANCELLED")
                                || rawStatus.equalsIgnoreCase("CANCELED")
                ) {
                    status = "<span style=\"color: red\">CANCELLED</span>";
                } else {
                    status = rawStatus;
                }


                myOrderArrayList.add(new MyOrderModel(
                        "Order",
                        "",
                        status,
                        null,
                        null,
                        null,
                        null,
                        orderIDs.get(currentOrderIndex).getErrorInfo(),
                        null,
                        null,
                        true,
                        null,
                        orderIDs.get(currentOrderIndex).getAmount(),
                        orderIDs.get(currentOrderIndex).getRefNo()
                ));

                incrementOrderIndex();
                checkIfOrderIdNull();

            } else {
                callOrderFetch();
            }

        } else {
            callOrderFetch();
        }*/

    }









    private void parseFetchUserOrders(String response) throws JSONException {

        orderIDs.clear();

        JSONObject jsonObject = new JSONObject(response);

        if (!jsonObject.has("orderId")){
            fragmentMyOrdersBinding.swipeLayout.setRefreshing(false);
            fragmentMyOrdersBinding.progressBar.setVisibility(View.GONE);
            fragmentMyOrdersBinding.noOrders.setVisibility(View.VISIBLE);
            return;
        }

        JSONArray orders = jsonObject.getJSONArray("orderId");

        for (int i=0; i<orders.length(); i++){

            JSONObject arrayItem = orders.getJSONObject(i);

            String status = arrayItem.has("status") ? arrayItem.getString("status") : "";
            String orderId = arrayItem.has("orderId") ? arrayItem.getString("orderId") : "";
            if (orderId.equalsIgnoreCase("null") || orderId.equals("")){
                orderId = null;
            }
            String errorInfo = arrayItem.has("errorInfo") ? arrayItem.getString("errorInfo") : "";
            if (errorInfo.equalsIgnoreCase("null") || errorInfo.equals("")){
                errorInfo = null;
            }
            String refNo = arrayItem.has("refno") ? arrayItem.getString("refno") : "";
            if(refNo.equalsIgnoreCase("null" ) || refNo.equals("")){
                refNo = null;
            }
            String sku = arrayItem.has("sku") ? arrayItem.getString("sku") : "";
            if (sku.equalsIgnoreCase("null") || sku.equals("")){
                sku = null;
            }
            String amount = arrayItem.has("amount") ? arrayItem.getString("amount") : "";
            if (amount.equalsIgnoreCase("null") || amount.equals("")){
                amount = null;
            }




            orderIDs.add(new UserOrderModel(orderId, refNo, status, errorInfo, sku, amount));

        }



        if (orderIDs.size() == 0){
            fragmentMyOrdersBinding.swipeLayout.setRefreshing(false);
            fragmentMyOrdersBinding.progressBar.setVisibility(View.GONE);
            fragmentMyOrdersBinding.noOrders.setVisibility(View.VISIBLE);
        } else {
            //callOrderFetch();
            checkIfOrderIdNull();
        }



    }


    private void fetchUserOrders(){

        callServer(getString(R.string.storeFetchUserOrders).replace("userId", myPreferenece.getUser().getUserId()), "Fetch User Orders", 1);

    }





    private void incrementOrderIndex(){
        currentOrderIndex += 1;
    }


    private void parseFetchActivatedCards(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);

        JSONArray cardsArray = jsonObject.getJSONArray("cards");

        for (int i=0; i<cardsArray.length(); i++){

            JSONObject card = cardsArray.getJSONObject(i);

            String sku = card.has("sku") ? card.getString("sku") : "";

            String image = "";
            if (!sku.equals("")) {
                JSONObject skuObject = jsonObject.getJSONObject("products").has(sku)
                        ? jsonObject.getJSONObject("products").getJSONObject(sku) : null;
                if (skuObject != null) {
                    JSONObject imageObject = skuObject.has("images") ? skuObject.getJSONObject("images") : null;
                    if (imageObject != null) {
                        image = imageObject.has("mobile") ? imageObject.getString("mobile") : "";
                    }
                }
            }

            String name = card.has("productName") ? card.getString("productName") : "";
            String activationCode = card.has("activationCode") ? card.getString("activationCode") : null;
            String cardNumber = card.has("cardNumber") ? card.getString("cardNumber") : null;
            String cardPin = card.has("cardPin") ? card.getString("cardPin") : null;
            if (cardPin.equalsIgnoreCase("null")){
                cardPin = null;
            }
            String expiry = card.has("validity") ? card.getString("validity") : null;
            if (expiry != null) {
                String[] eSplit = expiry.split("T"); //try using DateTimeFormatter
                expiry = eSplit[0];
            }
            String activationUrl = card.has("activationUrl") ? card.getString("activationUrl") : null;

            boolean isCardActivated = card.has("isCardActivated") && card.getBoolean("isCardActivated");


            myOrderArrayList.add(new MyOrderModel(
                    name,
                    image,
                    "COMPLETE",
                    activationCode,
                    expiry,
                    cardNumber,
                    cardPin,
                    null,
                    sku,
                    activationUrl,
                    isCardActivated,
                    orderIDs.get(currentOrderIndex).getOrderId(),
                    orderIDs.get(currentOrderIndex).getAmount(),
                    orderIDs.get(currentOrderIndex).getRefNo()
            ));

        }

        incrementOrderIndex();
        checkIfOrderIdNull();

    }




    private void callServer(String url, final String purpose, int value){


        ApiInterface apiInterface = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();
        User user = myPreferenece.getUser();

        switch (value){

            default:
            case 1: //fetch user orders
            case 2: //product api
            case 3: //get activated cards
            case 4: //get order status

                call = apiInterface.getObdDevice(user.getUserAccessToken());
                break;


        }


        Log.e(TAG, purpose + " Input : " + jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                try {

                    if (response.isSuccessful()){

                        Log.e(TAG, "Response is success");

                        ResponseBody responseBody = response.body();
                        if (responseBody == null){
                            return;
                        }


                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Resonse : " + responseStr);

                        switch (value){

                            case 1:
                                parseFetchUserOrders(responseStr);
                                break;




                            case 3:
                                parseFetchActivatedCards(responseStr);
                                break;



                            case 4:
                                parseFetchOrderStatus(responseStr);
                                break;

                            case 2:
                                parseFetchProduct(responseStr);
                                break;


                        }

                    } else {
                        Log.e(TAG, purpose + " Response is not successful");

                        fragmentMyOrdersBinding.progressBar.setVisibility(View.GONE);
                        String errResponse = response.errorBody().string();
                        Log.e("Error code 400", errResponse);
                        Log.e(TAG, purpose + "Response is : " + errResponse);
                        Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(context);
                            goBack();
                            return;
                        }
                    }

                } catch (Exception e){
                    e.printStackTrace();
                    fragmentMyOrdersBinding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                    //goBack();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.e(TAG, "error here since request failed");
                fragmentMyOrdersBinding.progressBar.setVisibility(View.GONE);
                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Please check your internet connection");
                }

            }
        });

    }


    private void parseFetchProduct(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);

        String name = jsonObject.has("name") ? jsonObject.getString("name") : "Order";
        String mobileImage = jsonObject.has("images")
                ? jsonObject.getJSONObject("images").has("mobile")
                ? jsonObject.getJSONObject("images").getString("mobile")
                : ""
                : "";

        String rawStatus = orderIDs.get(currentOrderIndex).getStatus();
        String status;
        if (
                rawStatus.equalsIgnoreCase("CANCELLED")
                || rawStatus.equalsIgnoreCase("CANCELED")
        ){
            status = "<span style=\"color: red\">CANCELLED</span>";
        } else {
            status = rawStatus;
        }


        myOrderArrayList.add(new MyOrderModel(
                name,
                mobileImage,
                status,
                null,
                null,
                null,
                null,
                orderIDs.get(currentOrderIndex).getErrorInfo(),
                null,
                null,
                true,
                null,
                orderIDs.get(currentOrderIndex).getAmount(),
                orderIDs.get(currentOrderIndex).getRefNo()
        ));

        incrementOrderIndex();
        checkIfOrderIdNull();

    }














}
