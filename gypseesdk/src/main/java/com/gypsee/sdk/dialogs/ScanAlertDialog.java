package com.gypsee.sdk.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;

import com.gypsee.sdk.R;
import com.gypsee.sdk.fragments.ScanningFragment;
import com.gypsee.sdk.jsonParser.ResponseFromServer;


public class ScanAlertDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = ScanAlertDialog.class.getSimpleName();
    private Context context;
    ResponseFromServer responseFromServer;


    public ScanAlertDialog(Context context, ResponseFromServer responseFromServer) {
        super(context);
        this.context = context;
        this.responseFromServer = responseFromServer;

    }

    private TextView cancel, scan, okay;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.dialog_layout_scan);
        cancel = findViewById(R.id.cancel);
        //okay = findViewById(R.id.okay);
        scan = findViewById(R.id.scan);
        cancel.setOnClickListener(this);
//      okay.setOnClickListener(this);
        scan.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.scan) {//                context.stopBluetooth();
            responseFromServer.responseFromServer(TAG, TAG, 2);

            FragmentManager fragmentManager = ((AppCompatActivity) context).getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .add(R.id.mainFrameLayout, ScanningFragment.newInstance(), ScanningFragment.class.getSimpleName())
                    .addToBackStack(ScanningFragment.class.getSimpleName())
                    .commit();

//                context.startActivity(new Intent(context, ScanningActivity.class));
            dismiss();
        } else if (id == R.id.cancel) {
            dismiss();
        } else if (id == R.id.okay) {
            dismiss();
        }
    }
}
