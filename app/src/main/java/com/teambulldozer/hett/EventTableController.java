package com.teambulldozer.hett;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;

/**
 * Created by flecho on 2016. 2. 23..
 */
public class EventTableController {

    private static EventTableController mEventTableController;
    private Context mContext;
    DatabaseHelper myDb;
    static final String TABLE_NAME = "event_table";

    static class Columns {
        static final String ID = "_id"; // CursorAdapter에서 id명은 반드시 _id. 수정하면 안된다.
        static final String MEMO = "MEMO";
        static final String IMPORTANCE = "IMPORTANCE";
        static final String COMPLETENESS = "COMPLETENESS";
        static final String DATE = "DATE";
        static final String REPEAT = "REPEAT";
        static final String ALARM = "ALARM";
    }

    private EventTableController(Context context){
        myDb = DatabaseHelper.get(context);
        mContext = context;
    }

    public static EventTableController get(Context context){
        if(mEventTableController == null){
            mEventTableController = new EventTableController(context);
        }
        return mEventTableController;
    }

    public boolean insertData(String memo, boolean completeness){
        SQLiteDatabase db = myDb.getWritableDatabase(); // It is going to create your database and table.
        ContentValues contentValues = new ContentValues();
        contentValues.put(Columns.MEMO, memo);
        contentValues.put(Columns.IMPORTANCE, 0); // 입력 단계에서 중요 설정 할 수 없으므로, default 값은 false.
        contentValues.put(Columns.DATE, ((MainActivity)mContext).getDate()); // 인풋 시에 날짜가 DB에 기입된다.

        if(completeness)
            contentValues.put(Columns.COMPLETENESS, 1); // 입력 단계에서 완수여부 마찬가지로 설정할 수 없음.
        else
            contentValues.put(Columns.COMPLETENESS, 0); // 입력 단계에서 완수여부 마찬가지로 설정할 수 없음.

        long result = db.insert(TABLE_NAME, null, contentValues); // takes 3 arguments..
        if(result == -1)
            return false;
        else
            return true;
    }

    public Cursor getCompletenessData(){
        SQLiteDatabase db = myDb.getReadableDatabase();
        String[] columns = { Columns.ID, Columns.COMPLETENESS };
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
        SQLiteDatabase db = myDb.getReadableDatabase();
        String[] columns = { Columns.ID, Columns.MEMO };
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
        SQLiteDatabase db = myDb.getReadableDatabase();
        String[] columns = { Columns.ID, Columns.IMPORTANCE };
        Cursor cursor = db.query(TABLE_NAME, columns, null, null, null, null, null);
        return cursor;
    }

    public boolean updateMemo(String id, String memo){ //일정의 완수여부만 업데이트한다.
        SQLiteDatabase db = myDb.getWritableDatabase(); // It is going to create your database and table.
        ContentValues values = new ContentValues();
        values.put(Columns.MEMO, memo);
        db.update(TABLE_NAME, values, " _id = ?", new String[]{id});

        return true;
    }

    public boolean updateCompleteness(String id, int completeness){ //일정의 완수여부만 업데이트한다.
        SQLiteDatabase db = myDb.getWritableDatabase(); // It is going to create your database and table.
        ContentValues values = new ContentValues();
        values.put(Columns.COMPLETENESS, completeness);
        db.update(TABLE_NAME, values, " _id = ?", new String[] { id });

        return true;
    }

    public boolean updateImportance(String id, int importance){ // 일정의 중요도만 업데이트한다.
        SQLiteDatabase db = myDb.getWritableDatabase(); // It is going to create your database and table.
        ContentValues values = new ContentValues();
        values.put(Columns.IMPORTANCE, importance);
        db.update(TABLE_NAME, values, " _id = ?", new String[] { id });

        return true;
    }

    public boolean updateData(String id, String memo, int importance, int completeness){
        SQLiteDatabase db = myDb.getWritableDatabase(); // It is going to create your database and table.
        ContentValues values = new ContentValues();
        values.put(Columns.ID, id);
        //values.put(Columns.MEMO, memo);
        values.put(Columns.IMPORTANCE, importance);
        values.put(Columns.COMPLETENESS, completeness);
        db.update(TABLE_NAME, values, " _id = ?", new String[]{id});

        return true;
    }

    public Integer deleteData(String id){ // Since id is a primary key
        SQLiteDatabase db = myDb.getWritableDatabase(); // It is going to create your database and table.
        return db.delete(TABLE_NAME, "_id = ?", new String[] { id });
    }

    public void deleteAllData(){
        SQLiteDatabase db = myDb.getWritableDatabase(); // It is going to create your database and table.
        db.execSQL("delete from " + TABLE_NAME);
    }

    public void rearrangeData(String id){
        SQLiteDatabase db = myDb.getWritableDatabase(); // It is going to create your database and table.
        db.execSQL("UPDATE " + TABLE_NAME + " SET _id = (_id - 1) WHERE _id > " + id);
        db.execSQL("UPDATE SQLITE_SEQUENCE SET SEQ=0 WHERE NAME='event_table'");
    }


