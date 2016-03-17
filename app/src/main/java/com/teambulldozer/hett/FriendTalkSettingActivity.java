package com.teambulldozer.hett;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

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
    private ArrayList<String> mChildListContent4 = null;
    private ArrayList<String> mChildListContent5 = null;
    private ArrayList<String> mChildListContent6 = null;

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
        /*기호*/
        //가장 위의 안드로이드 상태바를 없애주는 코드이다.
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        /*끝.*/
        BackgroundThemeManager.getInstance().setBackground(getApplicationContext(), (RelativeLayout) findViewById(R.id.friendTalkStyleActivity));
        NanumSquare_B = Typeface.createFromAsset(getAssets(), "NanumSquare_Bold.ttf");
        NanumBarunGothic_R = Typeface.createFromAsset(getAssets(), "NanumBarunGothic_Regular.ttf");

        tvFtsTalkStyle = (TextView)findViewById(R.id.tvFtsTalkStyle);
        tvBtnTalkStOk = (TextView)findViewById(R.id.tvBtnTalkStOk);
        tvTalkStyle = (TextView)findViewById(R.id.tvTalkStyle);

        friendDataManager = FriendDataManager.get(this);

        mListView = (ExpandableListView) findViewById(R.id.exLvTalkStyle);
        talkSt = "기본 테마";

        mGroupList = new ArrayList<String>();
        mChildList = new ArrayList<ArrayList<String>>();
        mChildListContent1 = new ArrayList<String>();
        mChildListContent2 = new ArrayList<String>();
        mChildListContent3 = new ArrayList<String>();
        mChildListContent4 = new ArrayList<String>();
        mChildListContent5 = new ArrayList<String>();
        mChildListContent6 = new ArrayList<String>();

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

        //말투 - 외국인
        mGroupList.add("한쿸어 어려훠효");

        mChildListContent3.add("아뇽하세효");
        mChildListContent3.add("이룬 자라고이찌요?");
        mChildListContent3.add("같치 파이팅!!");

        mChildList.add(mChildListContent3);

        //말투 - 극존칭
        mGroupList.add("극존칭");

        mChildListContent3.add("안녕하십니까");
        mChildListContent3.add("죄송하지만 혹시 일정을 잃어버리시진 않았을까 걱정됩니다.");
        mChildListContent3.add("힘내시길 바랍니다!");

        mChildList.add(mChildListContent3);

        //말투 - 새오체
        mGroupList.add("새오체");

        mChildListContent4.add("안녕하새오?");
        mChildListContent4.add("화이팅 하새오!");
        mChildListContent4.add("오늘도 채선을 다해서 힘내새오!");

        mChildList.add(mChildListContent4);

        //말투 - 연하남
        mGroupList.add("연하남");

        mChildListContent5.add("누나~ 뭐해요?");
        mChildListContent5.add("누나 지금 할 일 얼른하고 나랑 놀아요~");
        mChildListContent5.add("누나 힘내요 누나한테 내가 있잖아요!");

        mChildList.add(mChildListContent5);

        //말투 - 신하
        mGroupList.add("신하");

        mChildListContent6.add("옥체강령하시옵니까?");
        mChildListContent6.add("만세!만세!만만세! 천세!천세!천천세!");
        mChildListContent6.add("더 이상 일정으로 인해 고통받지 마시옵소서..");

        mChildList.add(mChildListContent6);

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

                //intent = new Intent(FriendTalkSettingActivity.this, FriendSettingActivity.class);
                finish();
                //startActivity(intent);

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
//                Intent intent = getIntent();
//                intent.putExtra("data_talk",talkSt);
//                setResult(2,intent);
                finish();
                break;
        }
    }

    public void setFont(){
        tvFtsTalkStyle.setTypeface(NanumSquare_B);
        tvBtnTalkStOk.setTypeface(NanumSquare_B);
    }




}