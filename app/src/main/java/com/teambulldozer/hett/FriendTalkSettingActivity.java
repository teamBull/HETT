package com.teambulldozer.hett;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by YUNSEON on 2016-02-16.
 */
public class FriendTalkSettingActivity extends AppCompatActivity {

    Button btnPrevTalkSt;
    TalkStAdapter adapter;
    String talkSt;
    Intent intent;

    TextView tvFtsTalkStyle;
    TextView tvBtnTalkStOk;
    TextView tvTalkStyle;

    private ArrayList<String> mGroupList = null;
    private ArrayList<ArrayList<String>> mChildList = null;
    private ArrayList<String> mChildListContent1 = null;
    private ArrayList<String> mChildListContent2 = null;
    private ArrayList<String> mChildListContent3 = null;
    private ExpandableListView mListView;
    FriendDataManager friendDataManager;
    FriendDto friendItem;

    // 폰트
    Typeface NanumSquare_B;
    Typeface NanumBarunGothic_R;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_talk_style);

        NanumSquare_B = Typeface.createFromAsset(getAssets(), "NanumSquare_Bold.ttf");
        NanumBarunGothic_R = Typeface.createFromAsset(getAssets(), "NanumBarunGothic_Regular.ttf");

        tvFtsTalkStyle = (TextView)findViewById(R.id.tvFtsTalkStyle);
        tvBtnTalkStOk = (TextView)findViewById(R.id.tvBtnTalkStOk);
        tvTalkStyle = (TextView)findViewById(R.id.tvTalkStyle);

        friendDataManager = new FriendDataManager(this);

        mListView = (ExpandableListView) findViewById(R.id.exLvTalkStyle);
        talkSt = "기본 테마";

        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<String>>();
        mChildListContent1 = new ArrayList<String>();
        mChildListContent2 = new ArrayList<String>();
        mChildListContent3 = new ArrayList<String>();

        //말투 - 기본 테마
        mGroupList.add("기본 테마");

        mChildListContent1.add("안녕!");
        mChildListContent1.add("오랜만이네?");
        mChildListContent1.add("잘 지냈어?");

        mChildList.add(mChildListContent1);

        //말투 - 연서복
        mGroupList.add("연서복");

        mChildListContent2.add("울 애긔~ㅎ안녕?ㅎ");
        mChildListContent2.add("어빠가 울 애긔 일 좀 도와줄까~ㅎ");
        mChildListContent2.add("넝담~ㅎ");

        mChildList.add(mChildListContent2);

        //말투 - 극존칭
        mGroupList.add("극존칭");

        mChildListContent3.add("1");
        mChildListContent3.add("1");
        mChildListContent3.add("1");

        mChildList.add(mChildListContent3);

        adapter = new TalkStAdapter(this, mGroupList, mChildList);
        mListView.setAdapter(adapter);

        //자동으로 닫히게..
        mListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                int groupCount = adapter.getGroupCount();

                for (int i = 0; i < groupCount; i++) {
                    if (!(i == groupPosition))
                        mListView.collapseGroup(i);
                }
            }
        });

        // 그룹 클릭 했을 경우 이벤트
        mListView.setOnGroupClickListener(new ExpandableListView.OnGroupClickListener() {
            @Override
            public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {

                talkSt = adapter.getGroup(groupPosition).toString();

                return false;
            }
        });

        // 그룹이 닫힐 경우 이벤트
        mListView.setOnGroupCollapseListener(new ExpandableListView.OnGroupCollapseListener() {
            @Override
            public void onGroupCollapse(int groupPosition) {

            }
        });

        btnPrevTalkSt = (Button)findViewById(R.id.btnPrevTalkSt);
        btnPrevTalkSt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                intent = new Intent(FriendTalkSettingActivity.this, FriendSettingActivity.class);
                finish();
                startActivity(intent);

            }
        });

        setFont();

    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.tvBtnTalkStOk:
                friendItem = (FriendDto)getIntent().getSerializableExtra("friendItem");
                friendDataManager.updateFriendTalkSt(talkSt, friendItem.getFriendName());
                intent = new Intent(FriendTalkSettingActivity.this, FriendSettingActivity.class);
                finish();
                startActivity(intent);
                break;
        }
    }

    public void setFont(){
        tvFtsTalkStyle.setTypeface(NanumSquare_B);
        tvBtnTalkStOk.setTypeface(NanumSquare_B);
    }




}