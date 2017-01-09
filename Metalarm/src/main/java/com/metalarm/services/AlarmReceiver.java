package com.metalarm.services;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.util.Log;

/**
 * Created by Jaydipsinh Zala on 21/6/16.
 */
public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Alarm", "Alarm");

        // code to keep service alive.
        //Intent i = new Intent(context, AlarmReceiver.class);
        //PendingIntent pendingIntent = PendingIntent.getBroadcast(
        //        context.getApplicationContext(), 234324243, i, 0);
        //AlarmManager alarmManager = (AlarmManager) context.getSystemService(context.ALARM_SERVICE);
        //alarmManager.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()
        //        + (60 * 1000), pendingIntent);

        //if (!Utils.isMyServiceRunning(context, MetraLocationService.class)) {
        //    Intent service = new Intent(context, MetraLocationService.class);
        //    context.startService(service);
        //}
        startWakefulService(context, new Intent(context, MetraLocationService.class));
    }
}