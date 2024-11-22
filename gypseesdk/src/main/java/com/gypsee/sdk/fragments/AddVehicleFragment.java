package com.gypsee.sdk.fragments;

import android.app.DatePickerDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;

import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;

import com.gypsee.sdk.Adapters.VehicleDocrecyclerAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.models.DocumentTypes;
import com.gypsee.sdk.models.User;
import com.gypsee.sdk.models.VehicleDocsModel;
import com.gypsee.sdk.models.Vehiclemodel;
import com.gypsee.sdk.serverclasses.ApiClient;
import com.gypsee.sdk.serverclasses.ApiInterface;
import com.gypsee.sdk.services.TripBackGroundService;
import com.gypsee.sdk.utils.FileCompressor;
import com.gypsee.sdk.utils.RecyclerItemClickListener;
import com.gypsee.sdk.utils.Utils;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import com.amazonaws.mobileconnectors.s3.transferutility.*;

import static android.Manifest.permission.CAMERA;
import static android.app.Activity.RESULT_OK;
import static androidx.core.content.ContextCompat.checkSelfPermission;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddVehicleFragment extends Fragment implements View.OnClickListener, View.OnTouchListener {

    private Vehiclemodel vehiclemodel;
    private VehicleDocrecyclerAdapter vehicleDocrecyclerAdapter;
    private LinearLayout progressLayout;
    private User user;
    private File mPhotoFile;

    boolean isServiceDone;

    public AddVehicleFragment() {
    }

    public static AddVehicleFragment newInstance(Vehiclemodel vehiclemodel) {

        Bundle args = new Bundle();
        args.putParcelable(Vehiclemodel.class.getSimpleName(), vehiclemodel);
        AddVehicleFragment fragment = new AddVehicleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public static AddVehicleFragment newInstance(Vehiclemodel vehiclemodel, boolean isServiceDone) {

        Bundle args = new Bundle();
        args.putParcelable(Vehiclemodel.class.getSimpleName(), vehiclemodel);
        args.putBoolean("isServiceDone", isServiceDone);
        AddVehicleFragment fragment = new AddVehicleFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vehiclemodel = getArguments().getParcelable(Vehiclemodel.class.getSimpleName());
        isServiceDone = getArguments().getBoolean("isServiceDone");
    }

    private View view;

    private Context context;
    private FileCompressor mCompressor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_add_vehicle, container, false);
        context = getContext();
        user = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser();
        mCompressor = new FileCompressor(context);

        initToolbar(view);
        initViews(view);
        setupRecyclerview(view);
        registerBroadcastReceivers();


        if (vehiclemodel == null)
            callServer(getResources().getString(R.string.Document_types), "Get Doc types", 0);
        else {
            Log.e(TAG, "vehicle Model : " + vehiclemodel.toString());
            checkcarDocs();
        }
        return view;

    }


    private ArrayList<VehicleDocsModel> vehicleDocsModelArrayList = new ArrayList<>();

    private void checkcarDocs() {

        try {
            JSONArray jsonArray = new JSONArray(new JsonObject());

            for (int i = 0; i < jsonArray.length(); i++) {

                JSONObject docObj = jsonArray.getJSONObject(i);
                String documentId, documentLink, createdOn, lastUpdatedOn;
                long documentTypeId;
                boolean verified;

                documentId = docObj.getString("documentId");
                documentLink = docObj.getString("documentLink");
                createdOn = docObj.getString("createdOn");
                lastUpdatedOn = docObj.getString("lastUpdatedOn");
                verified = docObj.getBoolean("verified");
                JSONObject documentType = docObj.getJSONObject("documentType");
                documentTypeId = documentType.getLong("id");
                String docName = documentType.getString("description");
                Log.e(TAG, "Documents :" + documentType + "Link - " + documentLink);
                VehicleDocsModel vehicleDocsModel = new VehicleDocsModel(documentId, documentLink, createdOn, lastUpdatedOn, verified, documentTypeId, docName);
                vehicleDocsModelArrayList.add(vehicleDocsModel);
            }

            Comparator<VehicleDocsModel> StuNameComparator = new Comparator<VehicleDocsModel>() {

                public int compare(VehicleDocsModel s1, VehicleDocsModel s2) {
                    long StudentName1 = s1.getDocumentTypeId();
                    long StudentName2 = s2.getDocumentTypeId();

                    return (int) (StudentName1 - StudentName2);


                }
            };

            Collections.sort(vehicleDocsModelArrayList, StuNameComparator);
            // loadDocrecyclerview(false);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 1001;
    private static final int PERMISSION_REQUEST_CODE = 10010;


    private boolean checkCamerapermission() {

        if (checkSelfPermission(context, CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "Permission granteed");
            return true;
        } else {
            Log.e(TAG, "Permission not granteed");

            requestPermissions(new String[]{CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            return false;

        }

    }

    private void setupRecyclerview(View view) {
        vehDocRecyclerview = view.findViewById(R.id.vehDocRecyclerview);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 2);
        vehDocRecyclerview.setLayoutManager(layoutManager);

    }


    private Button save;

    private RecyclerView vehDocRecyclerview;

    private TextInputEditText vinEt, vehicleRegistrationNo, purchaseDateEt, brandName, vehicleModelTv, pollutionRenewDate, insuranceRenewDate, odoMeterReading, servieReminderDueDate, couponCode;
    TextInputLayout vinTIL;

    private TextView smartServiceRemTv;

    private void initViews(View view) {

        vehicleRegistrationNo = view.findViewById(R.id.tv_vehicle_reg);
        vinEt = view.findViewById(R.id.vinNumberTxt);
        vinTIL = view.findViewById(R.id.vinTIL);
        purchaseDateEt = view.findViewById(R.id.tv_purchase_date);
        pollutionRenewDate = view.findViewById(R.id.pollutionRenewDate);
        insuranceRenewDate = view.findViewById(R.id.insuranceRenewDate);
        odoMeterReading = view.findViewById(R.id.odoMeterReading);
        brandName = view.findViewById(R.id.brandName);
        vehicleModelTv = view.findViewById(R.id.tv_vehicle_model);
        save = view.findViewById(R.id.btn_save);

        distancePostCC = view.findViewById(R.id.codeClearedDistance);
        seriveReminderKm = view.findViewById(R.id.serviceReminderkm);
        servieReminderDueDate = view.findViewById(R.id.servieReminderDueDate);
        smartServiceRemTv = view.findViewById(R.id.smartServiceRemTv);
        couponCode = view.findViewById(R.id.couponCode);

        purchaseDateEt.setOnClickListener(this);
        servieReminderDueDate.setOnClickListener(this);
        pollutionRenewDate.setOnClickListener(this);
        insuranceRenewDate.setOnClickListener(this);
        progressLayout = view.findViewById(R.id.ll_progress);

        if (vehiclemodel == null) {
            save.setText("Save");
            toolbarTitle.setText("Add Car");
        } else {
            Log.e(TAG, "Vehicle Model is : " + vehiclemodel.toString());
            if (!vehiclemodel.getVin().equals("")) {
                vinTIL.setVisibility(View.VISIBLE);
                vinEt.setText(vehiclemodel.getVin());
            }
            brandName.setText(vehiclemodel.getVehicleName());
            vehicleModelTv.setText(vehiclemodel.getVehicleModel());
            vehicleRegistrationNo.setText(vehiclemodel.getRegNumber());
            purchaseDateEt.setText(vehiclemodel.getPurchaseDate());
            view.findViewById(R.id.rsaLayout).setVisibility(View.VISIBLE);
            couponCode.setText(vehiclemodel.getRsaCouponCode());
            odoMeterReading.setText(vehiclemodel.getOdoMeterRdg());
            pollutionRenewDate.setText(vehiclemodel.getPollutionReminderDate());
            insuranceRenewDate.setText(vehiclemodel.getInsuranceReminderDate());
            distancePostCC.setText(vehiclemodel.getDistancePostCC());
            seriveReminderKm.setText(vehiclemodel.getServiceReminderkm());
            servieReminderDueDate.setText(vehiclemodel.getServicereminderduedate());
            //couponCode.setText(vehiclemodel.getServicereminderduedate());
            couponCode.setVisibility(View.VISIBLE);

            if (!vehiclemodel.getRsaCouponCode().equals("")) {
                couponCode.setEnabled(false);
            }
            Log.e(TAG, "servieReminderDueDate" + vehiclemodel.getServicereminderduedate());
            if (isServiceDone) {
                Toast.makeText(context, "Please change the Service reminder due date", Toast.LENGTH_LONG).show();
                servieReminderDueDate.setVisibility(View.VISIBLE);
            }
            toolbarTitle.setText("Update vehicle details");
            save.setText("Update");

            if (vehiclemodel.isApproved() || !vehiclemodel.getVin().equals("")) {
                vinEt.setEnabled(false);
                brandName.setEnabled(false);
                vehicleModelTv.setEnabled(false);
                vehicleRegistrationNo.setEnabled(false);
                purchaseDateEt.setEnabled(false);
                odoMeterReading.setEnabled(false);
            } else {
                vinEt.setEnabled(true);
                vinEt.setVisibility(View.VISIBLE);
            }

        }

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAllFieldsFilled();
            }
        });

        vehicleModelTv.setOnTouchListener(this);
        vehicleRegistrationNo.setOnTouchListener(this);
        brandName.setOnTouchListener(this);
        odoMeterReading.setOnTouchListener(this);
        distancePostCC.setOnTouchListener(this);
        distancePostCC.setOnTouchListener(this);
        smartServiceRemTv.setOnTouchListener(this);
    }

    private EditText distancePostCC, seriveReminderKm;

    private String pollutionDateStr, insuranceStr;

    private void checkAllFieldsFilled() {
        boolean error = false;

        pollutionDateStr = pollutionRenewDate.getText().toString();
        insuranceStr = insuranceRenewDate.getText().toString();
        if (brandName.getText().toString().isEmpty()) {
            brandName.setError("Vehicle make cannot be empty");
            error = true;
        } else if (vehicleModelTv.getText().toString().isEmpty()) {
            vehicleModelTv.setError("Vehicle Model cannot be empty");
            error = true;
        } else if (vehicleRegistrationNo.getText().toString().isEmpty()) {
            vehicleRegistrationNo.setError("Registration number cannot be empty");
            error = true;
        } else if (purchaseDateEt.getText().toString().isEmpty()) {
            purchaseDateEt.setError("Purchase date cannot be empty");
            error = true;
        } else if (odoMeterReading.getText().toString().isEmpty()) {
            odoMeterReading.setError("field cannot be empty");
            error = true;
        } else if (servieReminderDueDate.getText().toString().isEmpty() && isServiceDone) {
            servieReminderDueDate.setError("field cannot be empty");
            error = true;
        }


        boolean callServer = true;
        if (!error) {
           /* //call Server to store the vehicle details in the server.
            for (int i = 0; i < appversionModelArrayList.size(); i++) {

                DocumentTypes documentTypes = appversionModelArrayList.get(i);
                if (documentTypes.getFile() == null) {
                    Toast.makeText(context, "Please upload " + documentTypes.getName() + " Image", Toast.LENGTH_LONG).show();
                    progressLayout.setVisibility(View.GONE);
                    save.setEnabled(true);
                    return;
                } else {
                    if (documentTypes.getImagelink() == null) {
                        callServer = false;
                        progressLayout.setVisibility(View.VISIBLE);
                        save.setEnabled(false);
                        uploadWithTransferUtility(documentTypes.getFile(), i);
                    }
                }
            }

            for (int j = 0; j < vehicleDocsModelArrayList.size(); j++) {

                VehicleDocsModel vehicleDocsModel = vehicleDocsModelArrayList.get(j);
                if (vehicleDocsModel.getDocumentLink().equals("")) {
                    callServer = false;
                    progressLayout.setVisibility(View.VISIBLE);
                    save.setEnabled(false);
                    uploadWithTransferUtility(vehicleDocsModel.getFile(), j);
                }
            }*/

            if (callServer) {
                if (save.getText().toString().equals("Save")) {
                    callServer(getResources().getString(R.string.AddVehDetails_Url).replace("userid", user.getUserId()), "Add vehicle ", 1);

                } else {
                    callServer(getResources().getString(R.string.UpdateBasicVehDetails_Url).replace("vehicleId", vehiclemodel.getUserVehicleId()), "Update vehicle ", 2);
                }

            }

        }

    }


    private TextView toolbarTitle;

    private void initToolbar(View view) {

        Toolbar toolbar = view.findViewById(R.id.toolBarLayout);

        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);
        toolbarTitle = (TextView) view.findViewById(R.id.toolBarTitle);
        ImageView addIcon = view.findViewById(R.id.rightSideIcon);
        addIcon.setVisibility(View.GONE);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }

    private void goBack() {
        if (((AppCompatActivity) context).getSupportFragmentManager().getBackStackEntryCount() == 0) {
            ((AppCompatActivity) context).finish();
        } else {
            ((AppCompatActivity) context).getSupportFragmentManager().popBackStackImmediate();
        }


    }


    private int clickedDatePos = 0;

    @Override
    public void onClick(View v) {


        Utils.hideKeyboard((AppCompatActivity) context);

        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        monthOfYear = (monthOfYear + 1);
                        String day = dayOfMonth < 10 ? "0" + dayOfMonth : "" + dayOfMonth;
                        String month = monthOfYear < 10 ? "0" + monthOfYear : "" + monthOfYear;
                        String date = year + "-" + month + "-" + day;
                        switch (clickedDatePos) {
                            case 0:
                                purchaseDateEt.setText(date);
                                break;
                            case 1:
                                pollutionRenewDate.setText(date);

                                break;
                            case 2:
                                insuranceRenewDate.setText(date);

                                break;
                            case 3:
                                servieReminderDueDate.setText(date);

                                break;


                        }


                    }
                }, mYear, mMonth, mDay);


        int id = v.getId();
        if (id == R.id.tv_purchase_date) {
            datePickerDialog.getDatePicker().setMaxDate(c.getTimeInMillis());
            clickedDatePos = 0;
        } else if (id == R.id.pollutionRenewDate) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);
            clickedDatePos = 1;
        } else if (id == R.id.insuranceRenewDate) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            clickedDatePos = 2;
        } else if (id == R.id.servieReminderDueDate) {
            datePickerDialog.getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            clickedDatePos = 3;
        }


        datePickerDialog.show();

    }

    private static final int CAPTURE_IMAGE_REQUEST = 1001;
    private static final int GALLERY_IMAGE_REQUEST = 1002;


    Uri fileUri;

    private void selectImage() {

        if (checkCamerapermission()) {
            final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            builder.setTitle("Add Photo!");
            builder.setItems(options, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int item) {
                    if (options[item].equals("Take Photo")) {
                        dispatchTakePictureIntent();

                    } else if (options[item].equals("Choose from Gallery")) {
                        dispatchGalleryIntent();
                    } else if (options[item].equals("Cancel")) {
                        dialog.dismiss();
                    }
                }
            });
            builder.show();
        } else {
            isCompulsory = true;
        }
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(context.getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = createImageFile();

            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(context,
                        getString(R.string.applicationid) + ".provider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, CAPTURE_IMAGE_REQUEST);
            }
        }
    }

    /**
     * Select image fro gallery
     */
    private void dispatchGalleryIntent() {
        Intent pickPhoto = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickPhoto.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivityForResult(pickPhoto, GALLERY_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            try {
                if (requestCode == CAPTURE_IMAGE_REQUEST) {

                    mPhotoFile = mCompressor.compressToFile(mPhotoFile);
                    setImagetoRc(mPhotoFile);


                } else if (requestCode == GALLERY_IMAGE_REQUEST) {
                    Uri selectedImage = data.getData();

                    mPhotoFile = mCompressor.compressToFile(new File(getRealPathFromUri(selectedImage)));
                    setImagetoRc(mPhotoFile);

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Get real file path from URI
     */
    private String getRealPathFromUri(Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null, null, null);
            assert cursor != null;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }


    private void setImagetoRc(File file) {

        if (appversionModelArrayList.size() > 0) {
            appversionModelArrayList.get(RecyclerViewClickedPostion).setFile(file);
        } else {
            vehicleDocsModelArrayList.get(RecyclerViewClickedPostion).setFile(file);
            vehicleDocsModelArrayList.get(RecyclerViewClickedPostion).setDocumentLink("");
        }
        vehicleDocrecyclerAdapter.notifyItemChanged(RecyclerViewClickedPostion);

    }

    private String TAG = AddVehicleFragment.class.getSimpleName();


    private File createImageFile() {
        try {
            // Create an image file name
            String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss", Locale.ENGLISH).format(new Date());
            String imageFileName = "JPEG_" + timeStamp + "_";

            File storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
            File image = File.createTempFile(imageFileName, ".jpg", storageDir);
            mPhotoFile = image;
            // Save a file: path for use with ACTION_VIEW intents
            String mCurrentPhotoPath = image.getAbsolutePath();
            Log.e(TAG, "Image file name " + mCurrentPhotoPath);

            return image;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private void callServer(String url, String purpose, final int value) {

        progressLayout.setVisibility(View.VISIBLE);
        ApiInterface apiService = ApiClient.getClient(TAG, purpose, url).create(ApiInterface.class);

        Call<ResponseBody> call;
        JsonObject jsonInput = new JsonObject();
        String distancePostCCStr = distancePostCC.getText().toString();
        String serviceReminderkmStr = seriveReminderKm.getText().toString();
        JsonArray jsonArray = new JsonArray();

        String serviceReminderDateStr = servieReminderDueDate.getText().toString().equals("") ? "" : servieReminderDueDate.getText().toString();


        switch (value) {
            case 1:
                Log.e(TAG, "Documents size : " + appversionModelArrayList.size());
                for (DocumentTypes documentTypes : appversionModelArrayList) {
                    JsonObject docJson = new JsonObject();

                    Log.e(TAG, "Documents link : " + documentTypes.getImagelink());

                    //docJson.addProperty("documentLink", documentTypes.getImagelink());
                    docJson.addProperty("documentLink", "https://gypsee.ai/wp-content/uploads/2020/07/logo-e1593320976286_9005c63b90548db23bda935126cde576.png");
                    docJson.addProperty("documentTypeId", documentTypes.getId());
                    jsonArray.add(docJson);
                }

                jsonInput.add("vehicleDocuments", jsonArray);
                jsonInput.addProperty("fuelPercent", "10%");
               /* jsonInput.addProperty("fuelPercent", "");
                jsonInput.addProperty("latitude", "");
                jsonInput.addProperty("longitude", "");*/
                jsonInput.addProperty("purchaseDate", purchaseDateEt.getText().toString());
                jsonInput.addProperty("regNumber", vehicleRegistrationNo.getText().toString());
                jsonInput.addProperty("vehicleBrand", brandName.getText().toString());
                jsonInput.addProperty("vehicleModel", vehicleModelTv.getText().toString());
                jsonInput.addProperty("vehicleName", brandName.getText().toString());
                jsonInput.addProperty("serviceReminderDate", serviceReminderDateStr);

                if (!pollutionDateStr.equals(""))
                    jsonInput.addProperty("pollutionReminderDate", pollutionDateStr);
                if (!insuranceStr.equals(""))
                    jsonInput.addProperty("insuranceReminderDate", insuranceStr);

                serviceReminderkmStr = serviceReminderkmStr.equals("") ? "0" : serviceReminderkmStr;

                //jsonInput.addProperty("distancePostCC", Double.parseDouble(distancePostCCStr));
                jsonInput.addProperty("serviceReminderkm", Integer.parseInt(serviceReminderkmStr));

                jsonInput.addProperty("odoMeterRdg", Integer.parseInt(odoMeterReading.getText().toString()));
                Log.e(TAG, purpose + " Input is : " + jsonInput.toString());
                call = apiService.uploadVehDetails(user.getUserAccessToken(), jsonInput);
                break;

            case 2:

                pollutionDateStr = pollutionDateStr.equals("") ? vehiclemodel.getPollutionReminderDate() : pollutionDateStr;
                insuranceStr = insuranceStr.equals("") ? vehiclemodel.getInsuranceReminderDate() : insuranceStr;
                serviceReminderkmStr = serviceReminderkmStr.equals("") ? vehiclemodel.getServiceReminderkm() : serviceReminderkmStr;

                // jsonInput.addProperty("distancePostCC", distancePostCCStr);
                /*jsonInput.addProperty("engineRunTime", "");
                jsonInput.addProperty("fuelLevel", "");*/
                jsonInput.addProperty("insuranceReminderDate", insuranceStr);

                /*jsonInput.addProperty("maf", "");*/
                jsonInput.addProperty("odoMeterRdg", Integer.parseInt(odoMeterReading.getText().toString()));
                jsonInput.addProperty("pollutionReminderDate", pollutionDateStr);
                if (serviceReminderkmStr.equalsIgnoreCase("")) {
                    jsonInput.addProperty("serviceReminderkm", "");

                } else {
                    jsonInput.addProperty("serviceReminderkm", Integer.parseInt(serviceReminderkmStr));

                }

                jsonInput.addProperty("purchaseDate", purchaseDateEt.getText().toString());
                jsonInput.addProperty("regNumber", vehicleRegistrationNo.getText().toString());
                jsonInput.addProperty("vehicleBrand", brandName.getText().toString());
                jsonInput.addProperty("vehicleModel", vehicleModelTv.getText().toString());
                jsonInput.addProperty("vehicleName", brandName.getText().toString());
                jsonInput.addProperty("serviceReminderDate", serviceReminderDateStr);
                jsonInput.addProperty("vin", vinEt.getText().toString());
                if (vehiclemodel.getRsaCouponCode().equals(""))
                    jsonInput.addProperty("rsaCouponCode", couponCode.getText().toString());

                if (!serviceReminderDateStr.equals("") || isServiceDone)
                    jsonInput.addProperty("isServiceDone", true);
                else {
                    jsonInput.addProperty("isServiceDone", false);
                }

                //Need to write the condition for documents also.
                jsonArray = new JsonArray();
                Log.e(TAG, "Documents size : " + vehicleDocsModelArrayList.size());
                for (VehicleDocsModel vehicleDocsModel : vehicleDocsModelArrayList) {
                    JsonObject docJson = new JsonObject();

                    Log.e(TAG, "Documents link : " + vehicleDocsModel.getDocumentLink());

                    docJson.addProperty("documentLink", vehicleDocsModel.getDocumentLink());

                    //String getDocType= vehicleDocsModel.
                    docJson.addProperty("documentTypeId", vehicleDocsModel.getDocumentTypeId());
                    jsonArray.add(docJson);
                }
                jsonInput.add("vehicleDocuments", jsonArray);
                Log.e(TAG, purpose + " Input is : " + jsonInput.toString());
                call = apiService.uploadFCMToken(user.getUserAccessToken(), jsonInput);

                break;
            default:

                call = apiService.getDocTypes(user.getUserAccessToken(), true);
                break;
        }

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressLayout.setVisibility(View.GONE);
                try {
                    if (response.isSuccessful()) {
                        Log.e(TAG, "Response is success");
                        String responseStr = response.body().string();
                        Log.e(TAG, "Response :" + responseStr);

                        switch (value) {
                            case 0:
                                parseFetchDocTypes(responseStr);
                                break;
                            case 1:
                            case 2:
                                Intent intent = new Intent("ShowProgress");
                                LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                                Toast.makeText(context, "Added/Updated car succesfully", Toast.LENGTH_LONG).show();
                                save.setVisibility(View.GONE);
                                callBackgroundService(0);
                                goBack();
                                break;
                        }

                    } else {
                        Log.e(TAG, "Response is not succesfull");
                        String errorBody = response.errorBody().string();
                        Log.e(TAG, " Response :" + errorBody);

                        save.setEnabled(true);
                        int responseCode = response.code();
                        if (responseCode == 401 || responseCode == 403) {
                            //include logout functionality
                            Utils.clearAllData(context);
                            return;
                        }
                        if (response.errorBody() == null) {
                            Log.e("Error code 400", "" + response.errorBody().string());
                        } else {
                            JSONObject jsonObject = new JSONObject(errorBody);
                            Toast.makeText(context, jsonObject.getString("message"), Toast.LENGTH_LONG).show();
                            Log.e("Error code 400", "" + "Response is empty");
                        }

                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

               /* if (response.body() != null) {
                    try {
                       // String responseString = response.body().string();
                     Log.e(TAG,"Response : "+ responseString);

                        progressBar.setVisibility(View.GONE);
                        loginBtn.setVisibility(View.VISIBLE);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                } else {

                    Toast.makeText(LoginActivity.this,"Please try again",Toast.LENGTH_SHORT).show();
                }*/

            }


            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Logger.ErrorLog(TAG, "error here since request failed");

                progressLayout.setVisibility(View.GONE);
                save.setEnabled(true);
                save.setVisibility(View.VISIBLE);

                if (t.getMessage().contains("Unable to resolve host")) {
                    Toast.makeText(context, "Please Check your internet connection", Toast.LENGTH_LONG).show();
                }

                if (call.isCanceled()) {

                } else {

                }
            }
        });
    }

    private ArrayList<DocumentTypes> appversionModelArrayList = new ArrayList<>();

    private void parseFetchDocTypes(String responseStr) throws JSONException {

        appversionModelArrayList.clear();
        JSONObject mainJson = new JSONObject(responseStr);

        JSONArray jsonArray = mainJson.getJSONArray("documentTypes");

        for (int i = 0; i < jsonArray.length(); i++) {

            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String[] keys = {"id", "name", "description"};

            ArrayList<String> values = new ArrayList<>();
            for (String key : keys) {
                String value = jsonObject.has(key) ? jsonObject.getString(key) : "";
                values.add(value);
            }
            DocumentTypes appversionModel = new DocumentTypes(values.get(0), values.get(1), values.get(2), null, null, null);

            appversionModelArrayList.add(appversionModel);
        }
        //loadDocrecyclerview(true);

    }

    private boolean isApproved = false;

    private void loadDocrecyclerview(final boolean isAddVehicle) {
        if (isAddVehicle) {
            vehicleDocrecyclerAdapter = new VehicleDocrecyclerAdapter(context, appversionModelArrayList, isAddVehicle);
        } else {
            isApproved = vehiclemodel.isApproved() || !vehiclemodel.getVin().equals("");
            vehicleDocrecyclerAdapter = new VehicleDocrecyclerAdapter(context, vehicleDocsModelArrayList, isAddVehicle, 0);

        }
        vehDocRecyclerview.setAdapter(vehicleDocrecyclerAdapter);
        vehDocRecyclerview.setNestedScrollingEnabled(false);

        //Implement Rc image click listener

        vehDocRecyclerview.addOnItemTouchListener(
                new RecyclerItemClickListener(context, vehDocRecyclerview, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position) {
                        // do whatever
                        //Send brand name back
                        RecyclerViewClickedPostion = position;
                        if (!isApproved)
                            selectImage();
                    }

                    @Override
                    public void onLongItemClick(View view, int position) {
                        // do whatever
                    }
                })
        );
    }


    private int RecyclerViewClickedPostion;


    private void uploadWithTransferUtility(final File file, final int index) {

        final String ACCESS_KEY = getString(R.string.ACCESS_KEY),
                SECRET_KEY = getString(R.string.SECRET_KEY),
                MY_BUCKET = getString(R.string.MY_BUCKET);


        Log.e(TAG, "UUID is : " + file.getName());

        AWSCredentials credentials = new BasicAWSCredentials(ACCESS_KEY, SECRET_KEY);
        AmazonS3 s3Client = new AmazonS3Client(credentials);
        s3Client.setEndpoint("https://s3.ap-south-1.amazonaws.com/");


        TransferUtility transferUtility = new TransferUtility(s3Client, context);
        TransferObserver transferObserver = transferUtility.upload(MY_BUCKET, file.getName(), file, CannedAccessControlList.PublicRead);

        transferObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {

                if (state.equals(TransferState.COMPLETED)) {

                    switch (appversionModelArrayList.size()) {

                        case 0:
                            vehicleDocsModelArrayList.get(index).setDocumentLink("https://gypsee-android.s3.ap-south-1.amazonaws.com/" + file.getName());
                            Log.e(TAG, "Index : " + index + " Image link is : " + vehicleDocsModelArrayList.get(index).getDocumentLink());
                            //Check All the urls are made
                            for (VehicleDocsModel vehicleDocsModel : vehicleDocsModelArrayList) {
                                if (vehicleDocsModel.getDocumentLink().equals("")) {
                                    return;
                                }
                            }
                            callServer(getString(R.string.UpdateBasicVehDetails_Url).replace("vehicleId", vehiclemodel.getUserVehicleId()), "Update vehicle ", 2);

                            break;
                        default:
                            appversionModelArrayList.get(index).setImagelink("https://gypsee-android.s3.ap-south-1.amazonaws.com/" + file.getName());
                            Log.e(TAG, "Index : " + index + " Image link is : " + appversionModelArrayList.get(index).getImagelink());
                            //Check All the urls are made
                            for (DocumentTypes documentTypes : appversionModelArrayList) {
                                if (documentTypes.getImagelink() == null) {
                                    return;
                                }
                            }

                            callServer(getResources().getString(R.string.AddVehDetails_Url).replace("userid", new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser().getUserId()), "Add vehicle ", 1);
                            break;
                    }

                } else if (state.equals(TransferState.FAILED)) {
                    // If uploading the image got failed, we will retry until it uploads.
                    uploadWithTransferUtility(file, index);

                }

                Log.e(TAG, "ID " + id + "\nState " + state.name() + "\nImage ID " + file.getName());

//Implement the code for handle the file status changed.

            }

            @Override

            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {

                //Implement the code to handle the file uploaded progress.
            }

            @Override

            public void onError(int id, Exception exception) {
                Toast.makeText(context, "There was an error while uploading RC", Toast.LENGTH_LONG).show();
//Implement the code to handle the file upload error.

            }

        });


    }


    public File saveBitmapToFile(File file) {
        try {

            // BitmapFactory options to downsize the image
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            o.inSampleSize = 6;
            // factor of downsizing the image

            FileInputStream inputStream = new FileInputStream(file);
            //Bitmap selectedBitmap = null;
            BitmapFactory.decodeStream(inputStream, null, o);
            inputStream.close();

            // The new size we want to scale to
            final int REQUIRED_SIZE = 75;

            // Find the correct scale value. It should be the power of 2.
            int scale = 1;
            while (o.outWidth / scale / 2 >= REQUIRED_SIZE &&
                    o.outHeight / scale / 2 >= REQUIRED_SIZE) {
                scale *= 2;
            }

            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize = scale;
            inputStream = new FileInputStream(file);

            Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, null, o2);
            inputStream.close();

            // here i override the original image file
            file.createNewFile();
            FileOutputStream outputStream = new FileOutputStream(file);

            selectedBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);

            return file;
        } catch (Exception e) {
            return null;
        }
    }


    private boolean isCompulsory = false;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // checkCamerapermission();

        //If permission is not granted, we need to give popup  and navigate him to permissions page.
        if (isCompulsory) {

            if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
                for (String permission : permissions) {
                    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        //Show Dialog to Accept the permission
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        startActivityForResult(intent, PERMISSION_REQUEST_CODE);
                        Toast.makeText(context, "Please enable Camera permission", Toast.LENGTH_LONG).show();

                        return;
                    }
                    break;
                }
            }


        }

    }

    private void callBackgroundService(int value) {

        Intent intent = new Intent(context, TripBackGroundService.class);
        switch (value) {
            // Trip history update. Sending to service.
            case 0:
                intent.putExtra(TripBackGroundService.PURPOSE, "Get UserVehicles");
                break;
        }

        context.startService(intent);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {

        final int DRAWABLE_LEFT = 0;
        final int DRAWABLE_TOP = 1;
        final int DRAWABLE_RIGHT = 2;
        final int DRAWABLE_BOTTOM = 3;



        EditText touchedView = vehicleRegistrationNo;
        String text = "Your registration number looks like - TN01CA280";
        View anchorview = view.findViewById(R.id.placeholder1);
        int id = v.getId();
        if (id == R.id.tv_vehicle_reg) {
        } else if (id == R.id.brandName) {
            brandName.setInputType(InputType.TYPE_NULL);
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                Log.e(TAG, "Calling CarsBrandListFrgment");
                ((AppCompatActivity) context).getSupportFragmentManager()
                        .beginTransaction()
                        .add(R.id.mainFrameLayout, CarsBrandListFrgment.newInstance(), CarsBrandListFrgment.class.getSimpleName())
                        .addToBackStack(CarsBrandListFrgment.class.getSimpleName())
                        .commit();
            }

            return false;
        } else if (id == R.id.tv_vehicle_model) {
            touchedView = vehicleModelTv;
            text = "Tell us whether its a Tata Bolt or Tata Nexon";
            anchorview = view.findViewById(R.id.placeholder3);
        } else if (id == R.id.odoMeterReading) {
            touchedView = odoMeterReading;
            text = "Check out your car Odometer to know the KMs";
            anchorview = view.findViewById(R.id.placeholder4);
        } else if (id == R.id.smartServiceRemTv) {
            Log.e(TAG, "Ontouch listener");
            text = "Tell us when your car needs servicing in KMs";
            anchorview = view.findViewById(R.id.placeholder5);
            // your action here
          /*  tooltip = new Tooltip.Builder(anchorview)
                    .setText(text)
                    .setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                    .setTextColor(ContextCompat.getColor(context, R.color.white))
                    .setCornerRadius(5f)
                    .setCancelable(true)
                    .show();*/
            return false;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (event.getRawX() >= (touchedView.getRight() - touchedView.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {
                // your action here
            /*    tooltip = new Tooltip.Builder(anchorview)
                        .setText(text)
                        .setBackgroundColor(ContextCompat.getColor(context, R.color.colorPrimary))
                        .setTextColor(ContextCompat.getColor(context, R.color.white))
                        .setCornerRadius(5f)
                        .setCancelable(true)
                        .show();*/
                return false;
            }
        }
        return false;
    }


    private void registerBroadcastReceivers() {

        //This is to receive the obd commands from the device . Regarding the vehcile

        IntentFilter filter = new IntentFilter();
        filter.addAction("VehicleName");


        BroadcastReceiver vehicleBrandbroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (intent.getStringExtra("brandName") != null) {
                    brandName.setText(intent.getStringExtra("brandName"));
                }
            }
        };
        //this is for receiving the notification count etc
        LocalBroadcastManager.getInstance(context).registerReceiver(vehicleBrandbroadcastReceiver, filter);
    }
}
