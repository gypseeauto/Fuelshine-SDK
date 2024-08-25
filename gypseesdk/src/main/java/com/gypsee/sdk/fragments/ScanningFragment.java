package com.gypsee.sdk.fragments;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.gypsee.sdk.Adapters.DTCAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.ConfigActivity;
import com.gypsee.sdk.activities.GypseeMainActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.ScanningLayoutBinding;
import com.gypsee.sdk.io.BluetoothManager;
import com.gypsee.sdk.models.DtcModelClass;

import com.github.pires.obd.commands.control.TroubleCodesCommand;
import com.github.pires.obd.commands.protocol.EchoOffCommand;
import com.github.pires.obd.commands.protocol.LineFeedOffCommand;
import com.github.pires.obd.commands.protocol.ObdResetCommand;
import com.github.pires.obd.commands.protocol.SelectProtocolCommand;
import com.github.pires.obd.enums.ObdProtocols;
import com.github.pires.obd.exceptions.MisunderstoodCommandException;
import com.github.pires.obd.exceptions.NoDataException;
import com.github.pires.obd.exceptions.UnableToConnectException;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ScanningFragment extends Fragment {

    private Context context;
    private RecyclerView recyclerView;
    private TextView tvScanNumber, tvProblemText, tvScanning;
    private static final String TAG = ScanningFragment.class.getName();
    private static final int NO_BLUETOOTH_DEVICE_SELECTED = 0;
    private static final int CANNOT_CONNECT_TO_DEVICE = 1;
    private static final int NO_DATA = 3;
    private static final int DATA_OK = 4;
    private static final int OBD_COMMAND_FAILURE = 10;
    private static final int OBD_COMMAND_FAILURE_IO = 11;
    private static final int OBD_COMMAND_FAILURE_UTC = 12;
    private static final int OBD_COMMAND_FAILURE_IE = 13;
    private static final int OBD_COMMAND_FAILURE_MIS = 14;
    private static final int OBD_COMMAND_FAILURE_NODATA = 15;

    private TextView tvEmpty;

    int i = 0;
    private Handler mHandler = new Handler(new Handler.Callback() {

        public boolean handleMessage(Message msg) {
            if (getActivity() == null) {
                goBack();
                Toast.makeText(context, "An unknown error occured. Please contact Gypsee team", Toast.LENGTH_LONG).show();
                return false;
            }
            Log.e(TAG, "Message received on handler");
            switch (msg.what) {
                case NO_BLUETOOTH_DEVICE_SELECTED:
                    makeToast(getString(R.string.text_bluetooth_nodevice), "Not connected");
//                    finish();
                    break;
                case CANNOT_CONNECT_TO_DEVICE:
                    makeToast(getString(R.string.text_bluetooth_error_connecting), "Not connected");
                    // finish();
                    break;

                case OBD_COMMAND_FAILURE:
                    makeToast(getString(R.string.text_obd_command_failure), "Error!");
                    //finish();
                    break;
                case OBD_COMMAND_FAILURE_IO:
                    makeToast(getString(R.string.text_obd_command_failure) + " IO", "Error!");
//                    finish();
                    break;
                case OBD_COMMAND_FAILURE_IE:
                    makeToast(getString(R.string.text_obd_command_failure) + " IE", "Error!");
//                    finish();
                    break;
                case OBD_COMMAND_FAILURE_MIS:
                    makeToast(getString(R.string.text_obd_command_failure) + " MIS", "Error!");
//                    finish();
                    break;
                case OBD_COMMAND_FAILURE_UTC:
                    makeToastLong(getString(R.string.text_noerrors) + "!");

                    //  makeToast(getString(R.string.text_obd_command_failure) + " UTC", "Error!");
//                    finish();
                    break;
                case OBD_COMMAND_FAILURE_NODATA:
                    makeToastLong(getString(R.string.text_noerrors));
                    //finish();
                    break;

                case NO_DATA:
                    makeToast(getString(R.string.text_dtc_no_data), "No data");
                    ///finish();
                    break;
                case DATA_OK:
                    //makeToast(getString(R.string.text_dtc_no_data), "Connected");
                    scanningLayoutBinding.tvEmpty.setVisibility(View.GONE);
                    scanningLayoutBinding.rvError.setVisibility(View.VISIBLE);
                    dataOk((String) msg.obj);
                    return true;


            }

            //goBack();
            return false;
        }
    });
    private GetTroubleCodesTask getTroubleCodesTask;
    private BluetoothAdapter btAdapter;

    private void goBack() {
        GypseeMainActivity.activityMainBinding.bottomNavigationView.setSelectedItemId(R.id.nav_drive);
    }
    ScanningLayoutBinding scanningLayoutBinding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        scanningLayoutBinding = DataBindingUtil.inflate(inflater, R.layout.scanning_layout, container, false);
        context = getContext();
        initViews(scanningLayoutBinding.getRoot());
        return scanningLayoutBinding.getRoot();
    }

    private void initViews(View root) {

        String sourceString = "Please keep your Engine in idle state for 5 minutes before " + "<b>" + "CAR SCAN" + "</b> ";
        scanningLayoutBinding.carScanTxt.setText(Html.fromHtml(sourceString));

        tvScanNumber = root.findViewById(R.id.tv_scanning_number);
        tvProblemText = root.findViewById(R.id.tv_problem_text);
        recyclerView = root.findViewById(R.id.rv_error);
        tvScanning = root.findViewById(R.id.tv_scanning);
        tvEmpty = root.findViewById(R.id.tv_empty);
        scanningLayoutBinding.scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanningLayoutBinding.scanCardView.setVisibility(View.GONE);
                scanningLayoutBinding.scanLayout.setVisibility(View.VISIBLE);
                LocalBroadcastManager.getInstance(context).sendBroadcast(new Intent().setAction("ScanServiceActiviated"));
                enableBluetooth();
            }
        });
    }

    private void connectToBluetoothDevice() {
        //    private ProgressDialog progressDialog;
        String remoteDevice = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getStringData(ConfigActivity.BLUETOOTH_LIST_KEY);
        if (remoteDevice == null || "".equals(remoteDevice)) {
            Log.e(TAG, "No Bluetooth device has been selected.");
            mHandler.obtainMessage(NO_BLUETOOTH_DEVICE_SELECTED).sendToTarget();
        } else {


            getTroubleCodesTask = new GetTroubleCodesTask();
            getTroubleCodesTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, remoteDevice);
        }
    }


    private Map<String, String> getDict(int keyId, int valId) {
        String[] keys = getResources().getStringArray(keyId);
        String[] vals = getResources().getStringArray(valId);

        Map<String, String> dict = new HashMap<String, String>();
        for (int i = 0, l = keys.length; i < l; i++) {
            dict.put(keys[i], vals[i]);
        }

        return dict;
    }

    private void makeToast(String text, String connectionTexty) {

        tvScanning.setText(connectionTexty);

        scanningLayoutBinding.tvEmpty.setText(text);
        scanningLayoutBinding.tvEmpty.setVisibility(View.VISIBLE);
        scanningLayoutBinding.rvError.setVisibility(View.GONE);
    }

    private void makeToastLong(String text) {
        scanningLayoutBinding.tvEmpty.setText(text);
        scanningLayoutBinding.tvEmpty.setVisibility(View.VISIBLE);
        scanningLayoutBinding.rvError.setVisibility(View.GONE);
    }

    private void dataOk(String res) {


        Log.e(TAG, "Scan completed");


        scanningLayoutBinding.circleProgress.clearAnimation();

        if (getActivity() == null)
            goBack();
        Map<String, String> dtcVals = getDict(R.array.dtc_keys, R.array.dtc_values);
        //TODO replace below codes (res) with aboce troubleCodesMap
        //String tmpVal = troubleCodesMap.get(res.split("\n"));
        //String[] dtcModelClassArrayList = new String[]{};
        ArrayList<DtcModelClass> dtcModelClassArrayList = new ArrayList<>();
        //int i =1;
        if (res != null && !res.isEmpty()) {
            for (String dtcCode : res.split("\n")) {
                Log.e(TAG, dtcCode + " : " + dtcVals.get(dtcCode));
                if (dtcCode.equals("C0300") || dtcCode.equals("U3000") || dtcCode.equals("C0303")) {

                } else {
                    dtcModelClassArrayList.add(new DtcModelClass(dtcCode, dtcVals.get(dtcCode)));
                }
            }
        }

        tvProblemText.setVisibility(dtcModelClassArrayList.size() > 0 ? View.VISIBLE : View.GONE);
        tvProblemText.setText(String.format("%s Problems found", String.valueOf(dtcModelClassArrayList.size())));

        if (dtcModelClassArrayList.size() == 0) {
            tvEmpty.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
        } else {
            tvEmpty.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            DTCAdapter adapter = new DTCAdapter(dtcModelClassArrayList, context);
            recyclerView.setAdapter(adapter);
        }


    }


    public class ModifiedTroubleCodesObdCommand extends TroubleCodesCommand {
        @Override
        public String getResult() {
            // remove unwanted response from output since thisresults in erroneous error codes
            return rawData.replace("SEARCHING...", "").replace("NODATA", "");
        }
    }


    private class GetTroubleCodesTask extends AsyncTask<String, Integer, String> {

        @Override
        protected void onPreExecute() {
            tvScanning.setText("Scanning...");
            scanningLayoutBinding.circleProgress.animate();
        }

        @Override
        protected String doInBackground(String... params) {
            String result = "";

            //Get the current thread's token
            synchronized (this) {
                Log.e(TAG, "Starting service..");
                // get the remote Bluetooth device

                btAdapter = BluetoothAdapter.getDefaultAdapter();
                btAdapter.enable();
                BluetoothDevice dev = btAdapter.getRemoteDevice(params[0]);

                Log.d(TAG, "Stopping Bluetooth discovery.");
                btAdapter.cancelDiscovery();

                Log.d(TAG, "Starting OBD connection..");

                // Instantiate a BluetoothSocket for the remote device and connect it.
                BluetoothSocket sock = null;
                try {
                    sock = BluetoothManager.connect(dev);
                } catch (Exception e) {
                    Log.e(TAG, "There was an error while establishing connection. -> " + e.getMessage());
                    Log.d(TAG, "Message received on handler here");
                    mHandler.obtainMessage(CANNOT_CONNECT_TO_DEVICE).sendToTarget();

                    return null;
                }

                try {
                    // Let's configure the connection.
                    Log.d(TAG, "Queueing jobs for connection configuration..");
                    publishProgress(1);

                    new ObdResetCommand().run(sock.getInputStream(), sock.getOutputStream());


                    publishProgress(2);

                    new EchoOffCommand().run(sock.getInputStream(), sock.getOutputStream());

                    publishProgress(3);

                    new LineFeedOffCommand().run(sock.getInputStream(), sock.getOutputStream());

                    publishProgress(4);

                    // Get protocol from preferences
                    final String protocol = PreferenceManager.getDefaultSharedPreferences(context).getString(ConfigActivity.PROTOCOLS_LIST_KEY, "AUTO");

                    Log.e(TAG, "Protocol for scanning is : " + protocol);
                    new SelectProtocolCommand(ObdProtocols.valueOf(protocol)).run(sock.getInputStream(), sock.getOutputStream());

                    publishProgress(5);

                    ModifiedTroubleCodesObdCommand tcoc = new ModifiedTroubleCodesObdCommand();
                    tcoc.run(sock.getInputStream(), sock.getOutputStream());
                    result = tcoc.getFormattedResult();

                    publishProgress(6);

                } catch (IOException e) {
                    e.printStackTrace();
                    Log.e("DTCERR", e.getMessage());
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_IO).sendToTarget();
                    return null;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Log.e("DTCERR", e.getMessage());
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_IE).sendToTarget();
                    return null;
                } catch (UnableToConnectException e) {
                    e.printStackTrace();
                    Log.e("DTCERR", e.getMessage());
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_UTC).sendToTarget();
                    return null;
                } catch (MisunderstoodCommandException e) {
                    e.printStackTrace();
                    Log.e("DTCERR", e.getMessage());
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_MIS).sendToTarget();
                    return null;
                } catch (NoDataException e) {
                    Log.e("DTCERR", e.getMessage());
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE_NODATA).sendToTarget();
                    return null;
                } catch (Exception e) {
                    Log.e("DTCERR", e.getMessage());
                    mHandler.obtainMessage(OBD_COMMAND_FAILURE).sendToTarget();
                } finally {
                    closeSocket(sock);
                }

            }

            return result;
        }

        public void closeSocket(BluetoothSocket sock) {
            if (sock != null)
                // close socket
                try {
                    sock.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            tvScanNumber.setText(MessageFormat.format("Scanning {0} of 6", values[0]));
        }

        @Override
        protected void onPostExecute(String result) {
            tvScanning.setText("Complete");
            scanningLayoutBinding.circleProgress.clearAnimation();
            mHandler.obtainMessage(DATA_OK, result).sendToTarget();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getTroubleCodesTask != null) {
            if (getTroubleCodesTask.getStatus() != AsyncTask.Status.FINISHED) {
                getTroubleCodesTask.cancel(true);
            }
        }

        if (btAdapter != null && btAdapter.isDiscovering()) {
            btAdapter.cancelDiscovery();
        }
    }


    public static ScanningFragment newInstance() {

        Bundle args = new Bundle();
        ScanningFragment fragment = new ScanningFragment();
        fragment.setArguments(args);
        return fragment;
    }

    private boolean enableBluetooth() {
        final BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(ConfigActivity.ENABLE_BT_KEY, true).apply();

        if (btAdapter.isEnabled()) {
            connectToBluetoothDevice();
            return true;
        } else {
            btAdapter.enable();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    connectToBluetoothDevice();
                }
            }, 15000);
            return false;
        }
    }
}