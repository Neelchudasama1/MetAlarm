package com.metalarm.services;

import android.Manifest;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.metalarm.DialogActivity;
import com.metalarm.R;
import com.metalarm.database.AlarmTableHandler;
import com.metalarm.model.getSetAlarmData;
import com.metalarm.utils.General;
import com.metalarm.utils.MetalarmSync_Log;
import com.metalarm.utils.SessionManager;
import com.metalarm.utils.Utils;

import java.util.ArrayList;

/**
 * Created by qtm-android on 18/6/16.
 */
public class MetraLocationService extends Service
        implements LocationListener, GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {

    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    String TAG = MetraLocationService.class.getSimpleName();
    private static final int MIN_DISTANCE = 400;
    private static final float FIX_TIME_DIFF = 5.0f;
    public final static String MY_ACTION = "MY_ACTION";

    // Update interval in milliseconds
    long INTERVAL_MILLIS = 1000 * 10;

    // A fast ceiling of update intervals, used when the app is visible
    long FAST_INTERVAL_MILLIS = 1000 * 1;

    // A fast ceiling of update intervals, used when the app is visible
    float SMALLEST_DISPLACEMENT = 10;

    SQLiteDatabase db;
    ArrayList<getSetAlarmData> arrSetAlarm = new ArrayList<>();
    AlarmTableHandler alarmTableHandler = new AlarmTableHandler(this);
    private ArrayList<com.metalarm.model.Location> arrHomeLocations = new ArrayList<>();
    public SessionManager mSessionManager;

    private Location lastKnownLocation;
    private final IBinder myBinder = new MetraServiceBinder();

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return myBinder;
    }

    public class MetraServiceBinder extends Binder {
        public MetraLocationService getService() {
            return MetraLocationService.this;
        }
    }

    @Override
    public void onRebind(Intent intent) {
        Log.v("MetraLocationService", "in onRebind");
        super.onRebind(intent);
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.v("MetraLocationService", "in onUnbind");
        return true;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (General.allowToWriteLog) {
            MetalarmSync_Log.writeToLog("MetraLocationService : onCreate");
        }
        Log.e(TAG, "onCreate");
        buildGoogleApiClient();
        mGoogleApiClient.connect();
        db = openOrCreateDatabase("database", Context.MODE_PRIVATE, null);
        new GetHomeLocationsTask().execute();
        General.AppContext = MetraLocationService.this;

        arrSetAlarm = alarmTableHandler.getAlarmData();
        Log.e("res", "on create called and arraylist size  arrSetAlarm = " + arrSetAlarm.size());

        for (int i = 0; i < arrSetAlarm.size(); i++) {
            com.metalarm.model.Location model = new com.metalarm.model.Location();
            model.uid = arrSetAlarm.get(i).uid;
            model.lattitude = arrSetAlarm.get(i).lati;
            model.logitude = arrSetAlarm.get(i).longi;
            model.lattitudeonMAp = arrSetAlarm.get(i).mapLat;
            model.logitudeonMAp = arrSetAlarm.get(i).mapLong;
            model.isAlarm = arrSetAlarm.get(i).isAlarm;
            model.sId = arrSetAlarm.get(i).sId;
            model.line_id = arrSetAlarm.get(i).line_id;
            model.Address = arrSetAlarm.get(i).stationName;
            model.direction = arrSetAlarm.get(i).direction;
            model.timeInMillis = arrSetAlarm.get(i).timeInMillis;
            arrHomeLocations.add(model);
            Log.e("res", "on create called and arraylist name  = " + arrHomeLocations.get(i).lattitude);
        }
        Log.e("res", "on create called and arraylist size  = " + arrHomeLocations.size());

    }

    @Override
    public int onStartCommand(final Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        showNotification();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Utils.mBroadcastStationFix);
        filter.addAction(Utils.alarmRemoveReceiver);
        registerReceiver(alarmReceiver, filter);
        if (intent != null) {
            AlarmReceiver.completeWakefulIntent(intent);
        }
        if (General.allowToWriteLog) {
            MetalarmSync_Log.writeToLog("MetraLocationService : onStartCommand");
        }
        return START_STICKY;
    }

    protected synchronized void buildGoogleApiClient() {

        mLocationRequest = LocationRequest.create();
        /*
         * Set the update interval
		 */
        mLocationRequest.setSmallestDisplacement(SMALLEST_DISPLACEMENT);
        mLocationRequest.setInterval(INTERVAL_MILLIS);//
        mLocationRequest.setFastestInterval(FAST_INTERVAL_MILLIS);//
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    @Override
    public void onDestroy() {
        if (General.allowToWriteLog) {
            MetalarmSync_Log.writeToLog("MetraLocationService : onDestroy");
        }
        unregisterReceiver(alarmReceiver);
        super.onDestroy();
        Log.e("onDestroy", "onDestroy");
        if (mGoogleApiClient != null && mGoogleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        if (General.allowToWriteLog) {
            MetalarmSync_Log.writeToLog("MetraLocationService : onConnected");
        }
        try {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (General.allowToWriteLog) {
            MetalarmSync_Log.writeToLog("MetraLocationService : onConnectionSuspended");
        }
        mGoogleApiClient.connect();
    }

    public Location getLastKnownLocation() {
        return lastKnownLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        // broadcast that device has received request.

        lastKnownLocation = location;

        Log.v("AAA", "location changed = " + location.getLatitude() + " " + location.getLongitude());

        try {

            if (General.allowToWriteLog) {
                MetalarmSync_Log.writeToLog("Current Location latitude = " + location.getLatitude() + " longitude = " + location.getLongitude());
                MetalarmSync_Log.writeToLog("Battery Level : " + Utils.batteryLevel(MetraLocationService.this));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        Log.d("arrhome", "service arraysize : " + arrHomeLocations.size());

        for (int i = 0; i < arrHomeLocations.size(); i++) {

            Log.d("TTT", "arr home size  : " + arrHomeLocations.size());
            Log.d("TTT", " current  " + location.getLatitude() + "     " + location.getLongitude());

            Log.d("isAlarm ", " check ID " + arrHomeLocations.get(i).sId + "IS ALRAM  :  " + arrHomeLocations.get(i).isAlarm);
            com.metalarm.model.Location homeLocation = arrHomeLocations.get(i);
            Log.d("TTT", " Home location   " + homeLocation.lattitude + "     " + homeLocation.logitude);
            Log.d("TTT", " Idnotification :    " + arrHomeLocations.get(i).isNotificationShown);

            if (Utils.computeDistance(location.getLatitude(), location.getLongitude(),
                    Double.parseDouble(homeLocation.lattitudeonMAp),
                    Double.parseDouble(homeLocation.logitudeonMAp)) <= MIN_DISTANCE) {
                // update time stamp as user is in station's region i.e, 400 meters.
                alarmTableHandler.updateAlarmTimeStamp(arrHomeLocations.get(i), System.currentTimeMillis());
                if (General.allowToWriteLog) {
                    MetalarmSync_Log.writeToLog("Reset time stamp to current time as we are in "
                            + homeLocation.Address
                            + " station's region.");
                }
            }

            if (Utils.computeDistance(location.getLatitude(), location.getLongitude(),
                    Double.parseDouble(homeLocation.lattitude),
                    Double.parseDouble(homeLocation.logitude)) <= MIN_DISTANCE) {
                Log.d("TTT", "Minimum Distance");
                if (!arrHomeLocations.get(i).isNotificationShown && arrHomeLocations.get(i).isAlarm.equals("false")) {
                    if (arrHomeLocations.get(i).getTimeStampDiffInMinutes(alarmTableHandler) >= FIX_TIME_DIFF) {
                        mSessionManager = new SessionManager(MetraLocationService.this);
                        mSessionManager.prefClearLatLong(); // clear preference data
                        mSessionManager.storeLocationInPref(arrHomeLocations.get(i).lattitudeonMAp, arrHomeLocations.get(i).logitudeonMAp); // store location in preferance
                        mSessionManager.prefStationID(arrHomeLocations.get(i).sId);
                        Log.d("AAA", "pref station ID :" + arrHomeLocations.get(i).sId);
                        Log.d("TTT", "Storing lat long while notification generate : " + arrHomeLocations.get(i).lattitudeonMAp + "  " + arrHomeLocations.get(i).logitudeonMAp);
                        //generateNotification(arrHomeLocations.get(i));
                        showDialog(arrHomeLocations.get(i), General.allowToWriteLog, location);
                        alarmTableHandler.updateAlarmTimeStamp(arrHomeLocations.get(i), System.currentTimeMillis());
                        if (General.allowToWriteLog) {
                            MetalarmSync_Log.writeToLog("Alarm point coordinates : "
                                    + homeLocation.lattitude
                                    + ","
                                    + homeLocation.logitude);
                            float dist = Utils.computeDistance(location.getLatitude(), location.getLongitude(),
                                    Double.parseDouble(homeLocation.lattitude),
                                    Double.parseDouble(homeLocation.logitude));
                            MetalarmSync_Log.writeToLog("Distance between current location and alarm point : " + dist + " meters.");
                        }
                    } else {
                        if (General.allowToWriteLog) {
                            MetalarmSync_Log.writeToLog("Time diff is less than we've decided.");
                        }
                    }
                }

                arrHomeLocations.get(i).isNotificationShown = true;
                arrHomeLocations.get(i).isAlarm = "true";
                alarmTableHandler.updateAlarm(arrHomeLocations.get(i).sId, "true");
                Log.d("isAlarm", "true");
            } else {
                arrHomeLocations.get(i).isNotificationShown = false;
                alarmTableHandler.updateAlarm(arrHomeLocations.get(i).sId, "false");
                arrHomeLocations.get(i).isAlarm = "false";
                Log.d("isAlarm", "false");
            }
        }

        //mHandler.removeCallbacks(mRunnable);
        //mHandler.postDelayed(mRunnable, TIMER);
    }

    // for retrieving location updates in regular interval.
    private final int TIMER = 5000;
    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            mGoogleApiClient.disconnect();
            buildGoogleApiClient();
            mGoogleApiClient.connect();
        }
    };

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if (General.allowToWriteLog) {
            MetalarmSync_Log.writeToLog("MetraLocationService : onConnectionFailed");
        }
    }

    // get home location from api.
    private class GetHomeLocationsTask extends AsyncTask<Void, Void, Void> {

        private String msg;
        private boolean error = false;

        @Override
        protected Void doInBackground(Void... params) {
            //  loadData();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if (!error) {
                // Kick off the process of building a GoogleApiClient and requesting the LocationServices
                // API.
                buildGoogleApiClient();
                mGoogleApiClient.connect();
            } else {

                // broadcast that device has received request.
                Intent intent = new Intent();
                intent.setAction(General.BROAD_CAST_NO_LOCATION);
                sendBroadcast(intent);
                Utils.showToast(MetraLocationService.this, "No home location found.");
                stopSelf();
            }
        }
    }

    private BroadcastReceiver alarmReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            Log.i("station", " reciver Broadcast received: " + action);

            if (action.equals(Utils.mBroadcastStationFix)) {
                // String state = intent.getExtras().getString("arraylist");
                ArrayList<com.metalarm.model.Location> arrHomeLocations2;
                arrHomeLocations2 = intent.getParcelableArrayListExtra("arraylist");
                arrHomeLocations.addAll(arrHomeLocations2);

                Log.d("station", " recever" + arrHomeLocations.size());
            }

            if (action.equals(Utils.alarmRemoveReceiver)) {

                int i = intent.getIntExtra("removeArrayListPosition", 0);
                Log.d("Receiver", "array list position " + i);

                arrHomeLocations.remove(i); //
            }
        }
    };

    private void showDialog(com.metalarm.model.Location alarmData,
                            boolean allowToWriteLog, Location location) {
        try {

            if (allowToWriteLog) {
                MetalarmSync_Log.writeToLog("Alarm triggered at Location latitude = "
                        + location.getLatitude() + " longitude = "
                        + location.getLongitude());
                String towards = alarmData.getDirection();
                String stationName = alarmData.Address;

                MetalarmSync_Log.writeToLog("station : " + stationName
                        + ", direction : " + towards);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent i = new Intent(this, DialogActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        i.putExtra("alarm_data", alarmData);
        startActivity(i);

    }

    private void showNotification() {
       /* Intent notificationIntent = new Intent(this, MainActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
                notificationIntent, 0);*/


        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.app_icon);

        Notification notification = new NotificationCompat.Builder(this)
                .setContentTitle(getString(R.string.app_name))
                .setContentText("Running...")
                .setPriority(Notification.PRIORITY_MIN)
                .setSmallIcon(R.drawable.app_icon)
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                //  .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setOngoing(true).build();
        startForeground(7, notification);


    }

}
