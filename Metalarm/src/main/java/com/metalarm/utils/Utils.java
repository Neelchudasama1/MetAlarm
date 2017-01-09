package com.metalarm.utils;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.BatteryManager;
import android.widget.Toast;

import java.util.HashMap;

/**
 * Created by qtm-kaushik on 4/8/15.
 */
public class Utils {

    public static int k = 0;
    public static String TAG_DATABSE = "Databse";
    public static int ALARM_ID;
    public static String Direction;
    public static int GRIDVIEW_POSITION;
    public static String mBroadcastStationFix = "stationFixKEY";
    public static String alarmRemoveReceiver = "alarmRemoveReceiver";


    public static boolean isNetworkAvailable(Context mContext) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showToast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    // function to send broadcast
    public static void broadcastIntent(Context c, String broadcast_name, HashMap<String, String> mMap) {
        Intent intent = new Intent();
        intent.setAction(broadcast_name);
        if (mMap != null) {
            for (String key : mMap.keySet()) {
                intent.putExtra(key, mMap.get(key));
            }
        }
        c.sendBroadcast(intent);
    }

    // function to compute distance between two latitude longitude.
    public static float computeDistance(double lat1, double lon1, double lat2, double lon2) {
        Location start = new Location("start");
        start.setLatitude(lat1);
        start.setLongitude(lon1);

        Location end = new Location("end");
        end.setLatitude(lat2);
        end.setLongitude(lon2);

        return start.distanceTo(end);
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static String batteryLevel(Context context) {
        Intent intent = context.registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
        int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
        int percent = (level * 100) / scale;
        return String.valueOf(percent) + "%";
    }
}
