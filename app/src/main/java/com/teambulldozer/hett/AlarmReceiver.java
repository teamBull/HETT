package com.teambulldozer.hett;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by yoon on 16. 2. 22..
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("alarmOnReceive", "onReceive start");

        Bundle extra = intent.getExtras();

        if (extra != null) {
            Log.i("alarmOnReceive", "extra has values");

            boolean[] week = extra.getBooleanArray("days");

            for(int i = 0; i < 8; i++) {
                Log.i("alarmOnReceive", "week[" + Integer.toString(i)+ "] = " + Boolean.toString(week[i]));
            }

            Calendar cal = Calendar.getInstance();

            if (!week[cal.get(Calendar.DAY_OF_WEEK)])
                return;

            // 화면 호출
            Log.i("alarmOnReceive", "화면 호출");
            Intent OnTriggerIntent = new Intent(context, AlarmOnTrigger.class);
            PendingIntent p = PendingIntent.getActivity(context, 0, OnTriggerIntent, 0);
            try {
                p.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }

            // 진동
            Log.i("alarmOnReceive", "진동");
            //Vibrator mVibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            //mVibe.vibrate(2000);
        } else {
            Log.i("alarmOnReceive", "null extra");
        }

        Log.i("alarmOnReceive", "terminated");
    }
}
