package com.teambulldozer.hett;

/**
 * Created by flecho on 2016. 2. 7..
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by flecho on 2016. 2. 3..
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Event.db"; // case-insensitive
    public static final String TABLE_NAME = "event_table";

    // There are only two columns
    public static final String COL_1 = "_id"; // Cursor 는 무조건 _ID가 있어야...
    public static final String COL_2 = "MEMO";


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        // And, if you want to create a table inside your DB,
        db.execSQL("create table " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, MEMO TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String memo){
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, memo);
        long result = db.insert(TABLE_NAME, null, contentValues); // takes 3 arguments..
        if(result == -1)
            return false;
        else
            return true;
    }

    public Integer deleteData(String id){ // Since id is a primary key
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        return db.delete(TABLE_NAME, "_id = ?", new String[] { id });
    }

    public void rearrangeData(String id){
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        db.execSQL("UPDATE " + TABLE_NAME + " SET _id = (_id - 1) WHERE _id > " + id);
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='event_table'");
    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        Cursor res = db.rawQuery("select rowid _id, * from " + TABLE_NAME, null);
        // SQLiteDatabase class provides result sets as Cursor instances.
        return res;

    }
}
