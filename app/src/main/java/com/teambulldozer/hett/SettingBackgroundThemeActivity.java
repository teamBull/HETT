package com.teambulldozer.hett;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by GHKwon on 2016-02-17.
 */
public class SettingBackgroundThemeActivity extends AppCompatActivity {
    private ListView settingBackgroundThemeListView;
    private TextView okBtn;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settting_background_theme);
        initBackgroundTheme();
        okBtn = (TextView)findViewById(R.id.okBtn);
        okBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"저장되었습니다",Toast.LENGTH_SHORT).show();
            }
        });

    }
    private ArrayList<String> arrayList;
    private BackgroundThemeAdapter backgroundThemeAdapter;
    public void initBackgroundTheme() {
        arrayList = new ArrayList<String>();
        String []tempString = new String[]{"기본 테마","바다","나무나무","스트라이프","빗방울","눈송이"};
        for(int i=0 ; i < tempString.length ; i++)
            arrayList.add(tempString[i]);
        backgroundThemeAdapter = new BackgroundThemeAdapter(getApplicationContext() , arrayList ) ;
        settingBackgroundThemeListView = (ListView) findViewById( R.id.settingBackgroundThemeListView);
        settingBackgroundThemeListView.setAdapter(backgroundThemeAdapter);
    }
}
