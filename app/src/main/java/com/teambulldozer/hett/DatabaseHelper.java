package com.teambulldozer.hett;

/**
 * Created by flecho on 2016. 2. 7..
 */

import android.content.Context;
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

    private static DatabaseHelper mDatabaseHelper;
    private Context mContext;
    public static final String DATABASE_NAME = "HATT.db"; // case-insensitive
    public static final int DATABASE_VERSION = 1;
    public static final String TABLE_NAME = "event_table";


    // There are only two columns

    static class eventColumns {
        public static final String ID = "_id"; // CursorAdapter에서 id명은 반드시 _id. 수정하면 안된다.
        public static final String MEMO = "MEMO";
        public static final String IMPORTANCE = "IMPORTANCE";
        public static final String COMPLETENESS = "COMPLETENESS";
        public static final String DATE = "DATE";
        public static final String REPEAT = "REPEAT";
        public static final String ALARM = "ALARM";    }

    private static final String CREATE_EVENT_TABLE =
            "create table " + TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, MEMO TEXT, IMPORTANCE INTEGER, COMPLETENESS INTEGER, DATE INTEGER, REPEAT INTEGER, ALARM INTEGER);";


    private DatabaseHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static DatabaseHelper get(Context context){
        if(mDatabaseHelper == null){
            mDatabaseHelper = new DatabaseHelper(context);
        }
        return mDatabaseHelper;
    }

    @Override
    public void onCreate(SQLiteDatabase db){
        db.execSQL(CREATE_EVENT_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

}

