package com.teambulldozer.hett;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Calendar;

/*
 * Created by yoon on 16. 3. 3..
 */
public class AlarmOnTrigger extends Activity implements View.OnClickListener {

    PowerManager.WakeLock wakeLock;
    Vibrator mVibe;

    // DB Info
    String syncID = null; // Date is syncID for event and repeat table.
    EventTableController eventTableController = EventTableController.get(this);
    RepeatEventController repeatEventTableController = RepeatEventController.get(this);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_on_trigger);

        // intialize page info
        initPage();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_stop_alarm:
                stopClicked();
                break;

            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopClicked();
    }

    private void initPage() {
        //
        mVibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

        // 상태바 없애기, 진동울리기
        Log.i("alarmOnTrigger", "상태바 없애기, 진동");
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        mVibe.vibrate(100000);

        // intialize informations
        Calendar c = Calendar.getInstance();
        int curDay = c.get(Calendar.DAY_OF_WEEK);
        int curHour = c.get(Calendar.HOUR_OF_DAY);
        int curMinute = c.get(Calendar.MINUTE);
        int curAMPM = c.get(Calendar.AM_PM);
        int hasAlarm;
        int alarmHour, alarmMinute;
        String todo = null;
        String alarmDay = null;
        String curStrDay = intDayToStr(curDay);

        // 알람 시간 표시
        TextView timeTv = (TextView) findViewById(R.id.curTimeOnTrigger);
        TextView am_pmTv = (TextView) findViewById(R.id.curAM_PM_OnTrigger);
        timeTv.setText(makeTimeStr(curHour, curMinute));
        am_pmTv.setText(makeAM_PMStr(curAMPM));

        // 메모내용 표시 -> DB로부터 해당알람정보 끌어오기(알람시간이 같은 정보가 있으면 끌어온다.)
        TextView todoTv = (TextView) findViewById(R.id.todoOnTrigger);
        Cursor eventTableCursor = eventTableController.getAllData();
        Cursor repeatEventTableCursor = repeatEventTableController.getEventRepeatData();

        eventTableCursor.move(-1);
        while(eventTableCursor.moveToNext()) {
            hasAlarm = eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARM"));
            if(hasAlarm == 0) {
                continue;
            } else {
                alarmHour = eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARMHOUR"));
                alarmMinute = eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARMMINUTE"));
                Log.i("AlarmOnTrigger", "Saved alarm vs System time " + Integer.toString(alarmHour) + ":" + Integer.toString(alarmMinute)
                        + " vs " + Integer.toString(curHour) + ":" + Integer.toString(curMinute));
                if((curHour == alarmHour) && (curMinute == alarmMinute)) {
                    if(todo == null) {
                        todo = eventTableCursor.getString(eventTableCursor.getColumnIndex("MEMO"));
                    } else {
                        todo += ", " + eventTableCursor.getString(eventTableCursor.getColumnIndex("MEMO"));
                    }
                }
            }
        }

        repeatEventTableCursor.move(-1);
        while(repeatEventTableCursor.moveToNext()) {
            hasAlarm = repeatEventTableCursor.getInt(repeatEventTableCursor.getColumnIndex("ALARM"));
            if(hasAlarm == 0) {
                continue;
            } else {
                alarmDay = repeatEventTableCursor.getString(repeatEventTableCursor.getColumnIndex("DAY_OF_WEEK"));
                alarmHour = repeatEventTableCursor.getInt(repeatEventTableCursor.getColumnIndex("ALARMHOUR"));
                alarmMinute = repeatEventTableCursor.getInt(repeatEventTableCursor.getColumnIndex("ALARMMINUTE"));
                Log.i("AlarmOnTrigger", "Memo : " + repeatEventTableCursor.getString(repeatEventTableCursor.getColumnIndex("MEMO")) +
                        "\nSaved alarm vs System time " + Integer.toString(alarmHour) + ":" + Integer.toString(alarmMinute)
                        + " vs " + Integer.toString(curHour) + ":" + Integer.toString(curMinute));
                if((curHour == alarmHour) && (curMinute == alarmMinute) && (alarmDay.contains(curStrDay))) {
                    if(todo == null) {
                        todo = repeatEventTableCursor.getString(repeatEventTableCursor.getColumnIndex("MEMO"));
                    } else {
                        if(todo.contains(repeatEventTableCursor.getString(repeatEventTableCursor.getColumnIndex("MEMO")))) {
                            continue;
                        } else {
                            todo += ", " + repeatEventTableCursor.getString(repeatEventTableCursor.getColumnIndex("MEMO"));
                        }
                    }
                }
            }
        }

        if(todo != null) {
            todo += " 잊지 않았지?";
        } else {
            todo = "할거 없는데 알람이 울려버렸네,,,? 미안,,,";
        }
        todoTv.setText(todo);

        // 화면 On
        acquireWakeLock(this);
    }

    private void acquireWakeLock(Context context) {
        PowerManager pm = (PowerManager)context.getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.FULL_WAKE_LOCK
                | PowerManager.ACQUIRE_CAUSES_WAKEUP
                , context.getClass().getName());

        if (wakeLock != null) {
            wakeLock.acquire();
        }
    }

    private void releaseWakeLock() {
        if (wakeLock != null) {
            wakeLock.release();
            wakeLock = null;
        }
    }

    private String makeTimeStr(int h, int m) {
        String hour, minute;
        if(h < 12) {
            hour = String.valueOf(h);
            minute = String.valueOf(m);
        } else if (h < 24) {
            hour = String.valueOf(h%12);
            minute = String.valueOf(m);
        } else {
            hour = String.valueOf(12);
            minute = String.valueOf(m);
        }

        if(hour.length() == 1) {
            hour = "0" + hour;
        }
        if(minute.length() == 1) {
            minute = "0" + minute;
        }

        return hour + ":" + minute;
    }

    private String intDayToStr(int intDay) {
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

    private String makeAM_PMStr(int am_pm) {
        if(am_pm == 0) {
            return "am";
        } else {
            return "pm";
        }
    }

    private void stopClicked() {
        // 화면 돌리기, 진동 멈추기
        super.onBackPressed();
        releaseWakeLock();
        mVibe.cancel();
    }
}