    public void moveDataTo(int position, ContentValues values){
        SQLiteDatabase db= myDb.getWritableDatabase();
        String id = Integer.toString(position);
        db.update(TABLE_NAME, values, " _id = ?", new String[]{id});
    }

    public ContentValues getAllContent(int position){
        SQLiteDatabase db_read = myDb.getReadableDatabase();
        String[] columns = {Columns.ID, Columns.MEMO, Columns.IMPORTANCE, Columns.COMPLETENESS, Columns.DATE, Columns.REPEAT, Columns.ALARM};
        Cursor cursor = db_read.query(TABLE_NAME, columns, null, null, null, null, null);
        cursor.moveToFirst();

        ContentValues values = new ContentValues();

        try {

            do {
                if (Integer.toString(position).equals(cursor.getString(0))) {
                    values.put(Columns.MEMO, cursor.getString(1));
                    values.put(Columns.IMPORTANCE, cursor.getString(2));
                    values.put(Columns.COMPLETENESS, cursor.getString(3));
                    values.put(Columns.DATE, cursor.getString(4));
                    values.put(Columns.REPEAT, cursor.getString(5));
                    values.put(Columns.ALARM, cursor.getString(6));
                    break;
                    //ContentValues를 리턴하는 함수를 하나 만들고 업데이트하는 발표
                }

            } while (cursor.moveToNext());
        } finally {
            if(cursor != null)
                cursor.close(); // Error doesn't occur.
        }

        return values;
    }

    public void shiftAllData(int fromPos, int toPos){ // from의 자리에 새로 들어갈 메모가 들어가야 한다.

        for(; toPos >= fromPos; toPos--){
            shiftDataFromTo(toPos, toPos + 1);
        }
    }

    public void shiftAllDataUp(int fromPos, int toPos){ // from의 자리에 새로 들어갈 메모가 들어가야 한다.

        for(; toPos > fromPos; fromPos++){
            shiftDataFromTo(fromPos+1, fromPos);
        }
    }

    public void shiftAllDataDown(int fromPos, int toPos){ // from의 자리에 새로 들어갈 메모가 들어가야 한다.

        for(; toPos < fromPos; fromPos--){
            shiftDataFromTo(fromPos-1, fromPos);
        }
    }

    public void shiftDataFromTo(int fromPos, int toPos){

        SQLiteDatabase db_read = myDb.getReadableDatabase();
        String[] columns = {Columns.ID, Columns.MEMO, Columns.IMPORTANCE, Columns.COMPLETENESS, Columns.DATE, Columns.REPEAT, Columns.ALARM};
        Cursor cursor = db_read.query(TABLE_NAME, columns, null, null, null, null, null);
        cursor.moveToFirst();

        try {

            do {
                if (Integer.toString(fromPos).equals(cursor.getString(0))) {
                    SQLiteDatabase db_write = myDb.getWritableDatabase();
                    ContentValues values = new ContentValues();
                    values.put(Columns.MEMO, cursor.getString(1));
                    values.put(Columns.IMPORTANCE, cursor.getString(2));
                    values.put(Columns.COMPLETENESS, cursor.getString(3));
                    values.put(Columns.DATE, cursor.getString(4));
                    values.put(Columns.REPEAT, cursor.getString(5));
                    values.put(Columns.ALARM, cursor.getString(6));
                    //ContentValues를 리턴하는 함수를 하나 만들고 업데이트하는 발표

                    //String id = Integer.toString(position);
                    String id = Integer.toString(toPos); // 이 게 다른 값으로 바뀌면 됨.
                    db_write.update(TABLE_NAME, values, " _id = ?", new String[]{ id });

                }

            } while (cursor.moveToNext());
        } finally {
            if(cursor != null)
                cursor.close(); // Error doesn't occur.
        }

    }

    public void shiftContentValuesTo(ContentValues values, int toPos){

        SQLiteDatabase db_write = myDb.getWritableDatabase();
        String id = Integer.toString(toPos); // 이 게 다른 값으로 바뀌면 됨.
        db_write.update(TABLE_NAME, values, " _id = ?", new String[]{ id });

    }


    public Cursor getAllData(){
        SQLiteDatabase db = myDb.getWritableDatabase(); // It is going to create your database and table.
        Cursor res = db.rawQuery("select rowid _id, * from " + TABLE_NAME, null);
        // SQLiteDatabase class provides result sets as Cursor instances.
        return res;
        // Cursor가 하는 일이 뭐야?
        // Cursor를 이용하면 우리의 데이터에 접근할 수 있음.
    }

    public long numOfEntries() // int
    {
        SQLiteDatabase db = myDb.getReadableDatabase();
        return DatabaseUtils.queryNumEntries(db, TABLE_NAME);
    }

    public int getCompletedDataSize(){
        Cursor cursor = getCompletenessData();
        cursor.moveToFirst();

        int size = 0;

        try {
            do {
                if(cursor.getString(1).equals("1"))
                    size++;// "1" means it is completed.
                else
                    continue;
            } while (cursor.moveToNext());
        } finally {

            if(cursor != null)
                cursor.close();

        }

        return size;

    }
}
