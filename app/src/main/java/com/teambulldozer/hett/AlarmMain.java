package com.teambulldozer.hett;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmMain extends Activity implements OnClickListener {

    private static final String TAG = "MainActivity";
    private static final String INTENT_ACTION = "android.intent.action.CallResult";
    int year, hour, minute;
    static int alarmHour, alarmMinute;
    boolean importance = false;
    static boolean hasAlarm = false;
    boolean noRepeat, mon, tue, wed, thu, fri, sat, sun = false;

    // Set buttons
    private Button[] alarmButtons;
    private void setAlarmButtons(){
        alarmButtons = new Button[]{
                (Button) findViewById(R.id.btn_set_alarm),
                (Button) findViewById(R.id.btn_release_alarm),
                (Button) findViewById(R.id.btn_add_alarm_time),
                (Button) findViewById(R.id.btn_alarm_prev),
                (Button) findViewById(R.id.btn_turned_off_star),
                (Button) findViewById(R.id.btn_turned_on_star)
        };
        for(Button b : alarmButtons) {b.setOnClickListener(this);}
    }

    private void setCalenderParam(Context context) {
        GregorianCalendar calendar = new GregorianCalendar();
        year = calendar.get(Calendar.YEAR);
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_main);

        setAlarmButtons();
        setCalenderParam(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_alarm:
                setAlarm(this, 10000);
                break;

            case R.id.btn_release_alarm:
                releaseAlarm(this);
                Toast.makeText(getApplicationContext(), "releaseAlarm()", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_add_alarm_time:
                addAlarmTime();
                Toast.makeText(getApplicationContext(), "addAlarm()", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_turned_off_star:
                Log.i(TAG, "set Importance");
                importance = true;
                setImportance();
                Toast.makeText(getApplicationContext(), "setImportance()", Toast.LENGTH_SHORT).show();
                break;

            case R.id.btn_turned_on_star:
                Log.i(TAG, "set UnImportance");
                importance = false;
                setImportance();
                Toast.makeText(getApplicationContext(), "setImportance()", Toast.LENGTH_SHORT).show();
                break;

            case R.id.checkBoxNoRepeat:
                Log.i(TAG, "no repeat!");
                noRepeat = !noRepeat;
                printCheckedDays();
                break;

            case R.id.checkBoxMon:
                Log.i(TAG, "monday Clicked!");
                noRepeat = false;
                mon = !mon;
                printCheckedDays();
                break;

            case R.id.checkBoxTue:
                Log.i(TAG, "Days Clicked!");
                noRepeat = false;
                tue = !tue;
                printCheckedDays();
                break;

            case R.id.checkBoxWed:
                Log.i(TAG, "Days Clicked!");
                noRepeat = false;
                wed = !wed;
                printCheckedDays();
                break;

            case R.id.checkBoxThu:
                Log.i(TAG, "Days Clicked!");
                noRepeat = false;
                thu = !thu;
                printCheckedDays();
                break;

            case R.id.checkBoxFri:
                Log.i(TAG, "Days Clicked!");
                noRepeat = false;
                fri = !fri;
                printCheckedDays();
                break;

            case R.id.checkBoxSat:
                Log.i(TAG, "Days Clicked!");
                noRepeat = false;
                sat = !sat;
                printCheckedDays();
                break;

            case R.id.checkBoxSun:
                Log.i(TAG, "Days Clicked!");
                noRepeat = false;
                sun = !sun;
                printCheckedDays();
                break;

            default:
                break;
        }
    }

    private void setImportance() {
        if(importance) {
            findViewById(R.id.btn_turned_off_star).setVisibility(View.INVISIBLE);
            findViewById(R.id.btn_turned_on_star).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.btn_turned_off_star).setVisibility(View.VISIBLE);
            findViewById(R.id.btn_turned_on_star).setVisibility(View.INVISIBLE);
        }
    }

    // 알람 등록
    private void setAlarm(Context context, long second) {
        Log.i(TAG, "setAlarm()");
        /* 지정한 시간 이후에 알람이 울리게 함(for practice)
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent Intent = new Intent(INTENT_ACTION);
        PendingIntent pIntent = getActivity(context, 0, Intent, 0);

        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + second, pIntent);
        */
        // 반복적인 알람 설정에 관한 함수
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        boolean[] week = {noRepeat, sun, mon, tue, wed, thu, fri, sat};

        Intent intent = new Intent(this, AlarmReceiver.class);
        long triggerTime = 0;
        long intervalTime = 24 * 60 * 60 * 1000; // 24시간(ms)

        intent.putExtra("days", week);
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        triggerTime = setTriggerTime();

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime, intervalTime, pending);

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


            Toast.makeText(getApplicationContext(), Long.toString(alarmHour)+" "+Long.toString(alarmMinute), Toast.LENGTH_SHORT).show();
        }
        return triggerTime;
    }

    // 알람 해제
    private void releaseAlarm(Context context){
        Log.i(TAG, "releaseAlarm()");
        /*
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent Intent = new Intent(INTENT_ACTION);
        PendingIntent pIntent = getActivity(context, 0, Intent, 0);
        alarmManager.cancel(pIntent);

        // 주석을 풀면 먼저 실행되는 알람이 있을 경우, 제거하고
        // 새로 알람을 실행하게 된다. 상황에 따라 유용하게 사용 할 수 있다.
//      alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 3000, pIntent);
        */
        AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);

        Intent intent = new Intent(this, AlarmReceiver.class);
        PendingIntent pending = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.cancel(pending);
    }

    // 알람 시간 설정
    private void addAlarmTime() {
        if(hasAlarm == false) {
            DialogFragment newFragment = new TimePickerFragment();
            newFragment.show(getFragmentManager(),"TimePicker");
            setAlarmButtons();

            hasAlarm = true;
            findViewById(R.id.btn_add_alarm_time).setVisibility(View.INVISIBLE);
        }
    }

    // 알람 시간 삭제
    // TimePickerFragment.java 참고

    // 체크된 요일 출력
    private void printCheckedDays() {
        // 출력할 String 및 checkbox 초기화
        String daysText = "";
        if(noRepeat) {
            mon = false;
            tue = false;
            wed = false;
            thu = false;
            fri = false;
            sat = false;
            sun = false;
            findViewById(R.id.checkImageNoRepeat).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.checkImageNoRepeat).setVisibility(View.INVISIBLE);
        }

        // String 제작
        if(mon) {
            daysText = "월";
            findViewById(R.id.checkImageMon).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.checkImageMon).setVisibility(View.INVISIBLE);
        }

        if((daysText.equals("")) && tue) {
            daysText = "화";
            findViewById(R.id.checkImageTue).setVisibility(View.VISIBLE);
        } else if(tue) {
            daysText += ", 화";
            findViewById(R.id.checkImageTue).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.checkImageTue).setVisibility(View.INVISIBLE);
        }

        if((daysText.equals("")) && wed) {
            daysText = "수";
            findViewById(R.id.checkImageWed).setVisibility(View.VISIBLE);
        } else if(wed) {
            daysText += ", 수";
            findViewById(R.id.checkImageWed).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.checkImageWed).setVisibility(View.INVISIBLE);
        }

        if((daysText.equals("")) && thu) {
            daysText = "목";
            findViewById(R.id.checkImageThu).setVisibility(View.VISIBLE);
        } else if(thu) {
            daysText += ", 목";
            findViewById(R.id.checkImageThu).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.checkImageThu).setVisibility(View.INVISIBLE);
        }

        if((daysText.equals("")) && fri) {
            daysText = "금";
            findViewById(R.id.checkImageFri).setVisibility(View.VISIBLE);
        } else if(fri) {
            daysText += ", 금";
            findViewById(R.id.checkImageFri).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.checkImageFri).setVisibility(View.INVISIBLE);
        }

        if((daysText.equals("")) && sat) {
            daysText = "토";
            findViewById(R.id.checkImageSat).setVisibility(View.VISIBLE);
        } else if(sat) {
            daysText += ", 토";
            findViewById(R.id.checkImageSat).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.checkImageSat).setVisibility(View.INVISIBLE);
        }

        if((daysText.equals("")) && sun) {
            daysText = "일";
            findViewById(R.id.checkImageSun).setVisibility(View.VISIBLE);
        } else if(sun) {
            daysText += ", 일";
            findViewById(R.id.checkImageSun).setVisibility(View.VISIBLE);
        } else {
            findViewById(R.id.checkImageSun).setVisibility(View.INVISIBLE);
        }

        // Set String
        TextView daysTextView = (TextView) findViewById(R.id.days);
        daysTextView.setText(daysText);
    }
}