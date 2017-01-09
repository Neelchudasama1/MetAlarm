package com.metalarm.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by qtm-purvesh on 18/4/16.
 */
public class getSetAlarmData implements Parcelable {

    public String uid;
    public String line_id;
    public String lati; // alarm's left or right latitude.
    public String longi; // alarm's left or right longitude.
    public String mapLat; // station's left or right latitude.
    public String mapLong; // station's left or right longitude.
    public String stationName;
    public String sId;
    public String isAlarm;
    public String direction;
    public long timeInMillis; // alarm's trigger time in millis.

    public getSetAlarmData() {

    }

    protected getSetAlarmData(Parcel in) {
        uid = in.readString();
        lati = in.readString();
        line_id = in.readString();
        longi = in.readString();
        mapLat = in.readString();
        mapLong = in.readString();
        stationName = in.readString();
        sId = in.readString();
        isAlarm = in.readString();
        direction = in.readString();
        timeInMillis = in.readLong();
    }

    public static final Creator<getSetAlarmData> CREATOR = new Creator<getSetAlarmData>() {
        @Override
        public getSetAlarmData createFromParcel(Parcel in) {
            return new getSetAlarmData(in);
        }

        @Override
        public getSetAlarmData[] newArray(int size) {
            return new getSetAlarmData[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(uid);
        dest.writeString(lati);
        dest.writeString(line_id);
        dest.writeString(longi);
        dest.writeString(mapLat);
        dest.writeString(mapLong);
        dest.writeString(stationName);
        dest.writeString(sId);
        dest.writeString(isAlarm);
        dest.writeString(direction);
        dest.writeLong(timeInMillis);
    }
}
