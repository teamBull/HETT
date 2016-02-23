package com.teambulldozer.hett;

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
public class EditFriendNameActivity extends AppCompatActivity {
    private TextView okayBtn;
    private EditText friendNameEditText;
    private ImageView editFriendNamePrevPage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_friend_name);
        initView();
        registerListener();
    }
    private void initView() {
        okayBtn = (TextView)findViewById(R.id.okayBtn);
        friendNameEditText = (EditText) findViewById(R.id.friendNameEditText);
        editFriendNamePrevPage = (ImageView) findViewById ( R.id.editFriendNamePrevPage );

    }
    private void registerListener() {
        okayBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(friendNameEditText.getText()!=null)
                    Toast.makeText(getApplicationContext(),"가상친구 이름이 "+friendNameEditText.getText()+" 으로 저장되었습니다.", Toast.LENGTH_SHORT).show();
                finish();
            }
        });
        editFriendNamePrevPage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
