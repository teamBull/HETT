package com.teambulldozer.hett;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

/**
 * Created by yoon on 16. 3. 10..
 */
public class WidgetActivity extends Activity implements View.OnClickListener {

    String TAG = "위젯 액티비티 로그입니당.";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "위젯 activity가 불러졌습니당.");

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
    }

    @Override
    public void onClick(View v) {
        final Context context = WidgetActivity.this;
        AppWidgetManager widgetMgr = AppWidgetManager.getInstance(context);
        switch(v.getId()) {
            case R.id.widgetTodo1StarOn:
                Log.i(TAG, "첫번 째 별이 눌러졌네용.");
                //widget Provider에서 사용자가 정의한 함수를 호출하여 값을 넘겨준다.
                // Make sure we pass back the original appWidgetId
                Intent resultValue = new Intent();
                resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
                setResult(RESULT_OK, resultValue);
                finish();

                break;
            case R.id.widgetTodo1StarOff:
                break;
            case R.id.widgetTodo1Select:
                break;
            case R.id.widgetTodo1NoSelect:
                break;
            case R.id.widgetTodo2StarOn:
                break;
            case R.id.widgetTodo2StarOff:
                break;
            case R.id.widgetTodo2Select:
                break;
            case R.id.widgetTodo2NoSelect:
                break;
            case R.id.widgetTodo3StarOn:
                break;
            case R.id.widgetTodo3StarOff:
                break;
            case R.id.widgetTodo3Select:
                break;
            case R.id.widgetTodo3NoSelect:
                break;
            case R.id.widgetTodo4StarOn:
                break;
            case R.id.widgetTodo4StarOff:
                break;
            case R.id.widgetTodo4Select:
                break;
            case R.id.widgetTodo4NoSelect:
                break;
        }

    }
}
