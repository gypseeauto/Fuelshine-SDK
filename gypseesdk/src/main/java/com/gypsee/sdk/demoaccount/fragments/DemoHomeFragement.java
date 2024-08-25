package com.gypsee.sdk.demoaccount.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;
import androidx.databinding.DataBindingUtil;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import java.util.ArrayList;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;
import com.gypsee.sdk.R;
import com.gypsee.sdk.activities.Emergencyactivity;
import com.gypsee.sdk.activities.QuickManualActivity;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.database.DatabaseHelper;
import com.gypsee.sdk.databinding.FragmentHomeBinding;
import com.gypsee.sdk.demoaccount.activities.DemoSecondActivity;
import com.gypsee.sdk.dialogs.SelectCarDialog;
import com.gypsee.sdk.firebase.FirebaseLogEvents;
import com.gypsee.sdk.models.DailyTripAlertCountModel;
import com.gypsee.sdk.models.DrivingTipModel;
import com.gypsee.sdk.models.LastSafeTripModel;
import com.gypsee.sdk.models.ToolModelClass;
import com.gypsee.sdk.trips.TripRecord;
import com.gypsee.sdk.utils.StringFormater;

public class DemoHomeFragement extends Fragment implements View.OnClickListener {

    private View rootView;
    private Context context;
    private MyPreferenece myPreferenece;
    private Map<String, String> dtcVals;
    private FragmentHomeBinding fragmentHomeBinding;
    DatabaseHelper databaseHelper;
    private SelectCarDialog selectCarDialog;
    String TAG = DemoHomeFragement.class.getName();


    public static DemoHomeFragement newInstance(boolean isNotAttachedToContext) {
        Bundle args = new Bundle();
        args.putBoolean("isNotAttachedToContext", isNotAttachedToContext);
        DemoHomeFragement fragment = new DemoHomeFragement();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {


        fragmentHomeBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_home, container, false);

        rootView = fragmentHomeBinding.getRoot();
       // dtcVals = getDict(R.array.dtc_keys, R.array.dtc_values);
        databaseHelper = new DatabaseHelper(context);
        myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
       // user = myPreferenece.getUser();
        Log.e(TAG, "Firebasetoken : " + myPreferenece.getStringData(MyPreferenece.FCM_TOKEN));
       // latLongModelArrayList.clear();
        initStaticViews();
        //initSqlDatabaseOps();
        initViews();
        implementclickListener();
        initNavigationViews();
        setUpHeaderView();
        FirebaseLogEvents.firebaseLogEvent("viewed_product_demo",context);
        //setDrivingMode(true);

        //registerForegroundServiceReceiver();

        //checkGPSEnabledOrnotInit();

       /* isServiceRunning("BluetoothConnectionService");
        if (((AppCompatActivity) context).getIntent().getStringExtra("notificationType") != null) {
            checkNotificationIntentValues();
        }


        callServer(getResources().getString(R.string.getDrivingtoolsAPI), "Fetch Driving tool", 13);

        // For fresh login we will fetch the driving alerts.
        if (getActivity().getIntent().getBooleanExtra("freshlogin", false)) {
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(ConfigActivity.enable_distance_post_cc, true).apply();
            PreferenceManager.getDefaultSharedPreferences(context).edit().putBoolean(ConfigActivity.enable_speed_pid, true).apply();

            if (BuildConfig.DEBUG) {
                callServer(getResources().getString(R.string.fetchDrivingAlertUrl).replace("userid", user.getUserId()), "Fetch Driving Alerts", 8);
            }
            callServer(getResources().getString(R.string.Fetch_UserDetils_url).replace("userid", user.getUserId()), "Fetch user data", 7);
        }

        fragmentHomeBinding.alertsLayout.getRoot().setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
        addProtocol();

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(context);
        //checkGpsTimer();
        gotoBatteryOptiomization();*/
        callfunctionONResume();

        showTools();
        return rootView;
    }

    ArrayList<LastSafeTripModel> lastSafeTripModels = new ArrayList<>();
    ArrayList<DailyTripAlertCountModel> dailyTripAlertCountModelArrayList = new ArrayList<>();

    private void callfunctionONResume(){
        //12.922844331948363, 77.6471720881686
        //12.910852644663619, 77.64840790214964
        lastSafeTripModels.add(new LastSafeTripModel("tripId", "12.922844331948363", "77.6471720881686", "12.910852644663619", "77.64840790214964", "1.6", "7", "0", "2021-01-17 09:37:52", "Agara Village, 1st Sector, HSR Layout", "Parangi Palaya, Sector 2, HSR Layout","0","0"));
        loadUserImageInTotoalTrips();

        DailyTripAlertCountModel dailyTripAlertCountModel1 = new DailyTripAlertCountModel(3, 3, 1, 4, 4,
                10, "2021-04-21 09:35:52", "Wednesday", false, true);
        DailyTripAlertCountModel dailyTripAlertCountModel2 = new DailyTripAlertCountModel(2, 0, 0, 0, 0,
                15, "2021-04-22 09:35:52", "Thursday", false, true);
        DailyTripAlertCountModel dailyTripAlertCountModel3 = new DailyTripAlertCountModel(3, 0, 0, 0,0,
                12, "2021-04-23 09:35:52", "Friday", false, true);
        DailyTripAlertCountModel dailyTripAlertCountModel4 = new DailyTripAlertCountModel(1, 5, 6, 1, 3,
                13, "2021-04-19 09:35:52", "Monday", false, true);
        DailyTripAlertCountModel dailyTripAlertCountModel5 = new DailyTripAlertCountModel(2, 0, 0, 0, 0,
                11, "2021-04-20 09:35:52", "Tuesday", false, true);
        DailyTripAlertCountModel dailyTripAlertCountModel6 = new DailyTripAlertCountModel(5, 1, 2, 3, 5,
                16, "2021-04-24 09:35:52", "Saturday", false, true);
        DailyTripAlertCountModel dailyTripAlertCountModel7 = new DailyTripAlertCountModel(1, 2, 3, 4, 3,
                18, "2021-04-25 09:35:52", "Sunday", false, true);

        dailyTripAlertCountModelArrayList.add(dailyTripAlertCountModel7);
        dailyTripAlertCountModelArrayList.add(dailyTripAlertCountModel6);
        dailyTripAlertCountModelArrayList.add(dailyTripAlertCountModel3);
        dailyTripAlertCountModelArrayList.add(dailyTripAlertCountModel2);
        dailyTripAlertCountModelArrayList.add(dailyTripAlertCountModel1);
        dailyTripAlertCountModelArrayList.add(dailyTripAlertCountModel5);
        dailyTripAlertCountModelArrayList.add(dailyTripAlertCountModel4);
        showHideDailyTripAnalyticViews();

    }

