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
        return sharedPreferences.getBoolean("first",true);
    }
    public boolean isSecondPushAlarm(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("pushAlarm",context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("second",true);
    }
    public boolean isThirdPushAlarm(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("pushAlarm",context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("third",true);
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
    public int decreasePushNo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("pushAlarm",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("registerPushAlarmNo", searchPushNo(context)-1);
        editor.commit();
        return searchPushNo(context);
    }
    public int searchPushNo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("pushAlarm",context.MODE_PRIVATE);
        return sharedPreferences.getInt("registerPushAlarmNo",3);
    }
}
