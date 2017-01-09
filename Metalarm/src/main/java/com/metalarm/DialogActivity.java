package com.metalarm;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.metalarm.adapters.DetailAdapterDialog;
import com.metalarm.database.AlarmHitsHandler;
import com.metalarm.model.AdvertismentModel;
import com.metalarm.model.detailModel;
import com.metalarm.utils.ParsedResponse;
import com.metalarm.utils.Soap;
import com.metalarm.utils.Utils;

import org.json.JSONException;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

public class DialogActivity extends BaseActivity {
    MediaPlayer mp;
    private Vibrator mVibrator;
    ArrayList<AdvertismentModel> arrListAdvertisement = new ArrayList<>();
    ListView lv;
    ProgressDialog progressDialog;
    DetailAdapterDialog adapter;
    ArrayList<detailModel> mArrayList = new ArrayList<>();
    ImageView txt;
    private ImageView img;
    private TextView txtname, txtdata;
    private LinearLayout mainlayout;
    AlarmHitsHandler DBAlarmHitsHandler = new AlarmHitsHandler(this);
    com.metalarm.model.Location alarmdata;

    String alarmStation = "";

    private Handler mHandler = new Handler();
    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {
            stopAlarm(true);
            notifyUser();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // requestWindowFeature(Window.FEATURE_NO_TITLE);
        // getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON |
                WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD |
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED |
                WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.cust_dialog_notification);


        getDataFromIntent(getIntent());


        lv = (ListView) findViewById(R.id.listView);
        txt = (ImageView) findViewById(R.id.txt);
        img = (ImageView) findViewById(R.id.img);
        txtname = (TextView) findViewById(R.id.txtname);
        mainlayout = (LinearLayout) findViewById(R.id.mainlayout);
        txtdata = (TextView) findViewById(R.id.txtdata);

        txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Intent.ACTION_VIEW,
                        Uri.parse("http://www.webcomservicesinc.com/"));
                startActivity(i);
                if (mp.isPlaying()) {
                    mp.stop();
                    mp.release();

                }
                if (mVibrator.hasVibrator()) {
                    mVibrator.cancel();
                }
            }
        });

        new apiAdverisement().execute();
        mp = MediaPlayer.create(this, R.raw.sound);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.parseColor("#256aad"));
        }

        new AlarmTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

        Button btnCustYesButton = (Button) findViewById(R.id.btnCustYesButton);
        btnCustYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopAlarm(false);
            }
        });

        checkNetwork();

        mHandler.postDelayed(mRunnable, 60000);
    }


    public void notifyUser() {

        Intent intent = new Intent(DialogActivity.this, ListActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(DialogActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder b = new NotificationCompat.Builder(DialogActivity.this);

        Bitmap icon = BitmapFactory.decodeResource(getResources(),
                R.drawable.app_icon);
        b.setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.app_icon)
                // .setTicker("Hearty365")
                .setContentTitle(getResources().getString(R.string.app_name))
                .setLargeIcon(Bitmap.createScaledBitmap(icon, 128, 128, false))
                .setContentText("you've missed " + alarmStation + " ")
                .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_SOUND)
                .setContentIntent(contentIntent);
        //.setContentInfo("Info");


        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, b.build());

    }

    private void stopAlarm(boolean isAutomaticStop) {

        Log.d("button", "click");
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();

            }
            if (mVibrator.hasVibrator()) {
                mVibrator.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
//                new apiAddAlarmNotification().execute();
//                Intent i = new Intent(DialogActivity.this, DetailsActivity.class);
//                i.putExtra("alarm_data", alarmdata);
        if (!isAutomaticStop) {
            Intent i = new Intent(DialogActivity.this, ListActivity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }

        finish();

    }

    @Override
    public void finish() {
        mHandler.removeCallbacks(mRunnable);
        super.finish();
    }

    private void checkNetwork() {

        DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        String date = df.format(Calendar.getInstance().getTime());

        Log.e("WWW", "Station ID : " + alarmdata.sId + "  Line ID : " + alarmdata.line_id);
        Log.e("WWW", " date : " + date);
        Log.e("WWW", " User ID : " + mSessionManager.getuserId());
        if (Utils.isNetworkAvailable(DialogActivity.this)) {
            Log.e("WWW", "\n\n Network Available");

            new apiAlarmHits(alarmdata.sId, alarmdata.line_id, date).execute();
        } else {
            Log.e("WWW", "\n\n Network Not Available");
            DBAlarmHitsHandler.insertAlarmHitData(mSessionManager.getuserId(), mSessionManager.getDeviceToken(), alarmdata.sId, alarmdata.line_id, date);
            Log.e("WWW", "Alam Hits Table Size : " + DBAlarmHitsHandler.getAlarmHitsData().size());
        }
    }

    @Override
    public void onBackPressed() {

    }

    private void getDataFromIntent(Intent intent) {
        alarmdata = intent.getParcelableExtra("alarm_data");
        txtdata = (TextView) findViewById(R.id.txtdata);
        alarmStation = alarmdata.Address + " (" + alarmdata.getDirection() + ")";
        txtdata.setText(alarmStation.concat(" is approaching"));
    }


    public class apiAdverisement extends AsyncTask<Void, Void, Void> {

        boolean error;
        String msg;
        String selectedLine;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Log.d("AAA", "get pref station ID :" + mSessionManager.getPrefSattionID() + " date  : " + (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new java.util.Date())));
                ParsedResponse p = Soap.apiAdvertisement(DialogActivity.this, mSessionManager.getPrefSattionID(), (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new java.util.Date())));
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
            if (!error) {
                Log.e(getLocalClassName(), " Array Loist size" + arrListAdvertisement.size());

                setUpArrayList();
            } else {
                Log.e(getLocalClassName(), "" + msg);
            }
            new apiAddAlarmNotification().execute();
        }
    }

    private void setUpArrayList() {

        if (arrListAdvertisement.size() == 1) {
            lv.setVisibility(View.GONE);
            mainlayout.setVisibility(View.VISIBLE);
            Glide.with(DialogActivity.this)
                    .load(Soap.BASE_URL + arrListAdvertisement.get(0).image)
                    .into(img);
            txtname.setText(arrListAdvertisement.get(0).title);
            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openInBrowser(arrListAdvertisement.get(0).banner_url);
                }
            });
        } else {
            lv.setVisibility(View.VISIBLE);
            mainlayout.setVisibility(View.GONE);
            for (int i = 0; i < arrListAdvertisement.size(); i++) {
                detailModel model = new detailModel();
                model.drw = Soap.BASE_URL + arrListAdvertisement.get(i).image;
                model.name = arrListAdvertisement.get(i).title;
                model.desc = arrListAdvertisement.get(i).description;
                model.banner_url = arrListAdvertisement.get(i).banner_url;
                mArrayList.add(model);
            }
            adapter = new DetailAdapterDialog(DialogActivity.this, mArrayList);
            lv.setAdapter(adapter);
            lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    openInBrowser(mArrayList.get(position).banner_url);
                }
            });
        }
    }

    private void openInBrowser(String banner_url) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(banner_url));
        startActivity(i);
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (mp.isPlaying()) {
                mp.stop();
                mp.release();

            }
            if (mVibrator.hasVibrator()) {
                mVibrator.cancel();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public class apiAddAlarmNotification extends AsyncTask<Void, Void, Void> {

        boolean error;
        String msg;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                Log.d("AAA", "get pref station ID :" + mSessionManager.getPrefSattionID() + " date  : " + (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new java.util.Date())));
                ParsedResponse p = Soap.apiAddAlarmNotification(DialogActivity.this, mSessionManager.getuserId(), mSessionManager.getPrefSattionID(), (new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new java.util.Date())));
                error = p.error;
                if (!error) {
                    msg = (String) p.o;

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
            if (!error) {
                Log.e(getLocalClassName(), "" + msg);
            } else {
                Log.e(getLocalClassName(), "" + msg);
            }
        }
    }

    private class AlarmTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            while (!getWindow().isActive()) {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (mSessionManager.prefGetSound()) {
                mp.start();
                mp.setLooping(true);
            }
            if (mSessionManager.prefGetVib()) {
                mVibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Start without a delay
                // Vibrate for 100 milliseconds
                // Sleep for 1000 milliseconds
                long[] pattern = {0, 700, 1000};
                mVibrator.vibrate(pattern, 0);

            }
        }
    }

    private class apiAlarmHits extends AsyncTask<Void, Void, Void> {
        boolean error;
        String msg;
        String s_id, lineId, date;


        public apiAlarmHits(String s_id, String lineId, String date) {
            super();


            this.s_id = s_id;
            this.lineId = lineId;
            this.date = date;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                Soap.apiAlarmHit(DialogActivity.this, mSessionManager.getuserId(), s_id, lineId, "0", date);


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
            if (error) {
                Toast.makeText(DialogActivity.this, msg, Toast.LENGTH_SHORT).show();


            }

        }
    }

}
