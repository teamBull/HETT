package com.teambulldozer.hett;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by YUNSEON on 2016-02-16.
 */
public class FriendNameSettingActivity extends AppCompatActivity {

    Button btnPrevName;
    EditText etFriendName;

    FriendDataManager friendDataManager;

    FriendDto friendItem;

    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_friend_name);
        btnPrevName = (Button)findViewById(R.id.btnPrevName);
        btnPrevName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                intent = new Intent(FriendNameSettingActivity.this, FriendSettingActivity.class);
                finish();
                startActivity(intent);
            }
        });


        friendDataManager = new FriendDataManager(this);



        etFriendName = (EditText)findViewById(R.id.etFreindName);


    }

    public void onClick(View v){
        switch(v.getId()){
            case R.id.tvBtnNameOk:
                friendItem = (FriendDto)getIntent().getSerializableExtra("friendItem");
                friendDataManager.updateFriendName(etFriendName.getText().toString(), friendItem.getFriendTalkSt());
                intent = new Intent(FriendNameSettingActivity.this, FriendSettingActivity.class);
                finish();
                startActivity(intent);
                break;
        }
    }
}