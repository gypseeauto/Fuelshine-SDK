package com.gypsee.sdk.fragments;


import android.content.ActivityNotFoundException;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.gypsee.sdk.R;
import com.gypsee.sdk.config.MyPreferenece;
import com.gypsee.sdk.databinding.FragmentReferAndEarnBinding;
import com.gypsee.sdk.firebase.FirebaseLogEvents;
import com.gypsee.sdk.models.User;

/**
 * A simple {@link Fragment} subclass.
 */
public class ReferAndEarnFragment extends Fragment implements View.OnClickListener {


    private User user;

    public ReferAndEarnFragment() {
        // Required empty public constructor
    }

    Context context;

    private FragmentReferAndEarnBinding fragmentReferAndEarnBinding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        fragmentReferAndEarnBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_refer_and_earn, container, false);
        context = getContext();
        MyPreferenece myPreferenece = new MyPreferenece(MyPreferenece.GYPSEE_PREFERENCES, context);
        user = myPreferenece.getUser();
        fragmentReferAndEarnBinding.setRefrralCode(user.getReferCode());

        String sourceString = "<b>" + "Know Safety, No Pain. No Safety, Know Pain." + "</b> " + "<br></br>Share Fuelshine app with your loved ones & help them drive safe on the roads. " +
                "<br></br><br></br>After all, life is precious, hold it with both hands.";

        fragmentReferAndEarnBinding.nacre.setText(Html.fromHtml(sourceString));
        initToolBar();

        fragmentReferAndEarnBinding.shareTxtv.setOnClickListener(this);
        fragmentReferAndEarnBinding.copyImage.setOnClickListener(this);

        return fragmentReferAndEarnBinding.getRoot();
    }

    private void initToolBar() {
        fragmentReferAndEarnBinding.toolbarlayout.toolbar.setNavigationIcon(R.drawable.ic_keyboard_arrow_left_white_24dp);
        fragmentReferAndEarnBinding.toolbarlayout.toolBarTitle.setText("Share");
        fragmentReferAndEarnBinding.toolbarlayout.toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.back_button));

        fragmentReferAndEarnBinding.toolbarlayout.setTitle("Share");
        fragmentReferAndEarnBinding.toolbarlayout.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goBack();
            }
        });


    }

    private void goBack() {
        ((AppCompatActivity) context).finish();
    }

    @Override
    public void onClick(View v) {

        int id = v.getId();
        if (id == R.id.copyImage) {
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            cm.setText(fragmentReferAndEarnBinding.referralCodeTv.getText().toString());
            Toast.makeText(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.shareTxtv) {
            FirebaseLogEvents.firebaseLogEvent("Shared_app",context);
            Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
            whatsappIntent.setType("text/plain");
            whatsappIntent.setPackage("com.whatsapp");
            whatsappIntent.putExtra(Intent.EXTRA_TEXT, "Download Fuelshine App - " + "https://play.google.com/store/apps/details?id=in.gypsee.customer" + " Use referrral code : " + user.getReferCode());
            try {
                (/*(AppCompatActivity) */context).startActivity(whatsappIntent);
            } catch (ActivityNotFoundException ex) {
                try {
                    Intent newWhatsappIntent = new Intent(Intent.ACTION_SEND);
                    newWhatsappIntent.setType("text/plain");
                    newWhatsappIntent.setPackage("com.whatsapp.w4b");
                    newWhatsappIntent.putExtra(Intent.EXTRA_TEXT, "Download Fuelshine App - " + "https://play.google.com/store/apps/details?id=in.gypsee.customer" + " Use referrral code : " + user.getReferCode());
                    context.startActivity(newWhatsappIntent);
                } catch (ActivityNotFoundException e) {
                    Toast.makeText(context, "Whatsapp have not been installed.", Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
