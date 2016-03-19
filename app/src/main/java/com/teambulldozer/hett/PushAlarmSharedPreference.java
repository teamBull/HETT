package com.teambulldozer.hett;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by adminstrator on 2016-03-19.
 */
public class PushAlarmSharedPreference {
    private static PushAlarmSharedPreference pushAlarmSharedPreference;
    static {
        pushAlarmSharedPreference = new PushAlarmSharedPreference();
    }
    public static PushAlarmSharedPreference getInstance() {return pushAlarmSharedPreference; }
    public boolean isFirstPushAlarm(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("pushAlarm",context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("first",false);
    }
    public boolean isSecondPushAlarm(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("pushAlarm",context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("second",false);
    }
    public boolean isThirdPushAlarm(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("pushAlarm",context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("third",false);
    }
    public void useFirstPushAlarm(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("pushAlarm",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("first",false);
        editor.commit();
    }
    public void useSecondPushAlarm(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("pushAlarm",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("second",false);
        editor.commit();
    }

    public void useThirdPushAlarm(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("pushAlarm",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("third",false);
        editor.commit();
    }
    public boolean isSearchPushAlarm(Context context,String where) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("pushAlarm",context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(where,false);
    }
    public void usePushAlarm(Context context,String where) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("pushAlarm",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(where,false);
        editor.commit();
    }
}
