package com.teambulldozer.hett;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

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

            boolean[] week = extra.getBooleanArray("alarm_days");
            /* 반복요일을 DB에 넘겨준 후에 주석을 풀어서 확인
            for(int i = 0; i < 8; i++) {
                Log.i("alarmOnReceive", "week[" + Integer.toString(i)+ "] = " + Boolean.toString(week[i]));
            }

            Calendar cal = Calendar.getInstance();

            if (!week[cal.get(Calendar.DAY_OF_WEEK)])
                return;
            */
            // 화면 호출
            Log.i("alarmOnReceive", "화면 호출");
            Intent OnTriggerIntent = new Intent(context, AlarmOnTrigger.class);
            PendingIntent p = PendingIntent.getActivity(context, 0, OnTriggerIntent, 0);
            try {
                p.send();
            } catch (PendingIntent.CanceledException e) {
                e.printStackTrace();
            }
        } else {
            Log.i("alarmOnReceive", "null extra");
        }

        Log.i("alarmOnReceive", "terminated");
    }
}
