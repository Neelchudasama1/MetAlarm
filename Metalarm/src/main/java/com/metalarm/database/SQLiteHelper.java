package com.metalarm.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by qtm-purvesh on 18/4/16.
 */
public class SQLiteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "db_alarm.db";

    public static String ALARM_TABLE = "alarm_table";
    public static String SET_ALARM_TABLE = "set_alarm_table";

    public static String OFFLINE_STATION_TABLE = "ofline_station_table";



    public String direction = "direction";

    public String station_id = "station_id";





    public SQLiteHelper(Context mContext) {
        super(mContext, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql;

        sql = "create table if not exists '" + StationFixTableHandler.TABLE_STATION + "'(s_id INTEGER PRIMARY KEY   AUTOINCREMENT , name varchar2(15) , station_lat  varchar2(20) , station_lon varchar2(15) , towards_lat varchar2(15) ,  towards_lon varchar2(15) , opp_lat varchar2(15) , opp_lon varchar2(15) , line varchar2(15))";
        db.execSQL(sql);

        sql = "create table if not exists  '" + ALARM_TABLE + "'(a_id INTEGER PRIMARY KEY   AUTOINCREMENT , alarm_status varchar2(1) , direction varchar2(1) , s_id integer)";
        db.execSQL(sql);

        sql = "create table if not exists '" + SET_ALARM_TABLE + "'(" + AlarmTableHandler.KEY_UID + " INTEGER PRIMARY KEY   AUTOINCREMENT, station_id varchar2(3), name varchar2(15) , line_id varchar2(15) , latitude varchar2(15) ,  longitude varchar2(15) , maplatitude varchar2(15) , maplongitude varchar2(15)  , isAlarm varchar2(10) , direction varchar2(2), " + AlarmTableHandler.KEY_TIMESTAMP + " text)";
        db.execSQL(sql);

        sql = "create table if not exists '" + OffLineTablesHandler.TABLE_OFFLINE_LINE+ "'(" + OffLineTablesHandler.KEY_LINE_ID + " varchar2(3), " + OffLineTablesHandler.KEY_LINE_NAME + " varchar2(15) , " + OffLineTablesHandler.KEY_FROM_NAME + " varchar2(15) , " + OffLineTablesHandler.KEY_TO_NAME + " varchar2(15))";
        db.execSQL(sql);

        /*CREATE_STATION_TABLE= "create table if not exists '" + OFFLINE_LINE_TABLE + "'(" + line_id + " varchar2(3), " + line_name + " varchar2(15))";
        db.execSQL(CREATE_LINE_TABLE);*/

        sql = "create table if not exists '" + OFFLINE_STATION_TABLE + "'( uid varchar2(3),\n" +
                "    name varchar2(20),\n" +
                "    address varchar2(60),\n" +
                "    status varchar2(2),\n" +
                "    date varchar2(15),\n" +
                "    line_id varchar2(2),\n" +
                "    latitude varchar2(30),\n" +
                "    longitude varchar2(30),\n" +
                "    left_latitude varchar2(30),\n" +
                "    left_longitude varchar2(30),\n" +
                "    right_latitude varchar2(30),\n" +
                "    right_longitude varchar2(30))";
        db.execSQL(sql);

        sql = "create table if not exists '" + AddRemoveAlarmtableHandler.TABLE_ADD_REMOVE + "'(" + AddRemoveAlarmtableHandler.KEY_UID + " varchar2(4), " + AddRemoveAlarmtableHandler.KEY_DEVIC_TOKEN + " varchar2(50) , " + AddRemoveAlarmtableHandler.KEY_STATION_ID + " varchar2(5) , " + AddRemoveAlarmtableHandler.KEY_LINE_ID + " varchar2(15), " + AddRemoveAlarmtableHandler.KEY_STATUS + " varchar2(1))";
        db.execSQL(sql);


        sql = "create table if not exists '" + AlarmHitsHandler.ALARM_HITS_TABLE + "'(" + AlarmHitsHandler.KEY_UID + " text, " + AlarmHitsHandler.DEVIC_TOKEN + " text, " + AlarmHitsHandler.STATION_ID + " text , " + AlarmHitsHandler.LINE_ID + " text, " + AlarmHitsHandler.DATE_TIME + " text)";
        db.execSQL(sql);

    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        Log.e("res", "all table dropped");
        db.execSQL("DROP TABLE IF EXISTS station_fix_table");
        this.onCreate(db);
        db.execSQL("DROP TABLE IF EXISTS alarm_table");
        this.onCreate(db);

        db.execSQL("DROP TABLE IF EXISTS " + SET_ALARM_TABLE + "");
        this.onCreate(db);


    }
}
