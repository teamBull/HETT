package com.teambulldozer.hett;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Vibrator;

import java.util.Calendar;

/**
 * Created by yoon on 16. 2. 22..
 */
public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extra = intent.getExtras();

        if (extra != null) {
            boolean[] week = extra.getBooleanArray("days");

            Calendar cal = Calendar.getInstance();

            if (!week[cal.get(Calendar.DAY_OF_WEEK)])
                return;

            // 알람 울리기.
            Vibrator mVibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
            mVibe.vibrate(2000);
        }
    }
}
