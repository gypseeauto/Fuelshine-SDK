package com.gypsee.sdk.fragments.countrycodepicker;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentCountryCodeBinding;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CountryCodeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */


public class CountryCodeFragment extends Fragment {

    public interface CountryCodeListener {
        void onCountryCodeSelected(String countryCode);
    }

    public CountryCodeFragment() {
        // Required empty public constructor
    }
    private CountryCodeListener countryCodeListener;


    public void setCountryCodeListener(CountryCodeListener listener) {
        this.countryCodeListener = listener;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CountryCodeFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CountryCodeFragment newInstance() {

        return new CountryCodeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    FragmentCountryCodeBinding fragmentCountryCodeBinding;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        fragmentCountryCodeBinding=   FragmentCountryCodeBinding.inflate(inflater);
        loadCountryCodes();
        initViews();
        return fragmentCountryCodeBinding.getRoot();
    }


  private void  initViews()
    {

        // Clear button functionality
        fragmentCountryCodeBinding.clearButton.setOnClickListener(v ->
                fragmentCountryCodeBinding.countryCodeInput.setText(""));

        // Populate the ListView
        ArrayList<String> countryList = new ArrayList<>(countryCodeMap.keySet());
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, countryList);
        fragmentCountryCodeBinding.countryListView.setAdapter(adapter);

        // Set item click listener
        fragmentCountryCodeBinding.countryListView.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedCountry = (String) parent.getItemAtPosition(position);
            String selectedCode = countryCodeMap.get(selectedCountry);
            countryCodeListener.onCountryCodeSelected(selectedCode);
            getParentFragmentManager().popBackStack();

            //Send Broadcast
        });

        // Add text change listener for search
        fragmentCountryCodeBinding.countryCodeInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // Not needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {
                // Not needed
            }
        });
    }

    private HashMap<String, String> countryCodeMap;

    private void loadCountryCodes() {
        try {
            InputStream is = requireContext().getAssets().open("country_codes_southern_africa.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();

            String json = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(json);
            countryCodeMap = new HashMap<>();
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);
                String country = jsonObject.getString("country");
                String code = jsonObject.getString("code");
                countryCodeMap.put(country, code);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}