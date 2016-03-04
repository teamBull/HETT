package com.teambulldozer.hett;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.GregorianCalendar;

public class AlarmMain extends Activity implements OnClickListener {

    private static final String TAG = "MainActivity";
    int year, hour, minute;
    int alarmHour, alarmMinute;
    String repeatDays = null;
    boolean importance = false;
    String todo = "";
    boolean hasAlarm = false;
    boolean noRepeat = true;
    boolean mon, tue, wed, thu, fri, sat, sun = false;
    boolean[] week = {noRepeat, mon, tue, wed, thu, fri, sat, sun};

    // DB Info
    EventTableController eventTableController = EventTableController.get(this);
    RepeatEventController repeatEventTableController = RepeatEventController.get(this);

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

        initData();
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

            case R.id.btn_alarm_prev:
                onBackButtonPress();
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

    private void initData() {
        Intent i = getIntent();
        int position = i.getIntExtra("position", 1);

        Cursor eventTableCursor = eventTableController.getAllData();
        Cursor repeatEventTableCursor = repeatEventTableController.getEventRepeatData();
        Log.i("POSITION : ", "at " + position);

        // set pre-Importance, set pre-Todo
        int preImportance = 0;
        int _id = 0;
        String preTodo = "";
        int preHasAlarm = 0;
        int preAlarmHour = -1;
        int preAlarmMinute = -1;
        String preDate = null;
        while(eventTableCursor.moveToNext()) {
            _id = eventTableCursor.getInt(eventTableCursor.getColumnIndex("_id"));

            preImportance = eventTableCursor.getInt(eventTableCursor.getColumnIndex("IMPORTANCE"));
            Log.i("IMPORTANCE : ", "_id : " + Integer.toString(_id) + ", Importance : " + Integer.toString(preImportance));

            preTodo = eventTableCursor.getString(eventTableCursor.getColumnIndex("MEMO"));
            Log.i("MEMO : ", "_id : " + Integer.toString(_id) + ", Memo : " + preTodo);

            preHasAlarm = eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARM"));
            preAlarmHour = eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARMHOUR"));
            preAlarmMinute = eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARMMINUTE"));
            Log.i("ALARM : ", "_id : " + Integer.toString(_id) + ", HAS ALARM : " + Integer.toString(preHasAlarm) + ", HOUR : " + Integer.toString(preAlarmHour) + ", MINUTE : " + Integer.toString(preAlarmMinute));

            preDate = eventTableCursor.getString(eventTableCursor.getColumnIndex("DATE"));
            Log.i("DATE : ", "_id : " + Integer.toString(_id) + ", DATE : " + preDate);
            while(repeatEventTableCursor.moveToNext()) {
                String repeatEventTableDate = eventTableCursor.getString(repeatEventTableCursor.getColumnIndex("DATE"));
                if(preDate == repeatEventTableDate) {
                    break;
                }
            }

            if(position == _id) break;
        }

        // set star
        if(preImportance == 0) {
            importance = false;
        } else {
            importance = true;
        }
        setImportance();

        // set text
        EditText et = (EditText) findViewById(R.id.alarm_todo_title);
        et.setText(preTodo);

        // set Alarm time
        if(preHasAlarm == 0) {
            hasAlarm = false;
        } else {
            hasAlarm = true;
            alarmHour = preAlarmHour;
            alarmMinute = preAlarmMinute;

            // 알람추가버튼 초기화
            final Button addAlarmTimeButton = (Button) findViewById(R.id.btn_add_alarm_time);
            addAlarmTimeButton.setVisibility(View.INVISIBLE);
            // 새로운 layout 추가
            final LinearLayout inflatedLayout = (LinearLayout) findViewById(R.id.alarmContentLayout);
            LayoutInflater inflater =  (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            inflater.inflate(R.layout.alarm_content_layout, inflatedLayout);
            // Text 추가
            TextView tv = (TextView) findViewById(R.id.alarmTime);
            if (alarmHour < 12) {
                tv.setText("AM " + String.valueOf(alarmHour) + ":" + String.valueOf(alarmMinute) + "에 알리기");
            } else if (alarmHour == 12) {
                tv.setText("PM " + String.valueOf(12) + ":" + String.valueOf(alarmMinute) + "에 알리기");
            } else if (alarmHour < 24) {
                tv.setText("PM " + String.valueOf(alarmHour % 12) + ":" + String.valueOf(alarmMinute) + "에 알리기");
            } else {
                tv.setText("AM " + String.valueOf(12) + ":" + String.valueOf(alarmMinute) + "에 알리기");
            }
            // 삭제버튼 활성화
            Button removeButton = (Button) findViewById(R.id.btn_remove_alarm_time);
            removeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    inflatedLayout.removeAllViews();
                    hasAlarm = false;
                    addAlarmTimeButton.setVisibility(View.VISIBLE);
                }
            });
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
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        for(int i = 0; i < 8; i++) {
            Log.i("alarmSetAlarm", "week[" + Integer.toString(i)+ "] = " + Boolean.toString(week[i]));
        }
        Intent i = getIntent();
        alarmHour = i.getIntExtra("alarmHour", 0);
        alarmMinute = i.getIntExtra("alarmMinute", 0);

        Intent intent = new Intent(AlarmMain.this, AlarmReceiver.class);
        long triggerTime = 0;
        long intervalTime = 24 * 60 * 60 * 1000; // 24시간(ms)

        PendingIntent pending = PendingIntent.getBroadcast(AlarmMain.this, 0, intent, Intent.FILL_IN_DATA);
        intent.putExtra("alarm_days", week);


        triggerTime = setTriggerTime();
        Log.i("triggerTime vs SysTime", Long.toString(triggerTime) + "vs." + Long.toString(System.currentTimeMillis()));

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime /* for Debugging System.currentTimeMillis()+400*/, intervalTime, pending);
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
            newFragment.show(getFragmentManager(), "TimePicker");
            setAlarmButtons();

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

        // Make String for repeatEventTable
        if(daysText != "") {
            repeatDays = daysText.replaceAll("\\s", "");
        }
    }

    private void onBackButtonPress() {
        EditText et = (EditText) findViewById(R.id.alarm_todo_title);
        todo = et.getText().toString();

        Intent i = getIntent();
        int position = i.getIntExtra("position", 1);
        hasAlarm = i.getBooleanExtra("hasAlarm", false);
        alarmHour = i.getIntExtra("alarmHour", 0);
        alarmMinute = i.getIntExtra("alarmMinute", 0);

        ContentValues values = new ContentValues();
        values.put("MEMO", todo);
        values.put("IMPORTANCE", importance);
        values.put("ALARM", hasAlarm);
        values.put("ALARMHOUR", alarmHour);
        values.put("ALARMMINUTE", alarmMinute);
        eventTableController.shiftContentValuesTo(values, position);

        if(repeatDays != null) {
            values.put("DAY_OF_WEEK", repeatDays);
        }
        super.onBackPressed();
    }
}