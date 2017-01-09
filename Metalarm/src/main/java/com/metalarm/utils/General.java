package com.metalarm.utils;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;

import com.metalarm.model.CurrentLatLng;

/**
 * Created by qtm-kaushik on 4/8/15.
 */
public class General {

    public static CurrentLatLng CurrentLat;

    public static Context AppContext = null;
    public static Activity AppActivity = null;

    public static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    public static final String BROAD_CAST_LOCATIONS = "com.whereshome.LOCATION_LIST";
    public static final String BROAD_CAST_NO_LOCATION = "com.whereshome.NO_LOCATIONS";

    public static final String KEY_LOCATION_LIST = "location_list";
    public static final String KEY_CUR_LAT = "cur_lat";
    public static final String KEY_CUR_LNG = "cur_lng";

    public static final int GOOGLE_MAP_ZOOM_IN_LEVEL = 12;

    public static String latitude = "41.989489";
    public static String longitude = "-88.111862";

    public static double latitudeOnMap = 41.9893621;
    public static double longitudeOnmap = -88.1137641;

    public static String prefLat = "prefLat";
    public static String prefLong = "prefLong";
    public static String prefSound = "prefSound";
    public static String prefVib = "prefVib";

    public static String PREF_USER_ID;
    public static String PREF_EMAIL = "email";
    public static String PREF_PASSWORD = "password";
    public static String PREF_UID = "user_id";
    public static String PREF_STATION_ID = "station_id";
    public static String PREF_DEVICE_TOKEN = "device_token";

    public static String PREFS_IS_LOGGEND_IN = "isLoggedIN";
    public static String PREF_FIRST_NAME;
    public static int boundZoom = 0;

    public static boolean isOnline(Context c) {
        ConnectivityManager cm = (ConnectivityManager) c
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null
                && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public static final String SENT_TOKEN_TO_SERVER = "sentTokenToServer";
    public static final String REGISTRATION_COMPLETE = "registrationComplete";

    public static final String SENDER_ID = "396495268696";

    public static boolean allowToWriteLog = true;

}
