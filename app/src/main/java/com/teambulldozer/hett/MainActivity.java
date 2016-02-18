package com.teambulldozer.hett;


import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;

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
* (2016.2.19. 2:00 )
* 1. 일정 1개, 5개 입력시 알맞은 TOAST 뜨게 추가.
* 2. 레이아웃 올리고 내리는 게 가끔씩 작동하지 않아서 그 부분 수정.
* 3. DB 칼럼명 추가, 그리고 약간의 수정..
* */

public class MainActivity extends AppCompatActivity {

    private static boolean devMode = false;
    /* 디버그를 위한 개발자 모드 설정. true로 설정되어 있을 시, 오류 발생시 강제로 앱을 종료한다. */

    private static final String TAG = "HETT";

    DatabaseHelper myDb;
    MySimpleCursorAdapter mySimpleCursorAdapter;
    ListView lv1;
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

        setContentView(R.layout.activity_main);

        /* Call the database constructor */
        myDb = new DatabaseHelper(this); //

        /* Connecting XML widgets and JAVA code. */
        dateBar = (TextView) findViewById(R.id.dateBar);
        todayBar = (TextView) findViewById(R.id.todayBar);
        editMenu = (TextView) findViewById(R.id.editMenu);
        lv1 = (ListView) findViewById(R.id.lv1);
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
                            // 그 외에 클릭하면 삭제하는 기능은 MySimpleCursorAdapter에 구현.

        /* Additional details */

        // The following line makes software keyboard disappear until it is clicked again.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }

    public void toastProperMessage(String hatt_id, int numOfTODOs){
        // 완료된 일정말고, 일반 일정만 5개 이상 될 때, toast를 뜨게 하기.
        // 완료된 일정의 개수를 센 다음, 완료된 게 하나라도 있으면 다음의 메시지를 띄우지 않는다.
        if(myDb.isThereCompletedData())
            return;

        HattToast toast = new HattToast(this);

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

                MySimpleCursorAdapter.isOnEditMenu = false;
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

                MySimpleCursorAdapter.isOnEditMenu = true;
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
                MySimpleCursorAdapter.isUserOnTyping = true;
                rl1.setVisibility(View.INVISIBLE);

                ViewGroup.MarginLayoutParams LL = (ViewGroup.MarginLayoutParams) dateLayout.getLayoutParams();
                LL.topMargin = pixelToDP(40);

                ViewGroup.MarginLayoutParams LV = (ViewGroup.MarginLayoutParams) lv1.getLayoutParams();
                LV.topMargin = pixelToDP(122); //96

            }

            @Override
            public void onSoftKeyboardHide() {
                Log.d("SoftKeyboard", "Soft keyboard hidden");
                MySimpleCursorAdapter.isUserOnTyping = false;

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
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Cursor cursor = (Cursor) parent.getAdapter().getItem(position);
                cursor.moveToPosition(position);
                int rowId = (cursor.getPosition() + 1); // sqlite와 sync를 맞춰줘야함.
                //Toast.makeText(getBaseContext(), "Item Clicked: " + rowId, Toast.LENGTH_SHORT).show();

                if (myDb.isCompleted(rowId)) {
                    shiftAndInsert(rowId);
                    // 이 라인에 추가되어야 할 코드는 디비에 있는 내용을 한 칸씩 다음뷰로 shift하는 것임.
                } else {
                    deleteAndInsert(rowId);
                }

                requery();

            }
        });
    }

    public void ifClickedDeleteAllRows(){
        deleteAllButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myDb.deleteAllData();
                myDb.rearrangeData(Integer.toString((int) myDb.numOfEntries()));
                // 삭제와 관련된 부분은 adapter를 새로 설정해줘야함;
                requery();
            }
        });

    }

    public boolean deleteRow(String rowId){

        Integer deletedRows = myDb.deleteData(rowId);
        myDb.rearrangeData(rowId);
        requery();
        return deletedRows != 0;
    }


    public void shiftAndInsert(int rowId){
        String tempMemo = myDb.getMemoAt(rowId);

        if(rowId == 1){
            myDb.moveDataToTop(tempMemo); // Just update the value at position 1.
        } else {
            myDb.shiftData(rowId-1); // The reason for rowId-1 is for shifting.
            myDb.moveDataToTop(tempMemo); // 순서 굉장히 중요!
        }
    }

    public void deleteAndInsert(int rowId){

        String tempMemo = myDb.getMemoAt(rowId);
        deleteRow(Integer.toString(rowId));
        myDb.insertData(tempMemo, true);

    }

    public void addOnUserInput() {
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                String memo = memoInput.getText().toString();
                if (memo.isEmpty()) {
                    return;
                    /* If a user does not type any word, then, memo is not to be added to the list. */
                }

                if (myDb.numOfEntries() == 0) { // 이 조건이 있는 이유는, DB가 비어있을 때, isThereCompleteData()를 호출하면 에러가 뜨기 때문.
                    myDb.insertData(memo, false);
                } else {
                    if (myDb.isThereCompletedData()) {
                        myDb.insertData("", false);
                        myDb.shiftData((int) myDb.numOfEntries()); // 여기 왜 1이 들어가있지.. 마지막 숫자가 들어가 있어야되는데 ;
                        myDb.moveDataToTop(memo);
                    } else
                        myDb.insertData(memo, false);
                }

                    /*
                    * These lines makes a soft keyboard disappear when user finishes typing.
                    * */
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                memoInput.setText("");

                requery();
                toastProperMessage("hatti", (int) myDb.numOfEntries()); // hatti는 임시 ID, 나중에 유저가 set한 걸 받아와야 함;
            }
        });
    }

    private void populateListView(){
        Cursor cursor = myDb.getAllData();
        String[] fromFieldNames = new String[] { myDb.COL_2 };
        int[] toViewIDS = new int[] { R.id.memoContent};
        mySimpleCursorAdapter =
                new MySimpleCursorAdapter(this, R.layout.list_item_memo, cursor, fromFieldNames, toViewIDS, 0, myDb);
        lv1.setAdapter(mySimpleCursorAdapter);

        /* 어댑터를 세팅해주는 것은 한 번만 있으면 될 것 같다.
        즉 populateListView() 메소드는 onCreate에서 한 번만 호출되면 됨.

         */
        /*
         getBaseContext --> this : Since there was a cast problem when using getBaseContext, it was
         replaced with this.
         */
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
        Cursor cursor = myDb.getAllData();
        mySimpleCursorAdapter.changeCursor(cursor);
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
        ((SimpleCursorAdapter)lv1.getAdapter()).getCursor().close(); // ?
        myDb.close();
        Log.d(TAG, "onDestroy() called");
    }

    private void requery(){
        Cursor values = myDb.getAllData();
        mySimpleCursorAdapter.changeCursor(values);
    }

}
