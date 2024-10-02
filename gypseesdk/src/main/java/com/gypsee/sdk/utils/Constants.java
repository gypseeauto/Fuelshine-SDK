package com.gypsee.sdk.utils;

import static java.lang.Math.round;

public class Constants {
    public static boolean isNetworkConnected = false;
    public static boolean isFakeDriving = true;      //used to start drive on simulation

    public static long LOCATION_INTERVAL = 100;
    public static long LOCATION_FASTEST_INTERVAL = 100;
    public static float SMALLEST_DISPLACEMENT = 10;
    public static float LOCATION_ACCURACY = 150;
    public static float overSpeed_alert_max_duration = 180;

    //Voice alerts
//    public static String HARSH_BRAKING = "Please brake gently and safely so that both you and your vehicle are safe.";
    public static String HARSH_BRAKING = "Harsh Braking Detected: Brake gently; save tires, brakes, and costs.";
    public static String HARSH_BRAKING_HINDI = "कृपया धीरे और सुरक्षित तरीके से ब्रेक लगाएं ताकि आप और आपका वाहन दोनों सुरक्षित रहें।";
    public static String HARSH_BRAKING_TELUGU = "దయచేసి మీరు మరియు మీ వాహనం రెండూ సురక్షితంగా ఉండేలా సున్నితంగా మరియు సురక్షితంగా బ్రేక్ చేయండి";
    public static String switchOnBluetooth = "Please switch on bluetooth";
    public static String HARSH_BRAKING_MARATHI = "कृपया हळुवारपणे आणि सुरक्षितपणे ब्रेक लावा जेणेकरून तुम्ही आणि तुमचे वाहन दोघेही सुरक्षित राहाल.";
    public static String HARSH_BRAKING_TAMIL = "நீங்களும் உங்கள் வாகனமும் பாதுகாப்பாக இருக்க தயவுசெய்து மெதுவாகவும் பாதுகாப்பாகவும் பிரேக் செய்யவும்.";
    public static String HARSH_BRAKING_KANNADA = "ದಯವಿಟ್ಟು ನಿಧಾನವಾಗಿ ಮತ್ತು ಸುರಕ್ಷಿತವಾಗಿ ಬ್ರೇಕ್ ಮಾಡಿ ಇದರಿಂದ ನೀವು ಮತ್ತು ನಿಮ್ಮ ವಾಹನ";
//    public static String ABOVE_ECO_SPEED = "Please drive at the prescribed economic speed so that you can save fuel and protect the environment.";
    public static String ABOVE_ECO_SPEED = "Fuelburn mode: Slow down; save fuel and reduce pollution.";
    public static String ABOVE_ECO_SPEED_TAMIL = "எரிபொருளைச் சேமிக்கவும், சுற்றுச்சூழலைப் பாதுகாக்கவும் பரிந்துரைக்கப்பட்ட பொருளாதார வேகத்தில் வாகனத்தை ஓட்டவும்.";
    public static String ABOVE_ECO_SPEED_KANNADA = "ದಯವಿಟ್ಟು ನಿಗದಿತ ಆರ್ಥಿಕ ವೇಗದಲ್ಲಿ ಚಾಲನೆ ಮಾಡಿ ಇದರಿಂದ ನೀವು ಇಂಧನವನ್ನು ಉಳಿಸಬಹುದು ಮತ್ತು ಪರಿಸರವನ್ನು ರಕ್ಷಿಸಬಹುದು.";
    public static String ABOVE_ECO_SPEED_TELUGU = "దయచేసి నిర్దేశించిన ఆర్థిక వేగంతో డ్రైవ్ చేయండి, తద్వారా మీరు ఇంధనాన్ని ఆదా చేయవచ్చు మరియు పర్యావరణాన్ని రక్షించవచ్చు.";
    public static String ABOVE_ECO_SPEED_HINDI = "कृपया निर्धारित आर्थिक गति से वाहन चलाएं ताकि आप ईंधन बचा सकें और पर्यावरण की रक्षा कर सकें।";
    public static String ABOVE_ECO_SPEED_MARATHI = "कृपया विहित आर्थिक गतीने वाहन चालवा जेणेकरून तुम्ही इंधन वाचवू शकाल आणि पर्यावरणाचे संरक्षण करू शकाल.";
//    public static String ECO_SPEED = "Please drive at the prescribed economic speed so that you can save fuel, protect the environment and earn rewards.";
    public static String ECO_SPEED = "Fuel save mode: Great job! Saving fuel and protecting the environment.";
    public static String ECO_SPEED_MARATHI = "कृपया विहित आर्थिक वेगाने गाडी चालवा जेणेकरून तुम्ही इंधनाची बचत करू शकाल, पर्यावरणाचे रक्षण करू शकाल आणि बक्षिसे मिळवू शकाल.";
    public static String ECO_SPEED_HINDI = "कृपया निर्धारित आर्थिक गति से वाहन चलाएं ताकि आप ईंधन बचा सकें, पर्यावरण की रक्षा कर सकें और पुरस्कार अर्जित कर सकें।";
    public static String ECO_SPEED_TELUGU = "దయచేసి నిర్దేశించిన ఆర్థిక వేగంతో డ్రైవ్ చేయండి, తద్వారా మీరు ఇంధనాన్ని ఆదా చేయవచ్చు, పర్యావరణాన్ని రక్షించవచ్చు మరియు రివార్డ్ లను సంపాదించవచ్చు.";
    public static String ECO_SPEED_KANNADA = "ದಯವಿಟ್ಟು ನಿಗದಿತ ಆರ್ಥಿಕ ವೇಗದಲ್ಲಿ ಚಾಲನೆ ಮಾಡಿ ಇದರಿಂದ ನೀವು ಇಂಧನವನ್ನು ಉಳಿಸಬಹುದು, ಪರಿಸರವನ್ನು ರಕ್ಷಿಸಬಹುದು ಮತ್ತು ಪ್ರತಿಫಲಗಳನ್ನು ಗಳಿಸಬಹುದು.";
    public static String ECO_SPEED_TAMIL = "எரிபொருளைச் சேமிக்கவும், சுற்றுச்சூழலைப் பாதுகாக்கவும் மற்றும் வெகுமதிகளைப் பெறவும், நிர்ணயிக்கப்பட்ட பொருளாதார வேகத்தில் வாகனத்தை ஓட்டவும்.";
//    public static String HARSH_ACC = "Please accelerate gently and safely so that both you and your vehicle are safe.";
    public static String HARSH_ACC = "Rapid Acceleration Detected: Accelerate gently; save engine and fuel.";
    public static String HARSH_ACC_KANNADA = "ದಯವಿಟ್ಟು ನಿಧಾನವಾಗಿ ಮತ್ತು ಸುರಕ್ಷಿತವಾಗಿ ವೇಗವನ್ನು ಹೆಚ್ಚಿಸಿ ಇದರಿಂದ ನೀವು ಮತ್ತು ನಿಮ್ಮ ವಾಹನ";
    public static String HARSH_ACC_TAMIL = "நீங்களும் உங்கள் வாகனமும் பாதுகாப்பாக இருக்க, மெதுவாகவும் பாதுகாப்பாகவும் வேகப்படுத்தவும்.";
    public static String HARSH_ACC_TELUGU = "దయచేసి మీరు మరియు మీ వాహనం రెండూ సురక్షితంగా ఉండేలా సున్నితంగా మరియు సురక్షితంగా వేగవంతం చేయండి.";
    public static String HARSH_ACC_HINDI = "कृपया धीरे और सुरक्षित तरीके से गति बढ़ाएं ताकि आप और आपका वाहन दोनों सुरक्षित रहें।";
    public static String HARSH_ACC_MARATHI = "कृपया हळूवारपणे आणि सुरक्षितपणे वेग वाढवा जेणेकरून तुम्ही आणि तुमचे वाहन दोघेही सुरक्षित राहाल.";
    public static String notRewarded = "Oops! You missed safe coins. Drive safe, save lives on road.";
    public static String rewarded = "You drove well, %1$s safe coins credited to your account";
//    public static String OVER_SPEED = "Please drive within the prescribed speed limit to keep both you and your vehicle safe.";
    public static String OVER_SPEED = "Overspeeding Detected: Slow down; stay safe and avoid accidents.";
    public static String OVER_SPEED_HINDI = "कृपया अपने और अपने वाहन दोनों को सुरक्षित रखने के लिए निर्धारित गति सीमा के भीतर वाहन चलाएं।";
    public static String OVER_SPEED_TAMIL = "உங்களையும் உங்கள் வாகனத்தையும் பாதுகாப்பாக வைத்திருக்க, பரிந்துரைக்கப்பட்ட வேக வரம்பிற்குள் ஓட்டவும்.";
    public static String OVER_SPEED_KANNADA = "ನೀವು ಮತ್ತು ನಿಮ್ಮ ವಾಹನ ಎರಡನ್ನೂ ಸುರಕ್ಷಿತವಾಗಿರಿಸಲು ದಯವಿಟ್ಟು ನಿಗದಿತ ವೇಗದ ಮಿತಿಯೊಳಗೆ ಚಾಲನೆ ಮಾಡಿ.";
    public static String OVER_SPEED_TELUGU = "దయచేసి మీరు మరియు మీ వాహనం రెండింటినీ సురక్షితంగా ఉంచడానికి నిర్దేశించిన వేగ పరిమితిలోపు నడపండి.";
    public static String OVER_SPEED_MARATHI = "तुम्ही आणि तुमचे वाहन दोन्ही सुरक्षित ठेवण्यासाठी कृपया विहित वेग मर्यादेत वाहन चालवा.";

    public static String goToDashboard = "go to dashboard";
    public static String showSubscribeDialog = "show subscribe dialog";

    //Woker tags
    public static String logInRequestWorkerTag = "Periodic Log In Request";
    public static String connectToBluetoothDevice = "Pair Fuel Shine with your car's Bluetooth to get started.";
    public static String subsNowWorkerTag = "Periodic Subscribe Now Request";
    public static String tripStartedMsg = "Trip started. Drive safely and efficiently with Fuel Shine.";
    public static String tripEndedMsg = "Trip ended. Check your fuel savings on Fuel Shine.";
    public static String initialMsg = "Ready to drive fuel-efficiently and safely with Fuel Shine.";

    public static String tracking = "Tracking...";
    public static String fuelSaveMode= "Fuel save mode";
    public static String fuelBurnMode = "FuelBurn Mode";
    public static String overspeeding = "Overspeeding";
    public static String harshBrakingDetected = "Harsh Braking Detected";
    public static String harshAccelerationDetected = "Harsh Acceleration Detected";
    public static String deviceConnected = "Device Connected";
    public static String fuelEfficientTrips = "Fuel efficient trips";
    public static String fuelInefficientTrips = "Fuel inefficient trips";
}
