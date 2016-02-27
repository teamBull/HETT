package com.teambulldozer.hett;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.teambulldozer.hett.DatabaseHelper;

/**
 * Created by SEONGBONG on 2016-02-23.
 */
public class CompleteEventTableController {
    private static CompleteEventTableController mCompleteEventTableController;
    DatabaseHelper dbhelper;
    static final String COMPLETE_TABLE = "event_complete_table";

    static class Columns {
        static final String ID = "_id";
        static final String MEMO = "MEMO";
        static final String COMPLETENESS = "COMPLETENESS";
        static final String DATE = "DATE";
    }
    private CompleteEventTableController(Context context){
        dbhelper = DatabaseHelper.get(context);
    }
    public static CompleteEventTableController get(Context context){
        if(mCompleteEventTableController == null){
            mCompleteEventTableController = new CompleteEventTableController(context);
        }
        return mCompleteEventTableController;
    }
    //insert
    public boolean insertToEventCompletenessTable(int id, String memo, int completeness,int date){
        SQLiteDatabase sqLiteDatabase = dbhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.ID,id);
        contentValues.put(Columns.MEMO, memo);
        contentValues.put(Columns.COMPLETENESS,completeness);
        contentValues.put(Columns.DATE, date);

        long result = sqLiteDatabase.insert("event_complete_table", null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    //delete
    public Integer deleteData(String id){ // Since id is a primary key
        SQLiteDatabase db = dbhelper.getWritableDatabase(); // It is going to create your database and table.
        return db.delete("event_complete_table", "_id = ?", new String[] { id });
    }

    public void deleteAllData(){
        SQLiteDatabase db = dbhelper.getWritableDatabase(); // It is going to create your database and table.
        db.execSQL("DELETE FROM " + "event_complete_table");
    }

    public Cursor getEventTableCompleteData(){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String sql = "SELECT * FROM event_complete_table where completeness == ? ORDER BY DATE ASC";
        String[] completeness = {"1"};
        Cursor res = db.rawQuery(sql, completeness);
        return res;
    }

    public void rearrangeData(String id){
        SQLiteDatabase db = dbhelper.getWritableDatabase(); // It is going to create your database and table.
        db.execSQL("UPDATE event_complete_table SET _id = (_id - 1) WHERE _id > " + id);
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='event_complete_table'");
    }

}
