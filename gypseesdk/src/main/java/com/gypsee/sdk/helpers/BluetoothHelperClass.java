package com.gypsee.sdk.helpers;

import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.github.pires.obd.enums.AvailableCommandNames;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import com.gypsee.sdk.R;
import com.gypsee.sdk.databinding.AlertAddcarLayoutBinding;
import com.gypsee.sdk.databinding.ErrorDialogBinding;
import com.gypsee.sdk.databinding.FragmentZoopAddCarDialogBinding;
import com.gypsee.sdk.databinding.LayoutNotificationDialogBinding;
import com.gypsee.sdk.databinding.SuccessDialogBinding;
import com.gypsee.sdk.jsonParser.ResponseFromServer;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.ACTIVITY_RECOGNITION;
import static android.Manifest.permission.BLUETOOTH;
import static android.Manifest.permission.BLUETOOTH_ADMIN;
import static android.Manifest.permission.BLUETOOTH_CONNECT;
import static android.Manifest.permission.BLUETOOTH_SCAN;
import static android.Manifest.permission.MODIFY_AUDIO_SETTINGS;
import static android.Manifest.permission.POST_NOTIFICATIONS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.content.pm.PackageManager.PERMISSION_GRANTED;

public class BluetoothHelperClass {

    Context context;

    public BluetoothHelperClass(Context context) {
        this.context = context;
    }




    public static Set<BluetoothDevice> getBluetoothDevices() {
        // get Bluetooth device
        BluetoothAdapter btAdapter = BluetoothAdapter.getDefaultAdapter();
        if (btAdapter.isEnabled()) {
            btAdapter.startDiscovery();
        } else {
            btAdapter.enable();
        }

        //Make some Bluetooth operations
        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        return pairedDevices;
    }

    static boolean isPairObdDialogShowing = false;

