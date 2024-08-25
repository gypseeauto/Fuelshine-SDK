package com.gypsee.sdk.network;

import java.net.InetAddress;

public class InternetAccessThread extends Thread {

    private boolean internetAvailable = false;

    @Override
    public void run() {

        try{

            InetAddress inetAddress = InetAddress.getByName("google.com");
            internetAvailable = !inetAddress.getHostAddress().equals("");

        } catch (Exception e){
            e.printStackTrace();
            internetAvailable = false;
        }
    }

    public boolean isInternetAvailable(){
        return internetAvailable;
    }




}
