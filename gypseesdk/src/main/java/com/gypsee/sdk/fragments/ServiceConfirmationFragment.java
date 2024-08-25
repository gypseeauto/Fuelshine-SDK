package com.gypsee.sdk.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentServiceConfirmationBinding;
import com.gypsee.sdk.models.ServiceTransactionModel;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ServiceConfirmationFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ServiceConfirmationFragment extends Fragment implements View.OnClickListener {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ServiceConfirmationFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ServiceConfirmationFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ServiceConfirmationFragment newInstance(String param1, String param2) {
        ServiceConfirmationFragment fragment = new ServiceConfirmationFragment();
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

    FragmentServiceConfirmationBinding fragmentServiceConfirmationBinding;
    Context context;

    ServiceTransactionModel serviceTransactionModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        fragmentServiceConfirmationBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_service_confirmation, container, false);
        context = getContext();
        serviceTransactionModel = ((AppCompatActivity) context).getIntent().getParcelableExtra(ServiceTransactionModel.class.getSimpleName());

        if (serviceTransactionModel == null) {
            goback();
        }
        initToolBar();
        implementClickListeners();
        initViews();
        return fragmentServiceConfirmationBinding.getRoot();
    }

    private void goback() {
        ((AppCompatActivity) context).finish();
    }

    private void initViews() {
        String htmlString = "booking id <u>" + serviceTransactionModel.getBookingId() + "</u>";
        fragmentServiceConfirmationBinding.bookingIdtxv.setText(Html.fromHtml(htmlString));

    }

    private void implementClickListeners() {
        fragmentServiceConfirmationBinding.shareLocationBtn.setOnClickListener(this);
        fragmentServiceConfirmationBinding.bookingIdtxv.setOnClickListener(this);
    }

    private void initToolBar() {
        fragmentServiceConfirmationBinding.toolbarlayout.setTitle("Confirmation");
        fragmentServiceConfirmationBinding.toolbarlayout.toolbar.setNavigationIcon(R.drawable.back_button);
        fragmentServiceConfirmationBinding.toolbarlayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goback();
            }
        });
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.shareLocationBtn) {/* String url = "smsto:916362948112";
         *//*Intent i = new Intent(Intent.ACTION_VIEW);

                startActivity(i);*//*

                Intent i = new Intent(Intent.ACTION_SENDTO, Uri.parse(url));
                i.putExtra("sms_body", "Hai Good Morning");
                i.setPackage("com.whatsapp")
                startActivity(i);*/

            MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
            Location lastLocation = getLastBestLocation();
            String serviceName = "";
            try {
                JSONObject js = new JSONObject(serviceTransactionModel.getGypseeService());
                serviceName = js.getString("serviceName");
            } catch (JSONException e) {
                e.printStackTrace();

            }
            String text = "Hi There, \nI'm " + myPreferenece.getUser().getUserFullName() + " & I've booked " + serviceName + " at https://maps.google.com/?q=" + lastLocation.getLatitude() + "," + lastLocation.getLongitude() + " . Kindly provide me the best experience with your service. So that I will always avail services from you.\n";

            String str = "<b>" + "Let's drive together!" + "</b>";
            PackageManager packageManager = context.getPackageManager();
            Intent i = new Intent(Intent.ACTION_VIEW);

            try {
                String url = "https://api.whatsapp.com/send?phone=916362948112" + "&text=" + URLEncoder.encode(text + Html.fromHtml(str), "UTF-8");
                i.setPackage("com.whatsapp");
                i.setData(Uri.parse(url));
                if (i.resolveActivity(packageManager) != null) {
                    context.startActivity(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (id == R.id.bookingIdtxv) {
        }
    }

    private Location getLastBestLocation() {
        LocationManager mLocationManager = (LocationManager) context.getSystemService(LOCATION_SERVICE);

        Location locationGPS = mLocationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        Location locationNet = mLocationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);

        long GPSLocationTime = 0;
        if (null != locationGPS) {
            GPSLocationTime = locationGPS.getTime();
        }

        long NetLocationTime = 0;

        if (null != locationNet) {
            NetLocationTime = locationNet.getTime();
        }

        if (0 < GPSLocationTime - NetLocationTime) {
            return locationGPS;
        } else {
            return locationNet;
        }
    }
}