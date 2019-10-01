package com.example.locationtracker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity implements FetchAddressTask.OnTaskCompleted {

    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final String TAG = "Location";
    private Button button_location;

    private Location mLastLocation;
    private TextView textView_location;
    private FusedLocationProviderClient mFusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button_location = findViewById(R.id.button_location);
        textView_location = findViewById(R.id.textview_location);
        button_location.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocation();
            }
        });
        //initialize the Fused location client
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


    }

    public void getLocation() {

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
        else {
            //Log.d(TAG,"getLocation: permissions granted");
            mFusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                       // mLastLocation = location;
                        new FetchAddressTask(MainActivity.this,MainActivity.this).execute(location);
//                        textView_location.setText(getString(R.string.location_text,
//                                mLastLocation.getLatitude(),
//                                mLastLocation.getLongitude(),
//                                mLastLocation.getTime()));
                    }
                    else {
                        textView_location.setText(R.string.no_location);
                    }
                }
            });

        }

        //show some loading text while the FetchAddressTask runs in the background
        textView_location.setText(getString(R.string.address_text, getString(R.string.loading),System.currentTimeMillis()));

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String [] permission, int[] grantResults) {
        switch (requestCode){
            case REQUEST_LOCATION_PERMISSION:
                //if permission is granted, get the location
                //otherwise show the message

                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getLocation();
                }
                else {
                    Toast.makeText(this, R.string.location_permission_denied, Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public void onTaskCompleted(String result) {
        //update our UI
        textView_location.setText(getString(R.string.address_text, result, System.currentTimeMillis()));
    }
}
