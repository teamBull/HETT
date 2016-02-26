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
    public static final int DATABASE_VERSION = 2;
    public static final String TABLE_NAME = "event_table";
    public static final String TABLE_NAME2 = "friend_table";
    public static final String TABLE_NAME3 = "event_complete_table";
    public static final String TABLE_NAME4 = "repeat_table";
    public static final String VIEW_NAME = "event_repeat_view";
    public static final String TABLE_NAME6 = "hatt_setting_table";
    public static final String TABLE_NAME5="hatt_background_theme_table";


    private static final String CREATE_EVENT_TABLE =
            "create table " + TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, MEMO TEXT, IMPORTANCE INTEGER, COMPLETENESS INTEGER, DATE INTEGER, REPEAT INTEGER, ALARM INTEGER);";

    private static final String CREATE_FRIEND_TABLE =
            "create table " + TABLE_NAME2 +
                    "(_id integer primary key autoincrement, friend_name TEXT, talk_st TEXT, total_point double);";

    /*private static final String CREATE_EVENT_COMPLETE_TABLE =
            "create table " + TABLE_NAME3 +
                    "(_id INTEGER, MEMO TEXT NOT NULL, COMPLETENESS INTEGER,DATE INTEGER NOT NULL,FOREIGN KEY(_id) REFERENCES event_table(_id));";*/
    private static final String CREATE_EVENT_COMPLETE_TABLE =
            "create table " + TABLE_NAME3 +
                    "(_id INTEGER, MEMO TEXT NOT NULL, COMPLETENESS INTEGER NOT NULL,DATE INTEGER NOT NULL);";
    /*private static final String CREATE_EVENT_REPEAT_TABLE=
            "create table " + TABLE_NAME4 +
                    "(_id INTEGER, DAY_OF_WEEK TEXT NOT NULL, FOREIGN KEY(_id) REFERENCES event_table(_id))";*/

    private static final String CREATE_EVENT_REPEAT_TABLE=
            "create table " + TABLE_NAME4 +
                    "(_id INTEGER, DAY_OF_WEEK TEXT NOT NULL)";
    /*private static final String CREATE_EVENT_REPREAT_VIEW_TABLE=
            "CREATE OR REPLACE VIEW " + VIEW_NAME +
                    "AS " +
                    "SELECT E._ID,E.MEMO,E.IMPORTANCE,E.DATE,R.DAY_OF_WEEK " +
                    "FROM event_table e, repeat_table r " +
                    "WHERE e._id = r._id AND e.repeat = 1";*/

    //기호
    private static final String CREATE_HATT_SETTING_TABLE = "create table "+TABLE_NAME6+" (hatt_setting_code TEXT primary key ,hatt_friend_name TEXT ,is_push_alarm integer );";
    private static final String CREATE_HATT_BACKGROUND_THEME_TABLE = "create table "+TABLE_NAME5+" (background_code integer primary key autoincrement , background_theme_name text not null, is_background_permission integer not null,is_selected integer not null);";
    //
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
        db.execSQL(CREATE_FRIEND_TABLE);
        db.execSQL(CREATE_EVENT_COMPLETE_TABLE);
        db.execSQL(CREATE_EVENT_REPEAT_TABLE);
        //db.execSQL(CREATE_EVENT_REPREAT_VIEW_TABLE);
        //event_table
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(1,'장보기',1,0,20160227,1,0);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(2,'공부하기',0,0,20160227,1,0);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(3,'놀기',1,0,20160228,1,0);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(4,'춤추기',0,0,20160229,1,0);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(5,'노래부르기',1,0,20160210,1,0);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(6,'잠자기',1,0,20160211,1,0);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(7,'데이트',1,0,20160211,1,0);");

        //complete_table
        db.execSQL(CREATE_HATT_SETTING_TABLE);
        db.execSQL(CREATE_HATT_BACKGROUND_THEME_TABLE);

        db.execSQL("INSERT INTO " + TABLE_NAME2 + " VALUES (null, 'HATT', '기본 테마', 0);");

        db.execSQL("INSERT INTO " + TABLE_NAME3 + " VALUES (4,'네번째메모',1,20160227);");
        db.execSQL("INSERT INTO " + TABLE_NAME3 + " VALUES (5,'다섯번째메모',1,20160228);");
        db.execSQL("INSERT INTO " + TABLE_NAME3 + " VALUES (1,'첫번째메모',1,20160226);");
        db.execSQL("INSERT INTO " + TABLE_NAME3 + " VALUES (7,'일곱번째메모',1,20160229);");
        db.execSQL("INSERT INTO " + TABLE_NAME3 + " VALUES (2,'두번째메모',1,20160226);");
        db.execSQL("INSERT INTO " + TABLE_NAME3 + " VALUES (3,'세번째메모',1,20160227);");
        db.execSQL("INSERT INTO " + TABLE_NAME3 + " VALUES (6,'여섯번째메모',1,20160228);");
        //여기는 임시 REPEAT_TABLE 값
        db.execSQL("INSERT INTO " + TABLE_NAME4 + " VALUES(1,'월,화,수');" );
        db.execSQL("INSERT INTO " + TABLE_NAME4 + " VALUES(2,'화,수');" );
        db.execSQL("INSERT INTO " + TABLE_NAME4 + " VALUES(3,'월,화,수');" );
        db.execSQL("INSERT INTO " + TABLE_NAME4 + " VALUES(4,'수');" );
        db.execSQL("INSERT INTO " + TABLE_NAME4 + " VALUES(5,'금');" );
        db.execSQL("INSERT INTO " + TABLE_NAME4 + " VALUES(6,'일');" );
        db.execSQL("INSERT INTO " + TABLE_NAME4 + " VALUES(7,'화');" );

        db.execSQL("insert into "+TABLE_NAME6+" values('user1','Hatti',0);");

        db.execSQL("insert into "+TABLE_NAME5+" values(0,'기본 테마',1,1)");
        db.execSQL("insert into "+TABLE_NAME5+" values(1,'바다',0,0)");
        db.execSQL("insert into "+TABLE_NAME5+" values(2,'나무나무',0,0)");
        db.execSQL("insert into "+TABLE_NAME5+" values(3,'스트라이프',1,0)");
        db.execSQL("insert into "+TABLE_NAME5+" values(4,'빗방울',0,0)");
        db.execSQL("insert into "+TABLE_NAME5+" values(5,'눈송이',0,0)");



    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME3);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME4);
        //db.execSQL("DROP VIEW IF EXISTS " + VIEW_NAME);
        db.execSQL("drop table if exists "+TABLE_NAME6);
        db.execSQL("drop table if exists "+TABLE_NAME5);
        onCreate(db);
    }

}

