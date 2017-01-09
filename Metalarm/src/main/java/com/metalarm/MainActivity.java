package com.metalarm;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.database.sqlite.SQLiteDatabase;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.metalarm.database.AddRemoveAlarmtableHandler;
import com.metalarm.database.AlarmTableHandler;
import com.metalarm.model.addRemoveAlarmModel;
import com.metalarm.model.getSetAlarmData;
import com.metalarm.services.MetraLocationService;
import com.metalarm.services.NetworkChangeReceiver;
import com.metalarm.utils.General;
import com.metalarm.utils.ParsedResponse;
import com.metalarm.utils.Soap;
import com.metalarm.utils.Utils;

import org.json.JSONException;

import java.util.ArrayList;

public class MainActivity extends BaseActivity {

    private GoogleMap mMap;
    int boundZoom = 0;

    Toolbar mActionBarToolbar;

    MarkerOptions markerOptions;
    SQLiteDatabase db;
    AlarmTableHandler DBAlarmTableHandler = new AlarmTableHandler(this);
    getSetAlarmData alarm_data;

    // for binding service.
    MetraLocationService metraLocationService;
    boolean isBound = false;
    Location location;
    AddRemoveAlarmtableHandler DBAddRemoveTableHandler = new AddRemoveAlarmtableHandler(this);

    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MetraLocationService.MetraServiceBinder binder = (MetraLocationService.MetraServiceBinder) service;
            metraLocationService = binder.getService();
            isBound = true;
            location = metraLocationService.getLastKnownLocation();
            if (location != null) {
                Log.e("Tag", "lat, lng : " + location.getLatitude() + ", " + location.getLongitude());
            }
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Log.v("TTT","SplashActivity = "+General.CurrentLat.getCurrentLat());
//        Log.v("TTT","SplashActivity = "+General.CurrentLat.getCurrentLng());

        initUI();
        getDataFromIntent(getIntent());
        setUpMapIfNeeded();

        if (canGetLocation()) {

        } else {
            showSettingsAlert();
        }

        Intent intent = new Intent(this, MetraLocationService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);


    }

    private void getDataFromIntent(Intent intent) {
        alarm_data = intent.getParcelableExtra("alarm_data");
    }

    // initialize UI.
    private void initUI() {
        db = openOrCreateDatabase("database", Context.MODE_PRIVATE, null);

        markerOptions = new MarkerOptions();
        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText(ListActivity.stationname);

        Button btnStop = (Button) findViewById(R.id.btnstop);
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DBAlarmTableHandler.deleteAlarm(Utils.ALARM_ID, Utils.Direction);

                if (Utils.isNetworkAvailable(MainActivity.this)) {
                    new apiAddRemoveAlarm(mSessionManager.getuserId(), alarm_data.sId, alarm_data.line_id).execute();
                    Log.e("CCC" , "NetWork Available");
                } else {
                    DBAddRemoveTableHandler.insertAddRemoveData(mSessionManager.getuserId(), alarm_data.sId, alarm_data.line_id, "0");

                    ArrayList<addRemoveAlarmModel> arrayList = DBAddRemoveTableHandler.getAddRemoveAlarmData();
                    Log.e("CCC" , "NetWork Not Available");
                    Log.e("CCC", "get Data Size: " + arrayList.size());
                }

                Intent intent = new Intent(Utils.alarmRemoveReceiver);
                intent.putExtra("removeArrayListPosition", Utils.GRIDVIEW_POSITION);
                sendBroadcast(intent);

                Intent i = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(i);
                finish();
            }
        });
        if (isMyServiceRunning(MetraLocationService.class)) {
            startService(new Intent(MainActivity.this, MetraLocationService.class));
        } else {
            startService(new Intent(MainActivity.this, MetraLocationService.class));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // unregister accept request receiver.
//        unregisterReceiver(locationReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }

    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
//                addMarker(Double.parseDouble(alarm_data.mapLat), Double.parseDouble(alarm_data.mapLong),
//                        R.drawable.train_on_railroad, alarm_data.stationName);
//                moveCameraToLocation(Double.parseDouble(alarm_data.mapLat), Double.parseDouble(alarm_data.mapLong));
                mMap.clear();
                addMarker(Double.parseDouble(alarm_data.mapLat), Double.parseDouble(alarm_data.mapLong),
                        R.drawable.train_on_railroad, ListActivity.stationname);

                //Calculate the markers to get their position
                LatLng l1 = new LatLng(Double.parseDouble(alarm_data.mapLat), Double.parseDouble(alarm_data.mapLong));
                LatLngBounds.Builder b = new LatLngBounds.Builder();

                b.include(l1);

                if (location != null) {
                    double cur_lat = location.getLatitude();
                    double cur_lng = location.getLongitude();

                    Log.d("lat : ", " lati :" + cur_lat + " logi : " + cur_lng);

                    addMarker(cur_lat, cur_lng, R.drawable.men_user, "");
                    LatLng l2 = new LatLng(cur_lat, cur_lng);
                    b.include(l2);
                }

                final LatLngBounds bounds = b.build();

                if (boundZoom == 0) {
                    mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
                        @Override
                        public void onMapLoaded() {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(bounds, 50));
                        }
                    });
                    boundZoom = 1;
                }


            }
        });
        if (mMap == null) {
            return;
        }
    }

    // function to add marker to map.
    private Marker addMarker(double lat, double lng, int marker_drawable_resorce_id,
                             String marker_title) {
        // mLastLocation can be null.
        MarkerOptions marker = new MarkerOptions().position(
                new LatLng(lat, lng))
                .title(marker_title);

        // Changing marker icon
        marker.icon(BitmapDescriptorFactory.fromResource(marker_drawable_resorce_id));

        // adding marker
        return mMap.addMarker(marker);
    }

    // moving camera position to particular location.
    private void moveCameraToLocation(double lat, double lng) {
        if (lat != 0 && lng != 0) {
            // mLastLocation can be null.
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(lat, lng)).zoom(General.GOOGLE_MAP_ZOOM_IN_LEVEL).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        }
    }


    // to check is service running.
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(i);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MetraLocationService.MY_ACTION);
        super.onStart();
    }

    private class apiAddRemoveAlarm extends AsyncTask<Void, Void, Void> {
        boolean error;
        String msg;
        String s, name, s_id, lineId;


        public apiAddRemoveAlarm(String s, String s_id, String lineId) {
            super();

            this.s = s;
            this.s_id = s_id;
            this.lineId = lineId;
        }

        @Override
        protected void onPreExecute() {
            // mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Log.e("222" , "s_id" + s_id +"   " + lineId);
                ParsedResponse p = Soap.apiAddRemoveAlarm(MainActivity.this,mSessionManager.getuserId(),mSessionManager.getDeviceToken(), s_id, lineId, "0","0");
                error = p.error;
                if (!error) {
                    //arrListGetAll = (ArrayList<GetAllLineListModel>) p.o;

                } else {
                    msg = (String) p.o;
                }


            } catch (JSONException e) {
                e.printStackTrace();

                error = true;
                msg = e.getMessage();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            // mProgressDialog.dismiss();
            Log.e(getLocalClassName(), "" + msg);
            if (!error) {

            } else {
                ;
                Toast.makeText(MainActivity.this, msg, Toast.LENGTH_SHORT).show();

            }


        }
    }

}
