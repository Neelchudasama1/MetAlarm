package com.metalarm;

import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.metalarm.database.AddRemoveAlarmtableHandler;
import com.metalarm.database.AlarmTableHandler;
import com.metalarm.database.OffLineTablesHandler;
import com.metalarm.model.GetAllLineListModel;
import com.metalarm.model.LineModel;
import com.metalarm.model.LoginModel;
import com.metalarm.model.StationModel;
import com.metalarm.model.StationSelectedModel;
import com.metalarm.model.getSetAlarmData;
import com.metalarm.services.MetraLocationService;
import com.metalarm.utils.General;
import com.metalarm.utils.MetalarmSync_Log;
import com.metalarm.utils.ParsedResponse;
import com.metalarm.utils.Soap;
import com.metalarm.utils.Utils;

import org.json.JSONException;

import java.util.ArrayList;

public class StationListActivity extends BaseActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    ArrayList<getSetAlarmData> arrGetAlarmData = new ArrayList<>();
    Toolbar mActionBarToolbar;
    ImageView ivSetting;
    LoginModel loginModel;
    Spinner spLine, spDirection, spStation;
    Button btnSetAlarm;
    String[] direction = {"from downtown", "to downtown"};
    //String direction[] = new String[2];
    SQLiteDatabase db;
    ArrayList<String> line;
    ArrayList<String> station;
    String selectedLineName = "", selectedDirection = "";
    String selectedStationID = "";
    AlarmTableHandler DBAlarmTableHandler = new AlarmTableHandler(this);
    AddRemoveAlarmtableHandler DBAddRemoveTableHandler = new AddRemoveAlarmtableHandler(this);
    OffLineTablesHandler oflineTablHander = new OffLineTablesHandler(this);
    OffLineTablesHandler lineAlarmTableHandler = new OffLineTablesHandler(this);
    ArrayList<com.metalarm.model.Location> arrHomeLocations = new ArrayList<>();
    ArrayList<LineModel> arrListLine = new ArrayList<>();
    ArrayList<StationModel> arrListStation = new ArrayList<>();
    ArrayList<StationSelectedModel> arrListSelectedStation = new ArrayList<>();
    ArrayList<GetAllLineListModel> arrListGetAll = new ArrayList<>();
    private static final int REQUEST_WRITE_STORAGE = 112;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_station_list);
        getTokenInfo();
        setUp();
        getInfroFromDatabase();
        new getAllStations().execute();
        addPermision();
    }

    private void addPermision() {


    /*    int permissionCheck = ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (ContextCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(StationListActivity.this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    1);
*/
        // MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION is an
        // app-defined int constant. The callback method gets the
        // result of the request.
        //   }

        boolean hasPermission = (ContextCompat.checkSelfPermission(StationListActivity.this,
                android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(StationListActivity.this, new String[]{
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_WRITE_STORAGE);

        } else {
            MetalarmSync_Log.writeToLog("App start");
        }

        MetalarmSync_Log.writeToLog("App start");
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {

            case REQUEST_WRITE_STORAGE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {         //reload my activity with permission granted or use the features what required the permission-                } else {
                    // Toast.makeText(StationListActivity.this, "The app was not allowed to write to your storage. Hence, it cannot function properly. Please consider granting it this permission", Toast.LENGTH_LONG).show();

                } else {
                    aletDialog();
                }
            }
        }
    }


    private void getTokenInfo() {

        if (!mSessionManager.IsLoggedIn()) {
            Log.e(getLocalClassName(), "usernot logged in");
            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    new apiLogin().execute();
                }
            }, 3000);
        } else {
            Log.e(getLocalClassName(), "userlogged in");
        }
    }

    private void getInfroFromDatabase() {

        getLines();

    }

    private void getStation() {

        //  arrayListGetSelectedLineStation = DBstationHelper.getSelectedLineSattion(Integer.parseInt(selectedLineName)); // get all station for selected lines

    }

    private void getLines() {
        // set up all list first.
        setUpAllList();
        new apiLineList().execute();


    }

    // function set up list initially
    private void setUpAllList() {
        // for line list.
        line = new ArrayList<>();
        line.add("Select Line");
        ArrayAdapter aa = new ArrayAdapter(StationListActivity.this, R.layout.textview, line);
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spLine.setAdapter(aa);

        // for station list.
        Log.e(getLocalClassName(), " Array Loist size" + arrListStation.size());
        station = new ArrayList<>();
        station.add("Select Station");

        aa = new ArrayAdapter(StationListActivity.this, R.layout.textview, station);
        aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spStation.setAdapter(aa);

        ArrayAdapter aa1 = new ArrayAdapter(StationListActivity.this, R.layout.textview, direction);
        aa1.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
        spDirection.setAdapter(aa1);
    }

    private void setUp() {


        mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);
        assert toolbar_title != null;
        toolbar_title.setText("Set Alarm");


        ivSetting = (ImageView) findViewById(R.id.setting);
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(StationListActivity.this, SettingActitivty.class);
                startActivity(i);

            }
        });

        db = openOrCreateDatabase("database", Context.MODE_PRIVATE, null);
        spLine = (Spinner) findViewById(R.id.spLine);
        spLine.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                position--;
                if (position >= 0) {
                    selectedLineName = arrListLine.get(position).uid; // get line number
                    // selectedStationID = selectedStationId[position]; // get selectd line ID
                    //  Log.d("selected : ", "selected line : " + line[position] + "\nselected Station ID  : " + selectedStationID);
                    new apiStationList(arrListLine.get(position).uid).execute();
                } else {
                    selectedLineName = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spStation = (Spinner) findViewById(R.id.spStation);
        spStation.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //selectedStationName = station[position];
                position--;
                if (position >= 0) {
                    if (arrListStation.size() > 0) {
                        selectedStationID = arrListStation.get(position).station_id; // get ID of selected line and sattion
                        Log.d("statioin : ", "\nselected Station ID  : " + selectedStationID);
                    }
                } else {
                    selectedStationID = "";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spDirection = (Spinner) findViewById(R.id.spToward);
        // spDirection.setAdapter(new SpinnerAdapter(StationListActivity.this, R.layout.cust_spinner, direction));
        spDirection.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectedDirection = String.valueOf(1);
                } else {
                    selectedDirection = String.valueOf(0);
                }

                Log.d(Utils.TAG_DATABSE, "" + direction[position]);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        btnSetAlarm = (Button) findViewById(R.id.btnSetAlarm);
        btnSetAlarm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(selectedLineName)) {
                    Toast.makeText(StationListActivity.this, "Please select line to set alarm.", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(selectedStationID)) {
                    Toast.makeText(StationListActivity.this, "Please select station to set alarm.", Toast.LENGTH_SHORT).show();
                } else {
                    new asyncSetAlarm().execute();
                }
            }
        });

    }


    private String setAlarm() {

        String message;

        /*if (selectedDirection.equals("opposite"))  // get from sppiner click
            selectedDirection = "0";
        else
            selectedDirection = "1";*/

        boolean checkAlaram = checkAlarm(); // check alarm is set or not
        Log.d("station :", "IschaeckAlram : " + checkAlaram);
        if (checkAlaram) // alarm already set
            message = "Alarm set already";
        else {
            message = "Alarm set Successfully"; // if not set then insert data into alarm table

            Log.d("station : ", "old arraylist button click : " + arrListStation.size());
            arrListSelectedStation = new ArrayList<>();
            for (int i = 0; i < arrListStation.size(); i++) {

                if (arrListStation.get(i).station_id.equals(selectedStationID)) { // copy selected alarm data in to new arraylist in passit into service
                    Log.d("station : ", " in for loop  " + arrListStation.get(i).station_id + "  " + selectedStationID);
                    StationSelectedModel model = new StationSelectedModel();

                    String mapLat, mapLong;
                    String s_id = arrListStation.get(i).station_id;
                    String name = arrListStation.get(i).name;
                    String lineId = arrListStation.get(i).line_id;
                    String latitude = arrListStation.get(i).latitude;
                    String longitude = arrListStation.get(i).longitude;
                    if (selectedDirection.equals("1")) {
                        mapLat = arrListStation.get(i).right_latitude;
                        mapLong = arrListStation.get(i).right_longitude;

                        model.alarm_latitude = arrListStation.get(i).right_latitude;
                        model.alarm_longitude = arrListStation.get(i).right_longitude;
                    } else {
                        mapLat = arrListStation.get(i).left_latitude;
                        mapLong = arrListStation.get(i).left_longitude;

                        model.alarm_latitude = arrListStation.get(i).left_latitude;
                        model.alarm_longitude = arrListStation.get(i).left_longitude;
                    }
                    model.name = arrListStation.get(i).name;
                    model.station_id = arrListStation.get(i).station_id;
                    model.latitude = arrListStation.get(i).latitude;
                    model.longitude = arrListStation.get(i).longitude;
                    model.isAlarm = "false";


                    arrListSelectedStation.add(model);
                    Log.e("station ", "arr size : " + arrListSelectedStation.size());
                    Log.e("station ", "add data into new arraylist " + arrListStation.get(i).name);
                    DBAlarmTableHandler.insertSetAlarmData(s_id, name, lineId, mapLat, mapLong, latitude, longitude, "false", selectedDirection);

                    Log.d("AAA", "User ID : " + mSessionManager.getuserId());
                    Log.d("AAA", "station name : " + name);
                    Log.d("AAA", "station ID : " + s_id);
                    Log.d("AAA", "Line ID : " + lineId);
                    Log.d("AAA", "DevFice Token : " + General.PREF_DEVICE_TOKEN + " 2 : " + mSessionManager.getDeviceToken());

                    if (Utils.isNetworkAvailable(StationListActivity.this)) {
                        Log.e("111", "netwok available");
                        new apiAddRemoveAlarm(mSessionManager.getuserId(), name, s_id, lineId).execute();
                    } else {
                        Log.e("111", "netwok not available");
                        DBAddRemoveTableHandler.insertAddRemoveData(mSessionManager.getuserId(), s_id, lineId, "1");

                    }

                    Intent i1 = new Intent(getApplicationContext(), ListActivity.class);
                    startActivity(i1);
                    finish();
                }
            }


            //DBAlarmTableHandler.insertSetAlarmData(arrListStation.);
            loadData(); // added alarm data into arraylist

           /* Intent intent = new Intent(Utils.mBroadcastStationFix);
            intent.putParcelableArrayListExtra("arraylist", arrHomeLocations);
            sendBroadcast(intent);*/
            Log.e("start", "service");
            stopService(new Intent(this, MetraLocationService.class));
            startService(new Intent(this, MetraLocationService.class));
        }
        return message;

    }


    private boolean checkAlarm() { // check alarm if alarm is set or not

        Log.d("station :", " selected station ID : " + selectedStationID.toString());
        boolean b = DBAlarmTableHandler.checkAlarm(selectedStationID.toString(), selectedDirection);
        return b;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        arrGetAlarmData = DBAlarmTableHandler.getAlarmData();
        if (arrGetAlarmData.size() == 0) {
            finish();
        } else {
            Intent i1 = new Intent(getApplicationContext(), ListActivity.class);
            startActivity(i1);
            finish();
        }
        /*Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String newString = extras.getString("norecord");
            if (newString.equals("true")) {

            }
            else
            {
                finish();
            }

        }*/


    }

    @Override
    public void onConnected(Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }

    class asyncSetAlarm extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            /*if (isMyServiceRunning(LocationService.class) == false) {
                startService(new Intent(StationListActivity.this, LocationService.class));
            }*/
            String alarm = setAlarm(); // set alarm if not set then insert data into alarm table  and display message alarm set or not
            // loadData();
            return alarm;
        }

        @Override
        protected void onPostExecute(final String s) {
            final String msg = s;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    Toast.makeText(StationListActivity.this, msg, Toast.LENGTH_SHORT).show();
                }
            });


        }
    }


    private void aletDialog() {


        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        //Uncomment the below code to Set the message and title from the strings.xml file
        //builder.setMessage(R.string.dialog_message) .setTitle(R.string.dialog_title);

        //Setting message manually and performing action on button click
        builder.setMessage("You need to give all permission to use this app")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        addPermision();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        //  Action for 'NO' Button
                        //  dialog.cancel();
                        finish();
                    }
                });

        //Creating dialog box
        AlertDialog alert = builder.create();
        //Setting the title manually
        alert.setTitle(getString(R.string.app_name));
        alert.show();
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


    public void loadData() { //
        arrHomeLocations = new ArrayList<>();
        // arralistSetAlarmName = stationHelper.InsertedLastRecord(); // data inserted into last and added into arraylist

        Log.d("station  ", "size  arrListSelectedStation  : " + arrListSelectedStation.size());

        for (int i = 0; i < arrListSelectedStation.size(); i++) {
            com.metalarm.model.Location modelLocation = new com.metalarm.model.Location();
            modelLocation.lattitudeonMAp = arrListSelectedStation.get(i).latitude;
            modelLocation.logitudeonMAp = arrListSelectedStation.get(i).longitude;
            modelLocation.logitude = arrListSelectedStation.get(i).alarm_longitude;
            modelLocation.lattitude = arrListSelectedStation.get(i).alarm_latitude;
            modelLocation.sId = arrListSelectedStation.get(i).station_id;
            modelLocation.isAlarm = "false";
            arrHomeLocations.add(modelLocation);
            Log.d("station ", " size 2 " + arrHomeLocations.get(i).lattitude + "  " + arrHomeLocations.get(i).logitude + "  " + arrHomeLocations.get(i).lattitudeonMAp + "  " + arrHomeLocations.get(i).logitudeonMAp);
            Log.d("isAlarm : ", " isAlarm  : " + modelLocation.isAlarm + " id : " + modelLocation.sId);
        }
    }


    public class apiLineList extends AsyncTask<Void, Void, Void> {

        boolean error;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                if (Utils.isNetworkAvailable(StationListActivity.this)) {
                    ParsedResponse p = Soap.apiLine(StationListActivity.this);
                    error = p.error;
                    if (!error) {
                        arrListLine = (ArrayList<LineModel>) p.o;


                    } else {
                        msg = (String) p.o;
                    }
                } else {
                    // get from db as user is offline
                    arrListLine = oflineTablHander.getOfflineLines();
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
                Log.e(getLocalClassName(), " Array Loist size" + arrListLine.size());
                line.clear();
                line.add("Select Line");

                for (int i = 0; i < arrListLine.size(); i++) {
                    line.add(arrListLine.get(i).name);
                    Log.e(getLocalClassName(), arrListLine.get(i).name);
                }

                ArrayAdapter aa = new ArrayAdapter(StationListActivity.this, R.layout.textview, line);
                aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
                spLine.setAdapter(aa);

                lineAlarmTableHandler.deleteOflineLineTable(); // delete old record and added new
                for (int i = 0; i < arrListLine.size(); i++) {

                    lineAlarmTableHandler.insertLineData(arrListLine.get(i).uid, arrListLine.get(i).name);
                    Log.d("data", "Data inserted Line");
                }
                btnSetAlarm.setVisibility(View.VISIBLE);


            } else {
                Toast.makeText(StationListActivity.this, msg, Toast.LENGTH_SHORT).show();
                Log.e(getLocalClassName(), "" + msg);
                btnSetAlarm.setVisibility(View.VISIBLE);
            }
        }
    }

    public class apiStationList extends AsyncTask<Void, Void, Void> {

        boolean error;
        String msg;
        String selectedLine;

        public apiStationList(String strLineNumber) {
            selectedLine = strLineNumber;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.show();
//            progressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                if (Utils.isNetworkAvailable(StationListActivity.this)) {
                    ParsedResponse p = Soap.apiStation(StationListActivity.this, selectedLine);
                    error = p.error;
                    if (!error) {
                        arrListStation = (ArrayList<StationModel>) p.o;

                    } else {
                        msg = (String) p.o;
                    }
                } else {
                    arrListStation = oflineTablHander.getOffLineStationLines(selectedLineName);
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
            //createProgressDialog(this).dismiss();
            // progressDialog.dismiss();
            if (!error) {
                Log.e(getLocalClassName(), " Array Loist size" + arrListStation.size());
                station.clear();
                station.add("Select Station");

                for (int i = 0; i < arrListStation.size(); i++) {
                    //   line[i] = arrayListGetAllStation.get(i).getLine();
                    station.add(arrListStation.get(i).name);
                }
                btnSetAlarm.setVisibility(View.VISIBLE);
                //  direction = new String[]{"to downtown", "from downtown"};


            } else {
                Toast.makeText(StationListActivity.this, msg, Toast.LENGTH_SHORT).show();
            }
            ArrayAdapter aa = new ArrayAdapter(StationListActivity.this, R.layout.textview, station);
            aa.setDropDownViewResource(R.layout.simple_spinner_dropdown_item);
            spStation.setAdapter(aa);


            if (canGetLocation()) {

            } else {
                // showSettingsAlert();
                locationDialog();
            }
        }
    }

    private class getAllStations extends AsyncTask<Void, Void, Void> {
        boolean error;
        String msg;


        @Override
        protected void onPreExecute() {
            // mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                ParsedResponse p = Soap.apiAllStation(StationListActivity.this);
                error = p.error;
                if (!error) {
                    arrListGetAll = (ArrayList<GetAllLineListModel>) p.o;

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
                lineAlarmTableHandler.deleteOflineStationTable();
                for (int i = 0; i < arrListGetAll.size(); i++) {
                    for (int j = 0; j < arrListGetAll.get(i).station.size(); j++) {
                        Log.d("TTT", "in bith for loop " + arrListGetAll.get(i).station.get(j).name);

                        String uid = arrListGetAll.get(i).station.get(j).uid;
                        String name1 = arrListGetAll.get(i).station.get(j).name;
                        String address1 = arrListGetAll.get(i).station.get(j).address;
                        String status1 = arrListGetAll.get(i).station.get(j).status;
                        String date1 = arrListGetAll.get(i).station.get(j).date;
                        String line_id = arrListGetAll.get(i).station.get(j).line_id;
                        String latitude = arrListGetAll.get(i).station.get(j).latitude;
                        String longitude = arrListGetAll.get(i).station.get(j).longitude;
                        String left_latitude = arrListGetAll.get(i).station.get(j).left_latitude;
                        String left_longitude = arrListGetAll.get(i).station.get(j).left_longitude;
                        String right_latitude = arrListGetAll.get(i).station.get(j).right_latitude;
                        String right_longitude = arrListGetAll.get(i).station.get(j).right_longitude;


                        lineAlarmTableHandler.insertOflineStationData(uid, name1, address1, status1, date1,
                                line_id, latitude, longitude, left_latitude,
                                left_longitude, right_latitude, right_longitude);
                    }

                    btnSetAlarm.setVisibility(View.VISIBLE);
                }
                Log.e(getLocalClassName(), "" + error);
            } else {
                if (lineAlarmTableHandler.getOfflineLines().size() == 0) {
                    //  btnSetAlarm.setVisibility(View.GONE);

                }
                //Toast.makeText(StationListActivity.this, msg, Toast.LENGTH_SHORT).show();

            }


        }
    }

    public class apiLogin extends AsyncTask<Void, Void, Void> {

        boolean error;
        String msg;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Log.e("token", "  List activity api " + mSessionManager.getDeviceToken());
                ParsedResponse p = Soap.apiLogin(StationListActivity.this, mSessionManager.getDeviceToken());
                error = p.error;
                if (!error) {
                    loginModel = (LoginModel) p.o;

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
            //   mProgressDialog.dismiss();
            //  progressDialog.dismiss();
            if (!error) {

                if (TextUtils.isEmpty(mSessionManager.getDeviceToken())) {

                }
                // Log.e(getLocalClassName(), " Array Loist size" + arrList.size());
                else {
                    mSessionManager.login(loginModel);
                    //Toast.makeText(ListActivity.this, "Success", Toast.LENGTH_SHORT).show();
                }
                btnSetAlarm.setVisibility(View.VISIBLE);

            } else {
                Log.e(getLocalClassName(), "" + msg);
                //showDialog(msg);sdfsdf
                //btnSetAlarm.setVisibility(View.GONE);
            }
        }
    }

    public void locationDialog() {
        Context context = this;
        GoogleApiClient googleApiClient = null;
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(this)
                    .addApi(LocationServices.API)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this).build();
            googleApiClient.connect();

            LocationRequest locationRequest = LocationRequest.create();
            locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            locationRequest.setInterval(30 * 1000);
            locationRequest.setFastestInterval(5 * 1000);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                    .addLocationRequest(locationRequest);

            //**************************
            builder.setAlwaysShow(true); //this is the key ingredient
            //**************************

            PendingResult<LocationSettingsResult> result =
                    LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
            result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
                @Override
                public void onResult(LocationSettingsResult result) {
                    final Status status = result.getStatus();
                    final LocationSettingsStates state = result.getLocationSettingsStates();
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            // All location settings are satisfied. The client can initialize location
                            // requests here.
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                            // Location settings are not satisfied. But could be fixed by showing the user
                            // a dialog.
                            try {
                                // Show the dialog by calling startResolutionForResult(),
                                // and check the result in onActivityResult().
                                status.startResolutionForResult(StationListActivity.this, 1000);

                            } catch (IntentSender.SendIntentException e) {
                                // Ignore the error.
                            }
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            // Location settings are not satisfied. However, we have no way to fix the
                            // settings so we won't show the dialog.
                            break;
                    }
                }
            });
        }
    }

    private class apiAddRemoveAlarm extends AsyncTask<Void, Void, Void> {
        boolean error;
        String msg;
        String s, name, s_id, lineId;


        public apiAddRemoveAlarm(String s, String name, String s_id, String lineId) {
            super();

            this.s = s;
            this.name = name;
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
                Log.e("AAA", "Device token : " + mSessionManager.getDeviceToken());
                ParsedResponse p = Soap.apiAddRemoveAlarm(StationListActivity.this, mSessionManager.getuserId(), mSessionManager.getDeviceToken(), s_id, lineId, "1", "0");
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
                Toast.makeText(StationListActivity.this, msg, Toast.LENGTH_SHORT).show();

            }


        }
    }


}
