package com.teambulldozer.hett;


import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

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
    MyDragSortAdapter myDragSortAdapter;
    DragSortListView lv1;
    DragSortController dragSortController;
    EditText memoInput;
    TextView addButton;
    TextView dateBar;
    TextView todayBar;
    TextView editMenu;
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
        FriendDataManager manager = FriendDataManager.get(this);
        setContentView(R.layout.activity_main);
        //
        //java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getApplicationContext());

        /* Call the database constructor */
        myEventController = EventTableController.get(this); //

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
        // 리스트뷰에 아이템 올리기
        // 그 외에 클릭하면 삭제하는 기능은 MyDragSortAdapter에 구현.


        /* Additional details */

        // The following line makes software keyboard disappear until it is clicked again.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        /*기호*/

        initNavigationDrawer(); //drawer에 대한 모든것을 초기화 하기 위한 메소드.
    }

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
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

        if(numOfTODOs == 1) {
            String toastMessage = hatt_id + ": " + "오늘도 힘내구~!";
            toast.showToast(toastMessage, Toast.LENGTH_SHORT);
        } else if (numOfTODOs == 5){
            String toastMessage = hatt_id + ": " + "다 할 수 있겠어!? 대단하다~~";
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

    public void showDate(){

        Calendar rightNow = Calendar.getInstance();
        // Calendar's getInstance method returns a calendar whose locale is based on system settings
        // and whose time fields have been initialized with the current date and time.

        int monthInfo = rightNow.get(Calendar.MONTH);
        int dateInfo = rightNow.get(Calendar.DATE);
        int dayInfo = rightNow.get(Calendar.DAY_OF_WEEK);
        int ampmInfo = rightNow.get(Calendar.AM_PM);
        int hourInfo = rightNow.get(Calendar.HOUR);
        int minuteInfo = rightNow.get(Calendar.MINUTE);

        String KOR_DAY = dayConverter(dayInfo);
        String KOR_AMPM = ampmConverter(ampmInfo);
        String KOR_MINUTE = minuteConverter(minuteInfo);
        dateBar.setText(monthInfo + "월 " + dateInfo + "일 " + KOR_DAY + " " + KOR_AMPM + " " + hourInfo + ":" + KOR_MINUTE);
        // need to convert Eng-based day to Kor-based day.

    }

    public int getDate(){
        Calendar rightNow = Calendar.getInstance();
        int year = rightNow.get(Calendar.YEAR);
        int month = rightNow.get(Calendar.MONTH);
        int date = rightNow.get(Calendar.DATE);

        int timeKey = (year * 10000) + (month * 100) + date; // This timeKey is used to give input to database.
        return timeKey;
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
                    }, 80);
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
                myEventController.rearrangeData(Integer.toString((int) myEventController.numOfEntries()));
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
        myEventController.insertData("", true);
        myEventController.moveDataTo((int) myEventController.numOfEntries(), tempData);
        myEventController.updateCompleteness(Integer.toString((int) myEventController.numOfEntries()), 1);
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
                        ContentValues insertedData = myEventController.getAllContent((int) myEventController.numOfEntries());

                        int fromPos = myEventController.getBorderlinePos();
                        int toPos = (int) myEventController.numOfEntries();

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
                toastProperMessage(FriendDataManager.get(getApplicationContext()).getFriendName(), (int) myEventController.numOfEntries()); // hatti는 임시 ID, 나중에 유저가 set한 걸 받아와야 함;
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
    public void onPause(){
        super.onPause();
        Log.d(TAG, "onPause(Bundle) called");
    }

    @Override
    public void onResume(){
        Cursor cursor = myEventController.getAllData();
        myDragSortAdapter.changeCursor(cursor);
        super.onResume();
        Log.d(TAG, "onResume(Bundle) called");
    }

    @Override
    public void onStop(){
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    public void onDestroy(){
        super.onDestroy();
        //((CursorAdapter)lv1.getAdapter()).getCursor().close(); // ?
        //((SimpleCursorAdapter)lv1.getAdapter()).getCursor().close(); // ?
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
    private TextView completeSchedule;
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
    /**
     * Edit_friend_name_activity화면으로 이동 후, 이름을 서로 다른 메소드에서 변경을 하기 때문에, 로컬변수로는
     * 해결할 수 없는 문제점이 있었다.
     * 그래서 멤버필드로 정의하고, 다음과 같이 사용하게 되었다.
     */
    private TextView friend_edit;
    /**
     * NavigationDrawer를 초기화하는 메소드를 호출하는 메소드.
     */
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
        //배경화면 세팅 하는 TextView.
        setBackgroundTheme = (TextView)findViewById(R.id.setBackgroundTheme);
        //만얀 배경화면 세팅 화면을 선택했을 경우.
        setBackgroundTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(); // 인텐트를 생성
                intent.setClass(getApplicationContext(), SettingBackgroundThemeActivity.class); // SettingbackgroundActivity로 이동.
                startActivityForResult(intent, 0);
            }
        });
        backgroundThemeRightButton = (ImageView)findViewById(R.id.backgroundThemeRightButton); // 드로워의 배경 테마 변경 버튼이다(버튼이지만 이미지뷰로 구현했음)
        backgroundThemeRightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), SettingBackgroundThemeActivity.class);
                startActivity(intent);
            }
        });
        completeSchedule = (TextView)findViewById(R.id.completeSchedule);
        completeSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(),CompleteActivity.class);
                startActivityForResult(intent,0);
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
        boolean isPushMode = DrawerTableController.getInstance().searchPushMode();
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
                if (toggleButton.isChecked()) {
                    toggleButton.setBackground(getResources().getDrawable(R.drawable.on));
                    //PushAlarmReservation.getInstance().changePushAlarmMode(true);
                    DrawerTableController.getInstance().updatePushMode(true);
                } else {
                    toggleButton.setBackgroundDrawable(getResources().getDrawable(R.drawable.off));
                    //PushAlarmReservation.getInstance().changePushAlarmMode(false); break;
                    DrawerTableController.getInstance().updatePushMode(false);
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
        completeScheduleNo.setText(DrawerTableController.getInstance().searchByCompleteEvent() + "");
        //Toast.makeText(getApplicationContext(),DrawerTableController.getInstance().setDataBaseHelper(getApplicationContext()).searchByCompleteEvent()+"",Toast.LENGTH_SHORT).show();
        //반복일정 count.
        //searchByRepeatEvent
        TextView repeatScheduleNo = (TextView)findViewById(R.id.repeatScheduleNo);
        repeatScheduleNo.setText(DrawerTableController.getInstance().searchByRepeatEvent()+"");
        //selectByFriendName
        friend_edit = (TextView)findViewById(R.id.friend_edit);
        friend_edit.setText(DrawerTableController.getInstance().searchByFriendName()+"");
        //드로워의 시간 초기화
        ((TextView)findViewById(R.id.currentTimer)).setText(new SimpleDateFormat("MM월dd일 (E) a HH시 mm분", Locale.KOREA).format(new Date()).toString()); /*TextClock currentTimer = (TextClock) findViewById(R.id.currentTimer); currentTimer.setFormat12Hour("MM월dd일 (E) a HH시 mm분");*///이게 원래코드.
        //getTotalPoint
        ((TextView)findViewById(R.id.friendlyNo)).setText(FriendDataManager.get(getApplicationContext()).getTotalPoint()+"");

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
                startActivity(intent);
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
    }
}
