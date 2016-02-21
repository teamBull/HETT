package com.teambulldozer.hett;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by GHKwon on 2016-02-21.
 */
public class PushAlarmReservation {
    /**
     * 지금 해쉬맵의 개수.
     */
    public static int hashMapCurrentSize=0;
    /**
     * 해쉬맵의 크기.
     */
    public static final int HASH_MAP_SIZE=5;
    /**
     * default 푸쉬 알람 제목.
     */
    private static final String PUSH_ALARM_TITLE_HATT="HATT";
    /**
     * 알람의 정보를 저장할 자료형.
     */
    private Map<Integer,AlarmInfomation> hashMap;
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
    public static PushAlarmReservation getInstance() {
        return pushAlarmReservation;
    }
    /**
     * 기본 생성자.
     */
    private PushAlarmReservation() {
        hashMap = new HashMap<Integer,AlarmInfomation>();
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
        registerAlarm(context, hour, min, second, PUSH_ALARM_TITLE_HATT, pushAlarmBody);
        return true;
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
        hashMap.put(checkHashMapSize(), new AlarmInfomation(context, hour, min, second, pushAlarmTitle, pushAlarmBody));
        return true;
    }
    /**
     * 저장할 해쉬맵의 크기.
     * @return 사용할 해쉬맵의 key.
     */
    public int checkHashMapSize() {
        hashMapCurrentSize+=1; // 지금 해쉬맵 번호에 1을 더했어.
        if(hashMapCurrentSize>=HASH_MAP_SIZE) { // 근데 Max의 값보다 높아지면
            hashMapCurrentSize%=HASH_MAP_SIZE;//값을 초기화.
        }
        return hashMapCurrentSize; // 해쉬맵의 번호를 retrun.
    }
    /**
     * 푸쉬 알람의 정보를 저장하는 AlarmInfomation.
     */
    private class AlarmInfomation {
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
            registerAlarm();
        }

        /**
         * 알람 등록하는 메소드.
         */
        private void registerAlarm() {
            AlarmManager am = (AlarmManager)context.getSystemService(Context.ALARM_SERVICE);
            Intent intent = new Intent(context, SelfPushReceiver.class);
            intent.putExtra("pushAlarmTitle",pushAlarmTitle);
            intent.putExtra("pushAlarmBody",pushAlarmBody);
            PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);

            Calendar calendar = Calendar.getInstance();
            //알람시간 calendar에 set해주기

            calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), hour, min, second);

            //알람 예약
            am.set(AlarmManager.RTC, calendar.getTimeInMillis(), sender);
        }
    }
}
