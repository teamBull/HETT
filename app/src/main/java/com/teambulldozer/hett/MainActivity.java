package com.teambulldozer.hett;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.StrictMode;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mobeta.android.dslv.DragSortController;
import com.mobeta.android.dslv.DragSortListView;
import com.nhaarman.listviewanimations.appearance.simple.AlphaInAnimationAdapter;

import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;

/*
* (2016.2.18. 01:00 )
* 삭제 기능까지 구현한 상태.
* 최적화 문제.
* Adapter 관련 refactoring 진행함 --> 불필요하게 setAdapter() 메소드를 여러 번 호출하는 걸 줄이고,
* Cursor를 새롭게 로드하여, Adapter에 전달해서 속도를 개선함.
*
* (2016.2.18. 02:40 )
* 일반 일정, 완료 일정 사이 투명한 경계선 추가
*
* (2016.2.19.)
* 1. 일정 1개, 5개 입력시 알맞은 TOAST 뜨게 추가.
* 2. 레이아웃 올리고 내리는 게 가끔씩 작동하지 않아서 그 부분 수정.
* 3. DB 칼럼명 추가, 그리고 약간의 수정..
* 4. 어떤 경우에도 일정이 위에서 아래로 순서대로 뜨게 수정.
* 5. DB는 좀더 고쳐야..
* 6. 애니메이션을 주는 게 조금 어려움 ㅠㅜ
*
* (2016. 2. 20.)
* 1. 이 버전이 깔끔한 버전.
*
* (2016. 2. 26)
* 1. 일정 완료 시 선 그어지는 애니메이션 추가
* 2. 편집 탭과 완료탭에서 할 수 있는 기능 분리.
*
* (2016. 3. 1)
* 1. 패딩을 줘서 터치 미스를 줄임!
* 2. 토스트에서 친구 이름이 제대로 뜨도록 만듦.
* 3. 시간 이상하 뜨는 것 고침;; Calendar 클래스의 month는 0부터 시작하기 때문에;;;;; (업데이트 전에는 3월인데, 2월로 나왔음;;)
* 4. 오후 12시 20분이 0시 20분으로 뜨는 것을 12시 20분으로 뜨게 바꿈.
*
*(2016. 3. 8)
* 1. numOfEntries long으로 return하던 걸 int로 바꿈.
* 2. 
* */

public class MainActivity extends AppCompatActivity {

    private static boolean devMode = false;
    /* 디버그를 위한 개발자 모드 설정. true로 설정되어 있을 시, 오류 발생시 강제로 앱을 종료한다. */

    private static final String TAG = "HETT";

    Handler mHandler;

    // variables regarding back button
    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    EventTableController myEventController;
    RepeatEventController myRepeatEventController;
    CompleteEventTableController myCompleteEventController;

    DateController myDateController;

    MyDragSortAdapter myDragSortAdapter;
    DragSortListView lv1;
    DragSortController dragSortController;
    EditText memoInput;
    TextView addButton;
    TextView dateBar;
    TextView todayBar;
    TextView editMenu;
    TextView friend_ask_1;
    TextView friend_ask_2;
    ImageView addLine;
    RelativeLayout rl1;
    RelativeLayout rl2;
    LinearLayout dateLayout;
    SoftKeyboardLsnedRelativeLayout myLayout;


    //편집 버튼을 눌렀을 때 보이는 위젯들
    TextView finishMenu;
    ImageView deleteButton;
    ImageView orderButton;
    TextView deleteAllButton;

    // 폰트
    Typeface NanumSquare_B;
    Typeface NanumBarunGothic_R;

