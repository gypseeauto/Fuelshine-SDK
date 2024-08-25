package com.gypsee.sdk.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
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

import com.bumptech.glide.Glide;

import org.jetbrains.annotations.NotNull;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentProductPageBinding;

public class DemoProductPageFragment extends Fragment {

    private boolean userOrder;

    public DemoProductPageFragment(boolean userOrder){
        this.userOrder = userOrder;
    }

    private Context context;
    private FragmentProductPageBinding fragmentProductPageBinding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        fragmentProductPageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_page, container, false);


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

        fragmentProductPageBinding.toolBarLayout.setTitle("Gypsee Product");
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

        fragmentProductPageBinding.productScrollView.setVisibility(View.VISIBLE);
        if (this.userOrder){
            fragmentProductPageBinding.buyLayout.setVisibility(View.GONE);
            fragmentProductPageBinding.redeemLayout.setVisibility(View.VISIBLE);
        } else {
            fragmentProductPageBinding.buyLayout.setVisibility(View.VISIBLE);
            fragmentProductPageBinding.redeemLayout.setVisibility(View.GONE);
        }


        fragmentProductPageBinding.progressBar.setVisibility(View.GONE);

        setProductDetails();


        fragmentProductPageBinding.buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Toast.makeText(context, "Register to buy product", Toast.LENGTH_SHORT).show();

            }
        });

    }




    private void setProductDetails(){

            fragmentProductPageBinding.tapToActivateButton.setText("Tap to Activate");
            fragmentProductPageBinding.tapToActivateButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(context, "Voucher Active", Toast.LENGTH_SHORT).show();
                }
            });


            fragmentProductPageBinding.copyBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Copied to clipboard", "DEMO CODE");
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            });

            fragmentProductPageBinding.copyVoucherBlock.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                    ClipData clipData = ClipData.newPlainText("Copied to clipboard", "DEMO CODE");
                    clipboardManager.setPrimaryClip(clipData);
                    Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
                }
            });


        fragmentProductPageBinding.brandName.setText("Gypsee");

        Glide.with(context)
                .load(getResources().getDrawable(R.drawable.ic_subscribe_now_icon))
                .placeholder(R.drawable.ic_image_placeholder_new)
                .error(R.drawable.ic_broken_image_placeholder_new)
                .into(fragmentProductPageBinding.brandImage);

        Glide.with(context)
                .load(getResources().getDrawable(R.drawable.ic_subscribe_now))
                .placeholder(R.drawable.ic_image_placeholder_new)
                .error(R.drawable.ic_broken_image_placeholder_new)
                .into(fragmentProductPageBinding.productImage);

        fragmentProductPageBinding.productName.setText("10% Discount on drivemate subscription");
        fragmentProductPageBinding.productDescription.setText(Html.fromHtml("A mobile app to help car owners renew insurances, save on car maintenance and get fair resale value with drivemate which turns cars into smart cars."));
        fragmentProductPageBinding.tncContent.setText(Html.fromHtml("Consumer advisory \u2014 Frasers Property Retail Management Pte. Ltd, the holder of  Frasers Property Digital Gift Card stored value facility, does not require the approval of the Monetary Authority of Singapore. Consumers (users) are advised to read the terms and conditions carefully. \u2022 The stored value in this Frasers Property Digital Gift Card may be used in full or partial payment for goods and services at participating Frasers Property retailers only, and may not be returned or redeemed for cash. No reimbursements or replacements will be made for lost or stolen cards. \\u2022 The stored value in this Frasers Property Digital Gift Card expires 1 year from date of purchase. \u2022 Any use of this Frasers Property Digital Gift Card shall be governed by, and constitutes acceptance of, the full terms and conditions set out in www.FrasersExperience.com."));

        fragmentProductPageBinding.productCost.setText("180");

        fragmentProductPageBinding.walletFunds.setText("You have: 1000");



    }




}