    public static void showDialogToPairObd2Device(final Context context) {


        if (isPairObdDialogShowing) {
            return;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Connect with your safe driving kit!");
        builder.setMessage("Pair with OBDII in the bluetooth settings page of your mobile phone.");
        //builder.setIcon(R.drawable.icon);

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //Toast.makeText(context, "Success", Toast.LENGTH_SHORT).show();
                        Intent intentOpenBluetoothSettings = new Intent();
                        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
                        ((AppCompatActivity) context).startActivity(intentOpenBluetoothSettings);
                    }
                });
        builder.setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        isPairObdDialogShowing = false;
                        //If bluetooth not enabled, it will return back.
                        //Toast.makeText(context, "Please go to settings page and select the blutooth device", Toast.LENGTH_LONG).show();
                    }
                });
        isPairObdDialogShowing = true;
        AlertDialog alert = builder.create();
        if (!((AppCompatActivity) context).isFinishing()) {
            //show dialog
            alert.show();
        }
    }

    public static String LookUpCommand(String txt) {
        for (AvailableCommandNames item : AvailableCommandNames.values()) {
            if (item.getValue().equals(txt)) return item.name();
        }
        return txt;
    }

    public static String[] fetchDeniedPermissions(Context context) {


        ArrayList<String> permissions = new ArrayList<>();
        permissions.add(ACCESS_COARSE_LOCATION);
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(WRITE_EXTERNAL_STORAGE);
        permissions.add(READ_EXTERNAL_STORAGE);
        permissions.add(BLUETOOTH);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // activity_recognition permission is required only for gingerbread and newer versions
            permissions.add(ACTIVITY_RECOGNITION);
//            permissions.add(ACCESS_BACKGROUND_LOCATION);

        }
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S){
            //Bluetooth_scan and bluetooth_connect permissions required only for android S and newer version

            permissions.add(BLUETOOTH_CONNECT);
            permissions.add(BLUETOOTH_SCAN);
            permissions.remove(BLUETOOTH);
            permissions.remove(BLUETOOTH_ADMIN);

        }
        if(Build.VERSION.SDK_INT >=33)
        {
            permissions.remove(WRITE_EXTERNAL_STORAGE);
            permissions.remove(READ_EXTERNAL_STORAGE);
            permissions.add(POST_NOTIFICATIONS);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            permissions.add(MODIFY_AUDIO_SETTINGS);
        }


        List<String> missingPermissionList = new ArrayList<>();
        for (String permission : permissions) {
            if (context.checkSelfPermission(permission) != PERMISSION_GRANTED) {
                missingPermissionList.add(permission);
            }
        }
        return missingPermissionList.toArray(new String[0]);
    }

    public static void changeBackgrundColor(View textView, int color, String TAG) {
        //Changing the background color
        Drawable background = textView.getBackground();
        if (background instanceof ShapeDrawable) {
            // cast to 'ShapeDrawable'
            Log.e(TAG, "Background ShapeDrawable");
            ShapeDrawable shapeDrawable = (ShapeDrawable) background;
            shapeDrawable.getPaint().setColor(color);
        } else if (background instanceof GradientDrawable) {
            // cast to 'GradientDrawable'
            Log.e(TAG, "Background GradientDrawable");

            GradientDrawable gradientDrawable = (GradientDrawable) background;
            gradientDrawable.setColor(color);
        } else if (background instanceof ColorDrawable) {
            // alpha value may need to be set again after this call
            Log.e(TAG, "Background ColorDrawable");

            ColorDrawable colorDrawable = (ColorDrawable) background;
            colorDrawable.setColor(color);
        }
    }

    public static String getSingleTroubleCode(String cmdResult) {

        cmdResult = cmdResult.trim();
        if (cmdResult.equals("")) {
            return cmdResult;
        }

        String keys = "";
        String[] troubleCode = cmdResult.split("\n");
        for (String key :
                troubleCode) {

            if (key.startsWith("C") || key.startsWith("U") || key.startsWith("B")) {

            } else {
                keys = keys.equals("") ? key : keys + "\n" + key;
            }
        }

        troubleCode = keys.split("\n");

        if (troubleCode.length > 0) {
            return troubleCode[troubleCode.length - 1];
        }
        return keys;
    }

    public static String parseTroubleCodes(String cmdResult) {

        cmdResult = cmdResult.trim();
        if (cmdResult.equals("")) {
            return cmdResult;
        }

        String keys = "";
        String[] troubleCode = cmdResult.split("\n");
        for (String key :
                troubleCode) {

            if (key.startsWith("C") || key.startsWith("U") || key.startsWith("B")) {

            } else {
                keys = keys.equals("") ? key : keys + "\n" + key;
            }
        }
        return keys;
    }

    public static AlertDialog showSinpleWarningDialog(Context context, LayoutInflater layoutInflater, String title, String message) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertAddcarLayoutBinding alertAddcarLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.alert_addcar_layout, null, false);

        alertAddcarLayoutBinding.titleTv.setText(title);
        alertAddcarLayoutBinding.descriptionTV.setText(message);
        alertAddcarLayoutBinding.negativeBtn.setVisibility(View.GONE);
        alertAddcarLayoutBinding.lineView2.setVisibility(View.GONE);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog l``ayout
        builder.setView(alertAddcarLayoutBinding.getRoot());
        final AlertDialog addvehicleAlertDialog = builder.create();
        addvehicleAlertDialog.setCanceledOnTouchOutside(false);
        addvehicleAlertDialog.setCancelable(false);
        addvehicleAlertDialog.show();
        addvehicleAlertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        alertAddcarLayoutBinding.positioveBtn.setText("Okay");
        alertAddcarLayoutBinding.positioveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to AddVehicleFragment.
                addvehicleAlertDialog.dismiss();
            }
        });

       /* alertAddcarLayoutBinding.negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addvehicleAlertDialog.dismiss();
            }
        });*/
        return addvehicleAlertDialog;
    }

    public static void showTriponeKmDialog(Context context, LayoutInflater layoutInflater, String title, String message) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertAddcarLayoutBinding alertAddcarLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.alert_addcar_layout, null, false);

        alertAddcarLayoutBinding.titleTv.setText(title);
        alertAddcarLayoutBinding.titleTv.setVisibility(View.GONE);
        alertAddcarLayoutBinding.descriptionTV.setText(message);
        alertAddcarLayoutBinding.negativeBtn.setVisibility(View.GONE);
        alertAddcarLayoutBinding.lineView2.setVisibility(View.GONE);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog l``ayout
        builder.setView(alertAddcarLayoutBinding.getRoot());
        final AlertDialog addvehicleAlertDialog = builder.create();
        addvehicleAlertDialog.setCanceledOnTouchOutside(false);
        addvehicleAlertDialog.setCancelable(false);
        addvehicleAlertDialog.show();
        addvehicleAlertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        alertAddcarLayoutBinding.positioveBtn.setText("Okay");
        /*new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (addvehicleAlertDialog.isShowing())
                    addvehicleAlertDialog.dismiss();
            }
        }, 5000);*/
        alertAddcarLayoutBinding.positioveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to AddVehicleFragment.
                addvehicleAlertDialog.dismiss();
            }
        });

       /* alertAddcarLayoutBinding.negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addvehicleAlertDialog.dismiss();
            }
        });*/
    }

    public static void showTripEndDialog(Context context, LayoutInflater layoutInflater, String title, String message, final ResponseFromServer responseFromServer, int position) {


        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertAddcarLayoutBinding alertAddcarLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.alert_addcar_layout, null, false);

        alertAddcarLayoutBinding.titleTv.setText(title);
        alertAddcarLayoutBinding.titleTv.setVisibility(View.GONE);
        alertAddcarLayoutBinding.descriptionTV.setText(message);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog l``ayout
        builder.setView(alertAddcarLayoutBinding.getRoot());
        final AlertDialog addvehicleAlertDialog = builder.create();
        addvehicleAlertDialog.setCanceledOnTouchOutside(false);
        addvehicleAlertDialog.setCancelable(false);
        addvehicleAlertDialog.show();
        addvehicleAlertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        alertAddcarLayoutBinding.positioveBtn.setText("Yes");
        alertAddcarLayoutBinding.negativeBtn.setText("No");

        alertAddcarLayoutBinding.positioveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to AddVehicleFragment.
                responseFromServer.responseFromServer(position + "", "", 0);
                addvehicleAlertDialog.dismiss();
            }
        });

        alertAddcarLayoutBinding.negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                responseFromServer.responseFromServer(-1 + "", "", -1);
                addvehicleAlertDialog.dismiss();
            }
        });
    }



    public static void showOkCancelDialog(Context context, LayoutInflater layoutInflater, String title, String message, final ResponseFromServer responseFromServer,int value) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertAddcarLayoutBinding alertAddcarLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.alert_addcar_layout, null, false);

        alertAddcarLayoutBinding.titleTv.setText(title);
        //alertAddcarLayoutBinding.titleTv.setVisibility(View.GONE);
        alertAddcarLayoutBinding.descriptionTV.setText(message);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog l``ayout
        builder.setView(alertAddcarLayoutBinding.getRoot());
        final AlertDialog addvehicleAlertDialog = builder.create();
        addvehicleAlertDialog.setCanceledOnTouchOutside(false);
        addvehicleAlertDialog.setCancelable(false);
        addvehicleAlertDialog.show();
        addvehicleAlertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        alertAddcarLayoutBinding.positioveBtn.setText("Cancel");
        alertAddcarLayoutBinding.negativeBtn.setText("OK");

        alertAddcarLayoutBinding.positioveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to AddVehicleFragment.
                responseFromServer.responseFromServer("", "", -1);
                addvehicleAlertDialog.dismiss();

            }
        });

        alertAddcarLayoutBinding.negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                responseFromServer.responseFromServer("", "", value);
                addvehicleAlertDialog.dismiss();
            }
        });
    }


    public static void showErrorDialog(Context context, LayoutInflater layoutInflater, String title, String message, final ResponseFromServer responseFromServer,int value) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        ErrorDialogBinding errorDialogBinding = DataBindingUtil.inflate(layoutInflater, R.layout.error_dialog, null, false);

        errorDialogBinding.errorText.setText(title);
        errorDialogBinding.errorText.setText(message);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog l``ayout
        builder.setView(errorDialogBinding.getRoot());
        final AlertDialog addvehicleAlertDialog = builder.create();
        addvehicleAlertDialog.setCanceledOnTouchOutside(true);
        addvehicleAlertDialog.setCancelable(true);
        addvehicleAlertDialog.show();
        addvehicleAlertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        errorDialogBinding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseFromServer.responseFromServer("","",1);
                addvehicleAlertDialog.dismiss();
            }
        });
    }


    public static void showSuccessDialog(Context context, LayoutInflater layoutInflater,String okButton, String title, String message, final ResponseFromServer responseFromServer,int value) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        SuccessDialogBinding successDialogBinding = DataBindingUtil.inflate(layoutInflater, R.layout.success_dialog, null, false);

        successDialogBinding.successTitle.setText(title);
        successDialogBinding.successText.setText(message);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog l``ayout
        builder.setView(successDialogBinding.getRoot());
        final AlertDialog addvehicleAlertDialog = builder.create();
        addvehicleAlertDialog.setCanceledOnTouchOutside(false);
        addvehicleAlertDialog.setCancelable(false);
        addvehicleAlertDialog.show();
        addvehicleAlertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        successDialogBinding.okButton.setText(okButton);
        successDialogBinding.okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                responseFromServer.responseFromServer("","",1);
                addvehicleAlertDialog.dismiss();
            }
        });
    }

    public static void showCustomOptionDialog(Context context, LayoutInflater layoutInflater, String leftText, String rightText, String title, String message, final ResponseFromServer responseFromServer) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertAddcarLayoutBinding alertAddcarLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.alert_addcar_layout, null, false);

        alertAddcarLayoutBinding.titleTv.setText(title);
        //alertAddcarLayoutBinding.titleTv.setVisibility(View.GONE);
        alertAddcarLayoutBinding.descriptionTV.setText(message);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(alertAddcarLayoutBinding.getRoot());
        final AlertDialog addvehicleAlertDialog = builder.create();
        addvehicleAlertDialog.setCanceledOnTouchOutside(true);
        addvehicleAlertDialog.setCancelable(true);
        addvehicleAlertDialog.show();
        addvehicleAlertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        alertAddcarLayoutBinding.positioveBtn.setText(leftText);
        alertAddcarLayoutBinding.negativeBtn.setText(rightText);

        alertAddcarLayoutBinding.positioveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to AddVehicleFragment.
                responseFromServer.responseFromServer("", "", 1);
                addvehicleAlertDialog.dismiss();

            }
        });

        alertAddcarLayoutBinding.negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                responseFromServer.responseFromServer("", "", 0);
                addvehicleAlertDialog.dismiss();
            }
        });
    }





    public static void showInputDialog(Context context, LayoutInflater layoutInflater, String title, String message, final ResponseFromServer responseFromServer,int value) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        FragmentZoopAddCarDialogBinding alertAddcarLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.fragment_zoop_add_car_dialog, null, false);

        alertAddcarLayoutBinding.titleTv.setText(title);
        //alertAddcarLayoutBinding.titleTv.setVisibility(View.GONE);
        alertAddcarLayoutBinding.descriptionTV.setText(message);
        alertAddcarLayoutBinding.registrationNoEt.setHint(title);
        alertAddcarLayoutBinding.registrationNoEt.setInputType(InputType.TYPE_CLASS_TEXT);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog l``ayout
        builder.setView(alertAddcarLayoutBinding.getRoot());
        final AlertDialog addvehicleAlertDialog = builder.create();
        addvehicleAlertDialog.setCanceledOnTouchOutside(false);
        addvehicleAlertDialog.setCancelable(false);
        addvehicleAlertDialog.show();
        addvehicleAlertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        alertAddcarLayoutBinding.positioveBtn.setText("OK");
        alertAddcarLayoutBinding.negativeBtn.setText("Cancel");

        alertAddcarLayoutBinding.positioveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (alertAddcarLayoutBinding.registrationNoEt.getText().toString().trim().equals("")){
                    alertAddcarLayoutBinding.errorTxt.setText("Enter valid name");
                    alertAddcarLayoutBinding.errorTxt.setVisibility(View.VISIBLE);
                } else {

                    responseFromServer.responseFromServer(alertAddcarLayoutBinding.registrationNoEt.getText().toString().trim(), "", value);
                    addvehicleAlertDialog.dismiss();

                }

            }
        });

        alertAddcarLayoutBinding.negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                responseFromServer.responseFromServer("", "", -1);
                addvehicleAlertDialog.dismiss();


            }
        });
    }


    public static void notificationDialog(Context context, LayoutInflater inflater, String message, String targetUrl){

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutNotificationDialogBinding layoutNotificationDialogBinding = DataBindingUtil.inflate(inflater, R.layout.layout_notification_dialog, null, false);

        layoutNotificationDialogBinding.description.setText(message);

        builder.setView(layoutNotificationDialogBinding.getRoot());

        AlertDialog notificationAlert = builder.create();
        notificationAlert.setCancelable(true);
        notificationAlert.setCanceledOnTouchOutside(true);
        notificationAlert.show();
        notificationAlert.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        layoutNotificationDialogBinding.positioveBtn.setText("Proceed");
        layoutNotificationDialogBinding.negativeBtn.setText("Cancel");

        layoutNotificationDialogBinding.positioveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (targetUrl.startsWith("http://") || targetUrl.startsWith("https://")){
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(targetUrl));
                    context.startActivity(intent);
                }
                notificationAlert.dismiss();
            }
        });

        layoutNotificationDialogBinding.negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notificationAlert.dismiss();
            }
        });


    }




    public static void showExitTrainingModeDialog(Context context, LayoutInflater layoutInflater, String title, String message, final ResponseFromServer responseFromServer,int value) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        AlertAddcarLayoutBinding alertAddcarLayoutBinding = DataBindingUtil.inflate(layoutInflater, R.layout.alert_addcar_layout, null, false);

        alertAddcarLayoutBinding.titleTv.setText(title);
        //alertAddcarLayoutBinding.titleTv.setVisibility(View.GONE);
        alertAddcarLayoutBinding.descriptionTV.setText(message);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog l``ayout
        builder.setView(alertAddcarLayoutBinding.getRoot());
        final AlertDialog addvehicleAlertDialog = builder.create();
        addvehicleAlertDialog.setCanceledOnTouchOutside(false);
        addvehicleAlertDialog.setCancelable(false);
        addvehicleAlertDialog.show();
        addvehicleAlertDialog.getWindow().getDecorView().setBackgroundResource(android.R.color.transparent);
        alertAddcarLayoutBinding.positioveBtn.setText("No");
        alertAddcarLayoutBinding.negativeBtn.setText("Yes");

        alertAddcarLayoutBinding.positioveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Go to AddVehicleFragment.
                responseFromServer.responseFromServer("Exit Training Mode", "", -1);
                addvehicleAlertDialog.dismiss();

            }
        });

        alertAddcarLayoutBinding.negativeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                responseFromServer.responseFromServer("Exit Training Mode", "", value);
                addvehicleAlertDialog.dismiss();
            }
        });
    }

    public static String getAddressName(double lat, double longi, String TAG, Context context) {

        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
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
            return (addressArray[arraySize - 4] + " , " + addressArray[arraySize - 3]).trim();
        else
            return cityName;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        long factor = (long) Math.pow(10, places);
        value = value * factor;
        long tmp = Math.round(value);
        return (double) tmp / factor;
    }

    public static Bitmap resizeMapIcons(int resource, int width, int height, Context context) {
        Bitmap imageBitmap = BitmapFactory.decodeResource(context.getResources(), resource);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    public static Bitmap convertToBitmap(Drawable drawable, int widthPixels, int heightPixels) {
        Bitmap bitmap = Bitmap.createBitmap(widthPixels, heightPixels, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, widthPixels, heightPixels);
        drawable.draw(canvas);
        return bitmap;
    }

}


