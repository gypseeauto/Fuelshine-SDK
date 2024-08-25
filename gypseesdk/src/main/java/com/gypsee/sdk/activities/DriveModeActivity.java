package com.gypsee.sdk.activities;

import static com.gypsee.sdk.utils.Utils.getLine;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import java.util.ArrayList;
import java.util.Locale;

import com.gypsee.sdk.Adapters.DrivePagerDotsAdapter;
import com.gypsee.sdk.Adapters.DriveSliderAdapter;
import com.gypsee.sdk.R;
import com.gypsee.sdk.models.DriveSliderItem;

public class DriveModeActivity extends AppCompatActivity {

    ImageView exitDrive, driveHome, leftButton, rightButton;
    TextView googleText;
    ViewPager2 viewPager2;
    RecyclerView pagerDots;
    RelativeLayout voiceButton;
    DrivePagerDotsAdapter drivePagerDotsAdapter;
    TextToSpeech textToSpeech;
    private Location currentLocation;
    private final String TAG = DriveModeActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drive_mode);

        voiceButton = findViewById(R.id.voice_card);
        viewPager2 = findViewById(R.id.button_slider);
        pagerDots = findViewById(R.id.pager_dots_recycler);
        exitDrive = findViewById(R.id.exit_button);
        driveHome = findViewById(R.id.home_button);
        leftButton = findViewById(R.id.left_button);
        rightButton = findViewById(R.id.right_button);
        googleText = findViewById(R.id.google_text);

        voiceButton.bringToFront();
        pagerDots.bringToFront();
        exitDrive.bringToFront();
        driveHome.bringToFront();
        leftButton.bringToFront();
        rightButton.bringToFront();
        googleText.bringToFront();

        exitDrive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        driveHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), GypseeMainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            }
        });

        voiceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gAssistant();
            }
        });

        drivePagerDotsAdapter = new DrivePagerDotsAdapter(pagerDots);

        setUpTTS();
        initPagerDots();
        initViewPager();
        registerLocationBroadcastReceiver();

    }

    private void gAssistant(){
        try {
            Intent voiceIntent = new Intent(Intent.ACTION_VOICE_COMMAND).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            Log.e(TAG, "activating google assistant");
            startActivity(voiceIntent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "We are unable to launch assistant. Kindly download google Assistant", Toast.LENGTH_LONG).show();
            // ((AppCompatActivity) context).getSupportFragmentManager().popBackStack();

        }
    }

    private SpeechRecognizer speech = null;
    private Intent recognizerIntent;

    //To activate google assistant when user says "ok gypsee"
    private void initSpeechToText(){
        Log.e(TAG, "initSpeechtotext");
        speech = SpeechRecognizer.createSpeechRecognizer(this);
        speech.setRecognitionListener(recognitionListener);
        recognizerIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH);
        recognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.MODIFY_AUDIO_SETTINGS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO, Manifest.permission.MODIFY_AUDIO_SETTINGS},
                    100);
        }else{
            speech.startListening(recognizerIntent);
        }
    }



    RecognitionListener recognitionListener = new RecognitionListener() {
        @Override
        public void onReadyForSpeech(Bundle params) {
            muteAudio();
            Log.e(TAG+getLine(), "onReadyForSpeech");
        }

        @Override
        public void onBeginningOfSpeech() {
            Log.e(TAG+getLine(), "onBeginningOfSpeech");
        }

        @Override
        public void onRmsChanged(float rmsdB) {
        }

        @Override
        public void onBufferReceived(byte[] buffer) {
            Log.e(TAG+getLine(), "onBufferReceived "+buffer.toString());
        }

        @Override
        public void onEndOfSpeech() {
            Log.e(TAG+getLine(), "onEndOfSpeech");
        }

        @Override
        public void onError(int error) {
            Log.e(TAG+getLine(), "onError "+error);
            speech.startListening(recognizerIntent);
        }

        @Override
        public void onResults(Bundle results) {

            ArrayList<String> matches = results
                    .getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
            matches.add("");
            String text = "";
//            if(matches.) {
                for (String result : matches)
                    text += result + "\n";
                Log.e(TAG + getLine(), "onResults\n" + text);
                if (text.trim().equalsIgnoreCase("ok gypsy") || text.trim().equalsIgnoreCase("hey gypsy")) {
                    gAssistant();
                }
//            }
            speech.startListening(recognizerIntent);
        }

        @Override
        public void onPartialResults(Bundle partialResults) {
            Log.e(TAG+getLine(), "onPartialResults\n"+partialResults.toString());
        }

        @Override
        public void onEvent(int eventType, Bundle params) {
            Log.e(TAG+getLine(), "onEvent");
        }
    };

    // to avoid beep sound when listener is added to speech


