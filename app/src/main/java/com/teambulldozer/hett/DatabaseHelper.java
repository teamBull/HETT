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
    //public static final String VIEW_NAME = "event_repeat_view";
    public static final String TABLE_NAME6 = "hatt_setting_table";
    public static final String TABLE_NAME5="hatt_background_theme_table";
    public static final String TABLE_NAME7 = "talk_detail_talble";


    private static final String CREATE_EVENT_TABLE =
            "create table " + TABLE_NAME +
                    " (_id INTEGER PRIMARY KEY AUTOINCREMENT, MEMO TEXT, IMPORTANCE INTEGER, COMPLETENESS INTEGER, DATE TEXT, REPEAT INTEGER, ALARM INTEGER, ALARMHOUR INTEGER, ALARMMINUTE INTEGER);";

    private static final String CREATE_FRIEND_TABLE =
            "create table " + TABLE_NAME2 +
                    "(_id integer primary key autoincrement, friend_name TEXT, talk_st TEXT, total_point double);";

    private static final String CREATE_TALK_DETAIL_TABLE =
            "create table " + TABLE_NAME7 +
                    "(_id integer primary key autoincrement, talk_title TEXT, detail_1 TEXT, detail_2 TEXT, detail_3 TEXT" +
                    ", detail_4 TEXT, detail_5 TEXT, detail_6 TEXT, detail_7 TEXT);";

    /*private static final String CREATE_EVENT_COMPLETE_TABLE =
            "create table " + TABLE_NAME3 +
                    "(_id INTEGER, MEMO TEXT NOT NULL, COMPLETENESS INTEGER,DATE INTEGER NOT NULL,FOREIGN KEY(_id) REFERENCES event_table(_id));";*/

    private static final String CREATE_EVENT_COMPLETE_TABLE =
            "create table " + TABLE_NAME3 +
                    "(_id TEXT primary key, MEMO TEXT NOT NULL);";//,FOREIGN KEY(_id) REFERENCES event_table(_id)
    private static final String CREATE_REPEAT_TABLE=
            "create table " + TABLE_NAME4 +
                    "(_id TEXT primary key,MEMO TEXT, IMPORTANCE INTEGER, DAY_OF_WEEK TEXT NOT NULL,ALARM INTEGER, ALARMHOUR INTEGER NOT NULL, ALARMMINUTE INTEGER NOT NULL)";

    //기호
    private static final String CREATE_HATT_SETTING_TABLE = "create table "+TABLE_NAME6+" (hatt_setting_code TEXT primary key ,hatt_friend_name TEXT ,is_push_alarm integer );";
    private static final String CREATE_HATT_BACKGROUND_THEME_TABLE = "create table "+TABLE_NAME5+" (background_code integer primary key autoincrement , background_theme_name text not null, is_background_permission integer not null,is_selected integer not null);";

    /*private static final String CREATE_EVENT_REPREAT_VIEW=
            "CREATE VIEW " + VIEW_NAME +
                    " AS " +
                    " SELECT *" +
                    " FROM event_table e, repeat_table r" +
                    " WHERE e._id = r.code AND e.repeat = 1";*/
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
        db.execSQL(CREATE_REPEAT_TABLE);
        //db.execSQL(CREATE_EVENT_REPREAT_VIEW);
        //event_table _id INTEGER PRIMARY KEY AUTOINCREMENT, MEMO TEXT, IMPORTANCE INTEGER, COMPLETENESS INTEGER, DATE TEXT, REPEAT INTEGER, ALARM INTEGER, ALARMHOUR
        /*db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(1,'장보기',1,0,'16/02/27',0,1,-1,-1);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(2,'공부하기',0,0,'16/02/27',0,0,-1,-1);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(3,'놀기',1,0,'16/02/28',0,1,-1,-1);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(4,'춤추기',0,0,'16/02/29',0,0,-1,-1);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(5,'노래부르기',1,0,'16/02/10',0,0,-1,-1);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(6,'잠자기',1,0,'16/02/11',0,0,-1,-1);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(7,'데이트',1,0,'16/02/11',0,0,-1,-1);");*/


        db.execSQL(CREATE_HATT_SETTING_TABLE);
        db.execSQL(CREATE_HATT_BACKGROUND_THEME_TABLE);
        db.execSQL(CREATE_TALK_DETAIL_TABLE);

        /*
        //event_table
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(1,'장보기',1,0,20160227,1,0,0,0);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(2,'공부하기',0,0,20160227,1,0,0,0);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(3,'놀기',1,0,20160228,1,0,0,0);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(4,'춤추기',0,0,20160229,1,0,0,0);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(5,'노래부르기',1,0,20160210,1,0,0,0);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(6,'잠자기',1,0,20160211,1,0,0,0);");
        db.execSQL("INSERT INTO " + TABLE_NAME + " VALUES(7,'데이트',1,0,20160211,1,0,0,0);");*/


        db.execSQL("INSERT INTO " + TABLE_NAME2 + " VALUES (null, 'Hatti', '기본 말투', 0);");

        /*
        //임시 complete_tb : complete_table CODE TEXT, MEMO TEXT NOT NULL
        db.execSQL("INSERT INTO " + TABLE_NAME3 + " VALUES ('16/02/06/15/31/52','네번째메모');");
        db.execSQL("INSERT INTO " + TABLE_NAME3 + " VALUES ('16/02/06/16/20/13','다섯번째메모');");
        db.execSQL("INSERT INTO " + TABLE_NAME3 + " VALUES ('16/02/09/09/45/28','일곱번째메모');");
        db.execSQL("INSERT INTO " + TABLE_NAME3 + " VALUES ('16/02/10/13/29/42','두번째메모');");
        db.execSQL("INSERT INTO " + TABLE_NAME3 + " VALUES ('16/02/10/10/32/58','세번째메모');");
        db.execSQL("INSERT INTO " + TABLE_NAME3 + " VALUES ('16/02/07/08/28/36','첫번째메모');");
        db.execSQL("INSERT INTO " + TABLE_NAME3 + " VALUES ('16/02/11/09/20/20','여섯번째메모');");*/

        //임시 repeat_tb : CODE INTEGER, MEMO TEXT, IMPORTANCE INTEGER, DAY_OF_WEEK TEXT NOT NULL, ALARMHOUR INTEGER NOT NULL, ALARMMINUTE


        db.execSQL("insert into "+TABLE_NAME6+" values('user1','Hatti',0);");

        db.execSQL("insert into "+TABLE_NAME5+" values(0,'기본 테마',1,1)");
        db.execSQL("insert into "+TABLE_NAME5+" values(1,'나무나무',1,0)");
        db.execSQL("insert into "+TABLE_NAME5+" values(2,'스트라이프',1,0)");
        db.execSQL("insert into "+TABLE_NAME5+" values(3,'내 우주는 전부 너야',1,0)");
        db.execSQL("insert into "+TABLE_NAME5+" values(4,'on the snow',0,0)");
        /*db.execSQL("insert into "+TABLE_NAME5+" values(4,'빗방울',0,0)");
        db.execSQL("insert into "+TABLE_NAME5+" values(5,'눈송이',0,0)");*/

        new AlarmAMZero(mContext);
        // 말투
        db.execSQL("INSERT INTO " + TABLE_NAME7 + "values(null, '기본 말투', '안녕 :-)', '오늘도 우리 열심히 해보자', '화이팅!', " +
                "'힘내~', '일정은 다 완료했어?', '기운내!', '오늘 하루 즐거운 마음으로 보내~')");

        db.execSQL("INSERT INTO " + TABLE_NAME7 + "values(null, '연서복', '울 애긔~ㅎ안녕?ㅎ', '어빠가 울 애긔 일 좀 도와줄까~ㅎ', '울 애긔 머하니~?', " +
                "'어빠랑 밥 먹고 일정 완료 하자~ㅎ 울애긔가 사주는 거지?ㅎ 넝담~ㅎ', " +
                "'울 애긔 일정 다 완료 못하면 어빠랑 사귀는거다~?ㅎ', '울 애긔 어빠 생각하느냐고 일정 다 못하면 어쩌지~?ㅎ'," +
                " '울 애긔 밀.당.하는군하?ㅎ 어빠는 다 알아~ㅎㅎ')");

        db.execSQL("INSERT INTO " + TABLE_NAME7 + "values(null, '외국인', '아뇽하세효', '이룬 자라고이쩌요?', '오눌도 히믈내요! 자랄쑤이쏘!', '우리둘 모두 파이팅!!', "
                + "'할 릴 안하구 코골구 게신거 아니죠오?', " + "'읻다가 하지 말구 지굼해요!!'," + " '오눌 헹복하게 보네세요!!!')");

        db.execSQL("INSERT INTO " + TABLE_NAME7 + "values(null, '극존칭', '안녕하십니까!!!', '죄송하지만 혹시 일정을 잊어버리시진 않으셨지요?', " +
                "'진지는 잡수셨는지요?', '오늘 하루도 힘내십시오!', " + "'일정을 모두 완료하셨으면 편히 쉬셔도 됩니다!',"
                + " '오늘 하루는 어떠셨습니까?', '화이팅하십시요^^!')");

        db.execSQL("INSERT INTO " + TABLE_NAME7 + "values(null, '새오체', '안녕하새오?', '밥은 먹었어오? 지금 먹은 밥은 니 뱃살이 될거애오.', " +
                "'오늘도 채선을 다해서 힘내새오!', '오늘 하루 똑바로 사새오!!', " + "'일정 다 완료 못하면 디지새오!^.<',"
                + " '화이팅 하새오!', '두 손 꼭 잡아주깨오 가치 버텨바오!')");

        db.execSQL("INSERT INTO " + TABLE_NAME7 + "values(null, '연하남', '누나~ 뭐해요?', '누나 지금 할 일 얼른하고 나랑 놀아요~', " +
                "'누나 밥은 먹었어요? 밥 꼭 챙겨먹어요!', '누나 오늘 일정 완료하면 이뻐해줄게요~!', " + "'누나 왜 이렇게 귀여워요?',"
                + " '야', '누나 힘내요! 누나한테 내가 있잖아요!')");

        db.execSQL("INSERT INTO " + TABLE_NAME7 + "values(null, '신하', '옥체강령하시옵니까?', '아뢰옵기 황공하오나, 약조를 잊지 않고 계시온지요?', " +
                "'전~~하~~~~!!통~촉하여!!주시옵~소서~~!!', '수라는 드셨사옵니까? 수라를 내오라 할까요?', " + "'전~~하! 힘을 내십시오! 백성들이 지켜보고 있사옵니다!',"
                + " '만세!만세!만만세! 천세!천세!천천세!', '더 이상 일정으로 인해 고통받지 마시옵소서..')");



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

