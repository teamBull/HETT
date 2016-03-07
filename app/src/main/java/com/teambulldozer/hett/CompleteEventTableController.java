package com.teambulldozer.hett;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.teambulldozer.hett.DatabaseHelper;

/**
 * Created by SEONGBONG on 2016-02-23.
 */
public class CompleteEventTableController {
    private static CompleteEventTableController mCompleteEventTableController;
    DatabaseHelper dbhelper;
    static final String TABLE = "event_complete_table";

    static class Columns {
        static final String ID = "_id";
        static final String MEMO = "MEMO";
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
    public boolean insertToEventCompletenessTable(String id, String memo){
        SQLiteDatabase sqLiteDatabase = dbhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.ID,id);
        contentValues.put(Columns.MEMO, memo);

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

    public Cursor getEventTableCompleteData(){
        SQLiteDatabase db = dbhelper.getReadableDatabase();
        String sql = "SELECT * FROM "+ TABLE +" ORDER BY _id ASC";

        Cursor res = db.rawQuery(sql, null);
        return res;
    }
}
