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
import androidx.recyclerview.widget.GridLayoutManager;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import com.gypsee.sdk.Adapters.DemoStoreListAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentStoreMainBinding;
import com.gypsee.sdk.models.StoreProductListItemModel;
import com.gypsee.sdk.utils.RecyclerItemClickListener;


public class DemoStoreMainFragment extends Fragment {

    public DemoStoreMainFragment(){}

    FragmentStoreMainBinding fragmentStoreMainBinding;
    Context context;
    private String TAG = StoreMainFragment.class.getSimpleName();
    MyPreferenece myPreferenece;

    @Override
    public void onAttach(@NonNull @NotNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull @NotNull LayoutInflater inflater, @Nullable @org.jetbrains.annotations.Nullable ViewGroup container, @Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {

        fragmentStoreMainBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_store_main, container, false);

        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);


        initToolbar();

        initStaticViews();

        return fragmentStoreMainBinding.getRoot();
    }


    private void initToolbar(){
        Toolbar toolbar = fragmentStoreMainBinding.toolBarLayout.toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);
        fragmentStoreMainBinding.toolBarLayout.setTitle("Store");
        fragmentStoreMainBinding.toolBarLayout.rightText.setVisibility(View.VISIBLE);
        fragmentStoreMainBinding.toolBarLayout.rightText.setText("My Orders");
        //fragmentStoreMainBinding.toolBarLayout.rightText.setCompoundDrawables(getResources().getDrawable(R.drawable.ic_bag), null, null, null);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });

        fragmentStoreMainBinding.toolBarLayout.rightText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DemoMyOrdersFragment fragment = new DemoMyOrdersFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.mainFrameLayout, fragment, MyOrdersFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();


            }
        });


    }

    private void goBack() {
        ((AppCompatActivity) context).finish();
    }


    DemoStoreListAdapter storeListAdapter;
    private void initStaticViews(){
        productList.clear();
        StoreProductListItemModel model = new StoreProductListItemModel("INR", "123", "INR", "", "", "", "10% Discount on drivemate subscription");
        productList.add(model);

        fragmentStoreMainBinding.loadBar.setVisibility(View.GONE);
        fragmentStoreMainBinding.productList.setLayoutManager(new GridLayoutManager(context, 2 ));
        storeListAdapter = new DemoStoreListAdapter(productList, context);
        fragmentStoreMainBinding.productList.setAdapter(storeListAdapter);

        /*
         * Removed scroll view due to scrollListener not registering with disabled nested scrolling
         */

        registerOnclickListener();

    }

    private ArrayList<StoreProductListItemModel> productList = new ArrayList<>();


    private void registerOnclickListener(){


        fragmentStoreMainBinding.productList.addOnItemTouchListener(new RecyclerItemClickListener(context, fragmentStoreMainBinding.productList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position >= productList.size())return;

                DemoProductPageFragment demoProductPageFragment = new DemoProductPageFragment(false);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.mainFrameLayout, demoProductPageFragment, DemoProductPageFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();

            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


    }

}
