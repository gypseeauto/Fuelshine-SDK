package com.gypsee.sdk.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import java.util.ArrayList;

import com.gypsee.sdk.Adapters.OfferPointsAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentProductPurchasePageBinding;

public class ProductPurchasePageFragment extends Fragment {

    FragmentProductPurchasePageBinding fragmentProductPurchasePageBinding;
    Context context;

    public ProductPurchasePageFragment(){}

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentProductPurchasePageBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_product_purchase_page, container, false);

        initToolbar();
        initViews();


        return fragmentProductPurchasePageBinding.getRoot();
    }

    private void initToolbar(){

        //add title

        fragmentProductPurchasePageBinding.backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

    }

    private void initViews(){

        String text = "I agree to the <a href='http://appadmin.gypsee.in/html/gypsee_terms_v2.html'>terms and condition</a> of this offer";

        fragmentProductPurchasePageBinding.termsText.setMovementMethod(LinkMovementMethod.getInstance());
        fragmentProductPurchasePageBinding.termsText.setClickable(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            fragmentProductPurchasePageBinding.termsText.setText(Html.fromHtml(text, Html.FROM_HTML_MODE_COMPACT));
        } else {
            fragmentProductPurchasePageBinding.termsText.setText(Html.fromHtml(text));
        }

        ArrayList<String> offerCostPoints = new ArrayList<>();
        offerCostPoints.add("Offer Cost 20 Coins");
        offerCostPoints.add("10% Discount on monthly car wash package from Karspa");

        ArrayList<String> offerDetailPoints = new ArrayList<>();
        offerDetailPoints.add("Offer is applicable on all the services of Karspa via www.karspa.com");
        offerDetailPoints.add("Offer is not applicable on combos and merchandise.");
        offerDetailPoints.add("Home service carry additional charges according to the vendors.");
        offerDetailPoints.add("The discount code is only valid till 1st November 2021");

        fragmentProductPurchasePageBinding.offerCostList.setAdapter(new OfferPointsAdapter(offerCostPoints));
        fragmentProductPurchasePageBinding.offerCostList.setLayoutManager(new LinearLayoutManager(context));

        fragmentProductPurchasePageBinding.offerDetails.setAdapter(new OfferPointsAdapter(offerDetailPoints));
        fragmentProductPurchasePageBinding.offerDetails.setLayoutManager(new LinearLayoutManager(context));

        fragmentProductPurchasePageBinding.buyNowButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fragmentProductPurchasePageBinding.checkBox.isChecked()){
                    fragmentProductPurchasePageBinding.errorMessage.setVisibility(View.INVISIBLE);
                } else {
                    fragmentProductPurchasePageBinding.errorMessage.setVisibility(View.VISIBLE);
                }
            }
        });


    }


    private void goBack() {
        ((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();

    }


}
