package com.teambulldozer.hett;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.DialogFragment;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
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

// need refactoring...
public class AlarmMain extends Activity implements OnClickListener {

    // Log TAG
    private static final String TAG = "AlarmMain";

    // Values for internal settings
    String todo = "";
    boolean importance = false;
    boolean hasAlarm = false;
    int alarmHour, alarmMinute;
    boolean noRepeat = true;
    boolean mon, tue, wed, thu, fri, sat, sun = false;
    int numOfAlarm = 0;

    // Button components
    private Button[] alarmButtons;

    // Values for outer
    boolean[] week = {noRepeat, mon, tue, wed, thu, fri, sat, sun};

    // DB Info
    String syncID = null; // Date is syncID for event and repeat table.
    EventTableController eventTableController = EventTableController.get(this);
    RepeatEventController repeatEventTableController = RepeatEventController.get(this);
    String repeatDays = null;

    // font
    Typeface NanumSquare_B;
    Typeface NanumBarunGothic_R;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_main);

        // font init
        NanumSquare_B = Typeface.createFromAsset(getAssets(), "NanumSquare_Bold.ttf");
        NanumBarunGothic_R = Typeface.createFromAsset(getAssets(), "NanumBarunGothic_Regular.ttf");
        setFont();

        // component init
        setAlarmButtons(); // set button components

        // data init
        countNumOfAlarm();
        initData();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_set_alarm:
                setAlarm();
                break;

            case R.id.btn_release_alarm:
                releaseAlarm(this);
                break;

            case R.id.btn_add_alarm_time:
                addAlarmTime();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        onBackButtonPress();
    }

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

    private void initData() {
        Intent i = getIntent();
        int position = i.getIntExtra("position", 1);

        Cursor eventTableCursor = eventTableController.getAllData();
        Cursor repeatEventTableCursor = repeatEventTableController.getEventRepeatData();

        // set pre-Importance, set pre-Todo
        int preImportance = 0;
        int _id = 0;
        String preTodo = "";
        int preHasAlarm = 0;
        int preAlarmHour = -1;
        int preAlarmMinute = -1;
        String preDate = null;
        String preDays = null;

        eventTableCursor.move(-1);
        while(eventTableCursor.moveToNext()) {
            _id = eventTableCursor.getInt(eventTableCursor.getColumnIndex("_id"));

            preImportance = eventTableCursor.getInt(eventTableCursor.getColumnIndex("IMPORTANCE"));
            preTodo = eventTableCursor.getString(eventTableCursor.getColumnIndex("MEMO"));
            preHasAlarm = eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARM"));
            preAlarmHour = eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARMHOUR"));
            preAlarmMinute = eventTableCursor.getInt(eventTableCursor.getColumnIndex("ALARMMINUTE"));
            preDate = eventTableCursor.getString(eventTableCursor.getColumnIndex("DATE"));

            Log.i(TAG, "_id : " + Integer.toString(_id) + ", Memo : " + preTodo + ", Importance : " + Integer.toString(preImportance)
                    + ", HAS ALARM : " + Integer.toString(preHasAlarm) + ", HOUR : " + Integer.toString(preAlarmHour) + ", MINUTE : " + Integer.toString(preAlarmMinute)
                    + ", DATE : " + preDate);
            syncID = preDate;

            if(position == _id) break;
        }
        if(preDate !=  null) {
            repeatEventTableCursor.move(-1);
            while (repeatEventTableCursor.moveToNext()) {
                String repeatEventTableDate = repeatEventTableCursor.getString(repeatEventTableCursor.getColumnIndex("_id"));
                Log.i(TAG, syncID + "vs" + repeatEventTableDate);
                if (preDate.equals(repeatEventTableDate)) {
                    Log.i(TAG, "found match in repeat event table");
                    preImportance = repeatEventTableCursor.getInt(repeatEventTableCursor.getColumnIndex("IMPORTANCE"));
                    preTodo = repeatEventTableCursor.getString(repeatEventTableCursor.getColumnIndex("MEMO"));
                    preHasAlarm = repeatEventTableCursor.getInt(repeatEventTableCursor.getColumnIndex("ALARM"));
                    preAlarmHour = repeatEventTableCursor.getInt(repeatEventTableCursor.getColumnIndex("ALARMHOUR"));
                    preAlarmMinute = repeatEventTableCursor.getInt(repeatEventTableCursor.getColumnIndex("ALARMMINUTE"));
                    preDays = repeatEventTableCursor.getString(repeatEventTableCursor.getColumnIndex("DAY_OF_WEEK"));
                    break;
                }
                Log.i(TAG, "Not found in repeat event table");
            }
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

        // set Repeat days
        if(preDays == null) {
            noRepeat = true;
        } else {
            noRepeat = false;
            if(preDays.contains("월")) {mon = true;}
            if(preDays.contains("화")) {tue = true;}
            if(preDays.contains("수")) {wed = true;}
            if(preDays.contains("목")) {thu = true;}
            if(preDays.contains("금")) {fri = true;}
            if(preDays.contains("토")) {sat = true;}
            if(preDays.contains("일")) {sun = true;}
        }
        printCheckedDays();
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
    private void setAlarm() {
        Log.i(TAG, "setAlarm()");
        /* 지정한 시간 이후에 알람이 울리게 함(for practice)
        AlarmManager alarmManager = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);

        Intent Intent = new Intent(INTENT_ACTION);
        PendingIntent pIntent = getActivity(context, 0, Intent, 0);

        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + second, pIntent);
        */
        /* 반복적인 알람 설정에 관한 함수(하나밖에 설정이 안됨 -> alarm manager를 array로 선언해야함)
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        for(int i = 0; i < 8; i++) {
            Log.i("alarmSetAlarm", "week[" + Integer.toString(i)+ "] = " + Boolean.toString(week[i]));
        }
        Intent i = getIntent();

        // if time picker is not clicked
        if((i.getIntExtra("alarmHour", -1) == -1) && (i.getIntExtra("alarmMinute", -1) == -1)) {
            // Do not change values of alarm hour, minute, hasAlarm.
        } else {
            hasAlarm = i.getBooleanExtra("hasAlarm", false);
            alarmHour = i.getIntExtra("alarmHour", -1);
            alarmMinute = i.getIntExtra("alarmMinute", -1);
        }

        Intent intent = new Intent(AlarmMain.this, AlarmReceiver.class);
        long triggerTime = 0;
        long intervalTime = 24 * 60 * 60 * 1000; // 24시간(ms)

        PendingIntent pending = PendingIntent.getBroadcast(AlarmMain.this, 0, intent, Intent.FILL_IN_DATA);

        triggerTime = setTriggerTime();
        Log.i("triggerTime vs SysTime", Long.toString(triggerTime) + "vs." + Long.toString(System.currentTimeMillis()));

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, triggerTime for Debugging System.currentTimeMillis()+400, intervalTime, pending);
        */
        // alarm 다수 설정 가능 ver.
        countNumOfAlarm();
        numOfAlarm++;

        PendingIntent[] pending = new PendingIntent[numOfAlarm];
        Intent[] intent = new Intent[numOfAlarm];
        AlarmManager[] alarmManagers = new AlarmManager[numOfAlarm];
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
        } else {
            repeatDays = null;
        }
    }

    private void onBackButtonPress() {
        Cursor repeatEventTableCursor = repeatEventTableController.getEventRepeatData();
        repeatEventTableCursor.move(-1);

        EditText et = (EditText) findViewById(R.id.alarm_todo_title);
        todo = et.getText().toString();

        Intent i = getIntent();
        int position = i.getIntExtra("position", 1);
        // If TimePickerFragment is not clicked
        if((i.getIntExtra("alarmHour", -1) == -1) && (i.getIntExtra("alarmMinute", -1) == -1)) {
            // Do not change values of alarm hour, minute, hasAlarm.
        } else {
            hasAlarm = i.getBooleanExtra("hasAlarm", false);
            alarmHour = i.getIntExtra("alarmHour", -1);
            alarmMinute = i.getIntExtra("alarmMinute", -1);
        }
        int putHasAlarm = 0;
        int putImportance = 0;
        if(hasAlarm == true) {
            putHasAlarm = 1;
        } else {
            releaseAlarm(getBaseContext());
        }
        if(importance == true) {
            putImportance = 1;
        }
        Log.i(TAG, "alarmHour:alarmMinute" + Integer.toString(alarmHour) + ":" + Integer.toString(alarmMinute));

        /*
        Update tables
         */
        ContentValues values = new ContentValues();
        values.put("MEMO", todo);
        values.put("IMPORTANCE", putImportance);
        values.put("ALARM", putHasAlarm);
        values.put("ALARMHOUR", alarmHour);
        values.put("ALARMMINUTE", alarmMinute);

        // repeat table에서 아이디값을 찾아본 후 있으면 update 그렇지 않으면 insert
        if(repeatDays != null) {
            Calendar c = Calendar.getInstance();
            int curDay = c.get(Calendar.DAY_OF_WEEK);
            if (repeatDays.contains(intDayToStr(curDay))) {
                values.put("REPEAT", 1);
                eventTableController.shiftContentValuesTo(values, position); // Update event table
            } else {
                eventTableController.deleteData(Integer.toString(position));
                eventTableController.rearrangeData(Integer.toString(position));
            }

            ContentValues rValues = new ContentValues();
            rValues.put("MEMO", todo);
            rValues.put("IMPORTANCE", putImportance);
            rValues.put("ALARM", putHasAlarm);
            rValues.put("ALARMHOUR", alarmHour);
            rValues.put("ALARMMINUTE", alarmMinute);
            rValues.put("_id", syncID);
            rValues.put("DAY_OF_WEEK", repeatDays);

            while(repeatEventTableCursor.moveToNext()) {
                if(repeatEventTableCursor.getString(repeatEventTableCursor.getColumnIndex("_id")).equals(syncID)) {
                    repeatEventTableController.updateRepeatTable(syncID, rValues);
                    Log.i(TAG, "Update repeat Table");
                    if(hasAlarm) {
                        setAlarm();
                    }
                    super.onBackPressed();
                    return;
                }
            }

            repeatEventTableController.insertToRepeatTable(syncID, todo, putImportance, repeatDays, putHasAlarm, alarmHour, alarmMinute);
            Log.i(TAG, "Insert repeat Table");
        } else {
            values.put("REPEAT", 1);
            eventTableController.shiftContentValuesTo(values, position); // Update event table

            while(repeatEventTableCursor.moveToNext()) {
                if (repeatEventTableCursor.getString(repeatEventTableCursor.getColumnIndex("_id")).equals(syncID)) {
                    repeatEventTableController.deleteData(syncID);
                    Log.i(TAG, "Delete repeat Table");
                }
            }

        }

        if(hasAlarm) {
            setAlarm();
        }

        super.onBackPressed();
    }

    private void setFont(){
        ((EditText)findViewById(R.id.alarm_todo_title)).setTypeface(NanumSquare_B);
        ((TextView)findViewById(R.id.alarmInAlarm)).setTypeface(NanumSquare_B);
        ((TextView)findViewById(R.id.repeatInAlarm)).setTypeface(NanumSquare_B);
        ((TextView)findViewById(R.id.noRepeatTextInAlarm)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.monTextInAlarm)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.tueTextInAlarm)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.wedTextInAlarm)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.thuTextInAlarm)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.friTextInAlarm)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.satTextInAlarm)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.sunTextInAlarm)).setTypeface(NanumBarunGothic_R);
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

}