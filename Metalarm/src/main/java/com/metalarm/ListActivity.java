package com.metalarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.metalarm.adapters.mainGridAdapter;
import com.metalarm.database.AlarmTableHandler;
import com.metalarm.model.getSetAlarmData;
import com.metalarm.model.mainGridModel;
import com.metalarm.services.AlarmReceiver;
import com.metalarm.services.MetraLocationService;
import com.metalarm.utils.Utils;

import java.util.ArrayList;

public class ListActivity extends BaseActivity {

    Button btnAddMore;
    ImageView ivSetting;
    GridView gridView;
    AlarmTableHandler alarmHandler = new AlarmTableHandler(this);
    ArrayList<getSetAlarmData> arrGetAlarmData = new ArrayList<>();
    mainGridAdapter adapter;
    private ArrayList<mainGridModel> mArrListStation = new ArrayList<>();

    public static String stationname;
    public String IsRecord = "false";

    private AlarmManager alarmManager;
    private PendingIntent alarmIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);


        // code to keep service alive.
        //Intent intent = new Intent(this, AlarmReceiver.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(
        //        this.getApplicationContext(), 234324243, intent, 0);
        //AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
        //        + (3 * 1000), pendingIntent);

        // code to keep service alive.
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.cancel(PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReceiver.class), 0));
        alarmIntent = PendingIntent.getBroadcast(this, 0, new Intent(this, AlarmReceiver.class), 0);
        alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                15000, 15000, alarmIntent);

        setUp();
        startService(new Intent(ListActivity.this, MetraLocationService.class));

    }

    private void setUp() {


        Toolbar mActionBarToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mActionBarToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        TextView toolbar_title = (TextView) findViewById(R.id.toolbar_title);

        toolbar_title.setText("Alarm List");
        Log.e(getLocalClassName(), "user ID : " + mSessionManager.getuserId());

        btnAddMore = (Button) findViewById(R.id.btnAddMoreStation);
        gridView = (GridView) findViewById(R.id.gridView);

        ivSetting = (ImageView) findViewById(R.id.setting);
        ivSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(ListActivity.this, SettingActitivty.class);
                startActivity(i);

            }
        });

        setDataintogridView();

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Utils.ALARM_ID = Integer.parseInt(arrGetAlarmData.get(position).sId);
                Utils.Direction = arrGetAlarmData.get(position).direction;
                Log.d("alarm ID", "" + Utils.ALARM_ID + "\n station latitude : " + arrGetAlarmData.get(position).lati);
                Log.d("alarm ID ", "station longi " + arrGetAlarmData.get(position).longi);

                Log.d("AAA", "User ID : " + mSessionManager.getuserId());
                Log.d("AAA", "station name : " + arrGetAlarmData.get(position).stationName);
                Log.d("AAA", "station ID : " + arrGetAlarmData.get(position).sId);
                Log.d("AAA", "Line ID : " + arrGetAlarmData.get(position).line_id);

                Utils.GRIDVIEW_POSITION = position;
                Intent i = new Intent(getApplicationContext(), MainActivity.class);
                i.putExtra("alarm_data", arrGetAlarmData.get(position));
                stationname = arrGetAlarmData.get(position).stationName;
                startActivity(i);
                finish();
            }
        });

        btnAddMore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                IsRecord = "true";
                gotoSetAlarmPage(); // if there is a no alarm set

            }
        });
    }

    private void gotoSetAlarmPage() {
        Intent i = new Intent(getApplicationContext(), StationListActivity.class);
        i.putExtra("norecord", IsRecord);
        startActivity(i);
        finish();
    }

    private void setDataintogridView() {

        arrGetAlarmData = alarmHandler.getAlarmData();

        if (arrGetAlarmData.size() == 0) {
            IsRecord = "false";
            gotoSetAlarmPage(); // if there is a no alarm set
        } else {
            for (int i = 0; i < arrGetAlarmData.size(); i++) {
                // gridNames[i] = arrGetAlarmData.get(i).stationName;
                mainGridModel model = new mainGridModel();
                model.names = arrGetAlarmData.get(i).stationName;
                model.directoin = arrGetAlarmData.get(i).direction;

                mArrListStation.add(model);

            }

            adapter = new mainGridAdapter(ListActivity.this, mArrListStation);
            gridView.setAdapter(adapter);
        }

    }


    public void showDialog(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg)
                .setCancelable(false)
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        finish();
                    }
                })
        ;
        AlertDialog alert = builder.create();
        alert.show();
    }
}
