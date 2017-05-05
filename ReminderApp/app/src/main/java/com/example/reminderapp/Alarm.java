package com.example.reminderapp;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.PowerManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.view.WindowManager;
import android.widget.Toast;
import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;

public class Alarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent i) {

        Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);

        final Ringtone r = RingtoneManager.getRingtone(context, notification);
        r.play();


        Drawable icon = ResourcesCompat.getDrawable(context.getResources(), android.R.drawable.ic_dialog_alert, null);
        if (icon != null) {
            icon.setColorFilter(ContextCompat.getColor(context, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
        }
        // Create confirmation dialog
        // On confirm, revert changes and create Toast
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        alertDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);



        alertDialog.setTitle("Cancel Changes");
        alertDialog.setMessage("Are you sure you want to cancel changes?");
        alertDialog.setIcon(icon);
        alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Dismiss", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                r.stop();
            }
        });
        alertDialog.show();



    }

    public void setAlarm(Context context) {
        Intent intent = new Intent(context, Alarm.class);
        AlarmManager alarmMan = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        PendingIntent pendInt = PendingIntent.getBroadcast(context, 0, intent, 0);

        alarmMan.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 10000, pendInt);
    }

    public  void cancelAlarm(Context context) {
        Intent intent = new Intent(context, Alarm.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);
    }
}