    private void showHideDailyTripAnalyticViews() {
        if (dailyTripAlertCountModelArrayList.size() == 0) {
//            fragmentHomeBinding.weelyGoalsLayout.setVisibility(View.GONE);

        } else {
//            fragmentHomeBinding.weelyGoalsLayout.setVisibility(View.VISIBLE);
            checkTotalDaysWithNoalerts();
//            fragmentHomeBinding.drivingGloalsLayout.graphRecyclerView.setAdapter(new DrivingGoalsRecyclerAdapter(dailyTripAlertCountModelArrayList, context));
//            fragmentHomeBinding.weeklySpeedCountLayout.graphRecyclerView.setAdapter(new WeeklyOverspeedCountAdapter(dailyTripAlertCountModelArrayList, context, false));
//            fragmentHomeBinding.weeklySpeedCountLayout.tvValue.setText(dailyTripAlertCountModelArrayList.get(0).getTotalOverSpeedAlerts() + "");


//            fragmentHomeBinding.leadFootSyndromeLayout.ladFootCardView.setCardBackgroundColor(context.getResources().getColor(R.color.colorPrimary));
//
//            fragmentHomeBinding.leadFootSyndromeLayout.graphRecyclerView.setAdapter(new WeeklyOverspeedCountAdapter(dailyTripAlertCountModelArrayList, context, true));

            int totalAlerts = dailyTripAlertCountModelArrayList.get(0).getTotalHarshBreakingAlerts() + dailyTripAlertCountModelArrayList.get(0).getTotalHarshAccelerationAlerts() + dailyTripAlertCountModelArrayList.get(0).getTotalHighRPMAlerts();
//            fragmentHomeBinding.leadFootSyndromeLayout.tvValue.setText(totalAlerts + "");
//            fragmentHomeBinding.leadFootSyndromeLayout.title.setText("Lead foot syndrome");
//            fragmentHomeBinding.leadFootSyndromeLayout.subTitle.setText("Uneconomical & Unsafe driving");


            //Setting the alert type layout
//            fragmentHomeBinding.leadFootSyndromeLayout.harshAccelarationAlertLayout.rectangleView.setBackgroundColor(Color.parseColor("#91A1BE"));
//            fragmentHomeBinding.leadFootSyndromeLayout.harshAccelarationAlertLayout.alertName.setText("Harsh Accelaration");
//            fragmentHomeBinding.leadFootSyndromeLayout.highRpmAlertLayout.rectangleView.setBackgroundColor(Color.parseColor("#CED4DD"));
//            fragmentHomeBinding.leadFootSyndromeLayout.highRpmAlertLayout.alertName.setText("High RPM");
//
//            fragmentHomeBinding.leadFootSyndromeLayout.harshbreakingAlertLayout.rectangleView.setBackgroundColor(context.getResources().getColor(R.color.theme_blue));
//            fragmentHomeBinding.leadFootSyndromeLayout.harshbreakingAlertLayout.alertName.setText("Harsh Breaking");
//            fragmentHomeBinding.leadFootSyndromeLayout.alertsLayout.setVisibility(View.VISIBLE);

            //fetchDrivingGoalsAgainIfanydayIsLoading();
        }
    }

    private void checkTotalDaysWithNoalerts() {

        int daysWithZeroAlerts = 0;
        for (DailyTripAlertCountModel dailyTripAlertCountModel :
                dailyTripAlertCountModelArrayList) {

            int sum = dailyTripAlertCountModel.getTotalOverSpeedAlerts() + dailyTripAlertCountModel.getTotalHighRPMAlerts() + dailyTripAlertCountModel.getTotalHarshAccelerationAlerts() + dailyTripAlertCountModel.getTotalHarshBreakingAlerts();
            if (dailyTripAlertCountModel.getTotalTrips() > 0 && sum == 0) {
                daysWithZeroAlerts = daysWithZeroAlerts + 1;
            }
        }

        Log.e(TAG, "Drivng goals score : " + daysWithZeroAlerts);
//        fragmentHomeBinding.drivingGloalsLayout.tvValue.setText(daysWithZeroAlerts + "/7");

    }

    private void loadUserImageInTotoalTrips() {

//        fragmentHomeBinding.totalTripsCardView.safeTripCountTv.setText("23");
//
//        fragmentHomeBinding.totalTripsCardView.totalTripCountTv.setText("17");
//        fragmentHomeBinding.totalTripsCardView.kmDrivenTv.setText("67 KM");
//        fragmentHomeBinding.totalTripsCardView.drivingTimTv.setText(TimeUtils.timeConvert(Integer.parseInt("289")));
//        fragmentHomeBinding.lastTripSafeTripRecyerview.setHasFixedSize(true);
//        fragmentHomeBinding.lastTripSafeTripRecyerview.setAdapter(new LastSafestTripAdapter(context, lastSafeTripModels));
    }

