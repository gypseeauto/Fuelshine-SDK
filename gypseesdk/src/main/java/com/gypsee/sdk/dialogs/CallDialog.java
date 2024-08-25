package com.gypsee.sdk.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.gypsee.sdk.R;


public class CallDialog extends Dialog implements View.OnClickListener {
    private static final String TAG = CallDialog.class.getSimpleName();
    private Context context;


    public CallDialog(Context context) {
        super(context);
        this.context = context;
    }

    TextView okay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.onCreate(savedInstanceState);
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_SECURE, WindowManager.LayoutParams.FLAG_SECURE);
        setContentView(R.layout.dialog_layout_call);
        okay = findViewById(R.id.okay);
        okay.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.okay) {
            Intent intent = new Intent(Intent.ACTION_DIAL);
            intent.setData(Uri.parse("tel:+916362948112"));
            context.startActivity(intent);
            dismiss();
        }
    }
}