    HETTSettingSharedPreference hettSettingSharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (devMode)
        {
            StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                    .detectLeakedSqlLiteObjects()
                    .detectLeakedClosableObjects()
                    .penaltyLog()
                    .penaltyDeath()
                    .build());
        }

        super.onCreate(savedInstanceState);

        Log.d(TAG, "onCreate(Bundle) called");

        setContentView(R.layout.activity_main);


        //
        //java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

        /* Call the database constructor */
        myEventController = EventTableController.get(this); //
        myRepeatEventController = RepeatEventController.get(this);
        myCompleteEventController = CompleteEventTableController.get(this);
        myDateController = DateController.get(this);


        /* Connecting XML widgets and JAVA code. */
        dateBar = (TextView) findViewById(R.id.dateBar);
        todayBar = (TextView) findViewById(R.id.todayBar);
        editMenu = (TextView) findViewById(R.id.editMenu);
        lv1 = (DragSortListView) findViewById(R.id.lv1);
        memoInput = (EditText) findViewById(R.id.memoInput);
        addButton = (TextView) findViewById(R.id.addButton);
        addLine = (ImageView) findViewById(R.id.addLine);
        rl1 = (RelativeLayout) findViewById(R.id.rl1);
        rl2 = (RelativeLayout) findViewById(R.id.rl2);
        dateLayout = (LinearLayout) findViewById(R.id.dateLayout);
        myLayout = (SoftKeyboardLsnedRelativeLayout) findViewById(R.id.myLayout);

        friend_ask_1 = (TextView)findViewById(R.id.friend_ask_1);
        friend_ask_2 = (TextView)findViewById(R.id.friend_ask_2);






        /* 편집 눌렀을 때 추가 되는 버튼들 */
        finishMenu = (TextView) findViewById(R.id.finishMenu);
        deleteButton = (ImageView) findViewById(R.id.deleteButton);
        orderButton = (ImageView) findViewById(R.id.orderButton);
        deleteAllButton = (TextView) findViewById(R.id.deleteAllButton);

        /* Font Initialization
         * 폰트는 한 번만 생성한 뒤에, 레퍼런스를 가져다가 쓴다.
          * */
        NanumSquare_B = Typeface.createFromAsset(getAssets(), "NanumSquare_Bold.ttf");
        NanumBarunGothic_R = Typeface.createFromAsset(getAssets(), "NanumBarunGothic_Regular.ttf");

        mHandler= new Handler();

        dragSortSetting();

        /* These are the main functions of the main page. */
        setFont(); // 폰트 설정
        showDate(); // 날짜 보여주기, 동기화하는 식으로 나중에 업데이트해야할 수 있다.
        respondToUserInput(); // 유저가 타이핑을 시작하면, 입력버튼(addButton)이 뜨게 한다!
        screenUpAndDownOnItsState(); // 유저가 타이핑을 할 때, 레이아웃을 위로 끌어올리고, 타이핑 하지 않을 때 끌어내린다.

        ifEditMenuClicked();
        ifFinishMenuClicked();

        addOnUserInput(); // 유저가 입력하면 받기
        completeIfItemClicked(); // 아이템 클릭되면 View 바꿔주기
        ifClickedDeleteAllRows(); // 모두 지우기 버튼이 눌렀을 때, 일정 모두 지우기.
        populateListView(); // 한 번만 호출되면 된다.

        renewAllEvents();
        rearrangeCompletedEvents();


        // 리스트뷰에 아이템 올리기
        // 그 외에 클릭하면 삭제하는 기능은 MyDragSortAdapter에 구현.

        /* Additional details */

        // The following line makes software keyboard disappear until it is clicked again.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        MyDragSortAdapter.isOnEditMenu = true; //

        /*기호*/
        hettSettingSharedPreference = HETTSettingSharedPreference.getInstance();
        initNavigationDrawer(); //drawer에 대한 모든것을 초기화 하기 위한 메소드.
        //new AlarmAMZero(getApplicationContext());
        /*hettSettingSharedPreference.updatePushAlarm(getApplicationContext(),true);
        PushAlarmReservation pushAlarmReservation = PushAlarmReservation.getInstance();
        pushAlarmReservation.passPush(getApplicationContext(), 2016, 3, 19, 4, 39, 50, "제발푸쉬야", "날라오련..", true);*/

        //Ctrl + F -> 눈 / snow
        Animation animation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.tranlate);
        //v.startAnimation(animation);
        ImageView imageView = new ImageView(getApplicationContext());
        imageView.setImageDrawable(getResources().getDrawable(R.drawable.raining_star));
        imageView.startAnimation(animation);




        //Log.d("등록된순서-",calendar.get(Calendar.MONTH)+"월/"+calendar.get(Calendar.HOUR)+"시/"+(time + 2) + "/" + (time + 1) + "/" + (time + 3));

        Log.d("푸시알람 등록 시작","와라");
        Calendar calendar = Calendar.getInstance();

        PushAlarmSharedPreference pushAlarmSharedPreference = PushAlarmSharedPreference.getInstance();
        int pushCount = pushAlarmSharedPreference.searchPushNo(getApplicationContext());
        Toast.makeText(getApplicationContext(),"--남은 푸쉬 : "+pushCount+"",Toast.LENGTH_SHORT).show();
        try {
            if( pushCount!=0) {
                registerPushAlarm(0, 9, 0, 0, "first");
                registerPushAlarm(1, 18, 0, 0, "second");
                registerPushAlarm(2, 10, 0, 0, "third");
                //if (pushAlarmSharedPreference.isFirstPushAlarm(getApplicationContext()))
                //if (pushAlarmSharedPreference.isSecondPushAlarm(getApplicationContext()))
                //if (pushAlarmSharedPreference.isThirdPushAlarm(getApplicationContext()))
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(),"메인 264라인 에러 발생. 기호에게 말해주세용",Toast.LENGTH_SHORT).show();
        }

    }

    /**
     http://overcome26.tistory.com/16

     http://itmir.tistory.com/457
     */
    public void registerPushAlarm(int alarmNo,int hour,int min,int sec,String sequence) {

        AlarmManager alarmManager =(AlarmManager) getSystemService(ALARM_SERVICE);
        //Intent intent = new Intent(this,SelfPushReceiver.class);
        Intent intent = new Intent(this,SelfPushReceiver.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.putExtra("pushAlarmTitle", hour + "시" + min + "분" + sec + "초/" + alarmNo);
        intent.putExtra("pushAlarmBody", hour + "시" + min + "분" + sec + "초/에 알람이 등록되었으며 날라온 시간과 등록된 시간을 확인해 주세용" + alarmNo);
        intent.putExtra(sequence,sequence);
        intent.putExtra("requestCode",alarmNo);
        PendingIntent sender = PendingIntent.getBroadcast(this , alarmNo , intent, Intent.FILL_IN_DATA );
        Calendar calendar = Calendar.getInstance();
        //calendar.set(2016,3,20,20,59,0);
        /*calendar.set(Calendar.HOUR_OF_DAY,hour);
        calendar.set(Calendar.MINUTE,min);*/
                   // 년 월 일 시간 분 초


        /*Log.d("가자가자",calendar.get(Calendar.MONTH)+"월/"+calendar.get(Calendar.DATE)+"일/"+calendar.get(Calendar.HOUR)+"시/"+calendar.get(Calendar.MINUTE)+"분/"+calendar.get(Calendar.SECOND)+"초");
        Log.d("등록된순서","여기야!");*/
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, setTriggerTime(hour,min), 1000 * 60 * 60 * 24, sender);
        PushAlarmSharedPreference.getInstance().decreasePushNo(getApplicationContext());
        PushAlarmSharedPreference.getInstance().usePushAlarm(getApplicationContext(), sequence);
    }
    private long setTriggerTime(int alarmHour , int alarmMinute) {
        // current Time
        long atime = System.currentTimeMillis();
        // timepicker
        Calendar curTime = Calendar.getInstance();
        curTime.set(Calendar.HOUR_OF_DAY, alarmHour);
        curTime.set(Calendar.MINUTE, alarmMinute);
        curTime.set(Calendar.SECOND, 0);
        curTime.set(Calendar.MILLISECOND, 0);
        long btime = curTime.getTimeInMillis();
        long triggerTime = btime;
        if (atime > btime) {
            triggerTime += 1000 * 60 * 60 * 24;
        }
        return triggerTime;
    }
    //calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_WEEK), hour, min, sec);//이 부분만 수정해 주면 됨. 달 설정 할 시에는 3월일경우 -1 해서 2월을 작성할 것.
    //calendar.set(2016,3,19,19,56,30);
    //alarmManager.setRepeating(AlarmManager.RTC, calendar.getTimeInMillis(), 1000 * 60 * 60 * 24, sender);
    public void rearrangeCompletedEvents(){
        //Wrapper
        myEventController.renewCompletedEvent();
    }

    public void renewAllEvents(){

        if(isDateChanged()) {
            //친밀도 업뎃
            updateCloseness();
            // 이 앞에 완료된 일정을 db에 업데이트 시켜주는 명령어 필요.
            moveFinishedEvents(); // 데이터를 완료 일정 DB로 이동
            deleteFinishedEvents(); // 완료된 일정은 메인페이지에서 삭제
            requery();
            getRepeatEvents(); // 24시가 되었을 경우 메인페이지의 데이터 경신. //
            requery();

            //Toast.makeText(getApplicationContext(), "Date is changed.", Toast.LENGTH_SHORT).show();
        }
        //Toast.makeText(getApplicationContext(), "Date is not changed.", Toast.LENGTH_SHORT).show();
    }

    public void updateCloseness(){
        double todayPoint = 0;
        double totalPoint = 0;

        EventTableController eventTableControllerr;
        FriendDataManager friendDataManager;

        eventTableControllerr = EventTableController.get(this);
        friendDataManager = FriendDataManager.get(this);

        if (eventTableControllerr.numOfEntries() == 0) {
            todayPoint = 0;
        } else {
            todayPoint = (float) eventTableControllerr.getCompletedDataSize() / eventTableControllerr.numOfEntries();
        }
        //FriendDatamanager에서 점수 불러옥 오늘 점수를 더해준 후 없뎃
        totalPoint = friendDataManager.getTotalPoint() + todayPoint;
        friendDataManager.updateTotalPoint(1, totalPoint);
    }

    public void moveFinishedEvents(){

        CompleteEventTableController completeEventTableController = CompleteEventTableController.get(this);
        Cursor cursor = myEventController.getCompletenessDataAll();

        try {
            cursor.moveToFirst();
            // 완료된 일정 데이터를 event_complete_table에 넣는다.
            for(int i =0; i< cursor.getCount(); i++) {
                Log.d("MainActivity","event_complete_table로 추가");
                completeEventTableController.insertToEventCompletenessTable(cursor.getString(cursor.getColumnIndex("DATE")), cursor.getString(cursor.getColumnIndex("MEMO")));

                cursor.moveToNext();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

            if (cursor != null) {
                cursor.close();
            }
        }

    }

    public void deleteFinishedEvents(){
        int from, to;
        if(myEventController.isThereCompletedData()) {
            from = myEventController.getBorderlinePos();
            to = myEventController.numOfEntries();
            myEventController.deleteDataFrom(Integer.toString(from));
            myEventController.rearrangeData(Integer.toString(to));
        }
        else
            return;

    }

    public void getRepeatEvents(){
        // 날짜가 변했을 경우, 메모를 갈아 엎는다.
        // 반복일정은 가져오고, 완료일정은 삭제하는 것이다.
        // 그런데 중요한것은!!!! 완료일정을 먼저 삭제하고, 반복일정을 가져와야 한다.
        Calendar rightNow = Calendar.getInstance();
        int dayOfWeek = rightNow.get(Calendar.DAY_OF_WEEK);

        if(myRepeatEventController.numOfEntries() == 0)
            return ;

       // Toast.makeText(getBaseContext(), "getRepeatEvents()", Toast.LENGTH_SHORT).show();
        Cursor cursor = myRepeatEventController.getTodoRepeatData(dayTranslator(dayOfWeek));
        //Toast.makeText(getBaseContext(), Integer.toString(cursor.getCount()), Toast.LENGTH_SHORT).show();
        cursor.moveToFirst();


        ContentValues contentValues = new ContentValues();
        //int lastIdx = myEventController.numOfEntries();
        do{
            //contentValues.put("_id", ++lastIdx); // lastIdx값이 하나 증가.
            contentValues.put("MEMO", cursor.getString(1) );
            contentValues.put("IMPORTANCE", cursor.getString(2));
            contentValues.put("COMPLETENESS", "0");
            contentValues.put("DATE", getDate());
            contentValues.put("REPEAT", "1");
            contentValues.put("ALARM", cursor.getString(4));
            contentValues.put("ALARMHOUR", cursor.getString(5));
            contentValues.put("ALARMMINUTE", cursor.getString(6));

            myEventController.insertData("", false);
            EventTableController.get(this).moveDataTo(myEventController.numOfEntries(), contentValues);

        } while (cursor.moveToNext());

        requery();
    }

    public boolean isDateChanged(){
        // 날짜가 바뀔 때 해야할 일은 반복일정은 가져오고, 완료일정은 삭제하는 것이다.

        String todayDate =  getDate().substring(3, 8);
        String recentUpdatedDate = "no data yet.";

        if(myDateController.numOfEntries() == 0){
            // 최근에 접속한 날짜 데이타가 하나도 없는 경우
            if(myDateController.insertToTodayTable(todayDate))
                Log.d("INSERT_TEST", "insertion success");
            else
                Log.d("INSERT_TEST", "insertion failed");

            return false;
        } else {
            // 날짜 데이타가 있는 경우
            recentUpdatedDate = myDateController.getDateInfo();
        }

        //for debugging
        // Toast.makeText(getBaseContext(), "오늘 날짜: " + todayDate + "최근 업데이트 날짜: " + recentUpdatedDate , Toast.LENGTH_SHORT).show();


        if(recentUpdatedDate.equals(todayDate)){ // 오늘 날짜와 같을 때 아무런 일도 수행하지 않음.
            return false;
        } else { // 오늘 날짜와 다를 때, 날짜를 업데이트해주고, true를 리턴.
            myDateController.updateToday("1", todayDate); // true를 return하기 전에 DB값을 업데이트해줘야함.
            return true;
        }
    }

    @Override
    public void onBackPressed()
    {
        if( isOpened==2 ) {
            drawerLayout.closeDrawer(drawerView);
            return;
        }
        else if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            isOpened=0;
            super.onBackPressed();
            return;
        }
        else { Toast.makeText(getBaseContext(), "뒤로 버튼을 한번 더 누르면 앱을 종료합니다.", Toast.LENGTH_SHORT).show(); }

        mBackPressed = System.currentTimeMillis();
    }

    public void dragSortSetting(){

        dragSortController = new DragSortController(lv1);
        dragSortController.setDragHandleId(R.id.orderButton);
        dragSortController.setSortEnabled(true);
        dragSortController.setDragInitMode(0);

        lv1.setFloatViewManager(dragSortController);
        lv1.setOnTouchListener(dragSortController);
        lv1.setDragEnabled(true);
    }

    private DragSortListView.DropListener onDrop = new DragSortListView.DropListener(){

        @Override
            public void drop(int from, int to){

            from += 1;
            to += 1; // 위치 인식이 다르기 때문에.

            if(myEventController.isCompleted(to) || myEventController.isCompleted(from))
                return;

            ContentValues temp = myEventController.getAllContent(from);
            // 위에서 밑으로 내릴 때는 제대로 동작함. 밑에서 위로 올릴 때도 잘 고려해야함.
            if(from < to){
                myEventController.shiftAllDataUp(from, to); // from ~ to까지 위로 한 칸씩 업데이트
                myEventController.shiftContentValuesTo(temp, to);

            } else if (from > to){
                myEventController.shiftAllDataDown(from, to); // from ~ to 까지 밑으로 한 칸씩 업데이트
                myEventController.shiftContentValuesTo(temp, to);
            }

            requery();
            }

    };


    public void toastProperMessage(String hatt_id, int numOfTODOs){
        // 완료된 일정말고, 일반 일정만 5개 이상 될 때, toast를 뜨게 하기.
        // 완료된 일정의 개수를 센 다음, 완료된 게 하나라도 있으면 다음의 메시지를 띄우지 않는다.
        if(myEventController.isThereCompletedData())
            return;

        HattToast toast = new HattToast(this); // 메모리 누수 발생 지점!

        FriendDataManager dataManager = FriendDataManager.get(this);

        if(numOfTODOs == 1) {
            String toastMessage = hatt_id + ": " + dataManager.getTalkStDetail(dataManager.getTalkSt()).get(0);
            toast.showToast(toastMessage, Toast.LENGTH_SHORT);
        } else if (numOfTODOs == 5){
            String toastMessage = hatt_id + ": " + dataManager.getTalkStDetail(dataManager.getTalkSt()).get(4);
            toast.showToast(toastMessage, Toast.LENGTH_SHORT);

        } else
            return;
    }

    public void ifEditMenuClicked(){

        editMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                editMenu.setVisibility(View.INVISIBLE);
                finishMenu.setVisibility(View.VISIBLE);


                MyDragSortAdapter.isOnEditMenu = false;
                requery();

                rl2.setVisibility(View.INVISIBLE);
                deleteAllButton.setVisibility(View.VISIBLE);

            }
        });
    }

    public void ifFinishMenuClicked(){

        finishMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finishMenu.setVisibility(View.INVISIBLE);
                editMenu.setVisibility(View.VISIBLE);


                MyDragSortAdapter.isOnEditMenu = true;
                requery();

                rl2.setVisibility(View.VISIBLE);
                deleteAllButton.setVisibility(View.INVISIBLE);
                // 어댑터에 옵션 걸어줘서 다시 populateListView 해야함..

            }
        });
    }
    private Handler handler = new Handler() { // main쓰레드가 아니기 때문에 handler객체를 통하여 main을 modified 해줘야 함.
        public void handleMessage(Message msg) {
            setTimeIntervalByDateBar(); //handleMessage객체는 setTimeIntervalByDateDar메소드를 호출함.
        }
    };

    public void showDate(){
        //처음 showDate를 호출할 시 시간을 set해주고, 현재 분을 받아온다.
        final int startMinute = setTimeIntervalByDateBar();
        //anonymous한 TimerTask 객체 생성.
        final TimerTask task = new TimerTask() {
            public void run() { // 쓰레드를 상속받고 있기 때문에 오버라이딩 해준다.
                handler.obtainMessage().sendToTarget();// obtainMessage().sendToTarget()메소드를 호출해 줘야 메세지가 정상적으로 전달 됨.
            }
        };
        //해당 쓰레드는 처음 로딩 시 핸드폰 속의 시간과 앱의 시간을 동기화 해주는 쓰레드이다.
        new Thread(){ // 쓰레드 객체 하나 생성.
          public void run() { // 오버라이딩.
              while(true) { // 무한 조건검사.
                  try {
                        Thread.sleep(2000); // 처음에는 2초마다 검사를 하고(2초의 오차가 있을 수 있음)
                      if(startMinute==Calendar.getInstance().get(Calendar.MINUTE)) { //만약에 분이 변경 되었을 시부터
                          new Timer().schedule(task, 0, 60000); // 1분마다 set해준다.
                          break; // 설정이 완료 되었으니 10초마다 검사하는 반복문은 필요없지.
                      }
                  } catch (Exception ex ){ex.printStackTrace();} //for debug
              }
          }
        }.start();
        // need to convert Eng-based day to Kor-based day.
    }
    public int setTimeIntervalByDateBar() {
        Calendar rightNow = GregorianCalendar.getInstance();
        // Calendar's getInstance method returns a calendar whose locale is based on system settings
        // and whose time fields have been initialized with the current date and time.

        int monthInfo = rightNow.get(Calendar.MONTH) + 1;
        int dateInfo = rightNow.get(Calendar.DATE);
        int dayInfo = rightNow.get(Calendar.DAY_OF_WEEK);
        int ampmInfo = rightNow.get(Calendar.AM_PM);
        int hourInfo = rightNow.get(Calendar.HOUR);
        int minuteInfo = rightNow.get(Calendar.MINUTE);

        String KOR_DAY = dayConverter(dayInfo);
        String KOR_AMPM = ampmConverter(ampmInfo);
        String KOR_MINUTE = minuteConverter(minuteInfo);
        String KOR_HOUR = hourConverter(hourInfo, ampmInfo);

        /*처음 프로그램 시작 시 system의 시간을 받아온다.*/
        dateBar.setText(monthInfo + "월 " + dateInfo + "일 " + KOR_DAY + " " + KOR_AMPM + " " + KOR_HOUR + ":" + KOR_MINUTE);

        return Calendar.getInstance().get(Calendar.MINUTE)+1;//final int startInt = Calendar.getInstance().get(Calendar.MINUTE)+1;
    }


    public String getDate(){
        Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR) - 2000;
        int month = rightNow.get(Calendar.MONTH) + 1;
        int date = rightNow.get(Calendar.DATE);
        int hour = rightNow.get(Calendar.HOUR_OF_DAY); // HOUR_OF_DAY -> 24-hour clock.
        int minute = rightNow.get(Calendar.MINUTE);
        int second = rightNow.get(Calendar.SECOND);

        String key = Integer.toString(year)
                + "/"
                + DateConverter(month)
                + "/"
                + DateConverter(date)
                + "/"
                + DateConverter(hour)
                + "/"
                + DateConverter(minute)
                + "/"
                + DateConverter(second);

     //   Toast.makeText(getApplicationContext(), "key ID = " + key, Toast.LENGTH_SHORT).show();
        // For debugging, this key is designed to distinguish different IDs.

        return key;
    }

    public String DateConverter(int time){

        String temp = Integer.toString(time);

        if(time < 10){
            StringBuffer zero = new StringBuffer("0");
            return zero.append(temp).toString();
        }
        return temp;
    }

    public void respondToUserInput(){
        memoInput.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                addButton.setVisibility(View.VISIBLE);
                addLine.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                       /* After a user types some text, a button disappear.*/
                    addButton.setVisibility(View.INVISIBLE);
                    addLine.setVisibility(View.INVISIBLE);
                }

            }
        });

    }

    public void screenUpAndDownOnItsState(){
        myLayout.addSoftKeyboardLsner(new SoftKeyboardLsnedRelativeLayout.SoftKeyboardLsner() {
            @Override
            public void onSoftKeyboardShow() {
                Log.d("SoftKeyboard", "Soft keyboard shown");
                MyDragSortAdapter.isUserOnTyping = true;
                rl1.setVisibility(View.INVISIBLE);

                ViewGroup.MarginLayoutParams LL = (ViewGroup.MarginLayoutParams) dateLayout.getLayoutParams();
                LL.topMargin = pixelToDP(40);

                ViewGroup.MarginLayoutParams LV = (ViewGroup.MarginLayoutParams) lv1.getLayoutParams();
                LV.topMargin = pixelToDP(122); //96

            }

            @Override
            public void onSoftKeyboardHide() {
                Log.d("SoftKeyboard", "Soft keyboard hidden");
                MyDragSortAdapter.isUserOnTyping = false;

                rl1.setVisibility(View.VISIBLE);

                ViewGroup.MarginLayoutParams LL = (ViewGroup.MarginLayoutParams) dateLayout.getLayoutParams();
                LL.topMargin = pixelToDP(55);

                ViewGroup.MarginLayoutParams LV = (ViewGroup.MarginLayoutParams) lv1.getLayoutParams();
                LV.topMargin = pixelToDP(137);

            }
        });


    }

    public int pixelToDP(int paddingPixel){
        float density = getBaseContext().getResources().getDisplayMetrics().density;
        int paddingDp = (int)(paddingPixel * density);
        return paddingDp;
    }

    public void setFont(){
        dateBar.setTypeface(NanumSquare_B);
        todayBar.setTypeface(NanumSquare_B);
        memoInput.setTypeface(NanumSquare_B);
        editMenu.setTypeface(NanumSquare_B);
        addButton.setTypeface(NanumSquare_B);
        finishMenu.setTypeface(NanumSquare_B);
        deleteAllButton.setTypeface(NanumSquare_B);

        ((TextView)findViewById(R.id.friend_edit)).setTypeface(NanumSquare_B);
        ((TextView)findViewById(R.id.friend_ask_1)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.friend_ask_2)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.friendlyNo)).setTypeface(NanumSquare_B);
        ((TextView)findViewById(R.id.completeScheduleNo)).setTypeface(NanumSquare_B);
        ((TextView)findViewById(R.id.repeatScheduleNo)).setTypeface(NanumSquare_B);
        ((TextView)findViewById(R.id.friendNoTextView)).setTypeface(NanumSquare_B);
        ((TextView)findViewById(R.id.completeScheduleTextView)).setTypeface(NanumSquare_B);
        ((TextView)findViewById(R.id.repeatScheduleNoTextView)).setTypeface(NanumSquare_B);
        ((TextView)findViewById(R.id.myFriendNo)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.completeSchedule)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.againSchedule)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.setting)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.pushAlarm)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.backgroundTheme)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.setBackgroundTheme)).setTypeface(NanumBarunGothic_R);
        ((TextView)findViewById(R.id.bellMode)).setTypeface(NanumBarunGothic_R);

    }

    public String minuteConverter(int minuteInfo){
        String formatted = String.format("%02d", minuteInfo);
        return formatted;
    }

    public String ampmConverter(int ampmInfo){
        if(ampmInfo == 0)
            return "AM";
        else
            return "PM";
    }

    public String hourConverter(int hourInfo, int ampmInfo){
        if(ampmInfo == 1 && hourInfo == 0)
            return "12";
        else
            return Integer.toString(hourInfo);
    }

    public String dayConverter(int dayInfo){
        switch(dayInfo){
            case 1:
                return "(일)";
            case 2:
                return "(월)";
            case 3:
                return "(화)";
            case 4:
                return "(수)";
            case 5:
                return "(목)";
            case 6:
                return "(금)";
            case 7:
                return "(토)";
        }
        return "Error";
    }

    public String dayTranslator(int dayInfo){
        switch(dayInfo){
            case 1:
                return "일";
            case 2:
                return "월";
            case 3:
                return "화";
            case 4:
                return "수";
            case 5:
                return "목";
            case 6:
                return "금";
            case 7:
                return "토";
        }
        return "Error";
    }

    public void completeIfItemClicked(){
        lv1.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                // 편집 상태에서만 일정의 완료가 가능하게 만들기.
                if(!MyDragSortAdapter.isOnEditMenu)
                    return;

                final int rowId = position + 1;

                if (myEventController.isCompleted(rowId)) {
                    //클릭된 일정이 끝난 일정일 경우.
                    if (rowId == 1) { // 선택된 첫 번째 일정이 이미 완료됐을 경우, 해당 일정의 completeness 값만 0으로 바꿔줌.
                        //myEventController.moveDataTo(1, tempMemo); // Just update the value at position 1.
                        myEventController.updateCompleteness("1", 0); // 1번 자리에 있는 일정의 완수여부를 미완으로 바꿈.
                    } else // 선택된 일정이 두 번째나 두 번째 이후의 일정일 경우,
                        shiftAndInsert(rowId);
                    /* */
                } else {

                    //slide_out_right(lv1, position);
                    fade(lv1, position);
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            deleteAndInsert(rowId);
                            requery();
                        }
                    }, 70);
                }

                requery();
            }
        });
    }

    public void ifClickedDeleteAllRows(){
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myEventController.deleteAllData();
                myEventController.rearrangeData(Integer.toString(myEventController.numOfEntries()));
                // 삭제와 관련된 부분은 adapter를 새로 설정해줘야함;
                requery();
            }
        });

    }

    public boolean deleteRow(String rowId){

        Integer deletedRows = myEventController.deleteData(rowId);
        myEventController.rearrangeData(rowId);
        requery();
        return deletedRows != 0;
    }

    public void shiftAndInsert(int rowId){
        ContentValues tempData = myEventController.getAllContent(rowId);
        // tempMemo에 일정에 대한 다른 모든 정보가 들어가야 한다. 지금은 텍스트만 되고 있음.
        // 해당 포지션에 있는 모든 정보를 다 가져와야함.

        int fromPos = myEventController.getBorderlinePos();
        int toPos = rowId-1;
        myEventController.shiftAllData(fromPos, toPos);
        //myEventController.moveDataTo(fromPos, tempMemo);
        myEventController.moveDataTo(fromPos, tempData);
        myEventController.updateCompleteness(Integer.toString(fromPos), 0);

    }

    public void deleteAndInsert(int rowId){
        // 수정 필요
        ContentValues tempData = myEventController.getAllContent(rowId);
        deleteRow(Integer.toString(rowId));
        myEventController.insertData("", true); // 여기서의 insertData값은 어차피 업데이트 되므로 상관 없음.
        myEventController.moveDataTo(myEventController.numOfEntries(), tempData);
        myEventController.updateCompleteness(Integer.toString(myEventController.numOfEntries()), 1);
    }


    public void addOnUserInput() {
        // 이 부분 고쳐야 함;;
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String memo = memoInput.getText().toString();
                if (memo.isEmpty()) {
                    return;
                    /* If a user does not type any word, then, memo is not to be added to the list. */
                }

                if (myEventController.numOfEntries() == 0) { // 이 조건이 있는 이유는, DB가 비어있을 때, isThereCompleteData()를 호출하면 에러가 뜨기 때문.
                    myEventController.insertData(memo, false);
                } else {
                    if (myEventController.isThereCompletedData()) {
                        // 완료된 일정이 하나라도 있을 경우, 새로운 일정은 일반 일정의 맨 끝과 완료된 일정의 처음 부분 사이
                        myEventController.insertData(memo, false); // 일단 메모 내용을 맨 밑으로 넣은 다음에,
                        ContentValues insertedData = myEventController.getAllContent(myEventController.numOfEntries());

                        int fromPos = myEventController.getBorderlinePos();
                        int toPos =  myEventController.numOfEntries();

                        myEventController.shiftAllData(fromPos, toPos); // 인덱스 1부터 마지막거 하나 전까지의 데이터를 한칸씩 뒤로 밀고,
                        myEventController.moveDataTo(fromPos, insertedData);

                        //myEventController.updateMemo("1", memo); // 인덱스 1의 memo 내용 업데이트...

                        // 이부분에서 메모내용만 업데이트 하면 됨. 새로 입력한 데이터는 메모값만 갖고 있기 때문에.
                        // 근데 밀기 이전에 데이타가 남아있을 수 있다.
                        // 그래서 새로 생긴 걸로 다 업데이트를 해줘야돼.
                        // fromPos부터 밀기 시작하므로;
                        //myEventController.moveDataToTop(memo);
                    } else
                        myEventController.insertData(memo, false);
                }

                    /*
                    * These lines makes a soft keyboard disappear when user finishes typing.
                    * */
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                memoInput.setText("");


                requery();
                //Toast.makeText(getBaseContext(),"lastIdx = " + Integer.toString((int)myEventController.numOfEntries()), Toast.LENGTH_SHORT).show();
                toastProperMessage(hettSettingSharedPreference.searchHattFriendName(getApplicationContext()), myEventController.numOfEntries()); // hatti는 임시 ID, 나중에 유저가 set한 걸 받아와야 함;
            }
        });
    }

    private void populateListView(){
        Cursor cursor = myEventController.getAllData();
        String[] fromFieldNames = new String[] { EventTableController.Columns.MEMO };
        int[] toViewIDS = new int[] { R.id.memoContent};

        myDragSortAdapter =
                new MyDragSortAdapter(this, R.layout.list_item_memo, cursor, fromFieldNames, toViewIDS, myEventController);
        AlphaInAnimationAdapter animationAdapter = new AlphaInAnimationAdapter(myDragSortAdapter);
        animationAdapter.setAbsListView(lv1);
        lv1.setAdapter(animationAdapter);
        lv1.setDropListener(onDrop);

        /* 어댑터를 세팅해주는 것은 한 번만 있으면 될 것 같다.
        즉 populateListView() 메소드는 onCreate에서 한 번만 호출되면 됨.
         getBaseContext --> this : Since there was a cast problem when using getBaseContext, it was
         replaced with this.
         */
    }
    // 사라지는 애니메이션
    public void fade(DragSortListView lv1, int rowId){
        ImageView image = (ImageView) lv1.getChildAt(rowId).findViewById(R.id.finishLine1); // 이렇게 해야 정확히 차일드뷰의 뷰를 찾는다.
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade);
        image.startAnimation(animation);
        //return view;
    }

    // 줄 긋는 애니메이션
    public void slide_out_right(DragSortListView lv1, int rowId){
        ImageView image = (ImageView) lv1.getChildAt(rowId).findViewById(R.id.finishLine1);
        Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.slide_out_right);
        image.startAnimation(animation);
        //return view;
    }

    /*
    * 밑의 lifecycle methods는 디버그를 위해 존재한다.
    * */
    @Override
    public void onStart(){
        super.onStart();
        Log.d(TAG, "onStart(Bundle) called");
    }

    @Override
    public void onPause(){ // 화면이 이동되서 없어질 때.
        super.onPause();
        sendBroadcast(new Intent("android.appwidget.action.APPWIDGET_UPDATE"));
        Log.d(TAG, "onPause(Bundle) called");
        if(isOpened != 0)
            overridePendingTransition(R.anim.activity_start_first, R.anim.activity_start_second);// 화면 이동 시 애니메이션.
    }

    @Override
    public void onResume(){ // 화면이 다시 나타날 때.
        renewAllEvents();
        Cursor cursor = myEventController.getAllData();
        myDragSortAdapter.changeCursor(cursor);
        super.onResume();
        showDate(); // 시간을 동기화하기 위해!
        Log.d(TAG, "onResume(Bundle) called");
        overridePendingTransition(R.anim.activity_end_first, R.anim.activity_end_second);
        BackgroundThemeManager.getInstance().setBackground(getApplicationContext(), (SoftKeyboardLsnedRelativeLayout) findViewById(R.id.myLayout),ActivityNo.MAIN_ACTIVITY);
    }

    @Override
    public void onStop(){
        super.onStop();
        sendBroadcast(new Intent("android.appwidget.action.APPWIDGET_UPDATE"));
        Log.d(TAG, "onStop() called");

    }

    public void onDestroy(){
        super.onDestroy();
        //((CursorAdapter)lv1.getAdapter()).getCursor().close(); // ?
        //((SimpleCursorAdapter)lv1.getAdapter()).getCursor().close(); // ?
        sendBroadcast(new Intent("android.appwidget.action.APPWIDGET_UPDATE"));
        myEventController.myDb.close();
        Log.d(TAG, "onDestroy() called");
    }

    public void requery(){
        Cursor values = myEventController.getAllData();
        myDragSortAdapter.changeCursor(values);
    }
    /*ㄱㅎ*/
    /**
     * MainActivity속에 있는 include객체
     */
    private View includeView;
    /**
     * NavigationDrawer객체.
     */
    private DrawerLayout drawerLayout;
    /**
     * NavigationDrawer속에 있는 객체.
     */
    private View drawerView;
    /**
     * navigation_drawer에 include된 mainPage.
     */
    private View mainView;
    /**
     * 친구 버튼
     */
    private ImageView friendBtn;
    private TextView againSchedule;
    private TextView completeSchedule;
    private TextView repeatSchedule;
    /**
     * NavigationDrawer의 알람 토글 버튼.
     */
    private ToggleButton isBellMode;
    /**
     * 메세지 모드 토글버튼.
     */
    private ToggleButton isPushAlarm;

    /**
     * 배경테마 글씨를 저장하는 텍스트뷰.
     */
    private TextView setBackgroundTheme;
    /**
     * 배경화면 테마 선택하는 페이지로 이동하는 이미지뷰 버튼.
     */
    private ImageView backgroundThemeRightButton;
    /**
     * Edit_friend_name_activity화면으로 이동 시 startActivityForResult로 화면 이동을 하기 때문에
     * 화면에 대한 번호를 정해줬다.
     * 번호를 팀원들끼리 몇 번을 사용하겠다 라는 말이 없어서 10번부터 시작했다
     * -기호-
     */
    private static final int EDIT_FRIEND_NAME_ACTIVITY=10;
    private static final int SETTING_BACKGROUND_THEME_ACTIVITY=11;
    private static final int FRIEND_SETTING_ACTIVITY=12; // 윤선이꺼
    private static final int COMPLETE_ACTIVITY=13;
    private static final int REPEAT_ACTIVITY=14;
    /**
     * Edit_friend_name_activity화면으로 이동 후, 이름을 서로 다른 메소드에서 변경을 하기 때문에, 로컬변수로는
     * 해결할 수 없는 문제점이 있었다.
     * 그래서 멤버필드로 정의하고, 다음과 같이 사용하게 되었다.
     */
    private TextView friend_edit;
    /**
     * NavigationDrawer를 초기화하는 메소드를 호출하는 메소드.
     */
    private int isOpened=0;
    private TextView backgroundTheme;
    private void initNavigationDrawer(){
        DrawerTableController.getInstance(getApplicationContext());

        initForDrawer(); // 드로워 초기화 메소드
        initFriendAsk(); // 드로워의 대화상자 초기화 메소드.
        // 해당 메소드는 onDrawerStateChanged 메소드에서 호출하게 되었다.
        // initFriendlyNo(); // 드로워의 친밀도 점수 표기 메소드.
        initDrawerMenu();
    }
    /**
     * NavigationDrawer의 menu를 초기화 하는 메소드.
     */
    private void initDrawerMenu() {
        // 토글 버튼 객체 받아오고 이벤트 등록.
        isPushAlarm = (ToggleButton) findViewById(R.id.isPushAlarm);
        registerTogggleButtonByPushalarm(isPushAlarm);
        isBellMode = (ToggleButton) findViewById(R.id.isBellMode);
        registerToggleButtonByBellMode(isBellMode);
        //배경화면 세팅 하는 TextView.
        setBackgroundTheme = (TextView)findViewById(R.id.setBackgroundTheme);
        //만얀 배경화면 세팅 화면을 선택했을 경우.
        setBackgroundTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBackgroundMenu();
            }
        });
        backgroundThemeRightButton = (ImageView)findViewById(R.id.backgroundThemeRightButton); // 드로워의 배경 테마 변경 버튼이다(버튼이지만 이미지뷰로 구현했음)
        backgroundThemeRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBackgroundMenu();
                //Ctrl + F -> animation
                //Animation animation = AnimationUtils.loadAnimation(MainActivity.this,R.anim.tranlate);
                //v.startAnimation(animation);
                /*ImageView imageView = new ImageView(getApplicationContext());
                imageView.setImageDrawable(getResources().getDrawable(R.drawable.check));
                imageView.startAnimation(animation);*/
            }
        });
        backgroundTheme = (TextView)findViewById(R.id.backgroundTheme);
        backgroundTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectBackgroundMenu();
            }
        });
        completeSchedule = (TextView)findViewById(R.id.completeSchedule);
        completeSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), CompleteActivity.class);
                startActivityForResult(intent, COMPLETE_ACTIVITY);
            }
        });
        repeatSchedule = (TextView)findViewById(R.id.againSchedule);
        repeatSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), RepeatEventActivity.class);
                startActivityForResult(intent, REPEAT_ACTIVITY);
            }
        });
    }

    /**
     * 푸쉬알람의 ToggleButton의 이벤트를 등록하기 위한 메소드이다.
     * @param toggleButton 푸쉬알람 객체.
     */
    private void registerTogggleButtonByPushalarm(final ToggleButton toggleButton) {
        toggleButton.setText(null);
        toggleButton.setTextOn(null);
        toggleButton.setTextOff(null); // 토글의  OFF/ON 글자를 없앰.. 이거 안없애면 이미지 뒤에 글자가 나와서 화남.
        //DB에 접근해서 pushMode가 true인지 false인지 체크한다.
        boolean isPushMode = hettSettingSharedPreference.searchPushAlarm(getApplicationContext());
        if(isPushMode) {
            toggleButton.setSelected(true);
            toggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.on));
        } else {
            toggleButton.setSelected(false);
            toggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));
        }
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hettSettingSharedPreference.searchPushAlarm(getApplicationContext())) {
                    toggleButton.setBackground(getResources().getDrawable(R.drawable.on));
                    //PushAlarmReservation.getInstance().changePushAlarmMode(true);
                    hettSettingSharedPreference.updatePushAlarm(getApplicationContext(), true);

                } else {
                    toggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));
                    //PushAlarmReservation.getInstance().changePushAlarmMode(false); break;
                    hettSettingSharedPreference.updatePushAlarm(getApplicationContext(), false);

                }
            }
        });
    }

    /**
     * 무음모드 토글 버튼을 클릭했을 시 작동되는 동작.
     * 푸쉬 알람이나 기타 등등 무음모드를 자체적으로 설정할 수 있게끔 해달라 했다.
     * @param toggleButton
     */
    private void registerToggleButtonByBellMode(final ToggleButton toggleButton )  {
        toggleButton.setText(null);
        toggleButton.setTextOn(null);
        toggleButton.setTextOff(null); // 토글의  OFF/ON 글자를 없앰.. 이거 안없애면 이미지 뒤에 글자가 나와서 화남.
        //DB에 접근해서 pushMode가 true인지 false인지 체크한다.
        boolean isBellMode = hettSettingSharedPreference.searchBellMode(getApplicationContext());
        if(isBellMode) {
            //toggleButton.setSelected(true);
            toggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.on));
            ((AudioManager)getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE)).setMode(AudioManager.RINGER_MODE_SILENT);
        } else {
            //toggleButton.setSelected(false);
            toggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));
            ((AudioManager)getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE)).setMode(AudioManager.RINGER_MODE_NORMAL);
        }
        toggleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hettSettingSharedPreference.searchBellMode(getApplicationContext())) {

                    toggleButton.setBackground(getResources().getDrawable(R.drawable.on));
                    hettSettingSharedPreference.updateBellMode(getApplicationContext(), true);
                    ((AudioManager)getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE)).setMode(AudioManager.RINGER_MODE_SILENT);
                } else {
                    toggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));
                    //PushAlarmReservation.getInstance().changePushAlarmMode(false); break;
                    hettSettingSharedPreference.updateBellMode(getApplicationContext(), false);
                    ((AudioManager)getApplicationContext().getSystemService(getApplicationContext().AUDIO_SERVICE)).setMode(AudioManager.RINGER_MODE_NORMAL);
                }
            }
        });
    }
    /**
     * 드로워 부분의 친밀도를 DB에 접근해서 setting하는 메소드.
     */
    private void initFriendlyNo() {
        //drawerTableController에 instance를 생성함.

        //완료일정 count.
        TextView completeScheduleNo = (TextView)findViewById(R.id.completeScheduleNo);
        completeScheduleNo.setText(String.valueOf(myCompleteEventController.getCompleteDataCnt()));
        //Toast.makeText(getApplicationContext(),DrawerTableController.getInstance().setDataBaseHelper(getApplicationContext()).searchByCompleteEvent()+"",Toast.LENGTH_SHORT).show();
        //반복일정 count.
        //searchByRepeatEvent
        TextView repeatScheduleNo = (TextView)findViewById(R.id.repeatScheduleNo);
        repeatScheduleNo.setText(String.valueOf(myRepeatEventController.getRepeatDataCnt()));
       // repeatScheduleNo.setText(DrawerTableController.getInstance().searchByRepeatEvent() + "");
        //selectByFriendName
        friend_edit = (TextView)findViewById(R.id.friend_edit);
        friend_edit.setText(hettSettingSharedPreference.searchHattFriendName(getApplicationContext()));
        //드로워의 시간 초기화
        //
        //getTotalPoint
        double friendlyStr = FriendDataManager.get(getApplicationContext()).getTotalPoint() ;

        if(friendlyStr == 0.0)
            ((TextView)findViewById(R.id.friendlyNo)).setText("0");
        else
            ((TextView)findViewById(R.id.friendlyNo)).setText(new DecimalFormat("0.0").format(friendlyStr));

        setBackgroundTheme.setText(DrawerTableController.getInstance().searchSelectedBackgroundTheme());


    }
    /**
     * 가상친구 대화하는 부분을 DB에 접근해서 대화하는 창.
     */
    private void initFriendAsk() {

    }
    /**
     * NavigationDrawer 초기화 부분.
     */
    private void initForDrawer() {
        //mainView = (View)findViewById(R.id.main_view); // navigation_drawer에 있는 include속성값을 받아온다.
        DrawerLayout.DrawerListener myDrawerListener = new DrawerLayout.DrawerListener() {
            public void onDrawerClosed(View drawerView) {
                isOpened=-1;
            }
            public void onDrawerOpened(View drawerView) {

            }
            public void onDrawerSlide(View drawerView, float slideOffset) {

            }
            /**
             * state가 2일 때 Drawer가 Open되는것.
             * @param newState
             */
            public void onDrawerStateChanged(int newState) {
                switch (newState) {
                    case 2 :
                        isOpened=2;
                        initFriendlyNo();
                        break;
                }
            }
        };
        drawerLayout = (DrawerLayout) findViewById(R.id.drawerLayout);//1 drawerLayout을 받아온다.
        drawerView = (View) findViewById(R.id.navigation_drawer); // 2. drawer의 내용물을 받아온다.

        friendBtn = (ImageView) findViewById(R.id.friendBtn); // 3. mainView(activity_main의)버튼을 받아온다.
        friendBtn.setOnClickListener(new View.OnClickListener() { // 4버튼 이벤트 등록.

            public void onClick(View arg0) {
                drawerLayout.openDrawer(drawerView); // 5. DrawerLayout을 Drawer한다.
            }
        });
        drawerLayout.setDrawerListener(myDrawerListener); //6 DrawerListener를 등록해야한다.
        drawerView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {

                return true;
            }
        });
        //드로워의 시간 초기화
        ImageView friend_name_edit_btn = (ImageView) findViewById(R.id.friend_name_edit_btn);
        friend_name_edit_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("friend_edit", friend_edit.getText().toString());
                intent.setClass(getApplicationContext(), EditFriendNameActivity.class);
                startActivityForResult(intent, EDIT_FRIEND_NAME_ACTIVITY);
            }
        });

        TextView setting = (TextView)findViewById(R.id.setting);
        setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, FriendSettingActivity.class);
                startActivityForResult(intent,FRIEND_SETTING_ACTIVITY);
            }
        });

        TextView myFriendNo = (TextView)findViewById(R.id.myFriendNo);
        myFriendNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ClosenessActivity.class);
                startActivity(intent);
            }
        });
        //윤선
        FriendDataManager manager = FriendDataManager.get(this);
        friend_ask_1.setText("" + manager.getTalkStDetail(manager.getTalkSt()).get(2).toString());
        friend_ask_2.setText("" + manager.getTalkStDetail(manager.getTalkSt()).get(3).toString());

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);
        Bundle extraBundle;
        //MainActivity에서 부여한 번호표를 비교
        /*
        * initForDrawer메소드의 friend_name_edit_btn 버튼을 클릭하여 가상 친구의 이름 변경 액티비티로 이동 후, 다시 원래 화면으로 돌아왔을 당시,
        * 이름이 변경되었을 확률이 있으므로 변경된 이름을 set 해줘야 사용자가 볼 수 있다.
        * */
        if (requestCode == EDIT_FRIEND_NAME_ACTIVITY) {
            Log.d("EDIT_FRIEND_NAME_LOG", "THIS CLOSE !!");//로그기록
            //번호표를 부여한 Activity의 실행 여뷰, 켄슬, 오케이, 등등 실행에 관련된 행위 구분
            if (resultCode == RESULT_OK) {//세컨드 액티비티에서 이 값을 반환하는 코드가 동작 됐을때
                extraBundle = intent.getExtras();//번들로 반환됐으므로 번들을 불러오면 셋팅된 값이 있다.
                String new_friend_edit_name = extraBundle.getString("new_friend_edit_name");//인자로 구분된 값을 불러오는 행위를 하고
                friend_edit.setText(new_friend_edit_name); //변경된 이름을 setting!
            }

        }
        else if(requestCode == SETTING_BACKGROUND_THEME_ACTIVITY) {

            if (resultCode == RESULT_OK) {
                extraBundle = intent.getExtras();//번들로 반환됐으므로 번들을 불러오면 셋팅된 값이 있다.
                String new_friend_edit_name = extraBundle.getString("background_theme_name");//인자로 구분된 값을 불러오는 행위를 하고
                setBackgroundTheme.setText(new_friend_edit_name); //변경된 이름을 setting!
            }
        }
        else if (requestCode == FRIEND_SETTING_ACTIVITY) {
            if(resultCode==RESULT_OK) {
                extraBundle = intent.getExtras();
                friend_edit.setText(hettSettingSharedPreference.searchHattFriendName(getApplicationContext()));
            }
        }
        else if(requestCode == REPEAT_ACTIVITY){
            if(resultCode == RESULT_OK){
                extraBundle = intent.getExtras();
                TextView repeatScheduleNo = (TextView)findViewById(R.id.repeatScheduleNo);
                repeatScheduleNo.setText(extraBundle.getString("repeat_data_cnt"));
            }
        }
        else if(requestCode == COMPLETE_ACTIVITY){
            if(resultCode == RESULT_OK){
                extraBundle = intent.getExtras();
                TextView completeScheduleNo = (TextView)findViewById(R.id.completeScheduleNo);
                completeScheduleNo.setText(extraBundle.getString("complete_data_cnt"));
            }
        }
    }
    /**
     * 메뉴의 배경화면 텍스트나, 오른쪽 화살표, 혹은 선택되어 있는 테마의 이름을 선택할 시 이 메소드가 호출이 된다.
     */
    private void selectBackgroundMenu() {
        Intent intent = new Intent(getApplicationContext(), SettingBackgroundThemeActivity.class);
        startActivityForResult(intent, SETTING_BACKGROUND_THEME_ACTIVITY);
    }
}
//((TextView)findViewById(R.id.currentTimer)).setText(new SimpleDateFormat("MM월dd일 (E) a HH시 mm분", Locale.KOREA).format(new Date()).toString()); /*TextClock currentTimer = (TextClock) findViewById(R.id.currentTimer); currentTimer.setFormat12Hour("MM월dd일 (E) a HH시 mm분");*///이게 원래코드.
