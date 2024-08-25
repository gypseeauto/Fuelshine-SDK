package com.gypsee.sdk.demoaccount.activities;

import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentTripdetailsBinding;
import com.gypsee.sdk.helpers.BluetoothHelperClass;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.trips.TripRecord;

public class DemoMapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    FragmentTripdetailsBinding fragmentTripdetailsBinding;

    TripRecord tripRecord;
    private LocationManager mLocationManager;
    private LocationListener mLocationListener;
    private LatLng mOrigin;
    private LatLng mDestination;
    private MarkerOptions mMarkerOptions;
    private Polyline mPolyline;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        fragmentTripdetailsBinding = DataBindingUtil.setContentView(this, R.layout.fragment_tripdetails);
        //position = getIntent().getIntExtra(DemoTripListAdapter.class.getName(), 0);
        tripRecord = getIntent().getParcelableExtra(TripRecord.class.getSimpleName());
        id = tripRecord.getId();
        addLatLngData();

        setDate();
        setUpToolBar();
        //callServer(getString(R.string.ffetchTripLatLong).replace("tripId", tripRecord.getId()), "Fetch single trip lat,lng", 0);





    }



    private void initGoogleMap() {
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
//                .findFragmentById(R.id.map);
//        mapFragment.getMapAsync(this);
    }

    private void setDate() {

        SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

        try {
            Date date1 = new SimpleDateFormat("yyyy-MM-dd hh:mm").parse(tripRecord.getStartDate());
//            fragmentTripdetailsBinding.tripdetailsCardOne.tvDate.setText(format.format(Objects.requireNonNull(date1)));
//            fragmentTripdetailsBinding.tripdetailsCardOne.tripDurationTv.setText(TimeUtils.calcDiffTime(simpleDateFormat.parse(tripRecord.getStartDate()).getTime(), simpleDateFormat.parse(tripRecord.getEndDate()).getTime()));
        } catch (ParseException e) {
            e.printStackTrace();
        }

//        fragmentTripdetailsBinding.tripdetailsCardOne.sourceDestinationTv.setText(tripRecord.getStartLocationName() + " ➥ \n" +
//                tripRecord.getDestinationName());
//
//        fragmentTripdetailsBinding.tripdetailsCardOne.tripDistanceTv.setText(tripRecord.getDistanceCovered() + " KM");
//
//
//        fragmentTripdetailsBinding.tripdetailsCardOne.alertCountTv.setText(tripRecord.getAlertCount() + "");

    }

    private void setUpToolBar() {
//        fragmentTripdetailsBinding.toolbarlayout.setTitle("Trip detail");
//        fragmentTripdetailsBinding.toolbarlayout.toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);
//        fragmentTripdetailsBinding.toolbarlayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                finish();
//            }
//        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        //We will zoom more if the distance covered is less than 5 KM.

        mMap.setMinZoomPreference(Double.parseDouble(tripRecord.getDistanceCovered()) < 5 ? 13.0f : 10.0f);
        mMap.setMaxZoomPreference(Double.parseDouble(tripRecord.getDistanceCovered()) < 5 ? 15.0f : 13.0f);


        drawMapUsingLatLngOfTrip();

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Double.parseDouble(tripRecord.getStartLat()), Double.parseDouble(tripRecord.getStartLong()));
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .icon(BitmapDescriptorFactory.fromBitmap(BluetoothHelperClass.convertToBitmap(ContextCompat.getDrawable(DemoMapsActivity.this, R.drawable.ic_map_origin), 50, 50)))
                .title("start"));


        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        sydney = new LatLng(Double.parseDouble(tripRecord.getEndLat()), Double.parseDouble(tripRecord.getEndLong()));
        mMap.addMarker(new MarkerOptions()
                .position(sydney)
                .icon(BitmapDescriptorFactory.fromBitmap(BluetoothHelperClass.convertToBitmap(ContextCompat.getDrawable(DemoMapsActivity.this, R.drawable.ic_map_destination), 50, 50)))
                .title("Stop"));


        if (tripRecord.getAlertCount() > 0) {
            User user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, this).getUser();

            addDrivingAlerts();
            addVehicleAlerts();
            //callServer(getResources().getString(R.string.fetchDrivingAlertUrl).replace("userid", user.getUserId()), "Fetch Driving Alerts", 1);
        }

        //drawRoute();
        // getMyLocation();
    }

    private List<LatLng> routeCoordinates;
    private List<LatLng> actualroutePoints = new ArrayList<>();

    private void drawMapUsingLatLngOfTrip() {


        PolylineOptions lineOptions = new PolylineOptions();

        LatLng sydney = new LatLng(Double.parseDouble(tripRecord.getStartLat()), Double.parseDouble(tripRecord.getStartLong()));
        routeCoordinates.add(0, sydney);
        sydney = new LatLng(Double.parseDouble(tripRecord.getEndLat()), Double.parseDouble(tripRecord.getEndLong()));
        routeCoordinates.add(sydney);

        // Adding all the points in the route to LineOptions
        lineOptions.addAll(routeCoordinates);
        lineOptions.width(6);
        lineOptions.color(getResources().getColor(R.color.theme_blue));

        // Drawing polyline in the Google Map for the i-th route
        if (lineOptions != null) {
            if (mPolyline != null) {
                mPolyline.remove();
            }
            mPolyline = mMap.addPolyline(lineOptions);

        } else
            Toast.makeText(getApplicationContext(), "No route is found", Toast.LENGTH_LONG).show();
    }

    private void addLatLngData(){

        routeCoordinates = new ArrayList<>();
        //assign position data to routeCoordinate

        ArrayList<LatLng> trip1 = new ArrayList<>();
        trip1.add(new LatLng(13.0796498, 77.5147197));
        trip1.add(new LatLng(13.0797265, 77.5143366));
        trip1.add(new LatLng(13.0797809, 77.5140981));
        trip1.add(new LatLng(13.0798262, 77.513725));
        trip1.add(new LatLng(13.0798426, 77.5134123));
        trip1.add(new LatLng(13.0798447, 77.5131649));
        trip1.add(new LatLng(13.0798672, 77.5127991));
        trip1.add(new LatLng(13.0800352, 77.5106019));
        trip1.add(new LatLng(13.0801355, 77.5100641));
        trip1.add(new LatLng(13.0801675, 77.5097837));
        trip1.add(new LatLng(13.0801976, 77.5094845));
        trip1.add(new LatLng(13.0802019, 77.509033));
        trip1.add(new LatLng(13.0800134, 77.5085564));
        trip1.add(new LatLng(13.0797833, 77.5081704));
        trip1.add(new LatLng(13.0795815, 77.5077926));
        trip1.add(new LatLng(13.079387, 77.5074109));
        trip1.add(new LatLng(13.0792905, 77.5070592));
        trip1.add(new LatLng(13.0792534, 77.5067488));
        trip1.add(new LatLng(13.0792056, 77.5063873));
        trip1.add(new LatLng(13.0793928, 77.505849));
        trip1.add(new LatLng(13.0795491, 77.5056352));
        trip1.add(new LatLng(13.0798841, 77.5052363));
        trip1.add(new LatLng(13.0802041, 77.5047962));
        trip1.add(new LatLng(13.0803741, 77.504424));
        trip1.add(new LatLng(13.0805298, 77.5037579));
        trip1.add(new LatLng(13.0806473, 77.5031645));
        trip1.add(new LatLng(13.0806972, 77.5028883));
        trip1.add(new LatLng(13.0804061, 77.5028313));
        trip1.add(new LatLng(13.0806655, 77.5027821));
        trip1.add(new LatLng(13.0806711, 77.5031778));
        trip1.add(new LatLng( 13.080562, 77.5037349));
        trip1.add(new LatLng(13.0803952, 77.5043437));
        trip1.add(new LatLng(13.0802307, 77.5047517));
        trip1.add(new LatLng(13.0799488, 77.5051494));
        trip1.add(new LatLng(13.0795835, 77.505621));
        trip1.add(new LatLng(13.0793533, 77.5059109));
        trip1.add(new LatLng(13.0791962, 77.5062287));
        trip1.add(new LatLng(13.0792296, 77.5064572));
        trip1.add(new LatLng(13.0792851, 77.5068703));
        trip1.add(new LatLng(13.0793487, 77.5072759));
        trip1.add(new LatLng(13.0796765, 77.508023));
        trip1.add(new LatLng(13.0799931, 77.5085943));
        trip1.add(new LatLng(13.0802486, 77.5094482));
        trip1.add(new LatLng(13.0801871, 77.5099629));
        trip1.add(new LatLng(13.0801304, 77.510422));
        trip1.add(new LatLng(13.0800824, 77.5106789));
        trip1.add(new LatLng(13.0803659, 77.5108898));
        trip1.add(new LatLng(13.0806279, 77.5109282));
        trip1.add(new LatLng(13.0807287, 77.510527));
        trip1.add(new LatLng(13.0807609, 77.5102798));
        trip1.add(new LatLng(13.0808806, 77.5100514));


        ArrayList<LatLng> trip2 = new ArrayList<>();
        trip2.add(new LatLng(13.0793837, 77.5148244));
        trip2.add(new LatLng(13.0796146, 77.5147559));
        trip2.add(new LatLng(13.079569, 77.5150258));
        trip2.add(new LatLng(13.0795609, 77.5152943));
        trip2.add(new LatLng(13.0794939, 77.5155579));
        trip2.add(new LatLng(13.0793609, 77.5157483));
        trip2.add(new LatLng(13.0791222, 77.5161444));
        trip2.add(new LatLng(13.0789249, 77.5164212));
        trip2.add(new LatLng(13.0787047, 77.5166189));
        trip2.add(new LatLng(13.0785175, 77.5168129));
        trip2.add(new LatLng(13.0781581, 77.5171051));
        trip2.add(new LatLng(13.0779229, 77.517221));
        trip2.add(new LatLng(13.0776997, 77.5172589));
        trip2.add(new LatLng(13.0772975, 77.5173236));
        trip2.add(new LatLng(13.0768142, 77.5172675));
        trip2.add(new LatLng(13.0764596, 77.5172298));
        trip2.add(new LatLng(13.076079, 77.5171574));
        trip2.add(new LatLng(13.0758342, 77.5170656));
        trip2.add(new LatLng(13.0755781, 77.5170025));
        trip2.add(new LatLng(13.0753107, 77.5169481));
        trip2.add(new LatLng(13.0750383, 77.5168828));
        trip2.add(new LatLng(13.0747467, 77.5169753));
        trip2.add(new LatLng(13.074549, 77.5173061));
        trip2.add(new LatLng(13.0743509, 77.5175913));
        trip2.add(new LatLng(13.0742027, 77.5178879));
        trip2.add(new LatLng(13.0740217, 77.5182511));
        trip2.add(new LatLng(13.0738983, 77.5187082));
        trip2.add(new LatLng(13.0738192, 77.518963));
        trip2.add(new LatLng(13.0737135, 77.5192059));
        trip2.add(new LatLng(13.0736235, 77.5194781));
        trip2.add(new LatLng(13.0735904, 77.5197808));
        trip2.add(new LatLng(13.0735867, 77.5200402));
        trip2.add(new LatLng(13.0787294, 77.5158995));
        trip2.add(new LatLng(13.0788046, 77.5165269));
        trip2.add(new LatLng(13.0789851, 77.5162075));
        trip2.add(new LatLng(13.0791532, 77.5159587));
        trip2.add(new LatLng(13.0793394, 77.5156608));
        trip2.add(new LatLng(13.079471, 77.5154568));


        ArrayList<LatLng> trip3 = new ArrayList<>();
        trip3.add(new LatLng(13.1010046, 77.4830444));
        trip3.add(new LatLng(13.1009592, 77.4833108));
        trip3.add(new LatLng(13.1008294, 77.4836704));
        trip3.add(new LatLng(13.1004932, 77.4837659));
        trip3.add(new LatLng(13.1000627, 77.4838134));
        trip3.add(new LatLng(13.099732, 77.4838662));
        trip3.add(new LatLng(13.0992076, 77.4839619));
        trip3.add(new LatLng(13.098822, 77.4840331));
        trip3.add(new LatLng(13.0984994, 77.4840771));
        trip3.add(new LatLng(13.0981001, 77.4841505));
        trip3.add(new LatLng(13.097821, 77.4841999));
        trip3.add(new LatLng(13.0975384, 77.4842375));
        trip3.add(new LatLng(13.0969876, 77.4849508));
        trip3.add(new LatLng(13.0968095, 77.4853634));
        trip3.add(new LatLng(13.096532, 77.4857886));
        trip3.add(new LatLng(13.096076, 77.4862032));
        trip3.add(new LatLng(13.0957707, 77.4865289));
        trip3.add(new LatLng(13.0952442, 77.4870591));
        trip3.add(new LatLng(13.0947705, 77.4875115));
        trip3.add(new LatLng(13.0944421, 77.4878427));
        trip3.add(new LatLng(13.0940908, 77.4882153));
        trip3.add(new LatLng(13.09381, 77.4884921));
        trip3.add(new LatLng(13.0935651, 77.4887684));
        trip3.add(new LatLng(13.093343, 77.4889653));
        trip3.add(new LatLng(13.0931534, 77.4893228));
        trip3.add(new LatLng(13.0930591, 77.4897497));
        trip3.add(new LatLng(13.0928122, 77.4902863));
        trip3.add(new LatLng(13.0925925, 77.4906788));
        trip3.add(new LatLng(13.0923532, 77.4910688));
        trip3.add(new LatLng(13.0919899, 77.4914496));
        trip3.add(new LatLng(13.0916547, 77.4916076));
        trip3.add(new LatLng(13.0911178, 77.4919901));
        trip3.add(new LatLng(13.0908589, 77.4923486));
        trip3.add(new LatLng(13.0904552, 77.4927857));
        trip3.add(new LatLng(13.0899753, 77.4931698));
        trip3.add(new LatLng(13.0896784, 77.4933931));
        trip3.add(new LatLng(13.0891988, 77.4937653));
        trip3.add(new LatLng(13.0888405, 77.4941506));
        trip3.add(new LatLng(13.08829, 77.4947258));
        trip3.add(new LatLng(13.0879208, 77.4950756));
        trip3.add(new LatLng(13.0876503, 77.4953057));
        trip3.add(new LatLng(13.0874533, 77.4955343));
        trip3.add(new LatLng(13.0870563, 77.4958682));
        trip3.add(new LatLng(13.0866304, 77.4963043));
        trip3.add(new LatLng(13.0863846, 77.4965888));
        trip3.add(new LatLng(13.0862445, 77.4968039));
        trip3.add(new LatLng(13.0860692, 77.4971177));
        trip3.add(new LatLng(13.0859742, 77.4973881));

        switch (id){

            case "id2" : routeCoordinates = trip2;
                break;

            case "id3" : routeCoordinates = trip3;
                break;

            default: routeCoordinates = trip1;
                break;

        }



        initGoogleMap();
    }

    private void addDrivingAlerts(){
        //add marker here

        //Trip 1
        ArrayList<String> alertType1 = new ArrayList<>();
        alertType1.add("Harsh Accelaration");
        alertType1.add("Harsh Braking");
        alertType1.add("Harsh Braking");
        alertType1.add("Harsh Braking");

        ArrayList<LatLng> alert1 = new ArrayList<>();
        alert1.add(new LatLng(13.0796806, 77.5146223));
        alert1.add(new LatLng(13.0792056, 77.5063873));
        alert1.add(new LatLng(13.0799488, 77.5051494));
        alert1.add(new LatLng(13.0801304, 77.510422));

        //Trip 2
        ArrayList<String> alertType2 = new ArrayList<>();
        alertType2.add("Harsh Braking");
        alertType2.add("Harsh Braking");

        ArrayList<LatLng> alert2 = new ArrayList<>();
        alert2.add(new LatLng(13.0807737, 77.5102129));
        alert2.add(new LatLng(13.0751193, 77.5168833));

        //Trip 3
        ArrayList<String> alertType3 = new ArrayList<>();
        alertType3.add("Harsh Braking");
        alertType3.add("Harsh Braking");
        alertType3.add("Harsh Braking");
        alertType3.add("Harsh Braking");
        alertType3.add("Harsh Braking");

        ArrayList<LatLng> alert3 = new ArrayList<>();
        alert3.add(new LatLng(13.098822, 77.4840331));
        alert3.add(new LatLng(13.0975384, 77.4842375));
        alert3.add(new LatLng(13.0957707, 77.4865289));
        alert3.add(new LatLng(13.0936968, 77.4886047));
        alert3.add(new LatLng(13.0879208, 77.4950756));


        /*alert3.add(new LatLng(13.096076, 77.4862032));
        alert3.add(new LatLng(13.0923532, 77.4910688));
        alert3.add(new LatLng(13.08829, 77.4947258));
        alert3.add(new LatLng(13.0874533, 77.4955343));
        alert3.add(new LatLng(13.0862445, 77.4968039));
*/


        switch (id){

            case "id2":
                for (int i=0; i<alertType2.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .position(alert2.get(i))
                            .icon(BitmapDescriptorFactory.fromBitmap(BluetoothHelperClass.convertToBitmap(ContextCompat.getDrawable(DemoMapsActivity.this, checkAlertType(alertType2.get(i))), 50, 50)))
                            .title(alertType2.get(i)));
                }
                break;

            case "id3":
                for (int i=0; i<alertType3.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .position(alert3.get(i))
                            .icon(BitmapDescriptorFactory.fromBitmap(BluetoothHelperClass.convertToBitmap(ContextCompat.getDrawable(DemoMapsActivity.this, checkAlertType(alertType3.get(i))), 50, 50)))
                            .title(alertType3.get(i)));
                }
                break;

            case "id1":
            default:
                for (int i=0; i<alertType1.size(); i++) {
                    mMap.addMarker(new MarkerOptions()
                            .position(alert1.get(i))
                            .icon(BitmapDescriptorFactory.fromBitmap(BluetoothHelperClass.convertToBitmap(ContextCompat.getDrawable(DemoMapsActivity.this, checkAlertType(alertType1.get(i))), 50, 50)))
                            .title(alertType1.get(i)));
                }

                break;


        }








    }

    private void addVehicleAlerts(){

        if (id.equals("id1")){

            //trip1.add(new LatLng(13.0803741, 77.504424));
            mMap.addMarker(new MarkerOptions()
                    .position(new LatLng(13.0803741, 77.504424))
                    .icon(BitmapDescriptorFactory.fromBitmap(BluetoothHelperClass.convertToBitmap(ContextCompat.getDrawable(DemoMapsActivity.this, R.drawable.ic_vehicle_alert), 50, 50)))
                    .title("Trouble Code Found")
                    .snippet("P0101")
            );

        }

    }

    private int checkAlertType(String alerType) {

        int drawable = R.drawable.ic_map_highrpm;
        switch (alerType) {
            case "High RPM":
                break;

            case "Harsh Braking":
                drawable = R.drawable.ic_map_harshbraking;
                break;
            case "Harsh Accelaration":
                drawable = R.drawable.ic_map_harshaccelaration;
                break;
            case "Overspeed":
                drawable = R.drawable.ic_map_overspeed;
                break;
        }

        return drawable;
    }



}
