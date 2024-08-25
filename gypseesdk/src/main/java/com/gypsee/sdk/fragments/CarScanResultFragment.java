package com.gypsee.sdk.fragments;

import android.content.Context;
import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.gypsee.sdk.Adapters.FaultCodeDescriptionAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.FragmentCarScanResultBinding;
import com.gypsee.sdk.helpers.BluetoothHelperClass;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CarScanResultFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CarScanResultFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private HashMap<String, String> scanResult;
    private String mParam2;

    public CarScanResultFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CarScanResultFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static CarScanResultFragment newInstance(HashMap<String, String> param1, String param2) {
        CarScanResultFragment fragment = new CarScanResultFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            scanResult = (HashMap<String, String>) getArguments().getSerializable(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    String TAG = CarScanResultFragment.class.getSimpleName();

    FragmentCarScanResultBinding fragmentCarScanBinding;
    private Map<String, String> dtcVals;

    Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.e(TAG, "Trouble codes : " + scanResult.get("TROUBLE_CODES"));
        fragmentCarScanBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_car_scan_result, container, false);
        context = getContext();
        dtcVals = getDict(R.array.dtc_keys, R.array.dtc_values);
        initBackgrounds();
        checkConfirmedTroubles();
        checkPendingTroubles();
        checkPermanentTroublecodes();
        setDiagnosticReportLayout();
        return fragmentCarScanBinding.getRoot();
    }

    private void setDiagnosticReportLayout() {
        fragmentCarScanBinding.diagnosticReportLayout.descriptionTv.setText(faultCodesNo + " fault codes found");
        fragmentCarScanBinding.diagnosticReportLayout.lineView.setVisibility(View.GONE);
        fragmentCarScanBinding.diagnosticReportLayout.searchforMoreInfoTv.setVisibility(View.GONE);
        BluetoothHelperClass.changeBackgrundColor(fragmentCarScanBinding.diagnosticReportLayout.relativeLayout, ContextCompat.getColor(context, R.color.gold_color), TAG);
    }

    int faultCodesNo = 0;

    private void initBackgrounds() {
        BluetoothHelperClass.changeBackgrundColor(fragmentCarScanBinding.confirmedTxt, ContextCompat.getColor(context, R.color.gold_color), TAG);
        BluetoothHelperClass.changeBackgrundColor(fragmentCarScanBinding.pendingTxt, ContextCompat.getColor(context, R.color.colorPrimary), TAG);
        BluetoothHelperClass.changeBackgrundColor(fragmentCarScanBinding.permanentTxt, ContextCompat.getColor(context, R.color.eco_color), TAG);
    }

    private void checkConfirmedTroubles() {
        String troubleCodes = scanResult.get("TROUBLE_CODES");
        Log.e(TAG, "raw trouble codes: " + troubleCodes);

        if (troubleCodes != null)
            troubleCodes = BluetoothHelperClass.parseTroubleCodes(troubleCodes);

        if (troubleCodes == null || troubleCodes.trim().equals("")) {
            fragmentCarScanBinding.confirmedTxt.setVisibility(View.VISIBLE);
            fragmentCarScanBinding.confirmedTroubleCodeRecyclerView.setVisibility(View.GONE);
        } else {
            fragmentCarScanBinding.confirmedTxt.setVisibility(View.GONE);
            fragmentCarScanBinding.confirmedTroubleCodeRecyclerView.setVisibility(View.VISIBLE);

            parseMultiTroubleCodes(troubleCodes, fragmentCarScanBinding.confirmedTroubleCodeRecyclerView, ContextCompat.getColor(context, R.color.gold_color));
        }
    }

    private void parseMultiTroubleCodes(String troubleCodes, RecyclerView confirmedTroubleCodeRecyclerView, int color) {
        Log.e(TAG, "Trouble codes string: " + troubleCodes);

        ArrayList<String> keys = new ArrayList<>();
        ArrayList<String> values = new ArrayList<>();
        String[] troubleCode = troubleCodes.split("\n");
        for (String key :
                troubleCode) {

            keys.add(key);
            values.add(dtcVals.get(key) == null ? "Dtc not verified" : dtcVals.get(key));
        }

        loadRecyclerView(keys, values, confirmedTroubleCodeRecyclerView, color);
        faultCodesNo = faultCodesNo + values.size();
    }

    private void loadRecyclerView(ArrayList<String> keys, ArrayList<String> values, RecyclerView confirmedTroubleCodeRecyclerView, int color) {

        FaultCodeDescriptionAdapter faultCodeDescriptionAdapter = new FaultCodeDescriptionAdapter(keys, values, context, color);
        confirmedTroubleCodeRecyclerView.setAdapter(faultCodeDescriptionAdapter);

    }

    private void checkPendingTroubles() {
        String troubleCodes = scanResult.get("PENDING_TROUBLE_CODES");

        if (troubleCodes != null)
            troubleCodes = BluetoothHelperClass.parseTroubleCodes(troubleCodes);

        if (troubleCodes == null || troubleCodes.equals("")) {
            fragmentCarScanBinding.pendingTxt.setVisibility(View.VISIBLE);
            fragmentCarScanBinding.pendingTroubleCodeRecyclerView.setVisibility(View.GONE);
        } else {
            fragmentCarScanBinding.pendingTxt.setVisibility(View.GONE);
            fragmentCarScanBinding.pendingTroubleCodeRecyclerView.setVisibility(View.VISIBLE);
            parseMultiTroubleCodes(troubleCodes, fragmentCarScanBinding.pendingTroubleCodeRecyclerView, ContextCompat.getColor(context, R.color.colorPrimary));

        }
    }

    private void checkPermanentTroublecodes() {
        String troubleCodes = scanResult.get("PERMANENT_TROUBLE_CODES");

        if (troubleCodes != null)
            troubleCodes = BluetoothHelperClass.parseTroubleCodes(troubleCodes);

        if (troubleCodes == null || troubleCodes.equals("")) {
            fragmentCarScanBinding.permanentTxt.setVisibility(View.VISIBLE);
            fragmentCarScanBinding.perrmanentTroubleCodeRecyclerView.setVisibility(View.GONE);
        } else {
            fragmentCarScanBinding.permanentTxt.setVisibility(View.GONE);
            fragmentCarScanBinding.perrmanentTroubleCodeRecyclerView.setVisibility(View.VISIBLE);
            parseMultiTroubleCodes(troubleCodes, fragmentCarScanBinding.perrmanentTroubleCodeRecyclerView, ContextCompat.getColor(context, R.color.eco_color));

        }
    }

    Map<String, String> getDict(int keyId, int valId) {
        String[] keys = getResources().getStringArray(keyId);
        String[] vals = getResources().getStringArray(valId);

        Map<String, String> dict = new HashMap<String, String>();
        for (int i = 0, l = keys.length; i < l; i++) {
            dict.put(keys[i], vals[i]);
        }
        return dict;
    }


}