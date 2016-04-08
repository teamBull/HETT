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
/-
    /*
    http://regularmotion.kr/2%EA%B0%9C-%EC%9D%B4%EC%83%81%EC%9D%98-notification-%EC%82%AC%EC%9A%A9%EC%8B%9C-%EC%A3%BC%EC%9D%98%EC%A0%90/
    * */
    @Override
    public void onReceive(Context context,Intent intent) {
        HETTSettingSharedPreference hettSettingSharedPreference = HETTSettingSharedPreference.getInstance();
        Log.d("앗..앗뇽..", "SelfPushReceiver-" + hettSettingSharedPreference.searchPushAlarm(context));
        int testNo = intent.getExtras().getInt("requestCode");
        Log.d("intentNo",testNo+"");
        if(hettSettingSharedPreference.searchPushAlarm(context) ) {

            NotificationManager notificationmanager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, testNo, new Intent(context, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification.Builder builder = new Notification.Builder(context);
            builder.setSmallIcon(R.drawable.hatt).setTicker("HETT").setWhen(System.currentTimeMillis())
                    .setNumber(1).setContentTitle(intent.getStringExtra("pushAlarmTitle")).setContentText(intent.getStringExtra("pushAlarmBody")+"/intentNo-"+testNo)
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE).setContentIntent(pendingIntent).setAutoCancel(true);
            notificationmanager.notify(1, builder.build());
        }
    }
}
