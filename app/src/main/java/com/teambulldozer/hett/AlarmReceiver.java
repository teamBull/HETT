package com.teambulldozer.hett;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import java.util.Calendar;

/**
 * Created by yoon on 16. 2. 22..
 */
public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i("alarmOnReceive", "onReceive start");

        Calendar c = Calendar.getInstance();
        int curIntDay = c.get(Calendar.DAY_OF_WEEK);
        int curHour = c.get(Calendar.HOUR_OF_DAY);
        int curMinute = c.get(Calendar.MINUTE);
        String curStrDay = intDayToString(curIntDay);
        String repeatDays = null;
        int alarmHour = -1;
        int alarmMinute = -1;

        EventTableController eventTableController = EventTableController.get(context);
        Cursor eventTableCursor = eventTableController.getAllData();

        RepeatEventController repeatEventTableController = RepeatEventController.get(context);
        Cursor repeatEventTableCursor = repeatEventTableController.getEventRepeatData();

        eventTableCursor.move(-1);
        while(eventTableCursor.moveToNext()) {
            alarmHour = eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARMHOUR"));
            alarmMinute = eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARMMINUTE"));
            if ((alarmHour == curHour) && (alarmMinute == curMinute)) {
                repeatDays = curStrDay;
            }
        }

        repeatEventTableCursor.move(-1);
        while(repeatEventTableCursor.moveToNext()) {
            alarmHour = repeatEventTableCursor.getInt(repeatEventTableCursor.getColumnIndex("ALARMHOUR"));
            alarmMinute = repeatEventTableCursor.getInt(repeatEventTableCursor.getColumnIndex("ALARMMINUTE"));
            if ((alarmHour == curHour) && (alarmMinute == curMinute)) {
                repeatDays += repeatEventTableCursor.getString(repeatEventTableCursor.getColumnIndex("DAY_OF_WEEK"));
            }
        }

        if(repeatDays != null)
            callOnTriggerPage(context, repeatDays, curStrDay);

        Log.i("alarmOnReceive", "terminated");
    }

    String intDayToString(int intDay) {
        String dayStr = null;

        switch(intDay) {
            case 0:
                dayStr = "반복없음";
                break;
            case 1:
                dayStr = "일";
                break;
            case 2:
                dayStr = "월";
                break;
            case 3:
                dayStr = "화";
                break;
            case 4:
                dayStr = "수";
                break;
            case 5:
                dayStr = "목";
                break;
            case 6:
                dayStr = "금";
                break;
            case 7:
                dayStr = "토";
                break;
        }

        return dayStr;
    }

    public void callOnTriggerPage(Context context, String repeatDays, String curStrDay) {

        if (repeatDays.contains(curStrDay)) {
            Log.i("alarmOnReceive", "Alarm day is correct");

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
            Log.i("alarmOnReceive", "Today is not the day for alarm");
        }

    }
}
