package com.gypsee.sdk.fragments;

import android.app.AlertDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.telephony.PhoneNumberUtils;
import android.text.Html;
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
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.bumptech.glide.Glide;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.AlertAddcarLayoutBinding;
import com.gypsee.sdk.databinding.FragmentProductPageBinding;
import com.gypsee.sdk.databinding.LoadingDialogBinding;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.models.StoreProductModel;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProductPageFragment extends Fragment {

    private String sku;
    @Nullable
    private String activationUrl;
    @Nullable
    private String voucherCode;

    @Nullable
    private String orderId;
    private boolean isCardActivated;

    public ProductPageFragment(String sku,
                               @Nullable String activationUrl,
                               @Nullable String voucherCode,
                               @Nullable String orderId,
                               boolean isCardActivated){
        this.sku = sku;
        this.activationUrl = activationUrl;
        this.voucherCode = voucherCode;
        this.orderId = orderId;
        this.isCardActivated = isCardActivated;
    }

    private Context context;
    private FragmentProductPageBinding fragmentProductPageBinding;
    MyPreferenece myPreferenece;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentProductPageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_page, container, false);

        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);

        initToolbar();

        initStaticViews();


        return fragmentProductPageBinding.getRoot();
    }

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    private String TAG = ProductPageFragment.class.getSimpleName();

    private void initToolbar(){

        Toolbar toolbar = fragmentProductPageBinding.toolBarLayout.toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);

        fragmentProductPageBinding.toolBarLayout.setTitle("");
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



    private void initStaticViews(){

        fragmentProductPageBinding.productScrollView.setVisibility(View.GONE);
        fragmentProductPageBinding.buyLayout.setVisibility(View.GONE);

        fragmentProductPageBinding.progressBar.setVisibility(View.VISIBLE);

        //fragmentProductPageBinding.progressLayout.setVisibility(View.GONE);


        callServer(getString(R.string.storeFetchProduct).replace("sku", this.sku), "Fetch Product", 1);

        fragmentProductPageBinding.buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //enable this later

                /*BluetoothHelperClass.showCustomOptionDialog(
                        context,
                        getLayoutInflater(),
                        "No",
                        "Yes",
                        "Confirm Purchase",
                        "Are you sure want to buy this voucher? "
                                + ((productModel != null) ? productModel.getAmount() : "") +
                                " coins will be debited from your wallet.",
                        (Response, className, value) -> {
                            switch (value){
                                case 0:

                                    showLoading();

                                    callServer(getString(R.string.storeCreateOrder), "Create Order", 2);
                                    break;
                            }
                        }
                        );
*/

            }
        });

    }


    private String getPhoneInE164Format(){
        return PhoneNumberUtils.formatNumberToE164(myPreferenece.getUser().getUserPhoneNumber(), "IN");
    }





    private void callServer(String url, final String purpose, int value){

        ApiInterface apiInterface = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();
        User user = myPreferenece.getUser();

        switch (value){

            default:
            case 1:
            case 3:
                call = apiInterface.getObdDevice(user.getUserAccessToken());
                break;


            case 2:

                if (productModel == null){
                    //fragmentProductPageBinding.progressLayout.setVisibility(View.GONE);
                    BluetoothHelperClass.showErrorDialog(context, getLayoutInflater(), "Error", "Something went wrong. Please try again later.", (Response, className, value1) -> {}, 1);
                    //Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (productModel.getAmount() == null){
                    //fragmentProductPageBinding.progressLayout.setVisibility(View.GONE);
                    if(loadingDialog != null){loadingDialog.dismiss();}
                    BluetoothHelperClass.showErrorDialog(context, getLayoutInflater(), "Error", "This product is not available", (Response, className, value1) -> {}, 1);
                    //Toast.makeText(context, "This product is not available", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Integer.parseInt(productModel.getAmount()) > Integer.parseInt(user.getWalletAmount())
                        || Integer.parseInt(productModel.getPerceivedValue()) > Integer.parseInt(user.getWalletAmount())){
                    if(loadingDialog != null){loadingDialog.dismiss();}
                    BluetoothHelperClass.showErrorDialog(context, getLayoutInflater(), "Error", "Insufficient Safe Coins!", (Response, className, value1) -> {}, 1);
                    //Toast.makeText(context, "Insufficient Safe Coins!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String city = "";
                String lineOne = "";
                String lineTwo = "";
                String country = "";
                String pincode = "";

                if (!user.getUserAddresses().equals("")){
                    try {
                        JSONArray userAddressArray = new JSONArray(user.getUserAddresses());
                        if (userAddressArray.length() > 0){

                            JSONObject firstObject = userAddressArray.getJSONObject(0);

                            city = firstObject.has("city") ? firstObject.getString("city") : "";
                            lineOne = firstObject.has("addressLineOne") ? firstObject.getString("addressLineOne") : "";
                            lineTwo = firstObject.has("addressLineTwo") ? firstObject.getString("addressLineTwo") : "";
                            String fullCountry = firstObject.has("country") ? firstObject.getString("country") : "";
                            pincode = firstObject.has("pincode") ? firstObject.getString("pincode") : "";

                            country = fullCountry.substring(0,2).toUpperCase();


                        }



                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                JsonObject addressObject = new JsonObject();
                addressObject.addProperty("firstname", user.getUserName());
                addressObject.addProperty("email", user.getUserEmail());
                addressObject.addProperty("telephone", getPhoneInE164Format());
                addressObject.addProperty("country", country);
                addressObject.addProperty("postcode", pincode);
                addressObject.addProperty("billToThis", true);
                addressObject.addProperty("city", city);
                addressObject.addProperty("region", city);
                addressObject.addProperty("line1", lineOne);
                addressObject.addProperty("line2", lineTwo);

                jsonObject.add("address", addressObject);

                JsonObject paymentObject = new JsonObject();
                paymentObject.addProperty("code", "svc");
                paymentObject.addProperty("amount", productModel.getAmount());

                JsonArray paymentArray = new JsonArray();
                paymentArray.add(paymentObject);

                jsonObject.add("payments", paymentArray);

                //jsonObject.addProperty("refno", "212300002pqr");

                JsonObject productObject = new JsonObject();
                productObject.addProperty("sku", productModel.getSku());
                productObject.addProperty("price", productModel.getAmount());
                productObject.addProperty("qty", 1);
                productObject.addProperty("currency", Integer.parseInt(productModel.getNumericCode()));

                JsonArray productsArray = new JsonArray();
                productsArray.add(productObject);

                jsonObject.add("products", productsArray);

                jsonObject.addProperty("syncOnly", false);

                call = apiInterface.createOrder(user.getUserAccessToken(), jsonObject, user.getUserId());

                break;







        }


        Log.e(TAG, purpose + " Input : " + jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                fragmentProductPageBinding.progressBar.setVisibility(View.GONE);

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

                            default:
                            case 1:
                                parseFetchProduct(responseStr);
                                break;

                            case 2:

                                if(loadingDialog != null){loadingDialog.dismiss();}

                                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent("REFRESH_USER"));

                                parseCreateOrder(responseStr);

                                break;

                            case 3:
                                Toast.makeText(context, "Voucher Activated Successfully", Toast.LENGTH_SHORT).show();
                                break;




                        }

                    } else {
                        Log.e(TAG, purpose + " Response is not successful");

                        if(loadingDialog != null){loadingDialog.dismiss();}
                        BluetoothHelperClass.showErrorDialog(context, getLayoutInflater(), "Error", "Something went wrong. Please try again later.", (Response, className, value1) -> {}, 1);
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
                    //fragmentProductPageBinding.progressLayout.setVisibility(View.GONE);
                    if(loadingDialog != null){loadingDialog.dismiss();}
                    fragmentProductPageBinding.progressBar.setVisibility(View.GONE);
                    BluetoothHelperClass.showErrorDialog(context, getLayoutInflater(), "Error", "Something went wrong. Please try again later.", (Response, className, value1) -> {}, 1);
                    //Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
                    //goBack();
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                fragmentProductPageBinding.progressBar.setVisibility(View.GONE);
                //fragmentProductPageBinding.progressLayout.setVisibility(View.GONE);
                if(loadingDialog != null){loadingDialog.dismiss();}

                BluetoothHelperClass.showErrorDialog(context, getLayoutInflater(), "Error", "Something went wrong. Please try again later.", (Response, className, value1) -> {}, 1);

                Log.e(TAG, "error here since request failed");
                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Please check your internet connection");
                }

            }
        });

    }

    private void parseCreateOrder(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);

        if (jsonObject.has("code")){

            String message = jsonObject.has("message") ? jsonObject.getString("message")
                    : "Something went wrong. Please try again later.";

            BluetoothHelperClass.showErrorDialog(context, getLayoutInflater(), "Error", message, (Response, className, value1) -> {}, 1);

        } else {

            BluetoothHelperClass.showSuccessDialog(context,
                    getLayoutInflater(),
                    "View Details",
                    "Congratulations",
                    "Your order successfully placed.",
                    (Response, className, value1) -> {
                        goToMyOrdersFragment();
                    }, 1);

        }


    }

    StoreProductModel productModel;

    private void parseFetchProduct(String response) throws JSONException {

        JSONObject jsonObject = new JSONObject(response);

                String id = jsonObject.getString("id");
                String sku = jsonObject.getString("sku");
                String name = jsonObject.getString("name");
                String brandName = jsonObject.getString("brandName");
                String description = jsonObject.getString("description");
                String currencyCode = jsonObject.getJSONObject("price").getJSONObject("currency").getString("code");
                String currencySymbol = jsonObject.getJSONObject("price").getJSONObject("currency").getString("symbol");
                String numericCode = jsonObject.getJSONObject("price").getJSONObject("currency").getString("numericCode");
                String type = jsonObject.getString("type");
                String baseImage = jsonObject.getJSONObject("images").getString("base");
                String tncContent = jsonObject.getJSONObject("tnc").getString("content");
                String tncURL = jsonObject.getJSONObject("tnc").getString("link");
                String expiry = jsonObject.getString("expiry");
                String thumbnailImage = jsonObject.getJSONObject("images").getString("thumbnail");


                JSONArray denominations = jsonObject.getJSONObject("price").getJSONArray("denominations");
                String amount;
                if (denominations.length() > 0){
                    amount = denominations.getString(0);
                } else amount = null;

                String perceivedValue = jsonObject.has("perceivedValue") ? jsonObject.getString("perceivedValue") : amount;

                productModel = new StoreProductModel(
                        id,
                        sku,
                        name,
                        brandName,
                        description,
                        currencyCode,
                        currencySymbol,
                        numericCode,
                        type,
                        baseImage,
                        thumbnailImage,
                        tncContent,
                        tncURL,
                        expiry,
                        amount,
                        perceivedValue
                );

        fragmentProductPageBinding.productScrollView.setVisibility(View.VISIBLE);

                setProductDetails();

    }


    private void goToMyOrdersFragment(){

        MyOrdersFragment fragment = new MyOrdersFragment();
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.mainFrameLayout, fragment, MyOrdersFragment.class.getSimpleName())
                .addToBackStack(null)
                .commit();

    }


    private void setProductDetails(){

        if (productModel == null){
            Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (this.activationUrl == null) {
            //case: no activation url
            fragmentProductPageBinding.activationUrlLayout.setVisibility(View.GONE);
            fragmentProductPageBinding.redeemLayout.setVisibility(View.VISIBLE);
            fragmentProductPageBinding.buyLayout.setVisibility(View.GONE);
        } else if(this.activationUrl.equalsIgnoreCase("null")){
            //case: no activation url
            fragmentProductPageBinding.activationUrlLayout.setVisibility(View.GONE);
            fragmentProductPageBinding.redeemLayout.setVisibility(View.VISIBLE);
            fragmentProductPageBinding.buyLayout.setVisibility(View.GONE);
        } else if (this.activationUrl.equals("")){
            //case: product buy page
            fragmentProductPageBinding.redeemLayout.setVisibility(View.GONE);
            fragmentProductPageBinding.buyLayout.setVisibility(View.VISIBLE);
        } else {
            //case: activation url exist
            fragmentProductPageBinding.buyLayout.setVisibility(View.GONE);
            fragmentProductPageBinding.redeemLayout.setVisibility(View.VISIBLE);
            /*fragmentProductPageBinding.redeemBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(activationUrl)));

                    try{

                        new URL(activationUrl);
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(activationUrl));
                        context.startActivity(Intent.createChooser(intent, "Choose browser"));


                    } catch (Exception e){
                        if (e instanceof MalformedURLException){
                            Toast.makeText(context, "Link not available, please redeem it from the company website.", Toast.LENGTH_LONG).show();
                        }
                        e.printStackTrace();
                    }
                }
            });*/

            if (isCardActivated){
                fragmentProductPageBinding.tapToActivateButton.setText("Voucher Active");
            } else {
                fragmentProductPageBinding.tapToActivateButton.setText("Tap to Activate");
                fragmentProductPageBinding.tapToActivateButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        callServer(getString(R.string.storeCheckActivateCard).replace("orderId", orderId), "TAP TO ACTIVATE", 3);
                    }
                });
            }


            fragmentProductPageBinding.copyBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Copied to clipboard", activationUrl);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            });
            fragmentProductPageBinding.copyVoucherBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Copied to clipboard", voucherCode);
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            });
        }


        fragmentProductPageBinding.toolBarLayout.setTitle(String.valueOf(Html.fromHtml(productModel.getName())));
        fragmentProductPageBinding.brandName.setText(Html.fromHtml(productModel.getBrandName()));

        Glide.with(context)
                .load(productModel.getThumbnailImage())
                .placeholder(R.drawable.ic_image_placeholder_new)
                .error(R.drawable.ic_broken_image_placeholder_new)
                .into(fragmentProductPageBinding.brandImage);

        Glide.with(context)
                .load(productModel.getBaseImage())
                .placeholder(R.drawable.ic_image_placeholder_new)
                .error(R.drawable.ic_broken_image_placeholder_new)
                .into(fragmentProductPageBinding.productImage);

        fragmentProductPageBinding.productName.setText(Html.fromHtml(productModel.getName()));
        fragmentProductPageBinding.productDescription.setText(Html.fromHtml(productModel.getDescription()));
        fragmentProductPageBinding.tncContent.setText(Html.fromHtml(productModel.getTncContent()));

        fragmentProductPageBinding.productCost.setText((productModel.getPerceivedValue() == null) ? "" : productModel.getPerceivedValue());

        fragmentProductPageBinding.walletFunds.setText("You have: " + myPreferenece.getUser().getWalletAmount());



    }


    AlertDialog loadingDialog;

    private void showLoading(){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(getLayoutInflater().inflate(R.layout.loading_dialog, null, false));
        loadingDialog = builder.create();
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.setCancelable(false);
        loadingDialog.show();
        loadingDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);


    }











}
