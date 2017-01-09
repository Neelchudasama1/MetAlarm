package com.metalarm.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.metalarm.model.LoginModel;

/**
 * Created by qtm-purvesh on 5/5/16.
 */
public class SessionManager {

    private SharedPreferences mPrefs;

    public SessionManager(Context mContext) {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(mContext);
    }

    public void login(LoginModel model) {
        SharedPreferences.Editor e = mPrefs.edit();
        e.putString(General.PREF_UID, model.uid);
        e.putBoolean(General.PREFS_IS_LOGGEND_IN, true);
        e.apply();
    }

    public boolean IsLoggedIn() {
        return mPrefs.getBoolean(General.PREFS_IS_LOGGEND_IN, false);
    }

    public String getuserId() {
        return mPrefs.getString(General.PREF_UID, "12");
    }

    public void setDeviceToken(String deviceToken) {
        SharedPreferences.Editor e = mPrefs.edit();
        e.putString(General.PREF_DEVICE_TOKEN, deviceToken);
        e.apply();
    }

    public String getDeviceToken() {
        return mPrefs.getString(General.PREF_DEVICE_TOKEN, "");
    }

    public void storeLocationInPref(String lattitudeonMAp, String logitudeonMAp) {
        SharedPreferences.Editor e = mPrefs.edit();
        e.putString(General.prefLat, lattitudeonMAp);
        e.putString(General.prefLong, logitudeonMAp);
        Log.d("TTT", "pref lat long  store: " + lattitudeonMAp + "   " + logitudeonMAp);
        e.apply();
    }

    public String[] getLocationPref() {

        String latLong[] = new String[2];
        latLong[0] = mPrefs.getString(General.prefLat, "0");
        latLong[1] = mPrefs.getString(General.prefLong, "0");

        Log.d("TTT", "pref lat long  : " + latLong[0] + "   " + latLong[1]);

        return latLong;
    }

    public void prefClearLatLong() {
        mPrefs.edit().remove(General.prefLat).commit();
        mPrefs.edit().remove(General.prefLong).commit();
    }

    public void prefStationID(String id) {
        mPrefs.edit().remove(General.PREF_STATION_ID).commit();
        SharedPreferences.Editor e = mPrefs.edit();
        e.putString(General.PREF_STATION_ID, id);
        e.apply();
    }

    public String getPrefSattionID() {
        return mPrefs.getString(General.PREF_STATION_ID, "12");
    }

    public void prefStoreSound(boolean IsSound) {
        SharedPreferences.Editor e = mPrefs.edit();
        mPrefs.edit().remove(General.prefSound).commit();
        e.putBoolean(General.prefSound, IsSound);
        Log.d("MMM", "pref store Sound: " + IsSound);
        e.apply();
    }

    public boolean prefGetSound() {

        boolean isSound = mPrefs.getBoolean(General.prefSound, true);

        Log.d("MMM", "pref lget sound : " + isSound);

        return isSound;
    }


    public void prefStoreVib(boolean IsVib) {
        SharedPreferences.Editor e = mPrefs.edit();
        mPrefs.edit().remove(General.prefVib).commit();
        e.putBoolean(General.prefVib, IsVib);
        Log.d("MMM", "pref store vib: " + IsVib);
        e.apply();
    }

    public boolean prefGetVib() {

        boolean isSound = mPrefs.getBoolean(General.prefVib, true);

        Log.d("MMM", "pref lget vib : " + isSound);

        return isSound;
    }

}