    private void setUpHeaderView() {
        TextView profileusername = headerView.findViewById(R.id.profileUserName);
        TextView profileEmail = headerView.findViewById(R.id.profileEmail);
        CircleImageView profileImage = headerView.findViewById(R.id.profileImage);

        profileusername.setText(StringFormater.capitalizeWord("User"));
        String email = "useremail@xyz.com";
        profileEmail.setText(email);
            Glide
                    .with(context)
                    .load(R.drawable.ic_profile)
                    .placeholder(R.drawable.ic_profile)
                    .centerInside()
                    .into(profileImage);
    }


    ArrayList<ToolModelClass> gypseeStoriesModels = new ArrayList<>();
    ArrayList<ToolModelClass> gypseeOffersModels = new ArrayList<>();
    ArrayList<ToolModelClass> driverTrainingModels = new ArrayList<>();
    ArrayList<ToolModelClass> toolsModelClasses = new ArrayList<>();

    private void initStaticViews() {
        /*gypseeOffersModels.clear();
        gypseeStoriesModels.clear();
        driverTrainingModels.clear();
        toolsModelClasses.clear();*/
        //if (user.getUserImg().contains("http"))
//            Glide
//                    .with(context)
//                    .load(R.drawable.ic_profile)
//                    .placeholder(R.drawable.ic_profile)
//                    .centerInside()
//                    .into(fragmentHomeBinding.topBar.profileImage);

//            fragmentHomeBinding.notificationCountTV.setVisibility(View.GONE);


        String totalTriDuration = "289";
//        fragmentHomeBinding.totalTripsCardView.profileUserName.setText("User");
//
//            fragmentHomeBinding.totalTripsCardView.drivingTimTv.setText(TimeUtils.timeConvert(Integer.parseInt(totalTriDuration)));
//            fragmentHomeBinding.totalTripsCardView.profileUserName.setText("User Name");
//            fragmentHomeBinding.totalTripsCardView.safeTripCountTv.setText("23");
//
//            fragmentHomeBinding.totalTripsCardView.totalTripCountTv.setText("6");
//            fragmentHomeBinding.totalTripsCardView.kmDrivenTv.setText(new Double(67.5).intValue() + " KM");


        ArrayList<DrivingTipModel> drivingTipModels = new ArrayList<>();

        drivingTipModels.add(new DrivingTipModel("Good Driving Speed", "60 km/hr is the most safest and economical speed of the car."));
        drivingTipModels.add(new DrivingTipModel("Good RPM", "For the best fuel efficiency, keep your RPM between 1500-2000 at constant speed."));
        drivingTipModels.add(new DrivingTipModel("Harsh Breaking", "Decrease in the speed of 10.88 km/hr in 1 second is considered as a harsh breaking event."));
        drivingTipModels.add(new DrivingTipModel("Harsh Accelaration", "Increase in the speed of 9.88 km/hr in 1 second is considered as a harsh accelaration event."));
//        fragmentHomeBinding.drivingTechniqueRecylerview.setAdapter(new DrivingTipsAdapter(context, drivingTipModels));

        ArrayList<ToolModelClass> toolModelClasses = new ArrayList<>();




        ToolModelClass toolModelClass1 = new ToolModelClass("8",
                "RTO Learning Guide",
                "Learn-it-all that is needed. Score excellent in your RTO exams & get the driving licence.",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Learning_Guide-01.png?alt=media&token=8204507a-6682-4f7e-989d-3ef44bde6809",
                "https://gypsee.ai/questions/",
                "Driver training program",
                "#F9A602",
                "2021-01-17 09:37:14", "Driver training program");
        ToolModelClass toolModelClass2 = new ToolModelClass("21",
                "RTO Practice Tests",
                "Don't hold the practice back. Free practice set for your driving license exams, buckle up.",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Practice_Set-01.png?alt=media&token=99e3b098-fc39-417f-9c86-f461c3d75876",
                "https://gypsee.ai/practice-tests/",
                "Driver training program",
                "#EF820D",
                "2021-02-12 11:27:03", "Driver training program");
        ToolModelClass toolModelClass3 = new ToolModelClass("5",
                "RTO Mock Exams",
                "The top pick, mock test. Take mock tests, stay prepared & let's see how well you do.",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Mock_Test-01.png?alt=media&token=4b021af6-8ad0-48fc-8d88-b7dce3366321",
                "https://gypsee.ai/mock-exams",
                "Driver training program",
                "#1F3050",
                "2021-01-17 09:37:14", "Driver training program");
        ToolModelClass toolModelClass4 = new ToolModelClass("10",
                "RTO Vehicle Details",
                "Simply enter your vehicle number to get your vehicle registration details.",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/car_white-01.png?alt=media&token=0bc08e44-fc45-4d78-a47b-9da09aab5d33",
                "https://vahan.nic.in/nrservices/faces/user/searchstatus.xhtml",
                "Tools",
                "#1e1f26",
                "2021-01-17 09:37:23", "Vehicle Information Manager");
        ToolModelClass toolModelClass5 = new ToolModelClass("7",
                "Maintenance Reminders",
                "Set automated service reminders, Insurance renewals, PUC etc. & say No to penalties.",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Smart_Reminder-01.png?alt=media&token=a85ecc0a-ee6d-4bfe-9383-e408b0ca3327",
                "",
                "Tools",
                "#ef820d",
                "2021-01-17 09:35:52", "Vehicle Information Manager");
        ToolModelClass toolModelClass6 = new ToolModelClass("1",
                "Emergency Contacts",
                "Get easy access to emergency helpline numbers, check out the Gypsee directory.",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Emergency-01.png?alt=media&token=c7ff746f-738b-487a-a2c4-07ae21ff527a",
                "",
                "Tools",
                "#f9a602",
                "2021-01-17 09:35:39", "Vehicle Information Manager");
        ToolModelClass toolModelClass7 = new ToolModelClass("12",
                "Buy Safety Tracker",
                "A must have device for your and your loved ones safety on the roads. #SafeDriveLifestyle",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Drive-01.png?alt=media&token=54e11328-7da2-44fa-ad73-57f6d354bbae",
                "https://gypsee.ai/shop/gypsee-your-smart-safe-driving-kit/",
                "Offers",
                "#EF820D",
                "2021-01-17 09:35:52", "Offers");
        ToolModelClass toolModelClass8 = new ToolModelClass("11",
                "Gypsee Offers",
                "Save big on your car related services every time with our exclusive offers.",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/offers-01.png?alt=media&token=ea1e3a0c-82b8-4fc2-af58-70abae04b4d2",
                "https://gypsee.ai/offers/",
                "Offers",
                "#f9a602",
                "2021-01-17 09:37:40", "Offers");
        ToolModelClass toolModelClass9 = new ToolModelClass("13",
                "Gypsee Challenges",
                "Fasten your seat belt safe driving challenges are coming soon.",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/rewards_icon-01.png?alt=media&token=f34636b5-6d29-4101-92a9-7b1d56a4a4ff",
                "",
                "Offers",
                "#1e1f26",
                "2021-01-17 09:37:52", "Offers");
        ToolModelClass toolModelClass10 = new ToolModelClass("14",
                "How does it work ?",
                "",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Drive_Guide.png?alt=media&token=ace7de22-b747-4608-9f12-971be451e2d8",
                "https://gypsee.ai/self-help-guide/",
                "Stories",
                "",
                "2021-01-17 09:38:03", "Stories");
        ToolModelClass toolModelClass11 = new ToolModelClass("16",
                "Listen to our safe driving hero",
                "",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Copy%20of%20Safe%20drive_Heros.png?alt=media&token=121c91b2-9e04-4cc4-8992-63a8df74253f",
                "https://www.youtube.com/watch?v=ganLjdWlwQ4",
                "Stories",
                "",
                "2021-01-17 09:38:03", "Stories");
        ToolModelClass toolModelClass12 = new ToolModelClass("17",
                "#IARC",
                "India against roadcrash campaign",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/IARC.png?alt=media&token=261a6d5c-3e05-4c30-a2c1-d6d4dd16b611",
                "https://www.youtube.com/watch?v=8wXc0gXxpmc&feature=youtu.be",
                "Stories",
                "#F9A602",
                "2021-01-18 12:00:04", "Stories");
        ToolModelClass toolModelClass13 = new ToolModelClass("15",
                "Gypsee | Your smart driving assistant",
                "",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Drive_mate.png?alt=media&token=b36420a1-534f-40b3-8697-9435abf41eac",
                "https://gypsee.ai/7-mistakes-youre-shortening-the-life-of-your-car/",
                "Stories",
                "",
                "2021-01-17 09:38:03", "Stories");

        ToolModelClass toolModelClass14 = new ToolModelClass("25",
                "Verified Covid 19 Leads",
                "Verified covid 19 resources for oxygen, Plasma donor, doctors on call and ambulance",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/Covid%2019%20icon-1.png?alt=media&token=5f0b06ad-c783-4f89-b63e-6e12a3218c7c",
                "https://verifiedcovidleads.com/",
                "covid19resources",
                "#00bcd4",
                "2021-05-14 17:26:15",
                "COVID 19 Resources");

        ToolModelClass toolModelClass15 = new ToolModelClass(
                "27",
                "Availability of vaccine slots",
                "Here you get real-time availability of vaccine slots and you can put 3 locations on your watchlist.",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/noun_vaccine_865610.png?alt=media&token=5fed00bf-73a2-49bd-a554-f15727e16cf8",
                "https://vaccinetrackercowin.com/?sb=yRhY9njzsk",
                "covid19resources",
                "#1e3050",
                "",
                "COVID 19 Resources"
        );

        ToolModelClass toolModelClass16 = new ToolModelClass(
                "31",
                "Car Scan",
                "Scan your vehicle for issues",
                "https://firebasestorage.googleapis.com/v0/b/gypsee-6ee24.appspot.com/o/car_white-01.png?alt=media&token=0bc08e44-fc45-4d78-a47b-9da09aab5d33",
                "",
                "Tools",
                "#1e1f26",
                "",
                "Vehicle Information Manager"
        );

        toolModelClasses.add(toolModelClass14);
        toolModelClasses.add(toolModelClass15);
        toolModelClasses.add(toolModelClass1);
        toolModelClasses.add(toolModelClass2);
        toolModelClasses.add(toolModelClass3);
        toolModelClasses.add(toolModelClass16);
        toolModelClasses.add(toolModelClass4);
        toolModelClasses.add(toolModelClass5);
        toolModelClasses.add(toolModelClass6);
        toolModelClasses.add(toolModelClass7);
        toolModelClasses.add(toolModelClass8);
        toolModelClasses.add(toolModelClass9);
        toolModelClasses.add(toolModelClass10);
        toolModelClasses.add(toolModelClass11);
        toolModelClasses.add(toolModelClass12);
        toolModelClasses.add(toolModelClass13);


        /*
         Simple GridLayoutManager that spans two columns
         */


        ArrayList<String> categoryValues = new ArrayList<>();
        ArrayList<ArrayList<ToolModelClass>> homeData = new ArrayList<>();

        for (ToolModelClass toolModelClass: toolModelClasses){
            if (!categoryValues.contains(toolModelClass.getCategory())){
                categoryValues.add(toolModelClass.getCategory());
                homeData.add(new ArrayList<ToolModelClass>()); //this will create new arraylist inside main arraylist whenever a new category appears. it will also maintain order
            }
        }


        //grouping on basis of category in nested arraylist
        int j = 0, i = 0;
        while (i < categoryValues.size() && j < toolModelClasses.size()){
            if (categoryValues.get(i).equalsIgnoreCase(toolModelClasses.get(j).getCategory())){
                homeData.get(i).add(toolModelClasses.get(j));
                j++;
                i=0;
            } else {
                i++;
            }
        }

        Log.e(TAG, "demo grouped toolmodelclasses: " + homeData.toString());

//        fragmentHomeBinding.homeRecyclerview.setAdapter(new DemoHomeRecyclerAdapter(homeData, context));



       /* for (ToolModelClass toolModelClass :
                toolModelClasses) {
            if (toolModelClass.getType().equalsIgnoreCase("Stories")) {
                gypseeStoriesModels.add(toolModelClass);
            }
            if (toolModelClass.getType().equalsIgnoreCase("Offers")) {
                gypseeOffersModels.add(toolModelClass);
            }
            if (toolModelClass.getType().equalsIgnoreCase("Driver training program")) {
                driverTrainingModels.add(toolModelClass);
            }
            if (toolModelClass.getType().equalsIgnoreCase("Tools")) {
                toolsModelClasses.add(toolModelClass);
                Log.e(TAG, "Tools Add :  " + toolModelClass.getTitle() + " - " + toolModelClass.getRedirectUrl());
            }
        }*/


        /*fragmentHomeBinding.driverTrainingRecyclerview.setAdapter(new ToolsListRecyclerAdapter(driverTrainingModels, context));

        fragmentHomeBinding.driverTrainingRecyclerview.addOnItemTouchListener(
                new RecyclerItemClickListener(context, fragmentHomeBinding.driverTrainingRecyclerview, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        //Send brand name back
                        if (driverTrainingModels.size() <= position) {
                            Toast.makeText(context, "Please Contact Customer care for gypsee driver training program", Toast.LENGTH_LONG).show();

                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(driverTrainingModels.get(position).getRedirectUrl()));
                            startActivity(browserIntent);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );


        fragmentHomeBinding.vehicleInfoRecyclerview.setAdapter(new ToolsListRecyclerAdapter(toolsModelClasses, context));

        fragmentHomeBinding.vehicleInfoRecyclerview.addOnItemTouchListener(
                new RecyclerItemClickListener(context, fragmentHomeBinding.vehicleInfoRecyclerview, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        //Send brand name back
                        if (toolsModelClasses.get(position).getRedirectUrl().contains("http")) {

                            //add static car details
                           *//*
                                Intent carDetailsIntent = new Intent(context, MyCarDetailActivity.class);
                                carDetailsIntent.putExtra(Vehiclemodel.class.getName(), vehiclemodels.get(0));
                                startActivity(carDetailsIntent);*//*

                            startActivity(new Intent(context, DemoMyCarDetailActivity.class));


                        } else {

                            if (toolsModelClasses.get(position).getTitle().equalsIgnoreCase("Emergency Contacts")) {
                                context.startActivity(new Intent(context, Emergencyactivity.class));

                            } else if (toolsModelClasses.get(position).getTitle().equalsIgnoreCase("Maintenance Reminders")) {
                                context.startActivity(new Intent(context, DemoSecondActivity.class) //create demo second activity
                                        .putExtra("TAG", "MyCarsListFragment")
                                );
                            }
                        }

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        fragmentHomeBinding.gypseeStoriesRecyclerview.setAdapter(new GypseeStoriesRecyclerAdapter(context, gypseeStoriesModels));

        fragmentHomeBinding.gypseeStoriesRecyclerview.addOnItemTouchListener(
                new RecyclerItemClickListener(context, fragmentHomeBinding.gypseeStoriesRecyclerview, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        //Send brand name back
                        if (gypseeStoriesModels.size() <= position) {
                            Toast.makeText(context, "Please Contact Customer care for gypsee Stories", Toast.LENGTH_LONG).show();

                        } else {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gypseeStoriesModels.get(position).getRedirectUrl()));
                            startActivity(browserIntent);
                        }
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );

        fragmentHomeBinding.gypseeOffersRecyclerview.setAdapter(new ToolsListRecyclerAdapter(gypseeOffersModels, context));

        fragmentHomeBinding.gypseeOffersRecyclerview.addOnItemTouchListener(
                new RecyclerItemClickListener(context, fragmentHomeBinding.gypseeOffersRecyclerview, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        //Send brand name back
                        Log.e(TAG, "Redirect URL : " + gypseeOffersModels.get(position).getRedirectUrl());
                        if (gypseeOffersModels.size() <= position) {
                            Toast.makeText(context, "Please Contact Customer care for gypsee offers", Toast.LENGTH_LONG).show();
                        } else {
                            String redirectURL = gypseeOffersModels.get(position).getRedirectUrl();
                            if (redirectURL.contains("http")) {
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(gypseeOffersModels.get(position).getRedirectUrl()));
                                startActivity(browserIntent);
                            }
                        }

                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );*/

    }


