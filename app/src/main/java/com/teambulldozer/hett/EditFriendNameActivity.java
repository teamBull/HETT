package com.teambulldozer.hett;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by GHKwon on 2016-02-23.
 */

/**
 * 해당 클래스는 사용자가 가상친구의 이름을 지정할 때 호출되는 "친구 이름 변경 액티비티 이다."
 */
public class EditFriendNameActivity extends AppCompatActivity {
    /**
     * 화면상의 우측 상단의 okay버튼.
     */
    private TextView okayBtn;
    /**
     * 사용자가 가상친구의 이름을 입력할 EditText.
     */
    private EditText friendNameEditText;
    /**
     * 이전 화면으로 넘어가는 액티비티.
     */
    private ImageView editFriendNamePrevPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friend_name);
        //초기화 메소드.
        initView();
        //초기화 된 뷰들의 이벤트를 등록하는 메소드.
        registerListener();
    }

    /**
     * 해당 메소드는 해당 액티비티(activity_edit_friend_name) 에서 사용하는 View들의 객체를 xml로 접근해서 초기화 해주는 역할을 한다.
     */
    private void initView() {
        okayBtn = (TextView)findViewById(R.id.okayBtn);
        friendNameEditText = (EditText) findViewById(R.id.friendNameEditText);
        editFriendNamePrevPage = (ImageView) findViewById ( R.id.editFriendNamePrevPage );

    }

    /**
     * 해당 메소드는 해당 액티비티들의 뷰의 리스너를 등록하는 역할을 한다.
     */
    private void registerListener() {
        //해당 이벤트 리스너느 우측 상단의 확인 버튼을 클릭했을 경우 발생하는 이벤트이다.
        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*
                이 코드에 대해서는 많이 생각해 봤다.
                그 이유는, 사용자가 입력한 값을 friendNameEditText.getText().toString().trim().equals("")으로 검사를 해봐도
                아무리 해도 아무것도 입력하지 않는 것도 모두 통과가 되기 때문이다.
                그래서 문자열 길이로 체크를 해주게 되었다.
                 */
                if(friendNameEditText.getText().toString().trim().length()!=0 ) {//|| friendNameEditText.getText().equals(getIntent().getStringExtra("friend_edit"))

                    try {
                        // DrawerTableController의 인스턴스를 받아와서 이름을 set해주는 메소드이다.
                        DrawerTableController.getInstance(getApplicationContext()).updateByFriendName(friendNameEditText.getText().toString());
                        Toast.makeText(getApplicationContext(), "가상친구 이름이 " + friendNameEditText.getText() + " 으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                        Bundle bundle = new Bundle();
                        bundle.putString("new_friend_edit_name", friendNameEditText.getText().toString());
                        setResult(RESULT_OK, new Intent().putExtras(bundle));//이 코드를 줄인 것 ->Intent intent = new Intent();intent.putExtras(bundle);
                        finish();
                    } catch(Exception ex) {
                        new HattToast(getApplicationContext()).showToast("이름 변경 중 에러가 발생했습니다. 해당 에러를 리뷰를 통하여 알려주세요.",Toast.LENGTH_SHORT);
                    }

                }
                else
                    new HattToast(getApplicationContext()).showToast("내 이름을 없앨 작정이야!?ㅠㅠ 1글자 이상 입력해줘용~",Toast.LENGTH_SHORT);
            }
        });
        //그냥 이전 버튼을 클릭했을 때.
        editFriendNamePrevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // 이전 화면으로 이동.
            }
        });
    }
}
