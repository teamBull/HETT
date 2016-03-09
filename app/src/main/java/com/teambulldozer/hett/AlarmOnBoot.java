package com.teambulldozer.hett;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.IBinder;
import android.util.Log;

import java.util.Calendar;

/*
 * Created by yoon on 16. 3. 8..
 */
public class AlarmOnBoot extends Service {
    private final String TAG = "AlarmOnBoot";

    int numOfAlarm = 0;
    int alarmHour = -1, alarmMinute = -1;


    EventTableController eventTableController = EventTableController.get(this);
    RepeatEventController repeatEventTableController = RepeatEventController.get(this);
    String syncID;

    @Override
    public IBinder onBind(final Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        countNumOfAlarm();
        resetAlarm();
    }

    private void resetAlarm() {
        Log.i(TAG, "alarm 재설정 시작");
        AlarmManager[] alarmManagers = new AlarmManager[numOfAlarm];
        PendingIntent[] pending = new PendingIntent[numOfAlarm];
        Intent[] intent = new Intent[numOfAlarm];
        for(int i = 0; i < numOfAlarm; i++) {
            alarmManagers[i] = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            intent[i] = new Intent(this, AlarmReceiver.class);
            pending[i] = PendingIntent.getBroadcast(this, i, intent[i], Intent.FILL_IN_DATA);
        }
        int i = 0; // n-th alarm

        Cursor eventTableCursor = eventTableController.getAllData();
        Cursor repeatEventTableCursor = repeatEventTableController.getEventRepeatData();

        long triggerTime = 0;
        long intervalTime = 24 * 60 * 60 * 1000; // 24시간(ms)

        // 일반 일정 alarm set(반복일정 포함)
        repeatEventTableCursor.move(-1);
        while(eventTableCursor.moveToNext()) {

            if (eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARM")) == 1) {
                alarmHour = eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARMHOUR"));
                alarmMinute = eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARMMINUTE"));
                triggerTime = setTriggerTime();
                Log.i("triggerTime vs SysTime", Long.toString(triggerTime) + "vs." + Long.toString(System.currentTimeMillis()) + " -> 알람이 설정되었음 - 일반 일정");
                alarmManagers[i].setRepeating(AlarmManager.RTC_WAKEUP, triggerTime /*for Debugging System.currentTimeMillis()+400*/, intervalTime, pending[i]);

                i++;
            }

        }

        // 반복 일정 alarm set(일반 일정 포함)
        repeatEventTableCursor.move(-1);
        while(repeatEventTableCursor.moveToNext()) {
            if (repeatEventTableCursor.getInt(repeatEventTableCursor.getColumnIndex("ALARM")) == 1) {
                alarmHour = repeatEventTableCursor.getInt(repeatEventTableCursor.getColumnIndex("ALARMHOUR"));
                alarmMinute = repeatEventTableCursor.getInt(repeatEventTableCursor.getColumnIndex("ALARMMINUTE"));
                triggerTime = setTriggerTime();
                Log.i("triggerTime vs SysTime", Long.toString(triggerTime) + "vs." + Long.toString(System.currentTimeMillis()) + " -> 알람이 설정되었음 - 반복 일정");
                alarmManagers[i].setRepeating(AlarmManager.RTC_WAKEUP, triggerTime /*for Debugging System.currentTimeMillis()+400*/, intervalTime, pending[i]);
                i++;
            }
        }
    }

    private long setTriggerTime() {
        // current Time
        long atime = System.currentTimeMillis();
        // timepicker
        Calendar curTime = Calendar.getInstance();
        curTime.set(Calendar.HOUR_OF_DAY, alarmHour);
        curTime.set(Calendar.MINUTE, alarmMinute);
        curTime.set(Calendar.SECOND, 0);
        curTime.set(Calendar.MILLISECOND, 0);
        long btime = curTime.getTimeInMillis();
        long triggerTime = btime;
        if (atime > btime) {
            triggerTime += 1000 * 60 * 60 * 24;
        }
        return triggerTime;
    }

    private void countNumOfAlarm() {

        Cursor eventTableCursor = eventTableController.getAllData();
        Cursor repeatEventTableCursor = repeatEventTableController.getEventRepeatData();

        eventTableCursor.move(-1);
        while(eventTableCursor.moveToNext()) {
            if(eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARM")) == 1) numOfAlarm++;
        }

        repeatEventTableCursor.move(-1);
        while(repeatEventTableCursor.moveToNext()) {
            if (repeatEventTableCursor.getInt(repeatEventTableCursor.getColumnIndex("ALARM")) == 1) numOfAlarm++;
        }

        Log.i(TAG, "Number of Alarm is " + Integer.toString(numOfAlarm));

    }

}
