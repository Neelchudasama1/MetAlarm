package com.metalarm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.metalarm.model.LatLogModel;
import com.metalarm.model.tableStationModel;

import java.util.ArrayList;

/**
 * Created by qtm-purvesh on 18/4/16.
 */
public class StationFixTableHandler extends SQLiteHelper {

    public static String TABLE_STATION  = "station_fix_table";

    public static String KEY_SATION_LATITUDE = "station_lat";
    public static String KEY_STATION_LONGITUDE = "station_lon";
    public static String KEY_TOWARD_LATITUDE = "towards_lat";
    public static String KEY_TOWARD_LONGITUDE = "towards_lon";
    public static String KRY_OPPOSITE_LATITUDE = "opp_lat";
    public static String KEY_OPPOSITE_LONGITUDE = "opp_lon";
    public static String KEY_LINE = "line";
    public static String KEY_NAME = "name";


    public StationFixTableHandler(Context mContext) {
        super(mContext);
        // TODO Auto-generated constructor stub
    }


    public void createStation(tableStationModel model) {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(KEY_NAME, model.getName());
        values.put(KEY_SATION_LATITUDE, model.getStationLat());
        values.put(KEY_STATION_LONGITUDE, model.getStationLon());
        values.put(KEY_TOWARD_LATITUDE, model.getTowardsLat());
        values.put(KEY_TOWARD_LONGITUDE, model.getTowardsLon());
        values.put(KRY_OPPOSITE_LATITUDE, model.getOppLat());
        values.put(KEY_OPPOSITE_LONGITUDE, model.getOppLon());
        values.put(KEY_LINE, model.getLine());

        // insert book
        db.insert(TABLE_STATION, null, values);

        // close database transaction
        db.close();
    }


    public boolean IsRecored() {
        String query = "select * from " + TABLE_STATION;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        Log.d("database", "" + cursor.getCount());
        if (cursor.getCount() > 0)
            return true;
        else {
            return false;
        }

    }

