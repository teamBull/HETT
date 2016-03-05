package com.teambulldozer.hett;

import android.app.LoaderManager;
import android.content.AsyncQueryHandler;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.ContactsContract;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class CompleteActivity extends AppCompatActivity{
    private static boolean devMode = false;
    /* 디버그를 위한 개발자 모드 설정. true로 설정되어 있을 시, 오류 발생시 강제로 앱을 종료한다. */

    private static final String TAG = "CompleteActivity";

    CompleteEventTableController completeEventCtr;
    CompleteSimpleCursorAdapter cursorAdapter;
    Cursor cursor;

    //activity_complete.xml
    SoftKeyboardLsnedRelativeLayout softRelativeLayout;
    RelativeLayout completeToolbarLayout;
    ImageView prevBtn;
    TextView editMenuBtn;
    TextView finishMenuBtn;
    ListView listView;
    TextView deleteAllBtn;//편집 버튼을 눌렀을 때 보이는 위젯

    //list_item_complete.xml
    TextView memoContent;
    ImageView deleteBtn;//편집 버튼을 눌렀을 때 보이는 위젯

    // 폰트
    Typeface NanumSquare_B;
    Typeface NanumBarunGothic_R;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        if (CompleteActivity.devMode)
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

        setContentView(R.layout.activity_complete);
        /**
         * 기호
         * 배경화면 setting부분.
         */
        BackgroundThemeManager.getInstance().setBackground(getApplicationContext(), (SoftKeyboardLsnedRelativeLayout) findViewById(R.id.complete_layout));
        /* Call the database constructor */
        completeEventCtr = CompleteEventTableController.get(this);

        /* Connecting XML widgets and JAVA code. */
        this.softRelativeLayout = (SoftKeyboardLsnedRelativeLayout)findViewById(R.id.complete_layout);
        this.completeToolbarLayout = (RelativeLayout)findViewById(R.id.complete_toolbar_layout);
        this.prevBtn = (ImageView)findViewById(R.id.prev_btn);
        this.editMenuBtn = (TextView)findViewById(R.id.edit_menu);
        this.finishMenuBtn = (TextView)findViewById(R.id.finish_menu);
        this.listView = (ListView)findViewById(R.id.complete_listview);
        this.memoContent = (TextView)findViewById(R.id.memo_content);

        this.deleteBtn = (ImageView)findViewById(R.id.delete_btn);
        this.deleteAllBtn =(TextView)findViewById(R.id.delete_all_btn);

        /*font*/
        NanumSquare_B = Typeface.createFromAsset(getAssets(), "NanumSquare_Bold.ttf");
        NanumBarunGothic_R = Typeface.createFromAsset(getAssets(), "NanumBarunGothic_Regular.ttf");


        /* These are the complete_activity functions of the complete_event page. */
        setFont(); // 폰트 설정

        ifEditMenuClicked();
        ifFinishMenuClicked();
        ifPrevBtnClicked();
        ifClickedDeleteAllRows();   // 모두 지우기 버튼이 눌렀을 때, 일정 모두 지우기.

        populateListView();         // listview populate

        // The following line makes software keyboard disappear until it is clicked again.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void ifEditMenuClicked() {
        this.editMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("completeActivity", "edit menu click");
                editMenuBtn.setVisibility(View.INVISIBLE);
                finishMenuBtn.setVisibility(View.VISIBLE);

                CompleteSimpleCursorAdapter.isOnEditMenu = false;
                populateListView();

                deleteAllBtn.setVisibility(View.VISIBLE);
            }
        });
    }

    private void ifFinishMenuClicked() {
        this.finishMenuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("completeActivity", "finish menu click");
                editMenuBtn.setVisibility(View.VISIBLE);
                finishMenuBtn.setVisibility(View.INVISIBLE);

                CompleteSimpleCursorAdapter.isOnEditMenu = true;
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
                CompleteSimpleCursorAdapter.isOnEditMenu = true;
                finish();
            }
        });
    }
    private void ifClickedDeleteAllRows() {
        deleteAllBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                completeEventCtr.deleteAllData();
                populateListView();
            }
        });
    }

    private void populateListView() {
        this.cursor = completeEventCtr.getEventTableCompleteData();
        this.cursor.moveToFirst();
        String[] fromFieldNames = new String[] {CompleteEventTableController.Columns.MEMO};
        int[] toViewIDS = new int[] { R.id.memo_content};
        cursorAdapter = new CompleteSimpleCursorAdapter(this,R.layout.list_item_complete,this.cursor,fromFieldNames,toViewIDS,0,completeEventCtr);
        this.listView.setAdapter(cursorAdapter);

    }
    public void setFont(){
        this.editMenuBtn.setTypeface(NanumSquare_B);
        this.finishMenuBtn.setTypeface(NanumSquare_B);
    }
    public boolean deleteRow(String rowId){
        Integer deletedRows = completeEventCtr.deleteData(rowId);
        completeEventCtr.rearrangeData(rowId);
        requery();
        return deletedRows != 0;
    }
    public void requery(){
        Cursor cursor = completeEventCtr.getEventTableCompleteData();
        cursorAdapter.changeCursor(cursor);
    }
}
