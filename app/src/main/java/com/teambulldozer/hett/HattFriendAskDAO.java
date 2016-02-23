package com.teambulldozer.hett;

import android.content.ContentValues;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by GHKwon on 2016-02-23.
 */
public class HattFriendAskDAO extends SQLiteOpenHelper {
    private static HattFriendAskDAO hattFriendAskDAO;
    private static final String HATT_FRIEND_ASK_TB_NAME="hatt_friend_ask_tb";
    private static final String HATT_SETTING_CODE = "hatt_setting_code";
    private static final String HATT_FRIEND_ASK="hatt_friend_ask";

    /**
     * 싱글톤패턴으로 객체 참조를 받아 올 getInstance()메소드.
     * HattFriendAskDAO객체가 생성이 되지 않았다면, HattFriendAskDAO객체를 생성하고 참조값을 리턴한다.
     * @param context 객체 참조를 받아올 페이지의 context객체.
     * @return HattFriendAskDAO 객체.
     */
    public static HattFriendAskDAO getInstance(Context context) {
        if(hattFriendAskDAO==null)
            hattFriendAskDAO = new HattFriendAskDAO(context,"HATT.db",null,1);
        return hattFriendAskDAO;
    }
    /**
     * @param context SQLiteOpenHelper내부에서 DB파일을 생성 및 접근하기 위해서 사용함.
     * @param dbName db이름.
     * @param cursorFactory 사용자 정의 커서를 설정할 떄 사용하는데 대부분 null로 넘어온다.
     * @param version // DB버전 나중에 마이그레이션 사용할 때 쓰려고 사용함.
     */
    private HattFriendAskDAO(Context context, String dbName, SQLiteDatabase.CursorFactory cursorFactory, int version){
        super(context,dbName,cursorFactory,version);
    }

    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL("create table "+HATT_FRIEND_ASK_TB_NAME+" ("+HATT_SETTING_CODE+" integer primary key autoincrement ,"+HATT_FRIEND_ASK+" TEXT not null);");
    }

    public void onOpen(SQLiteDatabase sqLiteDatabase) {
        super.onOpen(sqLiteDatabase);
    }

    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        if(oldVersion < newVersion) { // 만약 새로운 버전이 재 배포 되었을 경우
            sqLiteDatabase.execSQL("drop table if exists "+HATT_FRIEND_ASK_TB_NAME); // 테이블을 삭제하고
            onCreate(sqLiteDatabase); // 새로운 테이블을 만든다.
        }
    }
}
