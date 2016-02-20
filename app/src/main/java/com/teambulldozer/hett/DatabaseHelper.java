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
 * 아래의 DB에서 Integer 값을 갖는 정보는 IMPORTANCE(중요도), COMPLETENESS(완수여부), DATE(날짜정보),
 * REPEAT(반복여부), ALARM(알람여부) 이다.
 *
 * 이중에서 DATE(날짜정보)만 20160208 과 같은 Integer key값을 갖고
 * 나머지 값들은 1과 0의 두 가지 값만 갖게 된다. 세팅되어 있을 경우 1을, 세팅되어 있지 않을 경우 0의 값을 갖는다.
 * ex) 어떤 일정이 IMPORTANCE = 1, COMPLETENESS = 0 의 값을 가질 경우, 이 일정은 노란별 표시가 있는, 아직 완료되지 않은 일정을 뜻한다.
 *
 */

public class DatabaseHelper extends SQLiteOpenHelper {

    private Context mContext;
    public static final String DATABASE_NAME = "Event.db"; // case-insensitive
    public static final String TABLE_NAME = "event_table";

    // There are only two columns
    public static final String COL_1 = "_id"; // CursorAdapter에서 id명은 반드시 _id. 수정하면 안된다.
    public static final String COL_2 = "MEMO";
    public static final String COL_3 = "IMPORTANCE";
    public static final String COL_4 = "COMPLETENESS";
    public static final String COL_5 = "DATE";
    public static final String COL_6 = "REPEAT";
    public static final String COL_7 = "ALARM";


    public DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, 1);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        // And, if you want to create a table inside your DB,
        db.execSQL("create table " + TABLE_NAME +
                " (_id INTEGER PRIMARY KEY AUTOINCREMENT, MEMO TEXT, IMPORTANCE INTEGER, COMPLETENESS INTEGER, DATE INTEGER, REPEAT INTEGER, ALARM INTEGER);");
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
        contentValues.put(COL_3, 0); // 입력 단계에서 중요 설정 할 수 없으므로, default 값은 false.

        if(completeness)
            contentValues.put(COL_4, 1); // 입력 단계에서 완수여부 마찬가지로 설정할 수 없음.
        else
            contentValues.put(COL_4, 0); // 입력 단계에서 완수여부 마찬가지로 설정할 수 없음.

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

    public int getBorderlinePos(){

        for(int position = 1; position <= (int)numOfEntries(); position++) {
            if ((!isCompleted(position)) && isCompleted(position + 1)) {
                return (position + 1);
            }
        }

        return 1; //
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

    public boolean updateCompleteness(String id, int completeness){ //일정의 완수여부만 업데이트한다.
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        ContentValues values = new ContentValues();
        values.put(COL_4, completeness);
        db.update(TABLE_NAME, values, " _id = ?", new String[] { id });

        return true;
    }

    public boolean updateImportance(String id, int importance){ // 일정의 중요도만 업데이트한다.
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        ContentValues values = new ContentValues();
        values.put(COL_3, importance);
        db.update(TABLE_NAME, values, " _id = ?", new String[] { id });

        return true;
    }

    public boolean updateData(String id, String memo, int importance, int completeness){
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

    public void moveDataTo(int position, String memo){
        SQLiteDatabase db = this.getWritableDatabase(); // It is going to create your database and table.
        ContentValues values = new ContentValues();
        values.put(COL_2, memo);
        values.put(COL_3, 0);
        values.put(COL_4, 0);

        String id = Integer.toString(position);
        db.update(TABLE_NAME, values, " _id = ?", new String[]{ id });
    }


    public void shiftData(int from, int to){ // from의 자리에 새로 들어갈 메모가 들어가야 한다.

        for(; to >= from; to--){
            shiftData_aux(to);
        }
    }


    public void shiftData_aux(int position){

        SQLiteDatabase db_read = this.getReadableDatabase();
        String[] columns = {COL_1, COL_2, COL_3, COL_4, COL_5, COL_6, COL_7};
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
                    values.put(COL_5, cursor.getString(4));
                    values.put(COL_6, cursor.getString(5));
                    values.put(COL_7, cursor.getString(6));

                    //String id = Integer.toString(position);
                    String id = Integer.toString(position + 1);
                    db_write.update(TABLE_NAME, values, " _id = ?", new String[]{ id });

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
