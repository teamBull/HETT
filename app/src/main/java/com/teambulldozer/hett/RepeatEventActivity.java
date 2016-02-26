package com.teambulldozer.hett;

import android.database.Cursor;
import android.graphics.Typeface;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class RepeatEventActivity extends AppCompatActivity {
    private static boolean devMode = false;
    /* 디버그를 위한 개발자 모드 설정. true로 설정되어 있을 시, 오류 발생시 강제로 앱을 종료한다. */

    private static final String TAG = "RepeatEventActivity";

    RepeatEventTableController repeatEventTableController;
    RepeatSimpleCursorAdapter repeatSimpleCursorAdapter;
    Cursor cursor;

    //activity_repeat.xml
    SoftKeyboardLsnedRelativeLayout softRelativeLayout;
    RelativeLayout repeatToolbarLayout;
    ImageView prevBtn;
    TextView deleteMenuBtn;
    TextView finishMenuBtn;
    ListView listView;
    TextView deleteAllBtn;//편집 버튼을 눌렀을 때 보이는 위젯

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
        setContentView(R.layout.activity_repeat);

        /* Call the database constructor */
        this.repeatEventTableController = RepeatEventTableController.get(this);

        /* Connecting XML widgets and JAVA code. */
        this.softRelativeLayout = (SoftKeyboardLsnedRelativeLayout)findViewById(R.id.repeat_layout);
        this.repeatToolbarLayout = (RelativeLayout)findViewById(R.id.repeat_toolbar_layout);
        this.prevBtn = (ImageView)findViewById(R.id.repeat_prev_btn);
        this.deleteMenuBtn = (TextView)findViewById(R.id.repeat_delete_menu);
        this.finishMenuBtn = (TextView)findViewById(R.id.repeat_finish_menu);
        this.listView = (ListView)findViewById(R.id.repeat_listview);
        this.deleteAllBtn =(TextView)findViewById(R.id.repeat_delete_all_btn);

        /*font*/
        NanumSquare_B = Typeface.createFromAsset(getAssets(), "NanumSquare_Bold.ttf");
        NanumBarunGothic_R = Typeface.createFromAsset(getAssets(), "NanumBarunGothic_Regular.ttf");

        /* These are the complete_activity functions of the complete_event page. */
        setFont(); // 폰트 설정

        ifDeleteMenuClicked();
        ifFinishMenuClicked();
        ifPrevBtnClicked();
        ifClickedDeleteAllRows();   // 모두 지우기 버튼이 눌렀을 때, 일정 모두 지우기.

        populateListView();         // listview populate

        // The following line makes software keyboard disappear until it is clicked again.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

    }
    private void ifDeleteMenuClicked() {
        this.deleteMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("RepeatEventActivity", "delete menu click");
                deleteMenuBtn.setVisibility(View.INVISIBLE);
                finishMenuBtn.setVisibility(View.VISIBLE);

                RepeatSimpleCursorAdapter.isOnEditMenu = false;
                populateListView();

                deleteAllBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void ifFinishMenuClicked() {
        this.finishMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("RepeatEventActivity", "finish menu click");
                deleteMenuBtn.setVisibility(View.VISIBLE);
                finishMenuBtn.setVisibility(View.INVISIBLE);

                RepeatSimpleCursorAdapter.isOnEditMenu = true;
                populateListView();

                deleteAllBtn.setVisibility(View.INVISIBLE);
            }
        });
    }
    private void ifPrevBtnClicked(){
        this.prevBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(getApplicationContext(),"Asdf",Toast.LENGTH_SHORT).show();
                RepeatSimpleCursorAdapter.isOnEditMenu = true;
                finish();
            }
        });
    }
    private void ifClickedDeleteAllRows() {
        deleteAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                repeatEventTableController.deleteAllData();
                populateListView();
            }
        });
    }

    private void populateListView() {
        this.cursor = repeatEventTableController.getEventRepeatData();
        this.cursor.moveToFirst();
        String[] fromFieldNames = new String[] {CompleteEventTableController.Columns.MEMO};
        int[] toViewIDS = new int[] { R.id.memo_content};
        repeatSimpleCursorAdapter = new RepeatSimpleCursorAdapter(this,R.layout.list_item_repeat,this.cursor,fromFieldNames,toViewIDS,0,repeatEventTableController);
        this.listView.setAdapter(repeatSimpleCursorAdapter);

    }
    public void setFont(){
        this.deleteMenuBtn.setTypeface(NanumSquare_B);
        this.finishMenuBtn.setTypeface(NanumSquare_B);
    }
    public boolean deleteRow(String rowId){
        Integer deletedRows = repeatEventTableController.deleteData(rowId);
        requery();
        return deletedRows != 0;
    }
    public void requery(){
        Cursor cursor = repeatEventTableController.getEventRepeatData();
        repeatSimpleCursorAdapter.changeCursor(cursor);
    }
}
