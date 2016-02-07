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

    public static final String DATABASE_NAME = "Event.db"; // case-insensitive -데이터베이스 이름.
    public static final String TABLE_NAME = "event_table"; // 테이블명

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
    /**
     * event table에 data를 inesrt하는 메소드.
     * @param memo insert할 Memo 내용.
     * @return insert 성공시 ture 리턴. 그 외엔 -1 리턴.
     */
    public boolean insertData(String memo){
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, memo);
        //DB에 insert하기 위해서는 ContentValues 객체가 필요하다. ContentValues- hash로 이루어짐.
        long result = db.insert(TABLE_NAME, null, contentValues); // takes 3 arguments..
        if(result == -1)
            return false;
        return true;
    }

    /**
     * event table의 한 row를 delete하는 메소드.
     * @param id event_table의 삭제할 primary key number.
     * @return 삭제된 컬럼 갯수.
     */
    public Integer deleteData(String id){ // Since id is a primary key
        //getWriteableDatabase는 database를 write하기 위하여 SQLiteDatabase객체를 retrun하는것.
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        return db.delete(TABLE_NAME, "_id = ?", new String[] { id });
    }

    /**
     * 테이블의 한 튜플(row)가 삭제될 때, id(event_table)의 number를 하나 낮춰서 update.
     * @param id 삭제될 database의 primary key number.
     */
    public void rearrangeData(String id){
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        db.execSQL("UPDATE " + TABLE_NAME + " SET _id = (_id - 1) WHERE _id > " + id); // 삭제되는 id보다 높은 번호들을 다 -1해서 저장.
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='event_table'"); // 시퀀스 넘버를 0으로 초기화..???????
    }

    /**
     * event_table의 모든 row를 Cursor로 return하는 메소드.
     * @return event_table의 모든 row정보가 담긴 Cursor
     */
    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        Cursor res = db.rawQuery("select rowid _id, * from " + TABLE_NAME, null);
        // SQLiteDatabase class provides result sets as Cursor instances.
        return res;

    }
}