//    private void muteAudio(){
//        AudioManager mAlramMAnager=(AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
//        } else {
//            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
//        }
//
//    }

    private void muteAudio() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_MUTE, 0);
            } else {
                mAudioManager.setStreamMute(AudioManager.STREAM_SYSTEM, true);
            }
        } catch (SecurityException e) {
            // Handle permission-related exceptions
            e.printStackTrace();
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
        }
    }



//    private void unmuteAudio(){
//        AudioManager mAlramMAnager=(AudioManager)getApplicationContext().getSystemService(Context.AUDIO_SERVICE);
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            mAlramMAnager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
//        } else {
//            mAlramMAnager.setStreamMute(AudioManager.STREAM_SYSTEM, false);
//        }
//
//    }

    private void unmuteAudio() {
        AudioManager mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);

        try {
            mAudioManager.adjustStreamVolume(AudioManager.STREAM_SYSTEM, AudioManager.ADJUST_UNMUTE, 0);
        } catch (SecurityException e) {
            // Handle permission-related exceptions
            e.printStackTrace();
        } catch (Exception e) {
            // Handle other exceptions
            e.printStackTrace();
        }
    }







    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                speech.startListening(recognizerIntent);
            } else {
                Toast.makeText(this, "Please allow audio permission to access all features.", Toast.LENGTH_LONG).show();
            }

        }
    }


    private void setUpTTS(){
        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    Log.e(TAG, "TTS initialized");
                    textToSpeech.setLanguage(Locale.ENGLISH);
                } else {
                    Log.e(TAG, "TTS failed");
                }
            }
        });
    }


    private void setArrowVisible(ImageView imageView){

        if (imageView == null){
            leftButton.setVisibility(View.VISIBLE);
            rightButton.setVisibility(View.VISIBLE);
        } else {
            leftButton.setVisibility(View.INVISIBLE);
            rightButton.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.VISIBLE);
        }

    }

    private void speakTTS(int position){

        switch (position){

            case 0:
                textToSpeech.stop();
                textToSpeech.speak("Dialer", TextToSpeech.QUEUE_FLUSH, null, null);
                break;

            case 1:
                textToSpeech.stop();
                textToSpeech.speak("Navigation", TextToSpeech.QUEUE_FLUSH, null, null);
                break;

            case 2:
                textToSpeech.stop();
                textToSpeech.speak("Music", TextToSpeech.QUEUE_FLUSH, null, null);
                break;

            case 3:
                textToSpeech.stop();
                textToSpeech.speak("Find Parking", TextToSpeech.QUEUE_FLUSH, null, null);
                break;

            case 4:
                textToSpeech.stop();
                textToSpeech.speak("Share Location", TextToSpeech.QUEUE_FLUSH, null, null);
                break;
            case 5:
                textToSpeech.stop();
                textToSpeech.speak("Find Fuel Station", TextToSpeech.QUEUE_FLUSH, null, null);
                break;
            case 6:
                textToSpeech.stop();
                textToSpeech.speak("Find EV charging Station", TextToSpeech.QUEUE_FLUSH, null, null);
                break;

        }

    }

    @Override
    protected void onResume() {
        super.onResume();
        callStopWidgetService();
        initSpeechToText();

    }

    @Override
    protected void onPause() {
        super.onPause();
        speech.destroy();
        unmuteAudio();
        callStartWidgetService();
    }

    @Override
    protected void onDestroy() {
        callStartWidgetService();
        if (textToSpeech != null) {
            textToSpeech.shutdown();
        }
        speech.destroy();
        unmuteAudio();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

        super.onDestroy();
    }

    private void initViewPager(){
        ArrayList<DriveSliderItem> sliderItems = new ArrayList<>();
//        sliderItems.add(new DriveSliderItem(R.drawable.ic_dial_orange));
//        sliderItems.add(new DriveSliderItem(R.drawable.ic_navigation_orange));
//        sliderItems.add(new DriveSliderItem(R.drawable.ic_music_orange));
//        sliderItems.add(new DriveSliderItem(R.drawable.ic_parking_transparent_full));
//        sliderItems.add(new DriveSliderItem(R.drawable.ic_location_share_transparent_full));
//        sliderItems.add(new DriveSliderItem(R.drawable.ic_gas_station_orange));
//        sliderItems.add(new DriveSliderItem(R.drawable.ic_ev_charging_station_orange));

        sliderItems.add(new DriveSliderItem(R.drawable.drive_mode_call));
        sliderItems.add(new DriveSliderItem(R.drawable.drive_mode_navigation));
        sliderItems.add(new DriveSliderItem(R.drawable.drive_mode_music));
        sliderItems.add(new DriveSliderItem(R.drawable.drive_mode_park));
        sliderItems.add(new DriveSliderItem(R.drawable.drive_mode_loaction_share));
        sliderItems.add(new DriveSliderItem(R.drawable.drive_mode_gas));
        sliderItems.add(new DriveSliderItem(R.drawable.drive_mode_ev));

        DriveSliderAdapter driveSliderAdapter = new DriveSliderAdapter(sliderItems, viewPager2, getApplicationContext());

        float nextItemVisiblePx = getResources().getDimension(R.dimen._26sdp);
        float currentItemHorizontalMarginPx = getResources().getDimension(R.dimen._42sdp);
        float pageTranslationX = nextItemVisiblePx + currentItemHorizontalMarginPx;


        viewPager2.setAdapter(driveSliderAdapter);
        viewPager2.setClipToPadding(false);
        viewPager2.setClipChildren(false);
        viewPager2.setOffscreenPageLimit(1);
        viewPager2.setOverScrollMode(View.OVER_SCROLL_NEVER);
        viewPager2.setCurrentItem(0);

        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
        //compositePageTransformer.addTransformer(new MarginPageTransformer(20));

        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
            @Override
            public void transformPage(@NonNull View page, float position) {
                //float r = 1 - Math.abs(position);
                //page.setScaleY(0.85f + r*0.30f);
                //page.setScaleX(0.85f + r*0.30f);

                page.setScaleY(1 - (0.40f * Math.abs(position)));
                page.setScaleX(1 - (0.40f * Math.abs(position)));
                page.setAlpha(0.25f + (1 - Math.abs(position)));

            }
        });

        viewPager2.setPageTransformer(compositePageTransformer);

        viewPager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels);
            }

            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                driveSliderAdapter.setSelectedPagePosition(position);
                if (currentLocation != null){
                    driveSliderAdapter.setCurrentLocation(currentLocation);
                }
                drivePagerDotsAdapter.setPagePosition(position);
                speakTTS(position);
                if (position == 6){
                    setArrowVisible(leftButton);
                } else if (position == 0){
                    setArrowVisible(rightButton);
                } else {
                    setArrowVisible(null);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                super.onPageScrollStateChanged(state);
            }
        });



        leftButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem()-1, true);
            }
        });

        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager2.setCurrentItem(viewPager2.getCurrentItem()+1, true);
            }
        });

    }


    private void initPagerDots(){
        pagerDots.setAdapter(drivePagerDotsAdapter);
        pagerDots.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
    }

    private void callStopWidgetService(){
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("WIDGET_GONE"));
    }

    private void callStartWidgetService(){
        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(new Intent("WIDGET_VISIBLE"));
    }

    BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            if (intent.getAction().equals("LocationReceiver")) {
                Log.e(TAG, "onreceive");
                Location location = intent.getParcelableExtra("Location");
                currentLocation = location;
            }

        }
    };

    private void registerLocationBroadcastReceiver() {

        IntentFilter newIntent = new IntentFilter();
        newIntent.addAction("LocationReceiver");
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, newIntent);

    }



}
