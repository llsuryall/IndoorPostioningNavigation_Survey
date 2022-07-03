package com.sadharan.indoor_positioning.surveyor;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;

import androidx.lifecycle.ViewModel;

//import java.util.Calendar;
//import java.util.Date;
import java.util.List;

public class WifiDetails extends ViewModel {
    public List<ScanResult> scanResults = null;

    public void scanWifi(WifiManager wifiManager) {
        wifiManager.startScan();
        this.scanResults = wifiManager.getScanResults();
    }
}