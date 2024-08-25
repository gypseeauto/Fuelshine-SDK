package com.gypsee.sdk.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import com.gypsee.sdk.Adapters.WalletTransactionsAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentNewVehiclePerformanceBinding;
import com.gypsee.sdk.databinding.FragmentSettingsBinding;
import com.gypsee.sdk.databinding.FragmentWalletBinding;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.WalletTransactionsModel;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link WalletFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WalletFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public WalletFragment() {
        // Required empty public constructor
    }

    public static WalletFragment newInstance() {
        return new WalletFragment();
    }


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WalletFragment.
     */
    // TODO: Rename and change types and number of parameters
//    public static WalletFragment newInstance(String param1, String param2) {
//        WalletFragment fragment = new WalletFragment();
//        Bundle args = new Bundle();
//        args.putString(ARG_PARAM1, param1);
//        args.putString(ARG_PARAM2, param2);
//        fragment.setArguments(args);
//        return fragment;
//    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    private String TAG = WalletFragment.class.getSimpleName();
    private MyPreferenece myPreferenece;
    FragmentWalletBinding fragmentWalletBinding;

    private User user;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentWalletBinding = FragmentWalletBinding.inflate(inflater, container, false);

//        databaseHelper = new DatabaseHelper(context);
        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, requireContext());
        user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, requireContext()).getUser();

        // Get the current date
        LocalDate currentDate = LocalDate.now();

        // Define the desired format
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Format the current date
        String formattedDate = currentDate.format(formatter);

        // Print the formatted date
        System.out.println("Current date: " + formattedDate);

        Log.e("userdate",user.getCreatedOn());


        String originalDateTimeString = user.getCreatedOn();

        // Define formatter for parsing the original datetime string
        DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        // Parse the original datetime string into LocalDateTime object
        LocalDateTime originalDateTime = LocalDateTime.parse(originalDateTimeString, originalFormatter);

        // Define formatter for the desired format
        DateTimeFormatter desiredFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        // Format the LocalDateTime object into the desired format
        String formattedPeriodFromDate = originalDateTime.format(desiredFormatter);




        callWallet(getResources().getString(R.string.wallet).replace("{","").replace("}","").replace("userId",user.getUserId()),"Get wallet Transactions",true,formattedDate,formattedDate);



        fragmentWalletBinding.dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDropdownMenu(view);
            }
        });

        // Inflate the layout for this fragment
        return fragmentWalletBinding.getRoot();

    }


    private void callWallet(String url, final String purpose,boolean includeTransactions, String periodFrom, String periodTo){

        fragmentWalletBinding.progressBar.setVisibility(View.VISIBLE);
        fragmentWalletBinding.walletRecView.setVisibility(View.GONE);

        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);
        Call<ResponseBody> call;
        JsonObject jsonObject = new JsonObject();
        User user = myPreferenece.getUser();

        call = apiService.getWallet(user.getUserAccessToken(),includeTransactions ,periodFrom, periodTo);


        Log.e(TAG, purpose + " Input : " + jsonObject);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                try {

                    fragmentWalletBinding.progressBar.setVisibility(View.GONE);
                    fragmentWalletBinding.walletRecView.setVisibility(View.VISIBLE);

                    if (response.isSuccessful()){
                        Log.e(TAG, "Response is success");

                        ResponseBody responseBody = response.body();
                        if (responseBody == null){
                            return;
                        }


                        String responseStr = responseBody.string();
                        Log.e(TAG, purpose + " Resonse : " + responseStr);

                        parseWallet(responseStr);




                    } else {

                        Log.e(TAG, purpose + " Response is not successful");

                        String errResponse = response.errorBody().string();
                        Log.e("Error code 400", errResponse);
                        Log.e(TAG, purpose + "Response is : " + errResponse);
                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(requireContext());
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
                    Toast.makeText(requireContext(), "Please Check your internet connection", Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Please check your internet connection");
                } else {}

            }
        });



    }





    private ArrayList<WalletTransactionsModel> walletTransactionsArrayList = new ArrayList<>();
    private void parseWallet(String response) throws JSONException {
        walletTransactionsArrayList.clear();
        JSONObject jsonObject = new JSONObject(response);

        if (!jsonObject.has("data")){
            Toast.makeText(requireContext(), "Something went wrong. Please try again later.", Toast.LENGTH_SHORT).show();
            return;
        }

        JSONObject walletObj = jsonObject.getJSONObject("data");


        String totalEarnedAmount = walletObj.getString("totalEarnedAmount");
        String totalSpentAmount = walletObj.getString("totalSpentAmount");
        String balanceAmount = walletObj.getString("balanceAmount");


        JSONArray transactionsArray = walletObj.getJSONArray("transactions");

        if (transactionsArray == null || transactionsArray.length() == 0) {
            Log.d(TAG, "Transaction array is empty");
        } else {

            for (int i = 0; i < transactionsArray.length(); i++) {
                JSONObject transactionObj = transactionsArray.getJSONObject(i);
                String id = transactionObj.getString("id");
                String amount = transactionObj.getString("amount");
                String type = transactionObj.getString("type");
                String description = transactionObj.getString("description");
                String createdOn = transactionObj.getString("createdOn");
                String lastUpdatedOn = transactionObj.getString("lastUpdatedOn");
                String debitAmount = transactionObj.getString("debitAmount");

                walletTransactionsArrayList.add(new WalletTransactionsModel(id,amount,type,description,createdOn,lastUpdatedOn,debitAmount));
            }
        }

        final LinearLayoutManager layoutManager = new LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false);
        fragmentWalletBinding.walletRecView.setLayoutManager(layoutManager);
        fragmentWalletBinding.walletRecView.setAdapter(new WalletTransactionsAdapter(walletTransactionsArrayList,requireContext()));



        fragmentWalletBinding.earnedWalletAmount.setText("₹"+totalEarnedAmount);
        fragmentWalletBinding.spentWalletAmount.setText("₹"+totalSpentAmount);
        fragmentWalletBinding.balanceWalletAmount.setText("₹"+balanceAmount);


    }




    private void showDropdownMenu(View anchorView) {
        // Create a PopupWindow with a custom layout
        PopupWindow popupWindow = new PopupWindow(anchorView.getContext());
        View dropdownView = LayoutInflater.from(anchorView.getContext()).inflate(R.layout.date_drop_down_menu, null);

        // Initialize the ListView for the dropdown menu
        ListView dateListView = dropdownView.findViewById(R.id.dates_list_view);

        // Create an ArrayList with the dropdown options
        ArrayList<String> dateOptions = new ArrayList<>();
        dateOptions.add("Today");
        dateOptions.add("14 days");
        dateOptions.add("28 days");
        dateOptions.add("3 months");
        dateOptions.add("6 months");
        dateOptions.add("1 Year");

        // Create the custom adapter
        CustomArrayAdapter dateAdapter = new CustomArrayAdapter(anchorView.getContext(), R.layout.date_dropdown_item, dateOptions);

        // Set the adapter to the ListView
        dateListView.setAdapter(dateAdapter);

        // Set item click listener for the dropdown menu items
        dateListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Handle item click based on position
                String selectedItem = dateOptions.get(position);
                fragmentWalletBinding.walletDate.setText(selectedItem);

                // Get the current date
                LocalDate currentDate1 = LocalDate.now();

                // Define the desired format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                // Format the current date
                String formattedDate = currentDate1.format(formatter);


                // Get the current date
                Calendar calendar = Calendar.getInstance();
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                String currentDate = dateFormat.format(calendar.getTime());

                // Calculate the date based on the selected option
                Calendar selectedDateCalendar = Calendar.getInstance();
                selectedDateCalendar.setTime(calendar.getTime());
                switch (selectedItem) {
                    case "Today":

                        // No need to do anything, currentDate already holds today's date
                        break;
                    case "14 days":
                        selectedDateCalendar.add(Calendar.DAY_OF_MONTH, -14);
                        break;
                    case "28 days":
                        selectedDateCalendar.add(Calendar.DAY_OF_MONTH, -28);
                        break;
                    case "3 months":
                        selectedDateCalendar.add(Calendar.MONTH, -3);
                        break;
                    case "6 months":
                        selectedDateCalendar.add(Calendar.MONTH, -6);
                        break;
                    case "1 Year":
                        selectedDateCalendar.add(Calendar.YEAR, -1);
                        break;
                }
                String selectedDate = dateFormat.format(selectedDateCalendar.getTime());

                Toast.makeText(getContext(), "Selected Date: " + selectedDate, Toast.LENGTH_SHORT).show();
                Log.e("selected dates","PeriodFrom = "+selectedDate+" periodTo = "+formattedDate);

                callWallet(getResources().getString(R.string.wallet).replace("{","").replace("}","").replace("userId",user.getUserId()),"Get wallet Transactions",true,selectedDate,formattedDate);


                // Dismiss the PopupWindow after item selection
                popupWindow.dismiss();
            }
        });

        // Set the PopupWindow dimensions
        popupWindow.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        popupWindow.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);

        // Set the PopupWindow content and show it at the specified location
        popupWindow.setContentView(dropdownView);
        popupWindow.setFocusable(true);
        popupWindow.showAsDropDown(anchorView);
    }

}