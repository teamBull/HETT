package com.teambulldozer.hett;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by GHKwon on 2016-02-21.
 */
public class HattSettingDAO extends SQLiteOpenHelper {

    private static HattSettingDAO hattSettingDAO;
    private Context context;
    private final static String HATT_DB = "HATTI.db";
    private static final int DB_VERSION = 1;
    private final static String HATT_SETTING_TB_NAME = "hatt_setting_tb";

    private static final String CREATE_HATT_SETTING_TABLE = "create table "+HATT_SETTING_TB_NAME+" (hatt_setting_code TEXT primary key ,hatt_friend_name TEXT ,is_bell_mode integer ,is_push_alarm integer );";

    public static HattSettingDAO getInstance(Context context) {
        if(hattSettingDAO==null)
            hattSettingDAO = new HattSettingDAO(context);
        return hattSettingDAO;
    }
    /**
     * @param context SQLiteOpenHelper내부에서 DB파일을 생성 및 접근하기 위해서 사용함.
     */
    private HattSettingDAO(Context context){
        super(context, HATT_DB, null, DB_VERSION);
        this.context=context;
    }

    /**
     * onCreate메소드에선 다음 쿼리를 생성한다.
     * create table if not exists hatt_setting_tb (hatt_setting_code TEXT,hatt_friend_name TEXT,is_bell_mode integer,is_push_alarm integer);
     * if not exists는 테이블이 생성되지 않았으면 생성하라는 명령어이다.
     *
     * @param sqLiteDatabase
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_HATT_SETTING_TABLE);
        ContentValues contentValues = new ContentValues();
        contentValues.put("hatt_setting_code","user1");
        contentValues.put("hatt_friend_name","Hatti");
        contentValues.put("is_bell_mode",0);
        contentValues.put("is_push_alarm",0);
        sqLiteDatabase.insert(HATT_SETTING_TB_NAME, null, contentValues);

    }

    @Override
    public void onOpen(SQLiteDatabase db) {
        super.onOpen(db);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
            sqLiteDatabase.execSQL("drop table if exists "+HATT_SETTING_TB_NAME); // 테이블을 삭제하고
            onCreate(sqLiteDatabase); // 새로운 테이블을 만든다.
    }
}
/*public long isnert(ContentValues contentValues) {
        //getWritableDatabase은 읽기 혹은 쓰기 전용으로 SQLiteDataBase객체를 반환함.
        return getWritableDatabase().insert(HATT_SETTING_TB_NAME, null, contentValues);
    }

    public int insertAll(ContentValues[] contentValues) {
        SQLiteDatabase sqLiteDatabase = getWritableDatabase();
        sqLiteDatabase.beginTransaction(); // 데이터를 많이 넣을꺼라서 트랜잭션 발생시킴.
        for (ContentValues contentValue : contentValues) {
            sqLiteDatabase.insert(HATT_SETTING_TB_NAME , null , contentValue); //for로 다 insert 해주고
        }
        sqLiteDatabase.setTransactionSuccessful(); // 트랜잭션 성공적으로 끝남.
        sqLiteDatabase.endTransaction(); // 트랜잭션 끝
        return contentValues.length; // insert된 data의 개수 return.
    }

    public Cursor query(String[] columns, String selection, String[] selectionArgs, String groupBy, String having, String orderBy) {
        return getReadableDatabase().query(HATT_SETTING_TB_NAME , columns, selection, selectionArgs, groupBy, having, orderBy);
    }

    public int update(ContentValues updateRowValue, String whereClause, String[] whereArgs) {
        return getWritableDatabase().update(HATT_SETTING_TB_NAME , updateRowValue, whereClause, whereArgs);
    }

    public int delete(String whereClause, String[] whereArgs) {
        return getWritableDatabase().delete(HATT_SETTING_TB_NAME,  whereClause, whereArgs);
    }*/