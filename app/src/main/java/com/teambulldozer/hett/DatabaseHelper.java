package com.teambulldozer.hett;

/**
 * Created by flecho on 2016. 2. 7..
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
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
    public static final String COL_3 = "IMPORTANCE";
    public static final String COL_4 = "COMPLETENESS";


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        // And, if you want to create a table inside your DB,
        db.execSQL("create table " + TABLE_NAME + " (_id INTEGER PRIMARY KEY AUTOINCREMENT, MEMO TEXT, IMPORTANCE TEXT, COMPLETENESS TEXT);");

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public boolean insertData(String memo, boolean completeness){
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, memo);
        contentValues.put(COL_3, "false"); // 입력 단계에서 중요 설정 할 수 없으므로, default 값은 false.

        if(completeness)
            contentValues.put(COL_4, "1"); // 입력 단계에서 완수여부 마찬가지로 설정할 수 없음.
        else
            contentValues.put(COL_4, "0"); // 입력 단계에서 완수여부 마찬가지로 설정할 수 없음.

        long result = db.insert(TABLE_NAME, null, contentValues); // takes 3 arguments..
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getCompletenessData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_1, COL_4 };
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

    public boolean isThereCompletedData(){
        Cursor cursor = getCompletenessData();
        cursor.moveToFirst();

        try {
            do {
                if(cursor.getString(1).equals("1")) // "1" means it is completed.
                    return true;
                else
                    continue;
            } while (cursor.moveToNext());
        } finally {

            if(cursor != null)
                cursor.close();

        }
        return false;
    }

    // 커서는 id와 별개로 움직이나?
    public boolean isCompleted(int position){ // maybe position_sync?
        Cursor cursor = getCompletenessData();
        cursor.moveToFirst();

        try {
            do {
                if (Integer.toString(position).equals(cursor.getString(0))) {
                    if(cursor.getString(1).equals("1")) // "1" means it is completed.
                        return true;
                    else
                        return false;
                }

            } while (cursor.moveToNext());

        } finally {
            if(cursor != null)
                cursor.close();
        }


        return false;
    }


    public Cursor getMemoData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_1, COL_2 };
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

    public String getMemoAt(int position){
        Cursor cursor = getMemoData();
        cursor.moveToFirst();
        try{
            do {
                if (Integer.toString(position).equals(cursor.getString(0)))
                    return cursor.getString(1);

            } while (cursor.moveToNext());
        } finally {
            if(cursor != null)
                cursor.close();
        }

        return "Error";
    }

    public Cursor getImportanceData(){
        SQLiteDatabase db = this.getReadableDatabase();
        String[] columns = { COL_1, COL_3 };
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }


    public boolean updateData(String id, String memo, String importance, String completeness){
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        ContentValues values = new ContentValues();
        values.put(COL_1, id);
        //values.put(COL_2, memo);
        values.put(COL_3, importance);
        values.put(COL_4, completeness);
        db.update(TABLE_NAME, values, " _id = ?", new String[] { id });

        return true;
    }

    public Integer deleteData(String id){ // Since id is a primary key
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        return db.delete(TABLE_NAME, "_id = ?", new String[] { id });
    }

    public void deleteAllData(){
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        db.execSQL("delete from " + TABLE_NAME);
    }

    public void rearrangeData(String id){
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        db.execSQL("UPDATE " + TABLE_NAME + " SET _id = (_id - 1) WHERE _id > " + id);
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='event_table'");
    }

    public void moveDataToTop(String memo){
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        ContentValues values = new ContentValues();
        values.put(COL_2, memo);
        values.put(COL_3, "false");
        values.put(COL_4, "0");
        db.update(TABLE_NAME, values, " _id = ?", new String[]{"1"});
    }


    public void shiftData(int position){

        for(; position >= 1; position--){
            shiftData_aux(position);
        }
    }


    public void shiftData_aux(int position){

        SQLiteDatabase db_read = this.getReadableDatabase();
        String[] columns = {COL_1, COL_2, COL_3, COL_4};
        Cursor cursor = db_read.query(TABLE_NAME, columns, null, null, null, null, null);
        cursor.moveToFirst();

        try {

            do {
                if (Integer.toString(position).equals(cursor.getString(0))) {
                    SQLiteDatabase db_write = this.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(COL_2, cursor.getString(1));
                    values.put(COL_3, cursor.getString(2));
                    values.put(COL_4, cursor.getString(3));
                    String id = Integer.toString(position + 1);
                    db_write.update(TABLE_NAME, values, " _id = ?", new String[]{id});

                }

            } while (cursor.moveToNext());
        } finally {
            if(cursor != null)
                cursor.close(); // Error doesn't occur.
        }

    }

    public Cursor getAllData(){
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        Cursor res = db.rawQuery("select rowid _id, * from " + TABLE_NAME, null);
        // SQLiteDatabase class provides result sets as Cursor instances.
        return res;
        // Cursor가 하는 일이 뭐야?
        // Cursor를 이용하면 우리의 데이터에 접근할 수 있음.
    }

    public long numOfEntries()
    {
        SQLiteDatabase db = this.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }


}
