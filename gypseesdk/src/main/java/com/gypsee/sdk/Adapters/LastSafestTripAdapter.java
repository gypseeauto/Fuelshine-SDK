package com.gypsee.sdk.Adapters;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.daimajia.numberprogressbar.NumberProgressBar;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.LastSafestTripLayoutBinding;
import com.gypsee.sdk.models.LastSafeTripModel;
import com.gypsee.sdk.utils.TimeUtils;

public class LastSafestTripAdapter extends RecyclerView.Adapter<LastSafestTripAdapter.ViewHolder> {

    //private ArrayList<Model1> list;
    private Context mContext;
    ArrayList<LastSafeTripModel> lastSafeTripModels = new ArrayList<>();

    public LastSafestTripAdapter(Context mContext, ArrayList<LastSafeTripModel> lastSafeTripModels) {
        super();
        this.mContext = mContext;
        this.lastSafeTripModels = lastSafeTripModels;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        LastSafestTripLayoutBinding itemBinding = DataBindingUtil.inflate(layoutInflater, R.layout.last_safest_trip_layout, parent, false);
        ViewHolder holder = new ViewHolder(itemBinding);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        LastSafestTripLayoutBinding lastSafestTripLayoutBinding = holder.lastSafestTripLayoutBinding;
        LastSafeTripModel lastSafeTripModel = lastSafeTripModels.get(position);
        lastSafestTripLayoutBinding.tripDurationTv.setText(TimeUtils.timeConvert(Integer.parseInt(lastSafeTripModel.getTripTimeInMin())));
        lastSafestTripLayoutBinding.dateTV.setText(TimeUtils.parseDate(lastSafeTripModel.getCreatedOn()));
        lastSafestTripLayoutBinding.tripDistanceTv.setText(lastSafeTripModel.getTripDistanceInKm() + " KM");

        //Set Progress foe alert values
        if (lastSafeTripModel.getTriAlerts().equals("0") || lastSafeTripModel.getTriAlerts().equals("[]")) {
            lastSafestTripLayoutBinding.alertCountTv.setText("0");
            int progress = 0;
            setUpProgressBar(holder.lastSafestTripLayoutBinding.overSpeedprogresss, progress);
            setUpProgressBar(holder.lastSafestTripLayoutBinding.harshBreakingprogresss, progress);
            setUpProgressBar(holder.lastSafestTripLayoutBinding.highRpmProgress, progress);
            setUpProgressBar(holder.lastSafestTripLayoutBinding.harshAccelarationprogresss, progress);

        } else {
            //We will parse the alerts
            HashMap<String, Double> alertValues = parseDrivingAlerts(lastSafeTripModel.getTriAlerts(), lastSafestTripLayoutBinding);
            setValuesBasedOnAlertValues(alertValues.get("Overspeed"), lastSafestTripLayoutBinding.overSpeedprogresss, 200d, lastSafestTripLayoutBinding.overSpeedTv);
            setValuesBasedOnAlertValues(alertValues.get("Harsh Braking"), lastSafestTripLayoutBinding.harshBreakingprogresss, 100d, lastSafestTripLayoutBinding.harshbreakingTv);
            setValuesBasedOnAlertValues(alertValues.get("Harsh Accelaration"), lastSafestTripLayoutBinding.harshAccelarationprogresss, 100d, lastSafestTripLayoutBinding.harshAccelarationTv);
            setValuesBasedOnAlertValues(alertValues.get("High RPM"), lastSafestTripLayoutBinding.highRpmProgress, 8000d, lastSafestTripLayoutBinding.highRPMTv);
        }

        String title = position == 0 ? "Last trip Details" : "Safest Recent Trip";
        int color = position == 0 ? mContext.getResources().getColor(R.color.gold_color) : mContext.getResources().getColor(R.color.colorPrimary);

        if (lastSafeTripModels.size() == 1 && lastSafeTripModel.getTriAlerts().equals("0")) {
            title = "Last Safest Trip";
            color = mContext.getResources().getColor(R.color.colorPrimary);
        }

        lastSafestTripLayoutBinding.origin.setText(lastSafeTripModel.getStartLocationName().equals("NA") ? getAddressName(Double.parseDouble(lastSafeTripModel.getStartLat()), Double.parseDouble(lastSafeTripModel.getStartLong())) : lastSafeTripModel.getStartLocationName());
        lastSafestTripLayoutBinding.destination.setText(lastSafeTripModel.getDestinationName().equals("NA") ? getAddressName(Double.parseDouble(lastSafeTripModel.getEndLat()), Double.parseDouble(lastSafeTripModel.getEndLong())) : lastSafeTripModel.getDestinationName());

        lastSafestTripLayoutBinding.cardView.setCardBackgroundColor(color);
        lastSafestTripLayoutBinding.tripTitleTv.setText(title);


    }

