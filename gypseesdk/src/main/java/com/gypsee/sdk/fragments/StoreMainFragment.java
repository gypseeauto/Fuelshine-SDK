package com.gypsee.sdk.fragments;

import android.content.Context;
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
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.gson.JsonObject;

import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import com.gypsee.sdk.Adapters.StoreListAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.customviews.StoreRecyclerViewPaginator;
import com.gypsee.sdk.databinding.FragmentStoreMainBinding;
import com.gypsee.sdk.firebase.FirebaseLogEvents;
import com.gypsee.sdk.models.StoreCategoryModel;
import com.gypsee.sdk.models.StoreProductListItemModel;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.RecyclerItemClickListener;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StoreMainFragment extends Fragment {

    public StoreMainFragment(){}

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

        resetAll();

        initStaticViews();
        //initViews();
        fetchAllCategories();
        FirebaseLogEvents.firebaseLogEvent("accessed_store",context);


        return fragmentStoreMainBinding.getRoot();
    }

    private ArrayList<StoreCategoryModel> categoryArrayList;


    private void resetAll(){
        isLastPage = false;
        currentCategoryIndex = 0;
        categoryArrayList = new ArrayList<>();
        productList = new ArrayList<>();
        loadNext = false;
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

                MyOrdersFragment fragment = new MyOrdersFragment();
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


    StoreListAdapter storeListAdapter;
    private void initStaticViews(){
        productList.clear();
        categoryArrayList.clear();
        fragmentStoreMainBinding.loadBar.setVisibility(View.GONE);
        //fragmentStoreMainBinding.progressLayout.setVisibility(View.VISIBLE);
        //fragmentStoreMainBinding.firstItemCard.firstItemCard.setVisibility(View.GONE);
        fragmentStoreMainBinding.productList.setLayoutManager(new GridLayoutManager(context, 2 ));
        storeListAdapter = new StoreListAdapter(productList, context);
        fragmentStoreMainBinding.productList.setAdapter(storeListAdapter);

       /*
       * Removed scroll view due to scrollListener not registering with disabled nested scrolling
       */

        registerOnclickListener();

    }

    private ArrayList<StoreProductListItemModel> productList;


    private void registerOnclickListener(){


        fragmentStoreMainBinding.productList.addOnItemTouchListener(new RecyclerItemClickListener(context, fragmentStoreMainBinding.productList, new RecyclerItemClickListener.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {

                if (position >= productList.size())return;

                ProductPageFragment fragment = new ProductPageFragment(productList.get(position).getSku(), "", null, null,true);
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.mainFrameLayout, fragment, ProductPageFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }

            @Override
            public void onLongItemClick(View view, int position) {

            }
        }));


        /*fragmentStoreMainBinding.firstItemCard.firstItemCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ProductPageFragment fragment = new ProductPageFragment();
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.mainFrameLayout, fragment, ProductPageFragment.class.getSimpleName())
                        .addToBackStack(null)
                        .commit();
            }
        });*/



    }



    private void fetchAllCategories(){
        callServer(getString(R.string.storeFetchCatalog), "Fetch all categories", 1, 0, 0);
    }


    private void fetchTopProductItems(){
        if (categoryArrayList.size() >= 1) {
            callServer(getString(R.string.storeFetchCatalog) + categoryArrayList.get(0).getId() + "/", "Fetch initial products"
                    , 2, 0, batchSize);
        }
    }

    private int batchSize = 8; //also change in StoreRecyclerViewPaginator
    private StoreRecyclerViewPaginator paginator;

    private void setupPaginator(){

        Log.e(TAG, "setup paginator");

        paginator = new StoreRecyclerViewPaginator(fragmentStoreMainBinding.productList) {
            @Override
            public boolean isLastPage() {
                Log.e(TAG, "isLastPage " + isLastPage);
                return isLastPage;
            }

            @Override
            public boolean canLoadNext() {
                Log.e(TAG, "canLoadNext: " + loadNext);
                return loadNext;
            }

            @Override
            public boolean isFirstCategory() {
                Log.e(TAG, "current category isFirstCategory: " + currentCategoryIndex + " " + (currentCategoryIndex == 0));
                return (currentCategoryIndex == 0);
            }

            @Override
            public boolean isLastCategory() {
                Log.e(TAG, "current category isLastCategory: " + currentCategoryIndex + " " + (currentCategoryIndex >= categoryArrayList.size()));
                return (currentCategoryIndex >= categoryArrayList.size());
            }

            @Override
            public void loadMore(int offset, int limit) {
                if (currentCategoryIndex >= categoryArrayList.size()) return;

                Log.e(TAG, "current category loadmore: " + currentCategoryIndex);

                callServer(getString(R.string.storeFetchCatalog) + categoryArrayList.get(currentCategoryIndex).getId() + "/"
                , "Fetch Product List", 3, offset, limit);
            }
        };
        fragmentStoreMainBinding.productList.addOnScrollListener(paginator);
    }


    private ArrayList<String> skumap = new ArrayList<>();


    private boolean isLastPage = false;
    private int currentCategoryIndex = 0;









    private void callServer(String url, final String purpose, int value, int offset, int limit){

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call <ResponseBody> call;
        JsonObject jsonObject = new JsonObject();
        User user = myPreferenece.getUser();

        fragmentStoreMainBinding.loadBar.setVisibility(View.VISIBLE);

        switch (value){

            case 2:
                //fetch init items
            case 3:
                //fetch product list
                loadNext = false;
                Log.e(TAG, "offset: " + offset + ", limit: " + limit);
                call = apiService.fetchProductList(user.getUserAccessToken(), offset, limit);
                break;

            case 1:
            default:
                call = apiService.getObdDevice(user.getUserAccessToken());
                break;


        }



        Log.e(TAG, purpose + " Input : " + jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    fragmentStoreMainBinding.progressLayout.setVisibility(View.GONE);

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
                                parseFetchCategories(responseStr);
                                break;

                            case 2:
                            case 3:
                                parseFetchProductList(responseStr, value);
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
                }

            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

                Log.e(TAG, "error here since request failed");
                Log.e(TAG, t.getMessage());
                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Please check your internet connection");
                } else {}

            }
        });



    }

    private StoreProductListItemModel topProduct;

   /* private void setTopProductItem(String response) throws JSONException {
        JSONObject jsonObject = new JSONObject(response);
        if (!jsonObject.has("products")) return;

        JSONArray productsArray = jsonObject.getJSONArray("products");

        if (productsArray.length() > 0){
            JSONObject firstObject = productsArray.getJSONObject(0);

            JSONObject currencyObject = firstObject.getJSONObject("currency");
            String code = currencyObject.getString("code");
            String numericCode = currencyObject.getString("numericCode");
            String symbol = currencyObject.getString("symbol");

            String imageThumbnail = firstObject.getJSONObject("images").getString("thumbnail");

            String name = firstObject.getString("name");
            String sku = firstObject.getString("sku");
            String url = firstObject.getString("url");

            topProduct = new StoreProductListItemModel(code, numericCode, symbol, imageThumbnail, sku, url, name);

            fragmentStoreMainBinding.firstItemCard.firstItemCard.setVisibility(View.VISIBLE);

            fragmentStoreMainBinding.firstItemCard.productTitle.setText(name);
            fragmentStoreMainBinding.firstItemCard.categoryChip.setText(categoryArrayList.get(0).getName());

            Glide.with(context)
                    .load(imageThumbnail)
                    .placeholder(R.drawable.ic_image_placeholder_new)
                    .error(R.drawable.ic_broken_image_placeholder_new)
                    .into(fragmentStoreMainBinding.firstItemCard.productImage);

        }
    }*/








    private void parseFetchCategories(String response) throws JSONException {

        categoryArrayList.clear();

        JSONObject jsonObject = new JSONObject(response);

        String mainId = jsonObject.getString("id");
        String mainUrl = jsonObject.getString("url");
        String mainName = jsonObject.getString("name");

        JSONArray categories = jsonObject.getJSONArray("subcategories");

        if (categories.length() == 0){

            categoryArrayList.add(new StoreCategoryModel("", mainId, "", "", mainName, mainUrl));

        } else {

            for (int i = 0; i < categories.length(); i++) {

                String description, id, image, thumbnail, name, url;

                JSONObject categoryObject = categories.getJSONObject(i);

                description = (categoryObject.has("description")) ? categoryObject.getString("description") : "";
                id = (categoryObject.has("id")) ? categoryObject.getString("id") : "";
                name = (categoryObject.has("name")) ? categoryObject.getString("name") : "";
                url = (categoryObject.has("url")) ? categoryObject.getString("url") : "";

                JSONObject imageObject = (categoryObject.has("images")) ? categoryObject.getJSONObject("images") : null;
                if (imageObject != null) {
                    image = (imageObject.has("image")) ? imageObject.getString("image") : "";
                    thumbnail = (imageObject.has("thumbnail")) ? imageObject.getString("thumbnail") : "";
                } else {
                    image = "";
                    thumbnail = "";
                }

                categoryArrayList.add(new StoreCategoryModel(description, id, image, thumbnail, name, url));

            }
        }


        fetchTopProductItems();
        /*callServer(getString(R.string.storeFetchCatalog) + categoryArrayList.get(0).getId() + "/"
                , "Fetch Product List", 3, 1, 19);*/
        //setupPaginator();

    }


    //parse fetch product list
    /*
    * 1. add product to productArraylist
    * 2. check if jsonArray size < batchSize
    *               isLastPage = true
    *               currentCategoryIndex++
    *          else
    *               isLastPage = false
    *
    * */


    @Override
    public void onResume() {
        super.onResume();
        setupPaginator();
    }

    private void parseFetchProductList(String response, int value) throws JSONException {

        int productListLastPosition = productList.size();

        JSONObject jsonObject = new JSONObject(response);

        if (!jsonObject.has("products")){
            Toast.makeText(context, "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONArray jsonProductList = jsonObject.getJSONArray("products");

        for (int i=0; i<jsonProductList.length(); i++){

            JSONObject product = jsonProductList.getJSONObject(i);

            String currencyCode = product.getJSONObject("currency").getString("code");
            String numericCode = product.getJSONObject("currency").getString("numericCode");
            String currencySymbol = product.getJSONObject("currency").getString("symbol");
            String imageThumbnail = product.getJSONObject("images").getString("mobile");
            String name = product.getString("name");
            String sku = product.getString("sku");
            String url = product.getString("url");

            //skuMap.put(String.valueOf(currentCategoryIndex) + " " + name, sku);

            skumap.add(categoryArrayList.get(currentCategoryIndex).getId() + " " + sku + " " + name);




            /*if (value == 2*//* && i == 0*//*){
                topProduct = new StoreProductListItemModel(currencyCode, numericCode, currencySymbol, imageThumbnail, sku, url, name);

                fragmentStoreMainBinding.firstItemCard.firstItemCard.setVisibility(View.VISIBLE);

                fragmentStoreMainBinding.firstItemCard.productTitle.setText(name);
                //fragmentStoreMainBinding.firstItemCard.categoryChip.setText(categoryArrayList.get(0).getName());

                Glide.with(context)
                        .load(imageThumbnail)
                        .placeholder(R.drawable.ic_image_placeholder_new)
                        .error(R.drawable.ic_broken_image_placeholder_new)
                        .into(fragmentStoreMainBinding.firstItemCard.productImage);

            } else {*/

                productList.add(new StoreProductListItemModel(
                        currencyCode,
                        numericCode,
                        currencySymbol,
                        imageThumbnail,
                        sku,
                        url,
                        name
                ));

            //}
        }

        fragmentStoreMainBinding.loadBar.setVisibility(View.GONE);

        storeListAdapter.notifyItemRangeChanged(productListLastPosition, jsonProductList.length());


        Log.e(TAG, "skumap");
        for (String sku: skumap){
            Log.e(TAG, "[ item " + sku);
        }

        /*if (value == 2){
            setupPaginator();
        }*/

        if(jsonProductList.length() < batchSize){
            Log.e(TAG, "current category 1: " + currentCategoryIndex);
            currentCategoryIndex += 1;
            paginator.resetCurrentPage();
            isLastPage = true;
            Log.e(TAG, "current category 2: " + currentCategoryIndex);
        } else {
            Log.e(TAG, "current category else: " + currentCategoryIndex);
            isLastPage = false;
        }

        loadNext = true;


    }



    private boolean loadNext = false;






}
