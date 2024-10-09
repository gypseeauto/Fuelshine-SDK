package com.gypsee.sdk.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentTransactionsBinding;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TransactionsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TransactionsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public TransactionsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment TransactionsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static TransactionsFragment newInstance(String param1, String param2) {
        TransactionsFragment fragment = new TransactionsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    FragmentTransactionsBinding transactionsBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        transactionsBinding = FragmentTransactionsBinding.inflate(inflater,container,false);

        transactionsBinding.backButtonLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requireActivity().getSupportFragmentManager().popBackStack();
            }
        });

        transactionsBinding.dateLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDropdownMenu(view);
            }
        });

        // Inflate the layout for this fragment
        return transactionsBinding.getRoot();
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
                transactionsBinding.walletDate.setText(selectedItem);

                // Get the current date
                LocalDate currentDate1 = LocalDate.now();

                // Define the desired format
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                // Format the current date
                String formattedDate = currentDate1.format(formatter);


                // Get the current date
                Calendar calendar = Calendar.getInstance();
                java.text.SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
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

//                callWallet(getResources().getString(R.string.wallet).replace("{","").replace("}","").replace("userId",user.getUserId()),"Get wallet Transactions",true,selectedDate,formattedDate);


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