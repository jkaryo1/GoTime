package com.example.reminderapp;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.os.PowerManager;
import android.widget.Toast;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent i) {
        PowerManager powMan = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wake = powMan.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "");
        wake.acquire();
        Toast.makeText(context, "Time to get ready!", Toast.LENGTH_LONG).show();
        wake.release();
    }

    public void setAlarm(Context context) {
        Intent i = new Intent(context, Alarm.class);
        AlarmManager alarmMan = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendInt = PendingIntent.getBroadcast(context, 0, i, 0);
        alarmMan.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendInt);
    }
}
