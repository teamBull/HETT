package com.teambulldozer.hett;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by GHKwon on 2016-02-21.
 */
public class SelfPushReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context,Intent intent) {
        HETTSettingSharedPreference hettSettingSharedPreference = HETTSettingSharedPreference.getInstance();
        Log.d("앗..앗뇽..", "SelfPushReceiver-" + hettSettingSharedPreference.searchPushAlarm(context));
        if(hettSettingSharedPreference.searchPushAlarm(context) ) {

            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.drawable.hatt).setTicker("HETT").setWhen(System.currentTimeMillis())
                    .setNumber(1).setContentTitle(intent.getStringExtra("pushAlarmTitle")).setContentText(intent.getStringExtra("pushAlarmBody"))
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);
            notificationmanager.notify(1, builder.build());
        }
    }
}
