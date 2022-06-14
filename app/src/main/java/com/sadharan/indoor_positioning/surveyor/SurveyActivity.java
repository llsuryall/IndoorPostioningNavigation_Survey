package com.sadharan.indoor_positioning.surveyor;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class SurveyActivity extends AppCompatActivity implements View.OnClickListener {
    private WifiDetails wifiDetails;
    private WifiManager wifiManager;
    private LocationManager locationManager;
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {
            System.exit(-1);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.building_catalogue_activity);
        //Handle Permissions
        if (!(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        //Get managers
        this.wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        this.locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        //Set eventListeners
        //Update initial results
        this.wifiDetails = new ViewModelProvider(this).get(WifiDetails.class);
    }

    public void onClick(View view) {
        //Check if wifi is on!
        if (!this.wifiManager.isWifiEnabled()) {
            Toast.makeText(getApplicationContext(),R.string.turn_on_wifi, Toast.LENGTH_SHORT).show();
            return;
        }
        //Check if location is on!
        if (!this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(getApplicationContext(), R.string.turn_on_location, Toast.LENGTH_SHORT).show();
            return;
        }
        wifiDetails.scanWifi(this.wifiManager);
    }
}