    private void initViews() {
//        fragmentHomeBinding.speedFAB.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
        fragmentHomeBinding.setBlueToothDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.bluetooth_red, null));
//        fragmentHomeBinding.alertsLayout.setTitle("Alerts");
//        fragmentHomeBinding.alertsLayout.alerts.setVisibility(View.GONE);
//        fragmentHomeBinding.alertsLayout.setAlertsCount(String.valueOf(0));
//        fragmentHomeBinding.alertsLayout.countTv.setVisibility(View.GONE);
//        fragmentHomeBinding.performanceLayout.setTitle("Car Health");
//        fragmentHomeBinding.performanceLayout.setAlertsCount(String.valueOf(0));
//        fragmentHomeBinding.performanceLayout.countTv.setVisibility(View.GONE);
        fragmentHomeBinding.setOdbDrawable(ResourcesCompat.getDrawable(context.getResources(), R.drawable.obd_disconnected, null));
        fragmentHomeBinding.setUserName("Hola, User");
//        fragmentHomeBinding.status.setOnClickListener(this);
//        fragmentHomeBinding.trips.setOnClickListener(this);
//        fragmentHomeBinding.alertsLayout.getRoot().setOnClickListener(this);
//        fragmentHomeBinding.homeTv.setOnClickListener(this);
//        fragmentHomeBinding.performanceLayout.getRoot().setOnClickListener(this);
//        fragmentHomeBinding.exitTrainingMode.setVisibility(View.INVISIBLE);


        /*if (user.getUserEmail().equals("bhaskarnagir@gmail.com") || user.getUserEmail().equals("dillibabukadati777@gmail.com")) {
            AppSignatureHelper appSignatureHelper = new AppSignatureHelper(context);
            ArrayList<String> hashList = appSignatureHelper.getAppSignatures();

            String hashes = "";
            for (int i = 0; i < hashList.size(); i++) {
                hashes = hashList.get(i) + " , ";
            }
            fragmentHomeBinding.emptyTextv.setText(hashList.get(0));
            fragmentHomeBinding.emptyTextv.setVisibility(View.VISIBLE);
        }*/


    }

    private void implementclickListener() {

//        fragmentHomeBinding.status.setOnClickListener(this);
//        fragmentHomeBinding.logoLayout.setOnClickListener(this);
//        fragmentHomeBinding.tvLocateMe.setOnClickListener(this);
//        fragmentHomeBinding.menuBtn.setOnClickListener(this);
//        fragmentHomeBinding.notificationIcon.setOnClickListener(this);
//        fragmentHomeBinding.shareIcon.setOnClickListener(this);
//        fragmentHomeBinding.driveModeLayout.setOnClickListener(this);

        /*if (BuildConfig.DEBUG) {
            fragmentHomeBinding.driveModeLayout.setOnClickListener(this);
        }*/
        //fragmentHomeBinding.startDriveButton.setOnClickListener(this);
    }

    boolean isDriveMode = true;

    @Override
    public void onClick(View v) {


//        if (v.getId() == R.id.performanceLayout) {
//
//            startActivity(new Intent(getActivity(), DemoSecondActivity.class)
//                    .putExtra("TAG", "PerformanceFragment"));
//            return;
//        }

//        switch (v.getId()) {
//
//
//            case R.id.homeTv:
//                Log.e(TAG, "Tools layout clicked");
//                changeBackgroundforSelectedDriveMode(fragmentHomeBinding.homeTv);
//                showTools();
//                break;
//
//            case R.id.termsConditionsTv:
//                drawer.closeDrawers();
//                startActivity(new Intent(getActivity(), DemoSecondActivity.class)
//                        .putExtra("TAG", "RSATermsAndConditionsFragment")
//                        .putExtra("IsPrivaypolicy", false));
//                break;
//
//            /*case R.id.startDriveButton:
//                if (fragmentHomeBinding.startDriveButton.getText().equals("Start Driving")) {
//
//                    checkGPSEnabledOrnotInit();
//                } else if (tripDistanceFromGPSm < 1) {
//                    BluetoothHelperClass.showSinpleWarningDialog(context, getLayoutInflater(), "Trying the app?", "Have a look, It seems that you have not moved. Please drive for atleast 1 KM to create a successful trip log." +
//                            "\n\nFor getting a Trip Analysis Report about your driving, make sure that your mobile has active internet connection.");
//                } else {
//                    BluetoothHelperClass.showTripEndDialog(context, getLayoutInflater(), "", "Are you sure you want to end your trip?", responseFromServer, 0);
//                }
//                break;
//*/
//
//            case R.id.driveModeLayout:
//                //setDrivingMode(!isDriveMode);
//
//                startActivity(new Intent(getActivity(), DemoSecondActivity.class)
//                        .putExtra("TAG", "MyDevicesFragment"));
//                break;
//
//            case R.id.shareIcon:
//                Log.e(TAG, "Clicked on share icon");
//
//                //Show refer and earn fragment.
//
//                /*startActivity(new Intent(getActivity(), SecondActivity.class)
//                        .putExtra("TAG", "ReferAndEarnFragment"));*/
//
//                Toast.makeText(context, "Please register your account to avail this service", Toast.LENGTH_LONG).show();
//
//                //we will check user approved, then only we will go to refer and earn page. THis is old logic.
//                //checkUserApproved(true);
//
//
//                break;
//
//            case R.id.logoLayout:
//                Log.e(TAG, "clicked on logo layout");
//
//                fragmentHomeBinding.setBlueToothDrawable(context.getResources().getDrawable(R.drawable.bluetooth_red));
//                rotateAnimation(true);
//                new Handler().postDelayed(new Runnable() {
//                    @Override
//                    public void run() {
//                        rotateAnimation(false);
//                        fragmentHomeBinding.setBlueToothDrawable(context.getResources().getDrawable(R.drawable.bluetooth_green));
//                    }
//                }, 4000);
//                break;
//
//            case R.id.notificationIcon:
//
//                //if (fragmentHomeBinding.getNotificationCount().equals("0")) {
//                    Toast.makeText(context, "There are no new notifications", Toast.LENGTH_LONG).show();
//                /*    return;
//                }*/
//                //Show notifictions
//                /*if (checkPerformanceFragmentVisible("NotificationOffersFragment")) {
//                    ((AppCompatActivity) context).getSupportFragmentManager().popBackStack();
//                    MainActivity.activityMainBinding.bottomNavigationView.setVisibility(View.VISIBLE);
//
//                } else {
//
//                    Fragment notificationOffersFragment = new NotificationOffersFragment();
//
//                    ((AppCompatActivity) context).getSupportFragmentManager()
//                            .beginTransaction()
//                            .add(R.id.mainFrameLayout, notificationOffersFragment, "NotificationOffersFragment")
//                            .addToBackStack("NotificationOffersFragment")
//                            .commit();
//                    MainActivity.activityMainBinding.bottomNavigationView.setVisibility(View.GONE);
//                }*/
//
//                break;
//
//            case R.id.menu_btn:
//                if (checkPerformanceFragmentVisible("NotificationOffersFragment")) {
//                    drawer.closeDrawers();
//
//                } else
//                    drawer.openDrawer(GravityCompat.END);
//
//                break;
//
//
//            case R.id.status:
//                changeBackgroundforSelectedDriveMode(fragmentHomeBinding.status);
//                break;
//
//            case R.id.tv_locate_me:
//
//                Uri gmmIntentUri = Uri.parse("google.navigation:q=" + "12.922800028858695" + "," + "77.64698850215564");
//                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
//                mapIntent.setPackage("com.google.android.apps.maps");
//                startActivity(mapIntent);
//                break;
//
//            case R.id.trips:
//                changeBackgroundforSelectedDriveMode(fragmentHomeBinding.trips);
//                fragmentHomeBinding.progressTv.setText("Please wait ...fetching your trip details.");
//                addTripData();
//                //callServer(getResources().getString(R.string.tripListUrl).replace("userId", user.getUserId()), "Fetch trips", 9);
//                break;
//           /* case R.id.alertsLayout:
//
//                changeBackgroundforSelectedDriveMode(fragmentHomeBinding.alertsLayout.alerts);
//                //fetchAlertsAndDisplay();
//                fragmentHomeBinding.emptyTextv.setVisibility(View.VISIBLE);
//                fragmentHomeBinding.driveDetailsRecyclerview.setVisibility(View.GONE);
//                break;*/
//
//            case R.id.privacyPolicy:
//                drawer.closeDrawers();
//
//                startActivity(new Intent(getActivity(), DemoSecondActivity.class)
//                        .putExtra("TAG", "RSATermsAndConditionsFragment")
//                        .putExtra("IsPrivaypolicy", true));
//
//                break;
//        }
    }

    private void setDrivingMode(boolean isDrivingMode) {
        isDriveMode = isDrivingMode;

//        fragmentHomeBinding.gypseeDrive.setImageDrawable(isDrivingMode ? context.getDrawable(R.drawable.ic_gypsee_drive) : context.getDrawable(R.drawable.ic_phone_mode));
//        fragmentHomeBinding.driveModeTv.setText(isDrivingMode ? "Drive" : "Phone");
//        fragmentHomeBinding.logoLayout.setEnabled(isDrivingMode);

        /*if (fragmentHomeBinding.status.getBackground() != null) {
            fragmentHomeBinding.startDriveButton.setVisibility(isDrivingMode ? View.GONE : View.VISIBLE);
        }*/

    }

    private void changeBackgroundforSelectedDriveMode(final TextView textView) {

//        fragmentHomeBinding.status.setBackground(null);
//        fragmentHomeBinding.trips.setBackground(null);
//        fragmentHomeBinding.alertsLayout.alerts.setBackground(null);
//        fragmentHomeBinding.performanceLayout.alerts.setBackground(null);
//        fragmentHomeBinding.homeTv.setBackground(null);
//        fragmentHomeBinding.homeScrollView.setVisibility(View.GONE);
//        fragmentHomeBinding.scrollView.setVisibility(View.GONE);
//        fragmentHomeBinding.emptyTextv.setVisibility(View.GONE);

        textView.setBackground(context.getResources().getDrawable(R.drawable.buttoncolor));
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                fragmentHomeBinding.status.setBackground(getResources().getDrawable(R.drawable.buttoncolor));
                textView.setBackground(null);
            }
        }, 1000);*/
        //boolean isDriveMode = myPreferenece.getSharedPreferences().getBoolean(MyPreferenece.DRIVING_MODE, true);

