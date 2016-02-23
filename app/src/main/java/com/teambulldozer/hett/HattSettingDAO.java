package com.teambulldozer.hett;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GHKwon on 2016-02-21.
 */
public class HattSettingDAO extends SQLiteOpenHelper {
    private final static String HATT_DB = "HATT.db";
    private final static String HATT_SETTING_TB_NAME = "hatt_setting_tb";
    private static final int DB_VERSION = 1;
    private Context context;
    private static HattSettingDAO hattSettingDAO;
    public static HattSettingDAO getInstance(Context context) {
        if(hattSettingDAO==null)
            hattSettingDAO = new HattSettingDAO(context,HATT_DB,null,0);
        return hattSettingDAO;
    }

    /**
     * @param context SQLiteOpenHelper내부에서 DB파일을 생성 및 접근하기 위해서 사용함.
     * @param dbName db이름.
     * @param cursorFactory 사용자 정의 커서를 설정할 떄 사용하는데 대부분 null로 넘어온다. 사용주의
     * @param version // DB버전 나중에 마이그레이션 사용할 때 쓰려고 사용함.
     */
    private HattSettingDAO(Context context, String dbName, SQLiteDatabase.CursorFactory cursorFactory, int version) {
        super(context, dbName, cursorFactory, version);
        this.context = context;
    }

    /**
     * onCreate메소드에선 다음 쿼리를 생성한다.
     * create table if not exists hatt_setting_tb (hatt_setting_code TEXT,hatt_friend_name TEXT,is_bell_mode integer,is_push_alarm integer);
     * if not exists는 테이블이 생성되지 않았으면 생성하라는 명령어이다.
     *
     * @param sqLiteDatabase
     */
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(
                "create table if not exists " + HATT_SETTING_TB_NAME + " (hatt_setting_code TEXT primary key,hatt_friend_name TEXT not null,is_bell_mode integer not null,is_push_alarm integer not null);");
    }

    public void onOpen(SQLiteDatabase sqLiteDatabase) {
        super.onOpen(sqLiteDatabase);
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(oldVersion < newVersion) { // 만약 새로운 버전이 재 배포 되었을 경우
            sqLiteDatabase.execSQL("drop table if exists "+HATT_SETTING_TB_NAME); // 테이블을 삭제하고
            onCreate(sqLiteDatabase); // 새로운 테이블을 만든다.
        }
    }

    public long isnert(ContentValues contentValues) {
        /*getWritableDatabase은 읽기 혹은 쓰기 전용으로 SQLiteDataBase객체를 반환함.*/
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
    }
}