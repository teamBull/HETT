package com.teambulldozer.hett;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

/**
 * Created by SEONGBONG on 2016-02-26.
 */
public class RepeatEventController {
    private static RepeatEventController mRepeatEventController;
    DatabaseHelper dbhelper;
    static final String TABLE = "repeat_table";

    static class Columns {
        static final String ID = "_id"; // CursorAdapter에서 id명은 반드시 _id. 수정하면 안된다.
        static final String MEMO = "MEMO";
        static final String IMPORTANCE = "IMPORTANCE";
        static final String DAY_OF_WEEK = "DAY_OF_WEEK";
        static final String ALARM = "ALARM";
        static final String ALARMHOUR = "ALARMHOUR";
        static final String ALARMMINUTE = "ALARMMINUTE";
    }
    private RepeatEventController(Context context){
        dbhelper = DatabaseHelper.get(context);
    }
    public static RepeatEventController get(Context context){
        if(mRepeatEventController == null){
            mRepeatEventController = new RepeatEventController(context);
        }
        return mRepeatEventController;
    }
    //insert
    public boolean insertToRepeatTable(String id, String memo, int importance, String dayOfWeek,int alarm, int alarmHour,int alarmMinute){
        SQLiteDatabase sqLiteDatabase = dbhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.ID,id);
        contentValues.put(Columns.MEMO, memo);
        contentValues.put(Columns.IMPORTANCE,importance);
        contentValues.put(Columns.DAY_OF_WEEK, dayOfWeek);
        contentValues.put(Columns.ALARM, alarm);
        contentValues.put(Columns.ALARMHOUR, alarmHour);
        contentValues.put(Columns.ALARMMINUTE, alarmMinute);

        long result = sqLiteDatabase.insert(TABLE, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    //delete
    public Integer deleteData(String id){ // Since id is a primary key
        SQLiteDatabase db = dbhelper.getWritableDatabase(); // It is going to create your database and table.
        return db.delete(TABLE, "_id = ?", new String[] { id });
    }

    public void deleteAllData(){
        SQLiteDatabase db = dbhelper.getWritableDatabase(); // It is going to create your database and table.
        db.execSQL("DELETE FROM " + TABLE);
    }
    //select
    public Cursor getEventRepeatData(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        String sql = "SELECT * FROM "+ TABLE +" ORDER BY _id ASC";
        Cursor res = db.rawQuery(sql, null);
        return res;
    }

    //준상이는 이 메소드 쓰면 됨
    public Cursor getTodoRepeatData(String dayOfWeek){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String sql = "SELECT * FROM "+ TABLE +" WHERE DAY_OF_WEEK like '%"+dayOfWeek+"%' ORDER BY _id ASC";
        Cursor res = db.rawQuery(sql, null);
        return res;
    }

    public int getEventImportance(String id){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String sql = "SELECT * from " + TABLE + " where _id = ?";
        String[] searchId = {id};
        Cursor cursor = db.rawQuery(sql,searchId);
        cursor.moveToFirst();

        return cursor.getInt(cursor.getColumnIndex("IMPORTANCE"));
    }

    public String getDayOfWeek(String id){
        // ID값을 받아서 반복일정 스트링을 받아오는 함수
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String sql = "SELECT " + Columns.DAY_OF_WEEK + " from " + TABLE + " where _id = ?";
        String[] searchId = {id};
        Cursor cursor = db.rawQuery(sql,searchId);

        if(cursor.getCount() == 0) {
            return "No Such Data";
        } else {
            cursor.moveToFirst();
            return cursor.getString(cursor.getColumnIndex(Columns.DAY_OF_WEEK));
        }
    }

    //update
    public int updateImportances(String id, int importance){

        Log.d("importance값:", String.valueOf(importance));
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(importance == 1){
            values.put(Columns.IMPORTANCE, 0);
        }else{
            values.put(Columns.IMPORTANCE, 1);
        }
        return db.update(TABLE, values, " _id = ?", new String[]{id});
    }

    // Joonsang method.
    public int updateImportance(String id, int importance){

        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Columns.IMPORTANCE, importance);

        return db.update(TABLE, values, " _id = ?", new String[]{id});
    }


    //태훈아 이메소드 쓰면 됭
    public int updateRepeatTable(String id, ContentValues contentValues){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        return db.update(TABLE,contentValues," _id = ?",new String[]{id});
    }

    public int numOfEntries() // int
    {
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        return (int)DatabaseUtils.queryNumEntries(db, TABLE);
    }
}