    private void setValuesBasedOnAlertValues(Double alertValues, NumberProgressBar overSpeedprogresss, Double maxValue, TextView Tv) {

        int percentage = 0;
        if (alertValues == null) {
        } else {
            percentage = (int) ((alertValues / maxValue) * 100);

            int id = Tv.getId();
            if (id == R.id.overSpeedTv) {
                Tv.setText(Tv.getText().toString() + " : " + alertValues.intValue() + " km/hr");
            } else if (id == R.id.harshbreakingTv) {
                Tv.setText(Tv.getText().toString() + " : -" + alertValues + " km/hr");
            } else if (id == R.id.harshAccelarationTv) {
                Tv.setText(Tv.getText().toString() + " : " + alertValues + " km/hr");
            } else if (id == R.id.highRPMTv) {
                Tv.setText(Tv.getText().toString() + " : " + alertValues.intValue() + " RPM");
            }

        }
        setUpProgressBar(overSpeedprogresss, percentage);

    }

    private HashMap<String, Double> parseDrivingAlerts(String triAlerts, LastSafestTripLayoutBinding lastSafestTripLayoutBinding) {
        try {
            JSONArray jsonArray = new JSONArray(triAlerts);
            //Values to store for alerts
            HashMap<String, Double> values = new HashMap<>();
            lastSafestTripLayoutBinding.alertCountTv.setText(jsonArray.length() + "");

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject jsonObject = jsonArray.getJSONObject(i);
                JSONObject alertData = new JSONObject(jsonObject.getString("data"));

                String alertType, alertValue, timeInterval, gForce, impact;
                long timeStamp;
                double lat, lng;
                Log.e(TAG, "Alerts is : " + alertData);

                //{"alert_Type":"Harsh Accelaration","alert_value ":"51 km\/hr","alert_description":"","g_force":"1.428m\/sec2","time_stamp":1598954550242,"time_interval":"1s","lat":12.9229433,"long":77.6471967,"impact":[32.484375,32.484375,32.546875,32.546875,32.59375,32.59375,32.703125,32.703125,32.625,32.625]}
                alertType = alertData.getString("alert_Type");
                alertValue = alertData.has("alert_value ") ? alertData.getString("alert_value ") : alertData.getString("alert_value");
                timeInterval = alertData.getString("time_interval");
                gForce = alertData.getString("g_force");
                impact = alertData.getString("impact");
                timeStamp = alertData.getLong("time_stamp");
                lat = alertData.getDouble("lat");
                lng = alertData.getDouble("long");

                alertValue = alertValue.replace(" RPM", "").replace(" km/hr", "").replace("-", "");
                if (values.get(alertType) == null) {
                    values.put(alertType, Double.parseDouble(alertValue));
                } else {
                    double newAlertValue = Double.parseDouble(alertValue);

                    if (newAlertValue >= values.get(alertType)) {
                        values.put(alertType, Double.parseDouble(alertValue));
                    }
                }


            }
            return values;
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return null;
    }

    private void setUpProgressBar(NumberProgressBar overSpeedprogresss, int progress) {

        overSpeedprogresss.setReachedBarHeight(15f);
        overSpeedprogresss.setUnreachedBarHeight(15f);
        overSpeedprogresss.setReachedBarColor(mContext.getResources().getColor(R.color.theme_blue));
        overSpeedprogresss.setUnreachedBarColor(mContext.getResources().getColor(R.color.white));
        overSpeedprogresss.setProgress(progress);
        overSpeedprogresss.setProgressTextSize(0f);
        overSpeedprogresss.setProgressTextVisibility(NumberProgressBar.ProgressTextVisibility.Invisible);
    }

    @Override
    public int getItemCount() {
        return lastSafeTripModels.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {


        LastSafestTripLayoutBinding lastSafestTripLayoutBinding;

        public ViewHolder(LastSafestTripLayoutBinding lastSafestTripLayoutBinding) {
            super(lastSafestTripLayoutBinding.getRoot());
            this.lastSafestTripLayoutBinding = lastSafestTripLayoutBinding;

        }
    }


    String TAG = LastSafestTripAdapter.class.getSimpleName();

    private String getAddressName(double lat, double longi) {

        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        List<Address> addresses = null;
        try {
            addresses = geocoder.getFromLocation(lat, longi, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (addresses == null || addresses.size() == 0) {
            return "NA";
        }
        String cityName = addresses.get(0).getAddressLine(0);
        String stateName = addresses.get(0).getAddressLine(1);
        String countryName = addresses.get(0).getAddressLine(2);
        Log.e(TAG, cityName + "-" + stateName + "-" + countryName);

        String[] addressArray = cityName.split(",");
        int arraySize = addressArray.length - 1;
        if (arraySize - 4 > 0)
            return addressArray[arraySize - 4] + " , " + addressArray[arraySize - 3];
        else
            return cityName;
    }
}
