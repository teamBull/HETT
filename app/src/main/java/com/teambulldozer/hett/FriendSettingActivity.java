package com.teambulldozer.hett;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by YUNSEON on 2016-02-16.
 */
public class FriendSettingActivity extends AppCompatActivity {
    Button btnPrevFriennd;
    TextView tvBtnOkFriend;

    TextView tvFriendName;
    TextView tvFriendTalkSt;
    TextView tvFsFriend;
    TextView tvFsName;
    TextView tvFsTalk;

    DatabaseHelper helper;
    FriendDto friendData;
    FriendDataManager friendDataManager;

    Intent intent=null;

    // 폰트
    Typeface NanumSquare_B;
    Typeface NanumBarunGothic_R;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_setting);

        NanumSquare_B = Typeface.createFromAsset(getAssets(), "NanumSquare_Bold.ttf");
        NanumBarunGothic_R = Typeface.createFromAsset(getAssets(), "NanumBarunGothic_Regular.ttf");

        helper = DatabaseHelper.get(this);
        friendDataManager = FriendDataManager.get(this);

        friendData = friendDataManager.getFriend();

        btnPrevFriennd = (Button)findViewById(R.id.btnPrevFriend);
        btnPrevFriennd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvBtnOkFriend = (TextView)findViewById(R.id.tvBtnFreindOk);

        tvFriendName = (TextView)findViewById(R.id.tvFriendName);
        tvFriendTalkSt = (TextView)findViewById(R.id.tvFreindTalkSt);
        tvFsFriend = (TextView)findViewById(R.id.tvFsFriend);
        tvFsName = (TextView)findViewById(R.id.tvFsName);
        tvFsTalk = (TextView)findViewById(R.id.tvFsTalk);

        tvFriendName.setText(friendData.getFriendName().toString());
        tvFriendTalkSt.setText(friendData.getFriendTalkSt().toString());

        setFont();

    }

    public void setFont(){
        tvBtnOkFriend.setTypeface(NanumSquare_B);
        tvFsFriend.setTypeface(NanumSquare_B);
        tvFsName.setTypeface(NanumBarunGothic_R);
        tvFriendName.setTypeface(NanumBarunGothic_R);
        tvFsTalk.setTypeface(NanumBarunGothic_R);
        tvFriendTalkSt.setTypeface(NanumBarunGothic_R);

    }
    public void onClick(View v){
        switch(v.getId()){
            case R.id.layoutName:
                intent = new Intent(FriendSettingActivity.this, FriendNameSettingActivity.class);
                intent.putExtra("friendItem", friendData);
                finish();
                startActivity(intent);
                break;

            case R.id.layoutTalkSt:
                intent = new Intent(FriendSettingActivity.this, FriendTalkSettingActivity.class);
                intent.putExtra("friendItem", friendData);
                finish();
                startActivity(intent);
                break;

            case R.id.tvBtnFreindOk:
                finish();
        }

    }
}