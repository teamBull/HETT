package com.teambulldozer.hett;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by GHKwon on 2016-02-23.
 */
public class DrawerTableController {
    /**
     * 싱글톤 패턴 적용함으로써 private static한 자기 자신 객체.
     */
    private static DrawerTableController drawerTableController;
    /**
     * 친구와 대화하는 테이블에 접근하는 객체.
     */
    private HattFriendAskDAO hattFriendAskDAO;

    /**
     * 이벤트 테이블의 객체.
     */
    private DatabaseHelper databaseHelper;

    /**
     * 싱글톤 패턴을 적용함으로써 instance를 get 하기 위하여 호출하는 메소드.
     * 대부분 앱의 첫번째 호출때 호출된다.
     * @param context 호출하는 Activity의 context객체.
     * @return DrawerTableController객체.
     */
    public static DrawerTableController getInstance(Context context) {
        if(drawerTableController==null)
            drawerTableController = new DrawerTableController(context);
        return drawerTableController;
    }

    /**
     * 싱글톤 패턴을 적용함으로써 DrawerTableController instance를 사용하기 위하여 호출하는 메소드.
     * 오버로딩 된 메소드로써, 해당 클래스를 사용하는 두번째 순서부터 호출하게 된다.
     * @return DrawerTableController객체.
     */
    public static DrawerTableController getInstance() {
        return drawerTableController;
    }

    /**
     * 기본 생성자.
     * 기본생성자로 인스턴스를 만들었을 경우 set메소드를 호출하여야 한다.
     */
    private DrawerTableController(){}
    /**
     * 모든 인자를 다 받는 생성자.
     */
    private DrawerTableController(Context context) {
        hattFriendAskDAO = HattFriendAskDAO.getInstance(context);

        databaseHelper = DatabaseHelper.get(context);
    }
    /**
     * 호출하는 액티비티에서 HattFriendASKDAO를 사용하기 위해서
     * Context객체를 새롭게 set하는 메소드.
     * @param context 호출하는 액티비티의 Context객체.
     */
    public DrawerTableController setHattFriendAskDAOContext(Context context) {
        hattFriendAskDAO = HattFriendAskDAO.getInstance(context);
        return this;
    }

    public DrawerTableController setDataBaseHelper(Context context) {
        databaseHelper = DatabaseHelper.get(context);
        return this;
    }
    public int searchByCompleteEvent() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String sql = "select * from event_table where COMPLETENESS=1;";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        int cursorNo = cursor.getCount();
        cursor.close();
        return cursorNo;
    }
    public int searchByRepeatEvent() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String sql = "select * from event_table where repeat=1;";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        int repeatNo = cursor.getCount();
        cursor.close();
        return repeatNo;

    }
    /*
    여기부턴 HattSettingDAO영역.
     */

    /**
     * 전달인자로 받은 friendName을 이용하여 사용자 친구의 이름을 변경한다.
     * @param friendName
     */
    /*public int updateByFriendName(String friendName) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues  = new ContentValues();
        contentValues.put("hatt_friend_name", friendName);
        *//* 3번째 전달인자는 where절의 조건이다 / 4번째 전달인자는 ?로 준 값의 input될 data들.*//*
        int result = sqLiteDatabase.update("HATT_SETTING_TABLE",contentValues,"hatt_setting_code=?",new String[]{"user1"});
        return result;
        *//*SQLiteDatabase sqLiteDatabase = hattSettingDAO.getReadableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("hatt_friend_name",friendName);*//*
    }*/

    /**
     * 사용자가 지정한 가상친구의 이름을 불러올 떄 호출하는 메소드.
     * @return 가상친구의 이름.
     */
    /*public String searchByFriendName() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String sql = "select * from hatt_setting_table;";
        String friendName = "";
        Cursor cursor = null;
        try {
            cursor = sqLiteDatabase.rawQuery(sql,null);
            if(cursor.moveToNext())
                friendName=cursor.getString(cursor.getColumnIndex("hatt_friend_name"));//String 재 생성하면 코드 길어지고 퍼포먼스 떨어져서 sql객체 한번 재활용 했습니다.
            else
                Log.d("error----","searchByFriendName_error");
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                cursor.close();
            } catch(Exception exe) {
                exe.printStackTrace();
            }
        }
        return friendName;
    }*/
    public int searchByBackgroundThemeCodeNo() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String sql = "select * from hatt_background_theme_table where is_selected=1;";

        int selectedBackgroundCode=0;
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        if(cursor.moveToNext())
            selectedBackgroundCode = cursor.getInt(cursor.getColumnIndex("background_code"));
        cursor.close();
        return selectedBackgroundCode;
    }

    /*public boolean updatePushMode(boolean isPushMode) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        if(isPushMode)
            contentValues.put("is_push_alarm",1);
        else
            contentValues.put("is_push_alarm",0);
        sqLiteDatabase.update("HATT_SETTING_TABLE",contentValues,"hatt_setting_code=?",new String[]{"user1"});
        return true;
    }*/
    /*public boolean searchPushMode() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String sql = "select * from HATT_SETTING_TABLE;";
        Cursor cursor = null ;
        boolean isPushMode;
        try {
            cursor= sqLiteDatabase.rawQuery(sql,null);
            if(cursor.moveToNext())
                if(cursor.getInt(cursor.getColumnIndex("is_push_alarm"))==1 )
                    return true; //알람 모드일 경우.
                else
                    return false; // 알람 모드가 아닐경우.
        } catch(Exception ex) {
            ex.printStackTrace();
        } finally {
            try {
                cursor.close();
            } catch(Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }*/
    public ArrayList<BackgroundThemeDTO> searchBackbroundThemeDTOAllData(){
        ArrayList<BackgroundThemeDTO> arrayList = new ArrayList<BackgroundThemeDTO>();
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String sql = "select * from hatt_background_theme_table;";
        Cursor cursor = null;
        cursor = sqLiteDatabase.rawQuery(sql,null);
        while( cursor . moveToNext() ) {
            arrayList.add(new BackgroundThemeDTO( cursor.getInt(0),cursor.getString(1),cursor.getInt(2),cursor.getInt(3) ));
        }
        cursor.close();
        return arrayList;
    }
    public byte updateSelectedBackgroundTheme(int i) {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getWritableDatabase();
        String sql = "update hatt_background_theme_table set is_selected=0";
        sqLiteDatabase.execSQL(sql);
        sql = "update hatt_background_theme_table set is_selected=1 where background_code="+i;
        sqLiteDatabase.execSQL(sql);
        return 0;
    }
    public String searchSelectedBackgroundTheme() {
        SQLiteDatabase sqLiteDatabase = databaseHelper.getReadableDatabase();
        String sql = "select * from hatt_background_theme_table where is_selected=1";
        Cursor cursor = sqLiteDatabase.rawQuery(sql,null);
        cursor.moveToNext();
        String theme = cursor.getString(cursor.getColumnIndex("background_theme_name"));
        cursor.close();
        return theme;
    }



}
