package com.teambulldozer.hett;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by flecho on 2016. 3. 9..
 */
public class DateController {
    private static DateController mDateController;
    DatabaseHelper myDb;
    static final String TABLE = "today_table";

    static class Columns {
        static final String ID = "_id"; // CursorAdapter에서 id명은 반드시 _id. 수정하면 안된다.
        static final String TODAY = "TODAY";
    }
    private DateController(Context context){
        myDb = DatabaseHelper.get(context);
    }

    public static DateController get(Context context){
        if(mDateController == null){
            mDateController = new DateController(context);
        }
        return mDateController;
    }
    //insert는 최초에 한 번만 일어난다.
    public boolean insertToTodayTable(String today){
        SQLiteDatabase db = myDb.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.TODAY, today);

        long result = db.insert(TABLE, null, contentValues);
        if(result == -1)
            return false;
        else
            return true;
    }

    public String getDateInfo(){
        SQLiteDatabase db = myDb.getReadableDatabase();
        String[] columns = { Columns.ID, Columns.TODAY };
        Cursor cursor = db.query(TABLE, columns, null, null, null, null, null);

        cursor.moveToFirst();

        try {
            do {
                if ("1".equals(cursor.getString(0)))
                    return cursor.getString(1);

            } while (cursor.moveToNext());

        } finally {
            if(cursor != null)
                cursor.close();
        }
        return "no such data";
        //return cursor.getString(1);
    }

    //delete
    public Integer deleteData(String id){ // Since id is a primary key
        SQLiteDatabase db = myDb.getWritableDatabase(); // It is going to create your database and table.
        return db.delete(TABLE, "_id = ?", new String[]{id});
    }

    public int updateToday(String id, String today){

        SQLiteDatabase db = myDb.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(Columns.TODAY, today);

        return db.update(TABLE, values, " _id = ?", new String[]{id});
    }


    public int numOfEntries() // int
    {
        SQLiteDatabase db = myDb.getReadableDatabase();
        return (int) DatabaseUtils.queryNumEntries(db, TABLE);
    }
}
