package com.sdk.gypsee.sampleapp;

import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import android.view.View;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.gypsee.sdk  .GypseeSdk;
import com.gypsee.sdk.utils.Utils;
import com.sdk.gypsee.sampleapp.databinding.ActivityMainBinding;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {

    private AppBarConfiguration appBarConfiguration;
    private ActivityMainBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       // Utils.registerNetworkCallback(getApplicationContext());
        Utils.registerNetworkCallback(getApplicationContext());
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        setSupportActionBar(binding.toolbar);
        GypseeSdk.setClientId("930731945545-5hso7raslpcbm6eupad8i5gnlfd434c7.apps.googleusercontent.com");
        binding.buttonFirst.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Call this function for the start sdk and pass the required details
                String email =  binding.emailEt.getText().toString();
                String password =  binding.emailEt.getText().toString();

                if(email.isEmpty() && password.isEmpty()){
                    GypseeSdk.start(MainActivity.this,"","","");

                }
                else if(!email.contains("@"))
                {
                    Toast.makeText(MainActivity.this, "Please enter proper email", Toast.LENGTH_SHORT).show();
                }else if(password.length()<4) {
                    Toast.makeText(MainActivity.this, "Please enter proper Password", Toast.LENGTH_SHORT).show();

                }else{
                    GypseeSdk.start(MainActivity.this,email,email,password);

                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main);
        return NavigationUI.navigateUp(navController, appBarConfiguration)
                || super.onSupportNavigateUp();
    }
}