    public ArrayList<tableStationModel> getAllStation() {
        ArrayList<tableStationModel> arrayList = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_STATION;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        tableStationModel model;
        if (cursor.moveToFirst()) {
            do {
                model = new tableStationModel();
                model.setId(cursor.getInt(cursor.getColumnIndex("s_id")));
                model.setName(cursor.getString(cursor.getColumnIndex("name")));
                model.setStationLat(cursor.getString(cursor.getColumnIndex("station_lat")));
                model.setStationLon(cursor.getString(cursor.getColumnIndex("station_lon")));
                model.setTowardsLat(cursor.getString(cursor.getColumnIndex("towards_lat")));
                model.setTowardsLon(cursor.getString(cursor.getColumnIndex("towards_lon")));
                model.setOppLat(cursor.getString(cursor.getColumnIndex("opp_lat")));
                model.setOppLon(cursor.getString(cursor.getColumnIndex("opp_lon")));
                model.setLine(cursor.getString(cursor.getColumnIndex("line")));
                arrayList.add(model);
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    public ArrayList<tableStationModel> getSelectedLineSattion(int line) {
        ArrayList<tableStationModel> arrayList = new ArrayList<>();
        String query = "select * from station_fix_table where line = " + line + "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        tableStationModel model;
        if (cursor.moveToFirst()) {
            do {
                model = new tableStationModel();
                model.setId(cursor.getInt(cursor.getColumnIndex("s_id")));
                model.setName(cursor.getString(cursor.getColumnIndex("name")));
                model.setStationLat(cursor.getString(cursor.getColumnIndex("station_lat")));
                model.setStationLon(cursor.getString(cursor.getColumnIndex("station_lon")));
                model.setTowardsLat(cursor.getString(cursor.getColumnIndex("towards_lat")));
                model.setTowardsLon(cursor.getString(cursor.getColumnIndex("towards_lon")));
                model.setOppLat(cursor.getString(cursor.getColumnIndex("opp_lat")));
                model.setOppLon(cursor.getString(cursor.getColumnIndex("opp_lon")));
                model.setLine(cursor.getString(cursor.getColumnIndex("line")));
                arrayList.add(model);
            } while (cursor.moveToNext());
        }
        return arrayList;

    }

    public ArrayList<tableStationModel> setAlarmStationName() {
        ArrayList<tableStationModel> arrayList = new ArrayList<>();
        String query = "select * from alarm_table";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor1 = db.rawQuery(query, null);
        tableStationModel model;
        if (cursor1.moveToFirst()) {
            do {
                model = new tableStationModel();
                model.setAlarmID(cursor1.getInt(cursor1.getColumnIndex("a_id")));
                int s_id = cursor1.getInt(cursor1.getColumnIndex("s_id"));
                String query2 = "select * from station_fix_table where s_id = " + s_id + "";
                Cursor cursor2 = db.rawQuery(query2, null);
                if (cursor2.moveToFirst()) {
                    do {

                        model.setId(cursor2.getInt(cursor2.getColumnIndex("s_id")));
                        model.setName(cursor2.getString(cursor2.getColumnIndex("name")));
                        model.setStationLat(cursor2.getString(cursor2.getColumnIndex("station_lat")));
                        model.setStationLon(cursor2.getString(cursor2.getColumnIndex("station_lon")));
                        model.setTowardsLat(cursor2.getString(cursor2.getColumnIndex("towards_lat")));
                        model.setTowardsLon(cursor2.getString(cursor2.getColumnIndex("towards_lon")));
                        model.setOppLat(cursor2.getString(cursor2.getColumnIndex("opp_lat")));
                        model.setOppLon(cursor2.getString(cursor2.getColumnIndex("opp_lon")));
                        model.setLine(cursor2.getString(cursor2.getColumnIndex("line")));
                        arrayList.add(model);


                    } while (cursor2.moveToNext());
                }

            } while (cursor1.moveToNext());
        }

        return arrayList;
    }

    public ArrayList<LatLogModel> finalLatLong() {
        ArrayList<LatLogModel> arrayList = new ArrayList<>();
        String query = "select * from alarm_table";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor1 = db.rawQuery(query, null);
        LatLogModel model = null;
        if (cursor1.moveToFirst()) {
            do {
                model = new LatLogModel();
                model.direction = cursor1.getString(cursor1.getColumnIndex("direction"));
                int s_id = cursor1.getInt(cursor1.getColumnIndex("s_id"));
                String query2 = "select * from station_fix_table where s_id = " + s_id + "";
                Cursor cursor2 = db.rawQuery(query2, null);
                if (cursor2.moveToFirst()) {
                    do {

                        model.lattitudeonMAp = (cursor2.getString(cursor2.getColumnIndex("station_lat")));
                        model.logitudeonMAp = (cursor2.getString(cursor2.getColumnIndex("station_lon")));

                        if (model.direction.equals("1")) {
                            model.lattitude = (cursor2.getString(cursor2.getColumnIndex("towards_lat")));
                            model.logitude = (cursor2.getString(cursor2.getColumnIndex("towards_lon")));
                        } else {
                            model.lattitude = (cursor2.getString(cursor2.getColumnIndex("opp_lat")));
                            model.logitude = (cursor2.getString(cursor2.getColumnIndex("opp_lon")));
                        }
                        arrayList.add(model);
                    } while (cursor2.moveToNext());
                }
            } while (cursor1.moveToNext());
        }
        return arrayList;
    }

    public ArrayList<LatLogModel> InsertedLastRecord() {
        ArrayList<LatLogModel> arrayList = new ArrayList<>();
        String query = "select * from alarm_table where a_id = (select max(a_id) from alarm_table)";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor1 = db.rawQuery(query, null);
        LatLogModel model = null;
        if (cursor1.moveToFirst()) {
            do {
                model = new LatLogModel();
                model.direction = cursor1.getString(cursor1.getColumnIndex("direction"));
                int s_id = cursor1.getInt(cursor1.getColumnIndex("s_id"));
                String query2 = "select * from station_fix_table where s_id = " + s_id + "";
                Cursor cursor2 = db.rawQuery(query2, null);
                if (cursor2.moveToFirst()) {
                    do {

                        model.lattitudeonMAp = (cursor2.getString(cursor2.getColumnIndex("station_lat")));
                        model.logitudeonMAp = (cursor2.getString(cursor2.getColumnIndex("station_lon")));

                        if (model.direction.equals("1")) {
                            model.lattitude = (cursor2.getString(cursor2.getColumnIndex("towards_lat")));
                            model.logitude = (cursor2.getString(cursor2.getColumnIndex("towards_lon")));
                        } else {
                            model.lattitude = (cursor2.getString(cursor2.getColumnIndex("opp_lat")));
                            model.logitude = (cursor2.getString(cursor2.getColumnIndex("opp_lon")));
                        }
                        arrayList.add(model);
                    } while (cursor2.moveToNext());
                }
            } while (cursor1.moveToNext());
        }
        return arrayList;
    }


}