//        fragmentHomeBinding.startDriveButton.setVisibility(View.GONE);

//        if (textView.getId() == fragmentHomeBinding.status.getId()) {
//
//           /* if (!isDriveMode) {
//                fragmentHomeBinding.startDriveButton.setVisibility(View.VISIBLE);
//            }*/
//            fragmentHomeBinding.tvLocateMe.setVisibility(View.VISIBLE);
//            fragmentHomeBinding.scrollView.setVisibility(View.VISIBLE);
//
//        } else {
//            //fragmentHomeBinding.startDriveButton.setVisibility(View.GONE);
//            fragmentHomeBinding.tvLocateMe.setVisibility(View.GONE);
//            fragmentHomeBinding.homeScrollView.setVisibility(View.VISIBLE);
//            fragmentHomeBinding.homeScreenViews.setVisibility(View.GONE);
//
//            if (textView.getId() == fragmentHomeBinding.homeTv.getId()) {
//                fragmentHomeBinding.homeScreenViews.setVisibility(View.VISIBLE);
//            }
//        }
    }

    private void showTools() {

//        fragmentHomeBinding.scrollView.setVisibility(View.GONE);

//        fragmentHomeBinding.driveDetailsRecyclerview.setVisibility(View.GONE);
//        fragmentHomeBinding.emptyTextv.setVisibility(View.GONE);

    }

    void rotateAnimation(boolean isRotate) {
//        if (isRotate) {
//            Animation anim = new CircularRotateAnimation(fragmentHomeBinding.bluetoothRotatingImg, context.getResources().getDimensionPixelSize(R.dimen.circle_radius));
//            //duration of animation
//            anim.setDuration(2000);
//            anim.setRepeatCount(Animation.INFINITE);
//            //start the animation
//            fragmentHomeBinding.bluetoothRotatingImg.startAnimation(anim);
//            fragmentHomeBinding.bluetoothImg.setVisibility(View.GONE);
//            fragmentHomeBinding.bluetoothRotatingImg.setVisibility(View.VISIBLE);
//        } else {
//            fragmentHomeBinding.bluetoothRotatingImg.clearAnimation();
//            fragmentHomeBinding.bluetoothImg.setVisibility(View.VISIBLE);
//            fragmentHomeBinding.bluetoothRotatingImg.setVisibility(View.GONE);
//        }
    }


    private boolean checkPerformanceFragmentVisible(String TAG) {

        Fragment performanceFragment = ((AppCompatActivity) context).getSupportFragmentManager().findFragmentByTag(TAG);
        if (performanceFragment != null && performanceFragment.isVisible()) {
            return true;
            //DO STUFF
        } else {
            //Whatever
            return false;
        }


    }

    private NavigationView navigationView;
    private DrawerLayout drawer;
    private View headerView;

    private void initNavigationViews() {

//        drawer = (DrawerLayout) rootView.findViewById(R.id.drawer_layout);
//        navigationView = (NavigationView) rootView.findViewById(R.id.nav_view);
//        headerView = navigationView.inflateHeaderView(R.layout.user_profile_header_layout);
//
//        TextView termsAndCondTv = navigationView.findViewById(R.id.termsConditionsTv);
//        TextView appVersionTv = navigationView.findViewById(R.id.appVersion);
//        TextView privacyPolicy = navigationView.findViewById(R.id.privacyPolicy);

        PackageInfo pInfo = null;
//        try {
//            pInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
//            appVersionTv.setText("V" + pInfo.versionName + " (" + pInfo.versionCode + ")");
//        } catch (PackageManager.NameNotFoundException e) {
//            e.printStackTrace();
//        }

//        termsAndCondTv.setOnClickListener(this);
//        privacyPolicy.setOnClickListener(this);
        navigationView.invalidate();
        //Setting Navigation View Item Selected Listener to handle the item click of the navigation menu
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {

            // This method will trigger on item Click of navigation menu
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                drawer.closeDrawers();

                //Check to see which item was being clicked and perform appropriate action
                int itemId = menuItem.getItemId();//Replacing the main content with ContentFragment Which is our Inbox View;
                if (itemId == R.id.nav_intro) {
                    startActivity(new Intent(context, QuickManualActivity.class));
                    //exit point. intent to login page
                } else if (itemId == R.id.nav_raiseTicket) {/* String userDetails = Utils.getDeviceName()
                                + "\n" + user.getUserFullName()
                                + "\n" + user.getUserPhoneNumber();
                        sendEmail("Issue with Gypsee App", userDetails);*/
                    //showGiveFeedbackDialog();
                    Toast.makeText(context, "Please register your account to avail this service", Toast.LENGTH_LONG).show();
                } else if (itemId == R.id.nav_emContact) {
                    startActivity(new Intent(context, Emergencyactivity.class));
                } else if (itemId == R.id.referral_code) {
                    Toast.makeText(context, "Please register your account to avail this service", Toast.LENGTH_LONG).show();
                    //checkReferralCodeApplied();
                } else if (itemId == R.id.nav_statting) {/*if (currentTrip == null) {
                            startActivity(new Intent(context, ConfigActivity.class));
                        } else {
                            Toast.makeText(context, "Change settings after end of your trip.", Toast.LENGTH_LONG).show();
                        }*/
                    Toast.makeText(context, "Please register your account to avail this service", Toast.LENGTH_LONG).show();
                } else if (itemId == R.id.nav_vehicles) {
                    startActivity(new Intent(getActivity(), DemoSecondActivity.class)
                            .putExtra("TAG", "MyCarsListFragment"));
                } else if (itemId == R.id.nav_buy_iot) {
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gypsee.ai/shop/gypsee-your-smart-safe-driving-kit/"));
                    startActivity(browserIntent);
                } else if (itemId == R.id.nav_offers) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://gypsee.ai/offers/"));
                    startActivity(intent);
                } else if (itemId == R.id.channel_referral_code) {
                    Toast.makeText(context, "Please register your account to avail this service", Toast.LENGTH_LONG).show();
                    //showChannelPromocodeDialog();
                } else if (itemId == R.id.technicalSupport) {
                    Toast.makeText(context, "Please register your account to avail this service", Toast.LENGTH_LONG).show();
                    //BluetoothHelperClass.showTripEndDialog(context, getLayoutInflater(), "Dear Customer!", "You are about to write mail to Gypsee Tech team. Within 48 hours, we will resolve your query.", responseFromServer, 2);
                }

                //Checking if the item is   in checked state or not, if not make it in checked state
                if (menuItem.isChecked()) {
                    menuItem.setChecked(false);
                } else {
                    menuItem.setChecked(true);
                }

                return true;
            }
        });

    }


    private void addTripData(){
        //add TripRecord objects in arraylist

        ArrayList<TripRecord> records = new ArrayList<>();

        TripRecord tripRecord1 = new TripRecord("id1",
                "2021-04-27 07:40:20", "2021-04-27 07:46:26", 0, 36, "00:06:15",
                "1.99", 4, "13.0796819", "77.5146879", "13.0809465", "77.5100663",
                "Doddabidarakallu, Kereguddadahalli, Chikkabanavara", "Lakeview Apartments Phase 4",90, 20.23, 6, 1, "NA","0","0");

        TripRecord tripRecord2 = new TripRecord("id2",
                "2021-04-27 07:18:33", "2021-04-27 07:30:39", 0, 26, "00:06:52",
                "1.16", 2, "13.080758", "77.5101952", "13.0794339", "77.5154589",
                "Lakeview Apartments Phase 4", "No#8, Kereguddadahalli, Chikkabanavara", 80, 15.1, 12, 0, "NA","0","0");

        TripRecord tripRecord3 = new TripRecord("id3",
                "2021-04-23 18:56:19", "2021-04-23 19:00:51", 0, 44, "00:19:43",
                "1.89", 5, "13.1010961", "77.4821561", "13.0859742", "77.4973881",
                "SH 39, Tarbanahalli, Karnataka", "SH 39, Chikkabanavara, Bengaluru, Karnataka 560090", 100, 18.94, 4, 0, "NA","0","0");

        records.add(tripRecord1);
        records.add(tripRecord2);
        records.add(tripRecord3);

//        fragmentHomeBinding.emptyTextv.setVisibility(View.GONE);
//        fragmentHomeBinding.driveDetailsRecyclerview.setVisibility(View.VISIBLE);
//
//        fragmentHomeBinding.driveDetailsRecyclerview.setAdapter(new DemoTripListAdapter(records, context));


    }






}
