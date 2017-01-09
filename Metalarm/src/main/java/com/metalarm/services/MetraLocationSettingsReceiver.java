package com.metalarm.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.util.Log;

import com.metalarm.utils.General;
import com.metalarm.utils.MetalarmSync_Log;

/**
 * Created by latitude on 14/12/16.
 */

public class MetraLocationSettingsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("SettingsReceiver", "MetraLocationSettingsReceiver : onReceive");

        LocationManager lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        boolean gps_enabled = false;
        boolean network_enabled = false;

        try {
            gps_enabled = lm.isProviderEnabled(LocationManager.GPS_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            network_enabled = lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (gps_enabled) {
            if (General.allowToWriteLog) {
                MetalarmSync_Log.writeToLog("Location settings are enabled : GPS");
            }
        } else if (network_enabled) {
            if (General.allowToWriteLog) {
                MetalarmSync_Log.writeToLog("Location settings are enabled : NETWORK_PROVIDER");
            }
        } else {
            if (General.allowToWriteLog) {
                MetalarmSync_Log.writeToLog("Location settings are disabled");
            }
        }
    }
}
