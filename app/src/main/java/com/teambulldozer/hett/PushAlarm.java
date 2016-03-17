package com.teambulldozer.hett;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by kwongyo on 2016-03-17.
 */
public class PushAlarm {
    private static final PushAlarm pushAlarm;

    static {
        pushAlarm = new PushAlarm();
    }
    private PushAlarm() {}
    public static PushAlarm getInstance() {
        return pushAlarm;
    }
    public boolean registerAlarm (Context context,int year,int month,int day, int hour,int min,int second,String pushAlarmTitle,String pushAlarmBody) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SelfPushReceiver.class);
        intent.putExtra("pushAlarmTitle",pushAlarmTitle);
        intent.putExtra("pushAlarmBody",pushAlarmBody);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);

        Calendar calendar = Calendar.getInstance();
        //알람시간 calendar에 set해주기

        calendar.set(year, month, day, hour, min, second);
        am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
        return true;
    }
}
