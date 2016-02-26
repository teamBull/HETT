package com.teambulldozer.hett;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by SEONGBONG on 2016-02-26.
 */
public class RepeatEventController {
    private static RepeatEventController mRepeatEventController;
    DatabaseHelper dbhelper;
    static final String EVENT_TABLE = "event_table";
    static class Columns {
        static final String ID = "_id"; // CursorAdapter에서 id명은 반드시 _id. 수정하면 안된다.
        static final String MEMO = "MEMO";
        static final String IMPORTANCE = "IMPORTANCE";
        static final String COMPLETENESS = "COMPLETENESS";
        static final String DATE = "DATE";
        static final String REPEAT = "REPEAT";
        static final String ALARM = "ALARM";
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
    public boolean insertToEventTable(int id, String memo, int importance, int completeness,int date,int repeat,int alarm){
        SQLiteDatabase sqLiteDatabase = dbhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.ID,id);
        contentValues.put(Columns.MEMO, memo);
        contentValues.put(Columns.IMPORTANCE,importance);
        contentValues.put(Columns.COMPLETENESS,completeness);
        contentValues.put(Columns.DATE, date);
        contentValues.put(Columns.REPEAT,repeat);
        contentValues.put(Columns.ALARM, alarm);

        long result = sqLiteDatabase.insert("event_table", null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    //delete
    public Integer deleteData(String id){ // Since id is a primary key
        SQLiteDatabase db = dbhelper.getWritableDatabase(); // It is going to create your database and table.
        unRepeatDate(id, db);
        return db.delete("repeat_table", "_id = ?", new String[] { id });
    }

    public void deleteAllData(){
        SQLiteDatabase db = dbhelper.getWritableDatabase(); // It is going to create your database and table.
        unRepeatAllData(db);
        db.execSQL("DELETE FROM " + "repeat_table");
    }

    public Cursor getEventRepeatData(){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String sql = "SELECT E._ID,MEMO,IMPORTANCE,DATE,DAY_OF_WEEK FROM event_table e,repeat_table r where e.repeat = ? AND e._id = r._id ORDER BY E.DATE ASC";
        String[] completeness = {"1"};
        Cursor res = db.rawQuery(sql, completeness);
        return res;
    }
    //update
    public int updateImportances(String id,int importance){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        if(importance == 1){
            importance = 0;
        }else{
            importance =1;
        }
        values.put(Columns.IMPORTANCE, importance);
        return db.update(EVENT_TABLE, values, " _id = ?", new String[] { id });
    }
    public void unRepeatDate(String id){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.execSQL("UPDATE event_table set repeat = 0 where _id = " + id);
    }
    public void unRepeatDate(String id,SQLiteDatabase db){
        db.execSQL("UPDATE event_table set repeat = 0 where _id = " + id);
    }
    public void unRepeatAllData(){
        SQLiteDatabase db = dbhelper.getWritableDatabase();
        db.execSQL("UPDATE event_table set repeat = 0");
    }
    public void unRepeatAllData(SQLiteDatabase db){
        db.execSQL("UPDATE event_table set repeat = 0");
    }
}
