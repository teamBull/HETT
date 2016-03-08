package com.teambulldozer.hett;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;

/**
 * Created by GHKwon on 2016-02-27.
 */
public class AlarmAMZeroReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        //여따가 12시 되면 해야될 것들 작성해라.
        Cursor cursor;
        double todayPoint = 0;
        double totalPoint = 0;
        EventTableController eventTableControllerr;
        FriendDataManager friendDataManager;

        eventTableControllerr = EventTableController.get(context);
        friendDataManager = FriendDataManager.get(context);


        if (eventTableControllerr.numOfEntries() == 0) {
            todayPoint = 0;
        } else {
            todayPoint = (float) eventTableControllerr.getCompletedDataSize() / eventTableControllerr.numOfEntries();
        }
        //FriendDatamanager에서 점수 불러옥 오늘 점수를 더해준 후 없뎃
        totalPoint = friendDataManager.getTotalPoint() + todayPoint;
        friendDataManager.updateTotalPoint(1, totalPoint);
        PushAlarmReservation.getInstance().registerAlarm(context, 0, 9, 0, 0, DrawerTableController.getInstance(context).searchByFriendName(), "오늘도 잘 일어났냐?");

        PushAlarmReservation.getInstance().registerAlarm(context,1,2,0,0,DrawerTableController.getInstance(context).searchByFriendName(),"얼렁 자라 ㅋㅋㅋㅋ");
        CompleteEventTableController completeEventCtr = CompleteEventTableController.get(context);
        EventTableController eventCtr = EventTableController.get(context);


    }
}
