package com.teambulldozer.hett;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by YUNSEON on 2016-02-16.
 */
public class FriendDataManager {
    Context context = null;
    DatabaseHelper dbHelper;
    SQLiteDatabase db;
    FriendDto friend;
    Cursor cursor;

    public FriendDataManager(Context context) {
        this.context = context;
        dbHelper = DatabaseHelper.get(context);
        friend = new FriendDto();
    }

    public void setFriend(FriendDto friend){
        this.friend = friend;
    }

    public FriendDto getFriend(){
        db = dbHelper.getReadableDatabase();

        cursor = db.rawQuery("SELECT * FROM friend_table", null);
        cursor.moveToNext();

        int id = cursor.getInt(0);
        String friend_name = cursor.getString(1);
        String talk_st = cursor.getString(2);
        float total_point = cursor.getFloat(3);

        friend.setId(id);
        friend.setFriendName(friend_name);
        friend.setFriendTalkSt(talk_st);
        friend.setTotalPoint(total_point);

        cursor.close();
        dbHelper.close();

        return friend;
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

    public float getTotalPoint(){
        db = dbHelper.getReadableDatabase();
        float size;

        cursor = db.rawQuery("SELECT * FROM friend_table", null);
        cursor.moveToNext();

        size = cursor.getFloat(3);

        dbHelper.close();
        cursor.close();
        return size;
    }
    public void updateTotalPoint(int id, float totalPoint){
        db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("total_point", totalPoint);
        db.update("friend_table", values, "_id=?", new String[]{""+id});
        dbHelper.close();
    }

}
