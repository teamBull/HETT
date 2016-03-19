package com.teambulldozer.hett;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;

import java.util.Map;
import java.util.Set;

/**
 * Created by adminstrator on 2016-03-15.
 */
public class HETTSettingSharedPreference {
    private static final HETTSettingSharedPreference instance;

    static {
        instance = new HETTSettingSharedPreference();
    }
    private HETTSettingSharedPreference(){}

    public static HETTSettingSharedPreference getInstance() {
        return instance;
    }
    public boolean updateHattFriendName(Context context , String newName) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("hett",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("hattFriendName",newName);
        editor.commit();
        return true;
    }
    public String searchHattFriendName(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("hett",context.MODE_PRIVATE);
        return sharedPreferences.getString("hattFriendName","Hatti");// 호출하는 키에 대응하는 값이 없을 경우 디폴트(Hatti)가 return됨.
    }
    public void updatePushAlarm(Context context,boolean isPushAlarm) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("hett",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("pushAlarm",isPushAlarm);
        editor.commit();
    }
    public boolean searchPushAlarm(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("hett",context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("pushAlarm",false);
    }
    public void updateBellMode(Context context,boolean bellMode) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("hett",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean("bellMode",bellMode);
        editor.commit();
    }
    public boolean searchBellMode(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("hett",context.MODE_PRIVATE);
        return sharedPreferences.getBoolean("bellMode",true);
    }
    public int searchLastUpdateDay(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("hett",context.MODE_PRIVATE);
        return sharedPreferences.getInt("lastUpdateDay", 0);
    }
    public boolean updateLastUpdateDay(Context context,int day) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("hett",context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("lastUpdateDay",day);
        return true;
    }
}
