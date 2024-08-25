package com.gypsee.sdk.fragments;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gypsee.sdk.Adapters.PerformanceListAdapter;
import com.gypsee.sdk.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class PerformanceFragment extends Fragment {

    RecyclerView recyclerView;
    ArrayList<String> ids = new ArrayList<>();
    TextView troubleCode;

    PerformanceListAdapter adapter;

    public static PerformanceFragment newInstance() {

        Bundle args = new Bundle();

        PerformanceFragment fragment = new PerformanceFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public PerformanceFragment() {
    }

    private View view;
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_performance, container, false);
        context = getContext();
        registerBroadcastReceivers();


        recyclerView = view.findViewById(R.id.rv_all);
        adapter = new PerformanceListAdapter();
//        troubleCode = view.findViewById(R.id.tv_trouble_code);
        recyclerView.setAdapter(adapter);
        return view;
    }


    public void updateList(String key, String name, String value) {

        value = value.equals("NODATA") ? "RESTRICTED" : value;

        if (ids.contains(key)) {
            adapter.data.set(ids.indexOf(key), name + ":" + value);
        } else {
            ids.add(key);
            adapter.data.add(ids.indexOf(key), name + ":" + value);
        }
       /* if (key.equals("TROUBLE_CODES") && !value.isEmpty()) {

            Map<String, String> troubleCodesMap = getErrocodesWithDescMap(R.array.dtc_keys, R.array.dtc_values);

            String troubleCodes[] = value.split("\n");
            Log.e(TAG, "Trouble code length : " + troubleCodes.length);

            String latestTroublecode = troubleCodes[troubleCodes.length - 1];
            troubleCode.setText(latestTroublecode);
            TextView troublecodeDesc = view.findViewById(R.id.troublecodeDesc);
            if (troubleCodesMap.get(latestTroublecode) != null)
                troublecodeDesc.setText(troubleCodesMap.get(latestTroublecode));


        }*/
        adapter.notifyDataSetChanged();
    }

    private String TAG = PerformanceFragment.class.getSimpleName();

    private Map<String, String> getErrocodesWithDescMap(int keyId, int valId) {
        String[] keys = context.getResources().getStringArray(keyId);
        String[] vals = context.getResources().getStringArray(valId);

        Map<String, String> dict = new HashMap<String, String>();
        for (int i = 0, l = keys.length; i < l; i++) {
            dict.put(keys[i], vals[i]);
        }

        return dict;
    }

    private void registerBroadcastReceivers() {

        //THis is to receive the obd commands from the device . Regarding the vehcile


        IntentFilter filter = new IntentFilter();
        filter.addAction("Performance");

        //this is for receiving the notification count etc
        LocalBroadcastManager.getInstance(context).registerReceiver(
                broadcastReceiver, filter);
    }


    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle b = intent.getExtras();

            if (b == null)
                return;
            updateList(b.getString("cmdID"), b.getString("cmdName"), b.getString("cmdResult"));


        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(context).unregisterReceiver(broadcastReceiver);

    }
}
