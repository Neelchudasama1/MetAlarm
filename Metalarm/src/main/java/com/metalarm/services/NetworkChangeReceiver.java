package com.metalarm.services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.metalarm.MyApplication;
import com.metalarm.database.AddRemoveAlarmtableHandler;
import com.metalarm.database.AlarmHitsHandler;
import com.metalarm.model.AlarmHitsModel;
import com.metalarm.model.addRemoveAlarmModel;
import com.metalarm.utils.General;
import com.metalarm.utils.ParsedResponse;
import com.metalarm.utils.SessionManager;
import com.metalarm.utils.Soap;
import com.metalarm.utils.Utils;

import org.json.JSONException;

import java.util.ArrayList;

public class NetworkChangeReceiver extends BroadcastReceiver {


    AddRemoveAlarmtableHandler DBAddRemoveTableHandler;
    AlarmHitsHandler DBAlarmHItsHandler;
    public SessionManager mSessionManager;
    Context mContext;

  //  MyApplication myContextManager = ((MyApplication) getApplicationContext());
    //Context context = myContextManager.getContext();



    String getTag() {
        return NetworkChangeReceiver.class.getSimpleName();
    }


    @Override
    public void onReceive(final Context context, final Intent intent) {
        mSessionManager = new SessionManager(context.getApplicationContext());
        DBAddRemoveTableHandler = new AddRemoveAlarmtableHandler(context.getApplicationContext());
        DBAlarmHItsHandler = new AlarmHitsHandler(context.getApplicationContext());
        mContext = context;
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        boolean isConnected = connectivityManager.getActiveNetworkInfo() != null
                && connectivityManager.getActiveNetworkInfo().isConnected();
        Log.e("BBB", "Internet connected: " + isConnected);
        if (isConnected) {
            Intent service = new Intent(context, MetraLocationService.class);
            context.startService(service);

            sendDataToApi();

        } else {
            Intent service = new Intent(context, MetraLocationService.class);
            context.startService(service);
        }
    }

    private void sendDataToApi() {

        sendAddRemoveData();
        sendAlarmHitData();


    }

    private void sendAlarmHitData() {

        ArrayList<AlarmHitsModel> arrayList = DBAlarmHItsHandler.getAlarmHitsData();
        Log.e("222", " Local database size (Alarm Hits : )" + arrayList.size());

        for (int i = 0; i < arrayList.size(); i++) {
            Log.e("222", "Api called AlarmHIts: " + i);
            Log.e("222" , "ID : " + arrayList.get(i).STATION_ID + "   status : " + arrayList.get(i).LINE_ID);
            new apiAlarmHits(arrayList.get(i).STATION_ID, arrayList.get(i).LINE_ID,arrayList.get(i).DATE_TIME,""+i).execute();
        }

       DBAlarmHItsHandler.deleteAlarmHitsData();
    }

    private void sendAddRemoveData() {
        ArrayList<addRemoveAlarmModel> arrayList = DBAddRemoveTableHandler.getAddRemoveAlarmData();
        Log.e("222", " Local database size (Add remove : )" + arrayList.size());

        for (int i = 0; i < arrayList.size(); i++) {
            Log.e("222", "Api called Add Remove: " + i);
            Log.e("222" , "ID : " + arrayList.get(i).STATION_ID + "   status : " + arrayList.get(i).STATUS);
            new apiAddRemoveAlarm(mSessionManager.getuserId(), arrayList.get(i).STATION_ID, arrayList.get(i).LINE_ID,arrayList.get(i).STATUS,""+i).execute();
        }

        DBAddRemoveTableHandler.deleteAddRemoveAlarmData();

    }

    private class apiAddRemoveAlarm extends AsyncTask<Void, Void, Void> {
        boolean error;
        String msg;
        String s, name, s_id, lineId,status,index;


        public apiAddRemoveAlarm(String s, String s_id, String lineId,String status,String index) {
            super();

            this.s = s;
            this.s_id = s_id;
            this.lineId = lineId;
            this.status = status;
            this.index = index;
        }

        @Override
        protected void onPreExecute() {
            // mProgressDialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {


            try {
                ParsedResponse p = Soap.apiAddRemoveAlarm(mContext,mSessionManager.getuserId(), mSessionManager.getDeviceToken(), s_id, lineId,status, index);
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

    }
    private class apiAlarmHits extends AsyncTask<Void, Void, Void> {
        boolean error;
        String msg;
        String  s_id, lineId,date;
        String index;


        public apiAlarmHits(String s_id, String lineId,String date,String index) {
            super();


            this.s_id = s_id;
            this.lineId = lineId;
            this.date = date;
            this.index = index;
        }

        @Override
        protected void onPreExecute() {
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {

                Soap.apiAlarmHit(mContext,mSessionManager.getuserId(), s_id, lineId, index, date);


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


        }
    }

}
