package com.metalarm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.metalarm.model.AlarmHitsModel;
import com.metalarm.model.addRemoveAlarmModel;
import com.metalarm.utils.General;

import java.util.ArrayList;

/**
 * Created by latitude on 5/9/16.
 */
public class AlarmHitsHandler extends SQLiteHelper {

    private Context mContext;
    public static String ALARM_HITS_TABLE="alarm_hits_table";
    public static String KEY_UID = "uid";
    public static String DEVIC_TOKEN = "device_token";
    public static String STATION_ID = "s_id";
    public static String LINE_ID = "l_id";
    public static String DATE_TIME = "date_time";

    public AlarmHitsHandler(Context mContext) {
        super(mContext);
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
    }

    public void insertAlarmHitData(String KEY_UID ,String DEVIC_TOKEN ,String STATION_ID ,String LINE_ID ,String DATE_TIME )
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(this.KEY_UID, KEY_UID);
        values.put(this.DEVIC_TOKEN, DEVIC_TOKEN);
        values.put(this.STATION_ID, STATION_ID);
        values.put(this.LINE_ID, LINE_ID);
        values.put(this.DATE_TIME, DATE_TIME);


        db.insert(ALARM_HITS_TABLE, null, values);

        db.close();
    }

    public ArrayList<AlarmHitsModel> getAlarmHitsData() {
        ArrayList<AlarmHitsModel> arrayList = new ArrayList<>();
        String query = "select * from " + ALARM_HITS_TABLE + "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        AlarmHitsModel model;
        if (cursor.moveToFirst()) {
            do {
                model = new AlarmHitsModel();
                model.KEY_UID = (cursor.getString(cursor.getColumnIndex(KEY_UID)));
                model.DEVIC_TOKEN = (cursor.getString(cursor.getColumnIndex(DEVIC_TOKEN)));
                model.LINE_ID = (cursor.getString(cursor.getColumnIndex(LINE_ID)));
                model.STATION_ID = (cursor.getString(cursor.getColumnIndex(STATION_ID)));
                model.DATE_TIME = (cursor.getString(cursor.getColumnIndex(DATE_TIME)));

                arrayList.add(model);
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    public void deleteAlarmHitsData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + ALARM_HITS_TABLE + "";
        db.execSQL(query);
        db.close();
    }


}
