package com.teambulldozer.hett;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;

/**
 * Created by YUNSEON on 2016-02-16.
 */
public class FriendDataManager {

    private static FriendDataManager mFriendDataManager;
    private Context context = null;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    FriendDto friend;
    Cursor cursor;

    private FriendDataManager(Context context) {
        this.context = context;
        dbHelper = DatabaseHelper.get(context);
        friend = new FriendDto();
    }

    public static FriendDataManager get(Context context){
        if(mFriendDataManager == null){
            mFriendDataManager = new FriendDataManager(context);
        }

        return mFriendDataManager;
    }

    public void setFriend(FriendDto friend){
        this.friend = friend;
    }

    public String getFriendName(){
        db = dbHelper.getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM friend_table", null);
        cursor.moveToNext();

        return cursor.getString(1);
    }
    
    public FriendDto getFriend(){
        db = dbHelper.getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM friend_table", null);
        cursor.moveToNext();

        int id = cursor.getInt(0);
        String friend_name = cursor.getString(1);
        String talk_st = cursor.getString(2);
        double total_point = cursor.getDouble(3);

        friend.setId(id);
        friend.setFriendName(friend_name);
        friend.setFriendTalkSt(talk_st);
        friend.setTotalPoint(total_point);

        cursor.close();
        dbHelper.close();

        return friend;
    }

    public ArrayList getTalkStDetail(String talkSt){
        db = dbHelper.getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM talk_detail_talble WHERE talk_title = \"" + talkSt + "\"", null);
        cursor.moveToNext();

        ArrayList talk_detail = new ArrayList();
        talk_detail.add(cursor.getString(2));
        talk_detail.add(cursor.getString(3));
        talk_detail.add(cursor.getString(4));
        talk_detail.add(cursor.getString(5));
        talk_detail.add(cursor.getString(6));
        talk_detail.add(cursor.getString(7));
        talk_detail.add(cursor.getString(8));


        cursor.close();
        dbHelper.close();

        return talk_detail;
    }

    public void updateFriendName(String friend_name, String talk_st){
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("friend_name", friend_name); //friend_name 값을 수정
        db.update("friend_table", values, "talk_st=?", new String[]{talk_st});

        dbHelper.close();
    }

    public void updateFriendTalkSt(String talk_st, String friend_name){
        db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put("talk_st", talk_st); //talk_st 값을 수정
        db.update("friend_table", values, "friend_name=?", new String[]{friend_name});
        dbHelper.close();
    }

    public double getTotalPoint(){
        db = dbHelper.getReadableDatabase();
        double point;

        cursor = db.rawQuery("SELECT * FROM friend_table", null);
        cursor.moveToNext();

        point = cursor.getDouble(3);

        dbHelper.close();
        cursor.close();
        return point;
    }

    public String getTalkSt(){
        db = dbHelper.getReadableDatabase();

        String talkSt;

        cursor = db.rawQuery("SELECT * FROM friend_table", null);
        cursor.moveToNext();

        talkSt = cursor.getString(2);

        dbHelper.close();
        cursor.close();
        return talkSt;
    }

    public void updateTodayPoint(int id, double todayPoint){
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("today_point", todayPoint);
        db.update("friend_table", values, "_id=?", new String[]{"" + id});
        dbHelper.close();
    }

    public void updateTotalPoint(int id, double totalPoint){
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("total_point", totalPoint);
        db.update("friend_table", values, "_id=?", new String[]{""+id});
        dbHelper.close();
    }

}
