package com.metalarm;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.metalarm.adapters.DetailAdapter;
import com.metalarm.model.AdvertismentModel;
import com.metalarm.model.Location;
import com.metalarm.model.detailModel;
import com.metalarm.services.MetraLocationService;
import com.metalarm.utils.General;
import com.metalarm.utils.ParsedResponse;
import com.metalarm.utils.SessionManager;
import com.metalarm.utils.Soap;
import com.metalarm.utils.Utils;

import org.json.JSONException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class DetailsActivity extends BaseActivity {

    private GoogleMap mMap;
    private ArrayList<Location> arrHomeLocations = new ArrayList<>();
    private boolean isMovedToCurLocation = false;
    ArrayList<detailModel> mArrayList = new ArrayList<>();

    Toolbar mActionBarToolbar;

    public SessionManager mSessionManager;
    String arrLatLong[] = new String[2];
    ArrayList<AdvertismentModel> arrListAdvertisement = new ArrayList<>();
    ListView lv;
    detailModel model;
    DetailAdapter adapter;

    com.metalarm.model.Location alarm_data;
    int boundZoom = 0;

    // for binding service.
    MetraLocationService metraLocationService;
    boolean isBound = false;
    android.location.Location location;

    private TextView txtMsg;

    private ServiceConnection myConnection = new ServiceConnection() {

        public void onServiceConnected(ComponentName className,
                                       IBinder service) {
            MetraLocationService.MetraServiceBinder binder = (MetraLocationService.MetraServiceBinder) service;
            metraLocationService = binder.getService();
            isBound = true;
            location = metraLocationService.getLastKnownLocation();
            Log.e("Tag", "lat, lng : " + location.getLatitude() + ", " + location.getLongitude());
        }

        public void onServiceDisconnected(ComponentName arg0) {
            isBound = false;
        }

    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

//        Log.v("TTT","SplashActivity = "+General.CurrentLat.getCurrentLat());
//        Log.v("TTT","SplashActivity = "+General.CurrentLat.getCurrentLng());

        Intent intent = new Intent(this, MetraLocationService.class);
        bindService(intent, myConnection, Context.BIND_AUTO_CREATE);

        setUp();
        getDataFromIntent(getIntent());
        getPrefLatLOng();
        setUpMapIfNeeded();
        canGetLocation();
        new apiAdverisement().execute();

    }

    private void getDataFromIntent(Intent intent) {
        alarm_data = intent.getParcelableExtra("alarm_data");
        if (alarm_data != null && alarm_data.direction.equals("0")) {
            txtMsg.setText("Your stop " + alarm_data.Address + " (from downtown) is approaching");
        } else {
            txtMsg.setText("Your stop " + alarm_data.Address + " (to downtown) is approaching");
        }
    }


    private void getPrefLatLOng() {

        mSessionManager = new SessionManager(DetailsActivity.this);
        arrLatLong = mSessionManager.getLocationPref();

        Log.d("TTT", "get lat long from pref  : " + arrLatLong[0] + "   " + arrLatLong[1]);
    }

    private void setUp() {
        setToolbar();


        txtMsg = (TextView) findViewById(R.id.txtMsg);
        lv = (ListView) findViewById(R.id.listView);


    }

    private void setToolbar() {
        mActionBarToolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        toolbar_title.setText("Metalarm");

        mActionBarToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.left_arrow));
        mActionBarToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), ListActivity.class);
                startActivity(i);
                finish();
            }
        });

    }

    private void setUpArrayList() {

        for (int i = 0; i < arrListAdvertisement.size(); i++) {
            model = new detailModel();
            model.drw = "http://latitudetechnolabs.com/metraApp/" + arrListAdvertisement.get(i).image;
            model.name = arrListAdvertisement.get(i).title;
            model.desc = arrListAdvertisement.get(i).description;
            mArrayList.add(model);
        }
        adapter = new DetailAdapter(DetailsActivity.this, mArrayList);
        lv.setAdapter(adapter);

       /* model = new detailModel();
        model.drw = R.drawable.img;
        model.name = "Better Burger";
        model.desc = "Simply yummy test of the burger.";
        mArrayList.add(model);

        model = new detailModel();
        model.drw = R.drawable.burgerr;
        model.name = "Better Burger with chips";
        model.desc = "Simply yummy test of the burger.";
        mArrayList.add(model);

        model = new detailModel();
        model.drw = R.drawable.optimized;
        model.name = "Shabari - Santacruz West";
        model.desc = "Simply yummy test.";
        mArrayList.add(model);

        model = new detailModel();
        model.drw = R.drawable.shabri;
        model.name = "Chinese Embraces Sagar Gaire Fast.";
        mArrayList.add(model);*/
    }

    private void setUpMapIfNeeded() {
        if (mMap != null) {
            return;
        }
        mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {

                mMap.clear();
                addMarker(Double.parseDouble(alarm_data.lattitudeonMAp), Double.parseDouble(alarm_data.logitudeonMAp),
                        R.drawable.train_on_railroad, ListActivity.stationname);

                double cur_lat = location.getLatitude();
                double cur_lng = location.getLongitude();

                Log.d("lat : ", " lati :" + cur_lat + " logi : " + cur_lng);

                addMarker(cur_lat, cur_lng, R.drawable.men_user, "");

                //Calculate the markers to get their position
                LatLng l1 = new LatLng(Double.parseDouble(alarm_data.lattitudeonMAp), Double.parseDouble(alarm_data.logitudeonMAp));
                LatLng l2 = new LatLng(cur_lat, cur_lng);
                LatLngBounds.Builder b = new LatLngBounds.Builder();

                b.include(l1);
                b.include(l2);

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
        if (!isMovedToCurLocation && lat != 0 && lng != 0) {
            // mLastLocation can be null.
            CameraPosition cameraPosition = new CameraPosition.Builder().target(
                    new LatLng(lat, lng)).zoom(General.GOOGLE_MAP_ZOOM_IN_LEVEL).build();

            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            isMovedToCurLocation = true;
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        isMovedToCurLocation = false;
        // unregister accept request receiver.
//        unregisterReceiver(locationReceiver);
    }


    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(MetraLocationService.MY_ACTION);
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpMapIfNeeded();

    }

    public class apiAdverisement extends AsyncTask<Void, Void, Void> {

        boolean error;
        String msg;
        String selectedLine;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {


                Log.d("AAA", "get pref station ID :" + mSessionManager.getPrefSattionID() + " date  : " + (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date())).toString());
                ParsedResponse p = Soap.apiAdvertisement(DetailsActivity.this,mSessionManager.getPrefSattionID(), (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").format(new java.util.Date())).toString());
                error = p.error;
                if (!error) {
                    arrListAdvertisement = (ArrayList<AdvertismentModel>) p.o;

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
            mProgressDialog.dismiss();
            if (!error) {
                Log.e(getLocalClassName(), " Array Loist size" + arrListAdvertisement.size());

                setUpArrayList();
            } else {
                Log.e(getLocalClassName(), "" + msg);
            }
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent i = new Intent(getApplicationContext(), ListActivity.class);
        startActivity(i);
        finish();
    }
}
