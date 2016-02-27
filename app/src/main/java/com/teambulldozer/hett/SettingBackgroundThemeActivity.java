package com.teambulldozer.hett;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by GHKwon on 2016-02-17.
 */
public class SettingBackgroundThemeActivity extends AppCompatActivity {
    private ListView settingBackgroundThemeListView;
    private TextView settingBackgroundThemeOkBtn;
    private BackgroundThemeAdapter backgroundThemeAdapter;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settting_background_theme);
        /*new Thread() { // 너무 느려서 Thread를 일단 사용했습니다. 나중에 DB접근하는 코드가 나와도 됩니다.
            public void run(){
                initBackgroundTheme();
            }
        }.start();*/
        initBackgroundTheme();
        settingBackgroundThemeOkBtn = (TextView)findViewById(R.id.settingBackgroundThemeOkBtn);
        settingBackgroundThemeOkBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DrawerTableController.getInstance().updateSelectedBackgroundTheme(BackgroundThemeAdapter.isSelected);
                Bundle bundle = new Bundle();
                bundle.putString("background_theme_name", DrawerTableController.getInstance().searchSelectedBackgroundTheme());
                setResult(RESULT_OK, new Intent().putExtras(bundle));//이 코드를 줄인 것 ->Intent intent = new Intent();intent.putExtras(bundle);
                finish();

            }
        });

    }

    public void initBackgroundTheme() {
        ArrayList<BackgroundThemeDTO> arrayList = DrawerTableController.getInstance().searchBackbroundThemeDTOAllData();
        /*ArrayList<String> arrayList = new ArrayList<String>();
        String []tempString = new String[]{"기본 테마","바다","나무나무","스트라이프","빗방울","눈송이"};
        for(int i = 0 ; i < 6 ; i++) {

            arrayList.add(tempString[i]);
        }*/
        backgroundThemeAdapter = new BackgroundThemeAdapter(this.getApplicationContext() , arrayList ) ;
        settingBackgroundThemeListView = (ListView) findViewById( R.id.settingBackgroundThemeListView);
        settingBackgroundThemeListView.setAdapter(backgroundThemeAdapter);
    }


}
