package com.metalarm.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.metalarm.model.addRemoveAlarmModel;
import com.metalarm.utils.SessionManager;

import java.util.ArrayList;

/**
 * Created by latitude on 3/9/16.
 */
public class AddRemoveAlarmtableHandler extends SQLiteHelper {

    private Context mContext;
    public static String KEY_UID = "uid";
    public static String KEY_DEVIC_TOKEN = "device_token";
    public static String KEY_STATION_ID = "s_id";
    public static String KEY_LINE_ID = "l_id";
    public static String KEY_STATUS = "status";
    public static String TABLE_ADD_REMOVE = "add_remove_table";


    public AddRemoveAlarmtableHandler(Context mContext) {
        super(mContext);
        // TODO Auto-generated constructor stub
        this.mContext = mContext;
    }

    public void insertAddRemoveData(String KEY_UID, String STATION_ID, String LINE_ID, String STATUS) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(this.KEY_UID, KEY_UID);
        values.put(this.KEY_DEVIC_TOKEN, new SessionManager(mContext).getDeviceToken());
        values.put(this.KEY_STATION_ID, STATION_ID);
        values.put(this.KEY_LINE_ID, LINE_ID);
        values.put(this.KEY_STATUS, STATUS);

        db.insert(TABLE_ADD_REMOVE, null, values);
        db.close();
    }

    public ArrayList<addRemoveAlarmModel> getAddRemoveAlarmData() {
        ArrayList<addRemoveAlarmModel> arrayList = new ArrayList<>();
        String query = "select * from " + TABLE_ADD_REMOVE + "";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        addRemoveAlarmModel model;
        if (cursor.moveToFirst()) {
            do {
                model = new addRemoveAlarmModel();
                model.KEY_UID = (cursor.getString(cursor.getColumnIndex(KEY_UID)));
                model.DEVIC_TOKEN = (cursor.getString(cursor.getColumnIndex(KEY_DEVIC_TOKEN)));
                model.LINE_ID = (cursor.getString(cursor.getColumnIndex(KEY_LINE_ID)));
                model.STATION_ID = (cursor.getString(cursor.getColumnIndex(KEY_STATION_ID)));
                model.STATUS = (cursor.getString(cursor.getColumnIndex(KEY_STATUS)));

                arrayList.add(model);
            } while (cursor.moveToNext());
        }
        return arrayList;

    }

    public void deleteAddRemoveAlarmData() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "delete from " + TABLE_ADD_REMOVE + "";
        db.execSQL(query);
        db.close();
    }


}
