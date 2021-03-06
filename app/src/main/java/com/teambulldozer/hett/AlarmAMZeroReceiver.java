package com.teambulldozer.hett;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Created by GHKwon on 2016-02-27.
 */
public class AlarmAMZeroReceiver extends BroadcastReceiver {

    private HETTSettingSharedPreference hettSettingSharedPreference;
    @Override
    public void onReceive(Context context, Intent intent) {//GregorianCalendar.getInstance().get(Calendar.DATE)
        //여따가 12시 되면 해야될 것들 작성해라.

        double totalPoint = 0;
        FriendDataManager friendDataManager = FriendDataManager.get(context);

        hettSettingSharedPreference = HETTSettingSharedPreference.getInstance();

        PushAlarm pushAlarm = PushAlarm.getInstance();
        /*int year = Ca

        pushAlarm.registerAlarm(context,)*/

        DrawerTableController drawerTableController = DrawerTableController.getInstance();

        totalPoint = friendDataManager.getTotalPoint();

        if ( totalPoint > 180 ) {
            drawerTableController.updateSelectedBackgroundTheme(4);
        } else if ( totalPoint > 160 ) {
            drawerTableController.updateSelectedBackgroundTheme(3);
        } else if ( totalPoint > 60 ) {
            drawerTableController.updateSelectedBackgroundTheme(2);
        } else if(totalPoint>40) {
            drawerTableController.updateSelectedBackgroundTheme(1);
         }


        CompleteEventTableController completeEventCtr = CompleteEventTableController.get(context);
        EventTableController eventCtr = EventTableController.get(context);


        hettSettingSharedPreference.updateLastUpdateDay(context, GregorianCalendar.getInstance().get(Calendar.DATE));

    }

}
//1. event_table에서 complete 인 것들만 가져온다.
        /*Log.d("AlarmAMZeroReceiver","시작");
        cursor = eventCtr.getCompletenessDataAll();

        try {
            cursor.moveToFirst();
            //2. 가져온 데이터들을 event_complete_table에 넣고,기존 event_table에서 삭제한다.
            for(int i =0; i< cursor.getCount(); i++) {
                Log.d("AlarmAMZeroReceiver","event_complete_table로 추가");
                completeEventCtr.insertToEventCompletenessTable(cursor.getString(cursor.getColumnIndex("DATE")), cursor.getString(cursor.getColumnIndex("MEMO")));
                //준상이가 확인해야 할 부분
                Log.d("AlarmAMZeroReceiver", "event_table에서 삭제");
                eventCtr.deleteData(String.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))));
                eventCtr.rearrangeData(String.valueOf(cursor.getInt(cursor.getColumnIndex("_id"))));

                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            Log.d("AlarmAMZeroReceiver","종료");
            if (cursor != null) {
                cursor.close();
            }
        }*/