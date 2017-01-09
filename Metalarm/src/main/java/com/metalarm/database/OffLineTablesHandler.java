package com.metalarm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.metalarm.model.LineModel;
import com.metalarm.model.StationModel;
import com.metalarm.model.getAllStationModel;

import java.util.ArrayList;

/**
 * Created by qtm-purvesh on 6/5/16.
 */
public class OffLineTablesHandler extends SQLiteHelper {

    private Context mContext;
    public static String TABLE_OFFLINE_LINE = "line_table";

    public static String KEY_LINE_ID = "line_id";
    public static String KEY_LINE_NAME = "line_name";

    public static String KEY_FROM_NAME = "from_name";
    public static String KEY_TO_NAME = "to_name";

    public OffLineTablesHandler(Context mContext) {
        super(mContext);
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
    }

    public void insertLineData(String line_id1, String line_name1) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_LINE_ID, line_id1);
        values.put(KEY_LINE_NAME, line_name1);

        db.insert(TABLE_OFFLINE_LINE, null, values);
        db.close();
    }

    public ArrayList<LineModel> getOfflineLines() {
        ArrayList<LineModel> arrayList = new ArrayList<>();
        String query = "SELECT  * FROM " + TABLE_OFFLINE_LINE;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        LineModel model;
        if (cursor.moveToFirst()) {
            do {
                model = new LineModel();
                model.name = (cursor.getString(cursor.getColumnIndex("" + KEY_LINE_NAME + "")));
                model.uid = (cursor.getString(cursor.getColumnIndex("" + KEY_LINE_ID + "")));


                arrayList.add(model);
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    public void deleteOflineLineTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.execSQL("delete from alarm_table where a_id = "+id+"");
        db.delete(TABLE_OFFLINE_LINE, null, null);
        db.close();
    }

    public void deleteOflineStationTable() {
        SQLiteDatabase db = this.getWritableDatabase();
        // db.execSQL("delete from alarm_table where a_id = "+id+"");
        db.delete(OFFLINE_STATION_TABLE, null, null);
        db.close();
    }

    public void insertOflineStationData(String uid, String name1, String address1, String status1, String date1,
                                        String line_id, String latitude, String longitude, String left_latitude,
                                        String left_longitude, String right_latitude, String right_longitude) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("uid", uid);
        values.put("name", name1);
        values.put("address", address1);
        values.put("status", status1);
        values.put("date", date1);
        values.put("line_id", line_id);
        values.put("latitude", latitude);
        values.put("longitude", longitude);
        values.put("left_latitude", left_latitude);
        values.put("left_longitude", left_longitude);
        values.put("right_latitude", right_latitude);
        values.put("right_longitude", right_longitude);

        db.insert(OFFLINE_STATION_TABLE, null, values);
        db.close();
    }

    public ArrayList<getAllStationModel> getOffStationLines(String line_id) {
        ArrayList<getAllStationModel> arrayList = new ArrayList<>();
        String query = "SELECT  * FROM " + OFFLINE_STATION_TABLE + " where line_id = " + line_id + "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        getAllStationModel model;
        Log.d("TTT", "Query : " + query);
        Log.d("TTT", "offline station size  : " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                model = new getAllStationModel();

                model.uid = (cursor.getString(cursor.getColumnIndex("" + model.uid + "")));
                model.name = (cursor.getString(cursor.getColumnIndex("" + model.name + "")));
                model.address = (cursor.getString(cursor.getColumnIndex("" + model.address + "")));
                model.status = (cursor.getString(cursor.getColumnIndex("" + model.status + "")));

                model.date = (cursor.getString(cursor.getColumnIndex("" + model.date + "")));
                model.line_id = (cursor.getString(cursor.getColumnIndex("" + model.line_id + "")));
                model.latitude = (cursor.getString(cursor.getColumnIndex("" + model.latitude + "")));
                model.longitude = (cursor.getString(cursor.getColumnIndex("" + model.longitude + "")));

                model.left_latitude = (cursor.getString(cursor.getColumnIndex("" + model.left_latitude + "")));
                model.left_longitude = (cursor.getString(cursor.getColumnIndex("" + model.left_longitude + "")));
                model.right_latitude = (cursor.getString(cursor.getColumnIndex("" + model.right_latitude + "")));
                model.right_longitude = (cursor.getString(cursor.getColumnIndex("" + model.right_longitude + "")));

                arrayList.add(model);
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

    public ArrayList<StationModel> getOffLineStationLines(String line_id) {
        ArrayList<StationModel> arrayList = new ArrayList<>();
        String query = "SELECT  * FROM " + OFFLINE_STATION_TABLE + " where line_id = " + line_id + " order by name";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        StationModel model;
        Log.d("TTT", "Query : " + query);
        Log.d("TTT", "offline station size  : " + cursor.getCount());
        if (cursor.moveToFirst()) {
            do {
                model = new StationModel();

                getAllStationModel glm = new getAllStationModel();

                model.station_id = (cursor.getString(cursor.getColumnIndex("" + glm.uid + "")));
                model.name = (cursor.getString(cursor.getColumnIndex("" + glm.name + "")));
                model.address = (cursor.getString(cursor.getColumnIndex("" + glm.address + "")));
                model.status = (cursor.getString(cursor.getColumnIndex("" + glm.status + "")));

                model.date = (cursor.getString(cursor.getColumnIndex("" + glm.date + "")));
                model.line_id = (cursor.getString(cursor.getColumnIndex("" + glm.line_id + "")));
                model.latitude = (cursor.getString(cursor.getColumnIndex("" + glm.latitude + "")));
                model.longitude = (cursor.getString(cursor.getColumnIndex("" + glm.longitude + "")));

                model.left_latitude = (cursor.getString(cursor.getColumnIndex("" + glm.left_latitude + "")));
                model.left_longitude = (cursor.getString(cursor.getColumnIndex("" + glm.left_longitude + "")));
                model.right_latitude = (cursor.getString(cursor.getColumnIndex("" + glm.right_latitude + "")));
                model.right_longitude = (cursor.getString(cursor.getColumnIndex("" + glm.right_longitude + "")));

                arrayList.add(model);
            } while (cursor.moveToNext());
        }
        return arrayList;
    }

}
