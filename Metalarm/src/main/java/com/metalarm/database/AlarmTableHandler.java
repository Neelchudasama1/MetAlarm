package com.metalarm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.metalarm.model.Location;
import com.metalarm.model.getSetAlarmData;

import java.util.ArrayList;

/**
 * Created by qtm-purvesh on 18/4/16.
 */
public class AlarmTableHandler extends SQLiteHelper {

    public static final String KEY_TIMESTAMP = "timestamp";
    public static final String KEY_UID = "uid";
    public static String KEY_LINE = "line";
    public static String KEY_NAME = "name";
    public static String KEY_LINE_ID = "line_id";
    public static String KAY_LATITUDE = "latitude";
    public static String KEY_LONGITUDE = "longitude";
    public static String KEY_MAP_LATITUDE = "maplatitude";
    public static String KEY_MAP_LONGITUDE = "maplongitude";

    public AlarmTableHandler(Context mContext) {
        super(mContext);
        // TODO Auto-generated constructor stub
    }

    public boolean checkAlarm(String id, String direction) {
        String query = "select * from " + SET_ALARM_TABLE + " where " + station_id + " = " + id + " and direction = " + direction + "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if (cursor.getCount() == 0)
            return false;
        else
            return true;

    }

    public void insertSetAlarmData(String station_id2, String name2, String line_id2,
                                   String latitude2, String longitude2, String maplatitude2,
                                   String maplongitude2, String isAlarm, String direction) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(station_id, station_id2);
        values.put(KEY_NAME, name2);
        values.put(KEY_LINE_ID, line_id2);
        values.put(KAY_LATITUDE, latitude2); // alarm's left or right latitude.
        values.put(KEY_LONGITUDE, longitude2); // alarm's left or right longitude.
        values.put(KEY_MAP_LATITUDE, maplatitude2); // station's left or right latitude.
        values.put(KEY_MAP_LONGITUDE, maplongitude2); // station's left or right longitude.
        values.put("direction", direction);
        values.put("isAlarm", isAlarm);
        values.put(KEY_TIMESTAMP, 0);
        db.insert(SET_ALARM_TABLE, null, values);
        db.close();
    }

    public void deleteAlarm(int id, String direction) {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.execSQL("delete from alarm_table where a_id = "+id+"");
        db.delete(SET_ALARM_TABLE, "" + station_id + "" + "=" + id + " AND " + "direction='" + direction + "'", null);
        db.close();
    }

    public ArrayList<getSetAlarmData> getAlarmData() {
        ArrayList<getSetAlarmData> arrayList = new ArrayList<>();
        String query = "select * from " + SET_ALARM_TABLE + "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        getSetAlarmData model;
        if (cursor.moveToFirst()) {
            do {
                model = new getSetAlarmData();
                model.uid = (cursor.getString(cursor.getColumnIndex(KEY_UID)));
                model.stationName = (cursor.getString(cursor.getColumnIndex(KEY_NAME)));
                model.lati = (cursor.getString(cursor.getColumnIndex(KAY_LATITUDE))); // alarm's left or right latitude.
                model.longi = (cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE))); // alarm's left or right longitude.
                model.mapLat = (cursor.getString(cursor.getColumnIndex(KEY_MAP_LATITUDE))); // station's left or right latitude.
                model.mapLong = (cursor.getString(cursor.getColumnIndex(KEY_MAP_LONGITUDE))); // station's left or right longitude.
                model.sId = (cursor.getString(cursor.getColumnIndex("station_id")));
                model.isAlarm = (cursor.getString(cursor.getColumnIndex("isAlarm")));
                model.direction = (cursor.getString(cursor.getColumnIndex("direction")));
                model.timeInMillis = (cursor.getLong(cursor.getColumnIndex(KEY_TIMESTAMP)));
                model.line_id = (cursor.getString(cursor.getColumnIndex(KEY_LINE_ID)));
                arrayList.add(model);
            } while (cursor.moveToNext());
        }
        return arrayList;

    }

    public getSetAlarmData getAlarmData(Location location) {
        String query = "select * from " + SET_ALARM_TABLE + " where " + KEY_UID + " = '" + location.uid + "'";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        getSetAlarmData model = new getSetAlarmData();
        if (cursor.moveToFirst()) {
            model.uid = (cursor.getString(cursor.getColumnIndex(KEY_UID)));
            model.stationName = (cursor.getString(cursor.getColumnIndex(KEY_NAME)));
            model.lati = (cursor.getString(cursor.getColumnIndex(KAY_LATITUDE))); // alarm's left or right latitude.
            model.longi = (cursor.getString(cursor.getColumnIndex(KEY_LONGITUDE))); // alarm's left or right longitude.
            model.mapLat = (cursor.getString(cursor.getColumnIndex(KEY_MAP_LATITUDE))); // station's left or right latitude.
            model.mapLong = (cursor.getString(cursor.getColumnIndex(KEY_MAP_LONGITUDE))); // station's left or right longitude.
            model.sId = (cursor.getString(cursor.getColumnIndex("station_id")));
            model.isAlarm = (cursor.getString(cursor.getColumnIndex("isAlarm")));
            model.direction = (cursor.getString(cursor.getColumnIndex("direction")));
            model.timeInMillis = (cursor.getLong(cursor.getColumnIndex(KEY_TIMESTAMP)));
        }
        return model;
    }

    public void updateAlarm(String id, String s) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("isAlarm", s); //These Fields should be your String values of actual column names
        db.update(SET_ALARM_TABLE, cv, station_id + "=" + id, null);
    }

    public void updateAlarmTimeStamp(Location location, long timeStamp) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put(KEY_TIMESTAMP, timeStamp); //These Fields should be your String values of actual column names
        db.update(SET_ALARM_TABLE, cv, station_id + "=" + location.sId, null);
    }


}
