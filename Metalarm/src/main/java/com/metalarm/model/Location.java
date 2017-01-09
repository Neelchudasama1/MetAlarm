package com.metalarm.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.metalarm.database.AlarmTableHandler;

/**
 * Created by qtm-kaushik on 4/8/15.
 */
public class Location implements Parcelable {

    public String uid = "";
    public String Address = "";
    public String lattitude = ""; // alarm's left or right latitude.
    public String logitude = ""; // alarm's left or right longitude.
    public String lattitudeonMAp = ""; // station latitude.
    public String logitudeonMAp = ""; // station longitude.
    public String created_date = "";
    public boolean isNotificationShown = false;
    public String sId = "";
    public String line_id = "";
    public String isAlarm;
    public String direction;
    public long timeInMillis; // alarm's last trigger time in millis.

    public Location() {

    }

    public Location(Parcel in) {
        readFromParcel(in);
    }

    public static final Parcelable.Creator<Location> CREATOR = new Parcelable.Creator<Location>() {
        @Override
        public Location createFromParcel(Parcel in) {
            return new Location(in);
        }

        @Override
        public Location[] newArray(int size) {
            return new Location[size];
        }
    };

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        dest.writeString(uid);
        dest.writeString(sId);
        dest.writeString(line_id);
        dest.writeString(Address);
        dest.writeString(lattitude);
        dest.writeString(logitude);
        dest.writeString(lattitudeonMAp);
        dest.writeString(logitudeonMAp);
        dest.writeString(created_date);
        dest.writeByte((byte) (isNotificationShown ? 1 : 0));
        dest.writeString(direction);
        dest.writeLong(timeInMillis);
    }

    private void readFromParcel(Parcel in) {
        if (in != null) {
            uid = in.readString();
            sId = in.readString();
            line_id = in.readString();
            Address = in.readString();
            lattitude = in.readString();
            logitude = in.readString();
            lattitudeonMAp = in.readString();
            logitudeonMAp = in.readString();
            created_date = in.readString();
            isNotificationShown = in.readByte() != 0;
            direction = in.readString();
            timeInMillis = in.readLong();
        }
    }

    public String getDirection() {
        return direction.equalsIgnoreCase("0") ? "to downtown" : "from downtown";
    }

    public float getTimeStampDiffInMinutes(AlarmTableHandler alarmTableHandler) {
        long currentTime = System.currentTimeMillis();
        //long duration = new Date(currentTime).getTime() - new Date(timeInMillis).getTime();
        this.timeInMillis = alarmTableHandler.getAlarmData(this).timeInMillis;
        long duration = currentTime - timeInMillis;
        float diffInMinutes = (duration / 1000f) / 60f;//TimeUnit.MILLISECONDS.toMinutes(duration);
        Log.e("Time", "currentTime : " + currentTime);
        Log.e("Time", "timeInMillis : " + timeInMillis);
        Log.e("Time", "TimeDiff : " + diffInMinutes);
        return diffInMinutes;
    }

}
