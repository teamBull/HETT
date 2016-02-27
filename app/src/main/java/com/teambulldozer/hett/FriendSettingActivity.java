package com.teambulldozer.hett;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    private final int EDIT_FRIEND_NAME_ACTIVITY = 10;
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
                setResult(RESULT_OK,new Intent().putExtras(new Bundle()));
                finish();
            }
        });
        tvBtnOkFriend = (TextView)findViewById(R.id.tvBtnFreindOk);

        tvFriendName = (TextView)findViewById(R.id.tvFriendName);
        tvFriendTalkSt = (TextView)findViewById(R.id.tvFreindTalkSt);
        tvFsFriend = (TextView)findViewById(R.id.tvFsFriend);
        tvFsName = (TextView)findViewById(R.id.tvFsName);
        tvFsTalk = (TextView)findViewById(R.id.tvFsTalk);

        tvFriendName.setText(DrawerTableController.getInstance(getApplicationContext()).searchByFriendName());
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
            case R.id.tvFriendName:
            case R.id.layoutName:
                intent = new Intent(FriendSettingActivity.this, EditFriendNameActivity.class);
                startActivityForResult(intent, EDIT_FRIEND_NAME_ACTIVITY);
                break;

            case R.id.layoutTalkSt:
                intent = new Intent(FriendSettingActivity.this, FriendTalkSettingActivity.class);
                intent.putExtra("friendItem", friendData);
                startActivity(intent);
                break;

            case R.id.tvBtnFreindOk: {
                Bundle bundle = new Bundle();
                setResult(RESULT_OK,new Intent().putExtras(bundle));
                finish();
            }

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        // 액티비티가 정상적으로 종료되었을 경우
        if(requestCode == EDIT_FRIEND_NAME_ACTIVITY )
        {
            // FriendSettingActivity 호출한 경우에만 처리합니다.
            if(resultCode==RESULT_OK){
                // 받아온 이름을 액티비티에 표시합니다.
                Bundle extraBundle = data.getExtras();//번들로 반환됐으므로 번들을 불러오면 셋팅된 값이 있다.
                String new_friend_edit_name = extraBundle.getString("new_friend_edit_name");//인자로 구분된 값을 불러오는 행위를 하고
                Toast.makeText(this,new_friend_edit_name,Toast.LENGTH_SHORT).show();
                tvFriendName.setText(new_friend_edit_name);
            }
        }
    }

}