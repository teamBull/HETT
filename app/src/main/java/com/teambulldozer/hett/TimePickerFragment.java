package com.teambulldozer.hett;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener{

    Intent i;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState){
        //Use the current time as the default values for the time picker
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        i = getActivity().getIntent();

        //Create and return a new instance of TimePickerDialog
        return new TimePickerDialog(getActivity(),this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    //onTimeSet() callback method
    public void onTimeSet(TimePicker view, int hourOfDay, int minute){
        // 알람시간 넘겨주기
        i.putExtra("alarmHour", hourOfDay);
        i.putExtra("alarmMinute", minute);
        i.putExtra("hasAlarm", true);
        Log.i("has Alarm in fragment", Boolean.toString(i.getBooleanExtra("hasAlarm", false)));

        // 알람추가버튼 초기화
        final Button addAlarmTimeButton = (Button) getActivity().findViewById(R.id.btn_add_alarm_time);

        // 새로운 layout 추가
        final LinearLayout inflatedLayout = (LinearLayout) getActivity().findViewById(R.id.alarmContentLayout);
        LayoutInflater inflater =  (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.alarm_content_layout, inflatedLayout);

        // 알람 텍스트 갱신
        TextView tv = (TextView) getActivity().findViewById(R.id.alarmTime);
        if(hourOfDay < 12) {
            tv.setText("AM " + String.valueOf(hourOfDay) + ":" + String.valueOf(minute) + "에 알리기");
        } else if (hourOfDay == 12) {
            tv.setText("PM " + String.valueOf(12) + ":" + String.valueOf(minute) + "에 알리기");
        } else if (hourOfDay < 24) {
            tv.setText("PM " + String.valueOf(hourOfDay%12) + ":" + String.valueOf(minute) + "에 알리기");
        } else {
            tv.setText("AM " + String.valueOf(12) + ":" + String.valueOf(minute) + "에 알리기");
        }

        // 삭제버튼 활성화
        Button removeButton = (Button) getActivity().findViewById(R.id.btn_remove_alarm_time);
        removeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inflatedLayout.removeAllViews();
                resetAlarmVar();
                addAlarmTimeButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void resetAlarmVar() {
        i.putExtra("alarmHour", 0);
        i.putExtra("alarmMinute", 0);
        i.putExtra("hasAlarm", false);
    }
}