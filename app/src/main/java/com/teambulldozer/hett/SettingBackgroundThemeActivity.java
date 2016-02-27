package com.teambulldozer.hett;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by GHKwon on 2016-02-17.
 */
public class SettingBackgroundThemeActivity extends AppCompatActivity {
    private ListView settingBackgroundThemeListView;
    private TextView settingBackgroundThemeOkBtn;
    private BackgroundThemeAdapter backgroundThemeAdapter;
    private Typeface NanumSquare_B;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settting_background_theme);


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
        setFont();

    }
    public void setFont() {
        NanumSquare_B = Typeface.createFromAsset(getAssets(), "NanumSquare_Bold.ttf");


        ((TextView)findViewById(R.id.settingBackgroundTextView)).setTypeface(NanumSquare_B);
        settingBackgroundThemeOkBtn.setTypeface(NanumSquare_B);

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
