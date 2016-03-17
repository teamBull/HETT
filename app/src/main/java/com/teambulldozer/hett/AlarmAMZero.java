package com.teambulldozer.hett;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by GHKwon on 2016-02-27.
 */
public class AlarmAMZero {
    public AlarmAMZero(Context context) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, AlarmAMZeroReceiver.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        Calendar calendar = Calendar.getInstance();
        //알람시간 calendar에 set해주기

        calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 11, 1, 0);
        long oneday = 24 * 60 * 60 * 1000;// 매일 밤 12시에 등록되어야 하니까.

        //알람 예약
        am.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(),oneday, sender);
    }
}
