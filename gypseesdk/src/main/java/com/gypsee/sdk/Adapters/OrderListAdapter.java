package com.gypsee.sdk.Adapters;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.card.MaterialCardView;
import com.google.gson.JsonObject;

import java.util.ArrayList;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.fragments.ProductPageFragment;
import com.gypsee.sdk.models.MyOrderModel;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderListAdapter extends RecyclerView.Adapter<OrderListAdapter.ViewHolder>{


    private ArrayList<MyOrderModel> orderList;
    private Context context;
    private FragmentActivity activity;

    public OrderListAdapter(ArrayList<MyOrderModel> orderList, Context context, FragmentActivity activity) {
        this.orderList = orderList;
        this.context = context;
        this.activity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.my_order_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        if (orderList.get(position).getCardNumber() == null){
            holder.codeLayout.setVisibility(View.GONE);
        } else {
            holder.codeLayout.setVisibility(View.VISIBLE);
            holder.activationCode.setText(orderList.get(position).getCardNumber());
            holder.copyImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Copied to clipboard", holder.activationCode.getText().toString());
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (orderList.get(position).getExpiry() == null){
            holder.expiry.setVisibility(View.GONE);
        } else {
            holder.expiry.setVisibility(View.VISIBLE);
            holder.expiry.setText("Expiry: " + orderList.get(position).getExpiry());
        }

        if (orderList.get(position).getErrorInfo() == null){
            holder.errorInfo.setVisibility(View.GONE);
        } else {
            holder.errorInfo.setVisibility(View.VISIBLE);
            holder.errorInfo.setText("Info: " + orderList.get(position).getErrorInfo());
        }

        if (orderList.get(position).getCardPin() == null){
            holder.cardPin.setVisibility(View.GONE);
            if (orderList.get(position).getCardNumber() != null) {
                holder.cardPinInfo.setVisibility(View.VISIBLE);
                holder.cardPinInfo.setText("Info: PIN number is not required to redeem this code");
            } else {
                holder.cardPinInfo.setVisibility(View.GONE);
            }
        } else {
            holder.cardPin.setVisibility(View.VISIBLE);
            holder.cardPinInfo.setVisibility(View.GONE);
            holder.cardPin.setText(Html.fromHtml("Card Pin: <b>" + orderList.get(position).getCardPin() + "</b>"));
        }

        holder.name.setText(orderList.get(position).getName());
        holder.status.setText(Html.fromHtml("Status: <b>" + orderList.get(position).getStatus() + "</b>"));

        Glide.with(context)
                .load(orderList.get(position).getImage())
                .placeholder(R.drawable.ic_image_placeholder_new)
                .error(R.drawable.ic_broken_image_placeholder_new)
                .into(holder.image);

        if (orderList.get(position).getActivationUrl() != null){
            if (!orderList.get(position).getActivationUrl().equalsIgnoreCase("null")){
                if (orderList.get(position).isCardActivated()){
                    //holder.tapToActivate.setText("Voucher Active");
                    holder.tapToActivate.setVisibility(View.GONE);
                } else {
                    holder.tapToActivate.setVisibility(View.VISIBLE);
                    holder.tapToActivate.setText("Tap to Activate");
                    holder.tapToActivate.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String orderId = orderList.get(holder.getAdapterPosition()).getOrderId();
                            if (orderId == null) return;
                            callServer(context.getString(R.string.storeCheckActivateCard).replace("orderId", orderId), "TAP TO ACTIVATE", 1);
                        }
                    });
                }
            }
        }


        if (orderList.get(position).getAmount() == null){
            holder.amount.setVisibility(View.GONE);
        } else {
            holder.amount.setVisibility(View.VISIBLE);
            holder.amount.setText(Html.fromHtml("Amount: <b>" + orderList.get(position).getAmount() + "</b>"));
        }

        if (orderList.get(position).getRefNo() == null){
            holder.refNo.setVisibility(View.GONE);
        } else {
            holder.refNo.setVisibility(View.VISIBLE);
            holder.refNo.setText(Html.fromHtml("Ref No: <b>" + orderList.get(position).getRefNo() + "</b>"));
        }


        if (orderList.get(position).getStatus().equalsIgnoreCase("COMPLETE")){

            holder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProductPageFragment fragment = new ProductPageFragment(orderList.get(holder.getAdapterPosition()).getSku(),
                            orderList.get(holder.getAdapterPosition()).getActivationUrl(),
                            orderList.get(holder.getAdapterPosition()).getCardNumber(),
                            orderList.get(holder.getAdapterPosition()).getOrderId(),
                            orderList.get(holder.getAdapterPosition()).isCardActivated());
                    activity.getSupportFragmentManager()
                            .beginTransaction()
                            .add(R.id.mainFrameLayout, fragment, ProductPageFragment.class.getSimpleName())
                            .addToBackStack(null)
                            .commit();
                }
            });
        }

    }

    @Override
    public int getItemCount() {
        return orderList.size();
    }



    private String TAG = OrderListAdapter.class.getSimpleName();

    private void callServer(String url, final String purpose, int value){


        ApiInterface apiInterface = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();
        User user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser();

        switch (value){

            default:
            case 1: //call activate card api
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
                                Toast.makeText(context, "Card Activated Successfully", Toast.LENGTH_SHORT).show();
                                break;


                        }

                    } else {
                        Log.e(TAG, purpose + " Response is not successful");

                        String errResponse = response.errorBody().string();
                        Log.e("Error code 400", errResponse);
                        Log.e(TAG, purpose + "Response is : " + errResponse);
                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(context);
                            return;
                        }
                    }

                } catch (Exception e){
                    e.printStackTrace();
                    Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                    //goBack();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.e(TAG, "error here since request failed");
                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Please check your internet connection");
                }

            }
        });

    }









    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView image, copyImage;
        TextView name, status, activationCode, expiry, cardPin, errorInfo, cardPinInfo, tapToActivate, amount, refNo;
        LinearLayout codeLayout;
        MaterialCardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.card_image);
            name = itemView.findViewById(R.id.product_name);
            status = itemView.findViewById(R.id.product_status);
            activationCode = itemView.findViewById(R.id.coupon_code);
            expiry = itemView.findViewById(R.id.product_expiry);
            codeLayout = itemView.findViewById(R.id.code_layout);
            //cardNumber = itemView.findViewById(R.id.card_number);
            cardPin = itemView.findViewById(R.id.card_pin);
            cardPinInfo = itemView.findViewById(R.id.card_pin_info);
            copyImage = itemView.findViewById(R.id.copy_image);
            errorInfo = itemView.findViewById(R.id.error_info);
            cardView = itemView.findViewById(R.id.order_item_card);
            tapToActivate = itemView.findViewById(R.id.tap_to_activate);
            amount = itemView.findViewById(R.id.order_amount);
            refNo = itemView.findViewById(R.id.ref_no);
        }
    }

}
