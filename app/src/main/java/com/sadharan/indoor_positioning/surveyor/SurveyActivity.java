package com.sadharan.indoor_positioning.surveyor;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.util.LinkedList;

public class SurveyActivity extends AppCompatActivity implements View.OnClickListener {
    private EditText x_coordinate_input, y_coordinate_input;
    private LocalSurveyDatabase localSurveyDatabase;
    private WifiDetails wifiDetails;
    private WifiManager wifiManager;
    private LocationManager locationManager;
    private SurveyViewModel surveyViewModel;
    private final ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
        if (!isGranted) {
            System.exit(-1);
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initialize();
    }

    public void initialize() {
        setContentView(R.layout.survey_activity);
        findViewById(R.id.save_button).setOnClickListener(this);
        findViewById(R.id.scan_button).setOnClickListener(this);
        x_coordinate_input = findViewById(R.id.x_coordinate_input);
        y_coordinate_input = findViewById(R.id.y_coordinate_input);
        localSurveyDatabase = new LocalSurveyDatabase(getApplicationContext());
        this.surveyViewModel= new ViewModelProvider(this).get(SurveyViewModel.class);
        if (getIntent().getLongExtra(getString(R.string.block_id_field), -1) >= 0) {
            this.surveyViewModel.block_id = getIntent().getLongExtra(getString(R.string.block_id_field), -1);
        }
        if (getIntent().getIntExtra(getString(R.string.floor_id_field), -1) >= 0) {
            this.surveyViewModel.floor_id = getIntent().getIntExtra(getString(R.string.floor_id_field), -1);
        }
        getIntent().removeExtra(getString(R.string.block_id_field));
        getIntent().removeExtra(getString(R.string.floor_id_field));
        if(this.surveyViewModel.signalStrengthJSON!=null && this.surveyViewModel.signalStrengthJSON.length()>0) {
            ((TextView) findViewById(R.id.ap_signal_strength_json)).setText(this.surveyViewModel.signalStrengthJSON);
        }
        //Handle Permissions
        if (!(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)) {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        //Get managers
        this.wifiManager = (WifiManager) getApplicationContext().getSystemService(WIFI_SERVICE);
        this.locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        //Update initial results
        this.wifiDetails = new ViewModelProvider(this).get(WifiDetails.class);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.save_button) {
            try {
                float x_coordinate = Float.parseFloat(x_coordinate_input.getText().toString());
                float y_coordinate = Float.parseFloat(y_coordinate_input.getText().toString());
                LinkedList<APSignalStrength> apSignalStrengths = new LinkedList<>();
                int count = 0;
                for (ScanResult scanResult : wifiDetails.scanResults) {
                    if (scanResult.level >= -70) {
                        apSignalStrengths.add(new APSignalStrength(scanResult.BSSID, scanResult.level, scanResult.SSID));
                        count++;
                        if (count >= 10) break;
                    }
                }
                if (apSignalStrengths.size() <= 0) {
                    throw new Exception("No scans yet or No APs nearby!");
                }
                boolean successful = localSurveyDatabase.addDatapoint(new SurveyElement(this.surveyViewModel.floor_id, this.surveyViewModel.block_id, x_coordinate, y_coordinate, apSignalStrengths));
                if (successful) {
                    Toast.makeText(getApplicationContext(), R.string.add_datapoint_successful, Toast.LENGTH_SHORT).show();
                    wifiDetails.scanResults = null;
                } else {
                    Toast.makeText(getApplicationContext(), R.string.add_datapoint_failure, Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                Toast.makeText(getApplicationContext(), R.string.add_datapoint_failure, Toast.LENGTH_SHORT).show();
            }
        } else if (view.getId() == R.id.scan_button) {
            //Check if wifi is on!
            if (!this.wifiManager.isWifiEnabled()) {
                Toast.makeText(getApplicationContext(), R.string.turn_on_wifi, Toast.LENGTH_SHORT).show();
                return;
            }
            //Check if location is on!
            if (!this.locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                Toast.makeText(getApplicationContext(), R.string.turn_on_location, Toast.LENGTH_SHORT).show();
                return;
            }
            wifiDetails.scanWifi(this.wifiManager);
            try{
                JSONObject signalStrengthJSON = new JSONObject();
                for (ScanResult scanResult : wifiDetails.scanResults) {
                    if(scanResult.level>=-70) {
                        signalStrengthJSON.put(scanResult.BSSID, scanResult.level);
                    }
                }
                this.surveyViewModel.signalStrengthJSON=signalStrengthJSON.toString(4);
                ((TextView)findViewById(R.id.ap_signal_strength_json)).setText(this.surveyViewModel.signalStrengthJSON);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}