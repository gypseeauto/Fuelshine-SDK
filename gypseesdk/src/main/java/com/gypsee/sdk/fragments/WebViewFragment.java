package com.gypsee.sdk.fragments;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Locale;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.LayoutWebViewBinding;
import com.gypsee.sdk.firebase.FirebaseLogEvents;

public class WebViewFragment extends Fragment {

    private LayoutWebViewBinding layoutWebViewBinding;
    private Context context;

    private String title = "";

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public WebViewFragment(String title){
        this.title = title;
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        this.context = context;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        layoutWebViewBinding = DataBindingUtil.inflate(inflater, R.layout.layout_web_view, container, false);

        initToolbar();
        initViews();
        //setupWebView();


        return layoutWebViewBinding.getRoot();
    }



    private void initToolbar(){

        Toolbar toolbar = layoutWebViewBinding.toolBarLayout.toolbar;
        toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);

        layoutWebViewBinding.toolBarLayout.setTitle(title);
        toolbar.setTitleTextColor(getResources().getColor(R.color.white));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });
    }


    private void goBack() {
        ((AppCompatActivity) context).finish();

    }


    private void initViews(){

        layoutWebViewBinding.startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStartDate();
            }
        });
        layoutWebViewBinding.endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getEndDate(startDate);
            }
        });
        layoutWebViewBinding.generateReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseLogEvents.firebaseLogEvent("generated_driving_report",context);
                if (startDate == null || endDate == null){
                    Toast.makeText(context, "Please select valid dates", Toast.LENGTH_SHORT).show();
                    return;
                }
                String periodFrom = startDate.getYear() + "-" + String.format(Locale.getDefault(), "%02d", startDate.getMonthValue()) + "-"
                        + String.format(Locale.getDefault(),"%02d", startDate.getDayOfMonth());

                String periodTo = endDate.getYear() + "-" + String.format(Locale.getDefault(),"%02d", endDate.getMonthValue()) + "-"
                        + String.format(Locale.getDefault(),"%02d", endDate.getDayOfMonth());

                setupWebView(getString(R.string.drivingReport).replace("userId", new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context).getUser().getUserId())
                        + "?periodFrom=" + periodFrom + "&periodTo=" + periodTo);

            }
        });

        layoutWebViewBinding.exportPdf.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseLogEvents.firebaseLogEvent("exported_driving_report_pdf",context);
                createWebPrintJob(layoutWebViewBinding.webView);
                //createWebImage(layoutWebViewBinding.webView);
            }
        });



    }






    private String TAG = WebViewFragment.class.getSimpleName();

    private void setupWebView(String url){

        layoutWebViewBinding.webView.setScrollbarFadingEnabled(true);
        layoutWebViewBinding.webView.getSettings().setSupportZoom(true);
        layoutWebViewBinding.webView.getSettings().setBuiltInZoomControls(true);
        layoutWebViewBinding.webView.getSettings().setDisplayZoomControls(false);
        layoutWebViewBinding.webView.setInitialScale(1);
        layoutWebViewBinding.webView.getSettings().setLoadWithOverviewMode(true);
        layoutWebViewBinding.webView.getSettings().setUseWideViewPort(true);
        layoutWebViewBinding.webView.setWebChromeClient(new WebChromeClient()
        {
            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);
                if (newProgress<90){
                    layoutWebViewBinding.progressBar.setVisibility(View.VISIBLE);
                    layoutWebViewBinding.webView.setVisibility(View.GONE);
                }
                else{
                    layoutWebViewBinding.progressBar.setVisibility(View.GONE);
                    layoutWebViewBinding.webView.setVisibility(View.VISIBLE);
                }
            }
        });
        layoutWebViewBinding.webView.getSettings().setJavaScriptEnabled(true);

        layoutWebViewBinding.webView.loadUrl(url);


    }




    private void getStartDate(){
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
               String date = year + "-" +
                        String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);

                startDate = LocalDateTime.of(year, month+1, dayOfMonth, 0, 1);
                layoutWebViewBinding.startDate.setText(date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();

    }



    private void getEndDate(LocalDateTime minDate){
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(context, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
               String date = year + "-" +
                        String.format("%02d", month + 1) + "-" + String.format("%02d", dayOfMonth);

                endDate = LocalDateTime.of(year, month+1, dayOfMonth, 0, 1);
                layoutWebViewBinding.endDate.setText(date);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        if (minDate != null){
            datePickerDialog.getDatePicker().setMinDate(minDate.toInstant(ZoneOffset.UTC).toEpochMilli() - 1000);
        }
        datePickerDialog.show();

    }

    public void createWebPrintJob(WebView webView) {
        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) context
                .getSystemService(Context.PRINT_SERVICE);

        PrintAttributes.Builder attr = new PrintAttributes.Builder();

        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter();



        attr.setMediaSize(PrintAttributes.MediaSize.NA_TABLOID);
        attr.setMinMargins(PrintAttributes.Margins.NO_MARGINS);
        attr.setResolution(new PrintAttributes.Resolution("1", "tabloid", 1056, 1632));

        // Create a print job with name and adapter instance
        String jobName = context.getString(R.string.app_name) + " Document";
        printManager.print(jobName, printAdapter,
                attr.build());

    }


/*    public void createWebImage(WebView webView){
        // measure the webview
        webView.measure(
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0,View.MeasureSpec.UNSPECIFIED)
        );

        //layout of webview
        webView.layout(0, 0, webView.getMeasuredWidth(), webView.getMeasuredHeight());

        webView.setDrawingCacheEnabled(true);
        webView.buildDrawingCache();

        //create Bitmap if measured height and width >0
        Bitmap b;
        if (webView.getMeasuredWidth()> 0 && webView.getMeasuredHeight()> 0) {
            b = Bitmap.createBitmap(
                    webView.getMeasuredWidth(),
                    webView.getMeasuredHeight(), Bitmap.Config.ARGB_8888
            );

            Canvas c = new Canvas(b);
            c.drawBitmap(b, 0f, (float) b.getHeight(), new Paint());
            webView.draw(c);

            //saveBitmapToFile(Environment.getExternalStorageDirectory(), "mypage2.jpeg", b);
            MediaStore.Images.Media.insertImage(context.getContentResolver(), b, "My Page" , "driving report");

        }
        else {
            b = null;
            Log.e(TAG, "bitmap null");
        }



    }*/







}
