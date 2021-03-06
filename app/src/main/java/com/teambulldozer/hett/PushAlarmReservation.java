package com.teambulldozer.hett;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GHKwon on 2016-02-21.
 */
public class PushAlarmReservation {


    /**
     * default 푸쉬 알람 제목.
     */
    private static final String PUSH_ALARM_TITLE_HATT="HATT";

    /**
     * 싱글톤적용.
     */
    private static PushAlarmReservation pushAlarmReservation;
    static {
        pushAlarmReservation = new PushAlarmReservation();
    }
    /**
     * PushAlarmReservation 객체를 얻어올 수 있는 메소드.
     * @return PushAlarmReservation의 인스턴스
     */
    public static PushAlarmReservation getInstance()
    {
        return pushAlarmReservation;
    }
    /**
     * 기본 생성자.
     */
    private PushAlarmReservation() {

    }
    /**
     * 알람을 등록하는 메소드. 푸쉬 알람의 제목이 앱 이름으로 등록되어 있다.
     * 내부에서 오버로딩된 registerAlarm메소드를 호출한다.
     * @param context 알람을 등록하는 화면의 context.
     * @param hour 알람 울릴 시간.
     * @param min 알름 울릴 분.
     * @param second 알람 울릴 초
     * @param pushAlarmBody 알람 울릴 내용.
     * @return 알람 등록을 성공했는지 실패했는지 체크하는 메소드.
     */
    public boolean registerAlarm (Context context, int hour,int min,int second,String pushAlarmBody) {
        return registerAlarm(context, hour, min, second, PUSH_ALARM_TITLE_HATT, pushAlarmBody);
    }
    /**
     * 알람을 등록하는 메소드. 푸쉬 알람의 제목이 앱 이름으로 등록되어 있다.
     * @param context 알람을 등록하는 화면의 context.
     * @param hour 알람 울릴 시간.
     * @param min 알름 울릴 분.
     * @param second 알람 울릴 초
     * @param pushAlarmBody 알람 울릴 내용.
     * @return 알람 등록을 성공했는지 실패했는지 체크하는 메소드.
     * @param context
     * @param hour
     * @param min
     * @param second
     * @param pushAlarmTitle
     * @param pushAlarmBody
     * @return 알람 등록을 성공했는지 실패했는지 체크하는 메소드.
     */
    public boolean registerAlarm (Context context, int hour,int min,int second,String pushAlarmTitle,String pushAlarmBody) {
         new AlarmInfomation(context, hour, min, second, pushAlarmTitle, pushAlarmBody);
        return true;
    }
    public boolean registerAlarm (Context context, int hour,int min,int second,String pushAlarmTitle,String pushAlarmBody,boolean repeat) {
        new AlarmInfomation(context, hour, min, second, pushAlarmTitle, pushAlarmBody, repeat);
        return true;
    }

    /**
     * 푸쉬 알람의 정보를 저장하는 AlarmInfomation.
     */
    public class AlarmInfomation {
        public Context context;
        public int hour;
        public int min;
        public int second;
        public String pushAlarmTitle;
        public String pushAlarmBody;
        /**
         * 기본생성자.
         */
        public AlarmInfomation() {}

        /**
         * 알람 정보의 생성자. 내부에서 registerAlarm을 호출한다..
         * @param context 사용자가 푸쉬 알람을 보고 들어올 화면.
         * @param hour 알람 울릴 시간
         * @param min // 알람 울릴 분
         * @param second // 알람 울릴 초
         * @param pushAlarmTitle 알람 제목.
         * @param pushAlarmBody 알람 내용.
         */
        public AlarmInfomation(Context context , int hour,int min,int second,String pushAlarmTitle,String pushAlarmBody) {
            this.context=context;
            this.hour=hour;
            this.min=min;
            this.second=second;
            this.pushAlarmTitle=pushAlarmTitle;
            this.pushAlarmBody=pushAlarmBody;
            registerAlarm(false);
        }
        public AlarmInfomation(Context context , int hour,int min,int second,String pushAlarmTitle,String pushAlarmBody,boolean repeat) {
            this.context=context;
            this.hour=hour;
            this.min=min;
            this.second=second;
            this.pushAlarmTitle=pushAlarmTitle;
            this.pushAlarmBody=pushAlarmBody;
            registerAlarm(repeat);
        }
        /**
         * 알람 등록하는 메소드.
         */
        private void registerAlarm(boolean isRepeat) {
            AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, SelfPushReceiver.class);
            intent.putExtra("pushAlarmTitle",pushAlarmTitle);
            intent.putExtra("pushAlarmBody",pushAlarmBody);
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            //알람시간 calendar에 set해주기

            // Calendar's getInstance method returns a calendar whose locale is based on system settings
            // and whose time fields have been initialized with the current date and time.

            int monthInfo = calendar.get(Calendar.MONTH) + 1;
            int dateInfo = calendar.get(Calendar.DATE);



            calendar.set(calendar.get(Calendar.YEAR), monthInfo, dateInfo, hour, min, second);
            Log.d("예약 잡는다잉~~", calendar.get(Calendar.YEAR) + "/" + monthInfo + "/" + dateInfo);
            //알람 예약
            if(isRepeat)
                am.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, sender);
            else
                am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
        }


    }
    public void passPush(Context context,int year,int month,int day,int hour,int min,int second,String title,String body,boolean isRepeat) {
        AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(context, SelfPushReceiver.class);
        intent.putExtra("pushAlarmTitle",title);
        intent.putExtra("pushAlarmBody",body);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_NO_CREATE);

        Calendar calendar = Calendar.getInstance();
        //알람시간 calendar에 set해주기

        // Calendar's getInstance method returns a calendar whose locale is based on system settings
        // and whose time fields have been initialized with the current date and time.

        int monthInfo = calendar.get(Calendar.MONTH) + 1;
        int dateInfo = calendar.get(Calendar.DATE);



        calendar.set(year, month, day, hour, min, second);
        Log.d("예약 잡는다잉~~", year + "/" + month + "/" + day+"/"+hour+"/"+min+"/"+second);
        //알람 예약
        if(isRepeat)
            am.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 24 * 60 * 60 * 1000, sender);
        else
            am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
    }
    /*public void changePushAlarmMode(boolean isBoolean){
        this.isPushAlarm=isBoolean;
    }*/
}
