package com.sadharan.indoor_positioning.surveyor;

public class APSignalStrength {
    String ssid,bssid;
    int rssi;

    public APSignalStrength(String bssid, int rssi, String ssid) {
        setBssid(bssid);
        setRssi(rssi);
        setSsid(ssid);
    }

    public void setSsid(String ssid) {
        this.ssid = ssid;
    }

    public void setBssid(String bssid) throws IllegalArgumentException {
        if(bssid.length()>0) {
            this.bssid = bssid;
        }else{
            throw new IllegalArgumentException("BSSID is null!");
        }
    }

    public void setRssi(int rssi) {
        this.rssi = rssi;
    }
}
