package com.teambulldozer.hett;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

/**
 * Created by yoon on 16. 3. 10..
 */
public class TodoWidgetProvider extends AppWidgetProvider {

    final String TAG = "위젯 로그입니당.";

    private String TODO1_STAR_ON = "android.action.TODO1_STAR_ON";
    private String TODO1_STAR_OFF = "android.action.TODO1_STAR_OFF";
    private String TODO1_SELECT = "android.action.TODO1_SELECT";
    private String TODO1_NO_SELECT = "android.action.TODO1_NO_SELECT";

    private String TODO2_STAR_ON = "android.action.TODO2_STAR_ON";
    private String TODO2_STAR_OFF = "android.action.TODO2_STAR_OFF";
    private String TODO2_SELECT = "android.action.TODO2_SELECT";
    private String TODO2_NO_SELECT = "android.action.TODO2_NO_SELECT";

    private String TODO3_STAR_ON = "android.action.TODO3_STAR_ON";
    private String TODO3_STAR_OFF = "android.action.TODO3_STAR_OFF";
    private String TODO3_SELECT = "android.action.TODO3_SELECT";
    private String TODO3_NO_SELECT = "android.action.TODO3_NO_SELECT";

    private String TODO4_STAR_ON = "android.action.TODO4_STAR_ON";
    private String TODO4_STAR_OFF = "android.action.TODO4_STAR_OFF";
    private String TODO4_SELECT = "android.action.TODO4_SELECT";
    private String TODO4_NO_SELECT = "android.action.TODO4_NO_SELECT";

    private String SEE_MORE = "android.action.SEE_MORE";

    AppWidgetManager mAppWidgetManager;
    int[] mAppWidgetId;
    RemoteViews mainViews;

    /**
     * 브로드캐스트를 수신할때, Override된 콜백 메소드가 호출되기 직전에 호출됨
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "위젯이 호출되었습니당.");
        super.onReceive(context, intent);

        mainViews = new RemoteViews(context.getPackageName(),R.layout.todo_widget_main);
        String action = intent.getAction();
        buttonEventControll(context, intent, action);
    }

    /**
     * 위젯을 갱신할때 호출됨
     *
     * 주의 : Configure Activity를 정의했을때는 위젯 등록시 처음 한번은 호출이 되지 않습니다
     */
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        Log.i(TAG, "업데이트를 진행합니당.");
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        updateInfo(context, appWidgetManager, appWidgetIds);
    }

    /**
     * 위젯이 처음 생성될때 호출됨
     *
     * 동일한 위젯이 생성되도 최초 생성때만 호출됨
     */
    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
    }

    /**
     * 위젯의 마지막 인스턴스가 제거될때 호출됨
     *
     * onEnabled()에서 정의한 리소스 정리할때
     */
    @Override
    public void onDisabled(Context context) {
        Log.i(TAG, "위젯이 Disable 됩니당.");
        super.onDisabled(context);
    }

    /**
     * 위젯이 사용자에 의해 제거될때 호출됨
     */
    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
    }

    public void updateInfo(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        mAppWidgetManager = appWidgetManager;
        mAppWidgetId = appWidgetIds;

        EventTableController eventTableController = EventTableController.get(context);
        Cursor eventTableCursor = eventTableController.getAllData();
        String[] todo = new String[4];
        int[] importance = new int[4];
        int[] completeness = new int[4];

        int memoIndex = 0;
        eventTableCursor.move(-1);
        while(eventTableCursor.moveToNext()) {
            todo[memoIndex] = eventTableCursor.getString(eventTableCursor.getColumnIndex("MEMO"));
            importance[memoIndex] = eventTableCursor.getInt(eventTableCursor.getColumnIndex("IMPORTANCE"));
            completeness[memoIndex] = eventTableCursor.getInt(eventTableCursor.getColumnIndex("COMPLETENESS"));
            memoIndex++;
            if(memoIndex > 3) break;
        }

        int widgetId;

        for(int i = 0; i < appWidgetIds.length; i++){
            widgetId = appWidgetIds[i];
            Log.i(TAG, "위젯의 아이디는 이것 입니당. -> " + Integer.toString(widgetId));

            // 목록 배경 표시
            if(todo[0] == null) {
                mainViews.setViewVisibility(R.id.widgetTodo1Layout, View.INVISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo2Layout, View.INVISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo3Layout, View.INVISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo4Layout, View.INVISIBLE);
            } else if(todo[1] == null) {
                mainViews.setViewVisibility(R.id.widgetTodo1Layout, View.VISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo2Layout, View.INVISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo3Layout, View.INVISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo4Layout, View.INVISIBLE);
            } else if(todo[2] == null) {
                mainViews.setViewVisibility(R.id.widgetTodo1Layout, View.VISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo2Layout, View.VISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo3Layout, View.INVISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo4Layout, View.INVISIBLE);
            } else if(todo[3] == null) {
                mainViews.setViewVisibility(R.id.widgetTodo1Layout, View.VISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo2Layout, View.VISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo3Layout, View.VISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo4Layout, View.INVISIBLE);
            } else {
                mainViews.setViewVisibility(R.id.widgetTodo1Layout, View.VISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo2Layout, View.VISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo3Layout, View.VISIBLE);
                mainViews.setViewVisibility(R.id.widgetTodo4Layout, View.VISIBLE);
            }

            // 항목 표시
            if(todo[0] != null) {
                mainViews.setTextViewText(R.id.widgetTodo1, todo[0]);
                if(importance[0] == 1) {
                    mainViews.setViewVisibility(R.id.widgetTodo1StarOn, View.VISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo1StarOff, View.INVISIBLE);
                } else {
                    mainViews.setViewVisibility(R.id.widgetTodo1StarOn, View.INVISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo1StarOff, View.VISIBLE);
                }
                if(completeness[0] == 1) {
                    mainViews.setViewVisibility(R.id.widgetTodo1Select, View.VISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo1NoSelect, View.INVISIBLE);
                } else {
                    mainViews.setViewVisibility(R.id.widgetTodo1Select, View.INVISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo1NoSelect, View.VISIBLE);
                }
            }
            if(todo[1] != null) {
                mainViews.setTextViewText(R.id.widgetTodo2, todo[1]);
                if(importance[1] == 1) {
                    mainViews.setViewVisibility(R.id.widgetTodo2StarOn, View.VISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo2StarOff, View.INVISIBLE);
                } else {
                    mainViews.setViewVisibility(R.id.widgetTodo2StarOn, View.INVISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo2StarOff, View.VISIBLE);
                }
                if(completeness[1] == 1) {
                    mainViews.setViewVisibility(R.id.widgetTodo2Select, View.VISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo2NoSelect, View.INVISIBLE);
                } else {
                    mainViews.setViewVisibility(R.id.widgetTodo2Select, View.INVISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo2NoSelect, View.VISIBLE);
                }
            }
            if(todo[2] != null) {
                mainViews.setTextViewText(R.id.widgetTodo3, todo[2]);
                if(importance[2] == 1) {
                    mainViews.setViewVisibility(R.id.widgetTodo3StarOn, View.VISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo3StarOff, View.INVISIBLE);
                } else {
                    mainViews.setViewVisibility(R.id.widgetTodo3StarOn, View.INVISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo3StarOff, View.VISIBLE);
                }
                if(completeness[2] == 1) {
                    mainViews.setViewVisibility(R.id.widgetTodo3Select, View.VISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo3NoSelect, View.INVISIBLE);
                } else {
                    mainViews.setViewVisibility(R.id.widgetTodo3Select, View.INVISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo3NoSelect, View.VISIBLE);
                }
            }
            if(todo[3] != null) {
                mainViews.setTextViewText(R.id.widgetTodo4, todo[3]);
                if(importance[3] == 1) {
                    mainViews.setViewVisibility(R.id.widgetTodo4StarOn, View.VISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo4StarOff, View.INVISIBLE);
                } else {
                    mainViews.setViewVisibility(R.id.widgetTodo4StarOn, View.INVISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo4StarOff, View.VISIBLE);
                }
                if(completeness[3] == 1) {
                    mainViews.setViewVisibility(R.id.widgetTodo4Select, View.VISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo4NoSelect, View.INVISIBLE);
                } else {
                    mainViews.setViewVisibility(R.id.widgetTodo4Select, View.INVISIBLE);
                    mainViews.setViewVisibility(R.id.widgetTodo4NoSelect, View.VISIBLE);
                }
            }

            // 버튼 설정 및 넘기기
            PendingIntent todo1StarOnBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO1_STAR_ON), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent todo1StarOffBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO1_STAR_OFF), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent todo1SelectBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO1_SELECT), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent todo1rNoSelectBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO1_NO_SELECT), PendingIntent.FLAG_UPDATE_CURRENT);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo1StarOn, todo1StarOnBtn);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo1StarOff, todo1StarOffBtn);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo1Select, todo1SelectBtn);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo1NoSelect, todo1rNoSelectBtn);

            PendingIntent todo2StarOnBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO2_STAR_ON), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent todo2StarOffBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO2_STAR_OFF), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent todo2SelectBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO2_SELECT), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent todo2rNoSelectBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO2_NO_SELECT), PendingIntent.FLAG_UPDATE_CURRENT);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo2StarOn, todo2StarOnBtn);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo2StarOff, todo2StarOffBtn);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo2Select, todo2SelectBtn);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo2NoSelect, todo2rNoSelectBtn);

            PendingIntent todo3StarOnBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO3_STAR_ON), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent todo3StarOffBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO3_STAR_OFF), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent todo3SelectBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO3_SELECT), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent todo3rNoSelectBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO3_NO_SELECT), PendingIntent.FLAG_UPDATE_CURRENT);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo3StarOn, todo3StarOnBtn);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo3StarOff, todo3StarOffBtn);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo3Select, todo3SelectBtn);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo3NoSelect, todo3rNoSelectBtn);

            PendingIntent todo4StarOnBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO4_STAR_ON), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent todo4StarOffBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO4_STAR_OFF), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent todo4SelectBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO4_SELECT), PendingIntent.FLAG_UPDATE_CURRENT);
            PendingIntent todo4rNoSelectBtn = PendingIntent.getBroadcast(context, 0, new Intent(TODO4_NO_SELECT), PendingIntent.FLAG_UPDATE_CURRENT);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo4StarOn, todo4StarOnBtn);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo4StarOff, todo4StarOffBtn);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo4Select, todo4SelectBtn);
            mainViews.setOnClickPendingIntent(R.id.widgetTodo4NoSelect, todo4rNoSelectBtn);

            PendingIntent seeMoreBtn = PendingIntent.getActivity(context.getApplicationContext(), 0, new Intent(SEE_MORE), PendingIntent.FLAG_UPDATE_CURRENT);
            mainViews.setOnClickPendingIntent(R.id.widgetSeeMore, seeMoreBtn);

            appWidgetManager.updateAppWidget(widgetId, mainViews);
        }

        eventTableController.myDb.close();
        onDisabled(context);
    }

    public void buttonEventControll(Context context, Intent intent, String e) {
        Log.i(TAG, "클릭 된 버튼은 무엇일까요옹? 답 :" + e);

        ComponentName name = new ComponentName(context, TodoWidgetProvider.class);
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        int[] widgetIds = appWidgetManager.getAppWidgetIds(name);

        EventTableController eventTableController = EventTableController.get(context);

        if(e.equals(TODO1_STAR_ON)) {
            eventTableController.updateImportance("1", 0);
            mainViews.setViewVisibility(R.id.widgetTodo1StarOn, View.INVISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo1StarOff, View.VISIBLE);
        }
        if(e.equals(TODO1_STAR_OFF)) {
            eventTableController.updateImportance("1", 1);
            mainViews.setViewVisibility(R.id.widgetTodo1StarOn, View.VISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo1StarOff, View.INVISIBLE);
        }
        if(e.equals(TODO1_SELECT)) {
            eventTableController.updateCompleteness("1", 0);
            mainViews.setViewVisibility(R.id.widgetTodo1Select, View.INVISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo1NoSelect, View.VISIBLE);
        }
        if(e.equals(TODO1_NO_SELECT)) {
            eventTableController.updateCompleteness("1", 1);
            mainViews.setViewVisibility(R.id.widgetTodo1Select, View.VISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo1NoSelect, View.INVISIBLE);
        }

        if(e.equals(TODO2_STAR_ON)) {
            eventTableController.updateImportance("2", 0);
            mainViews.setViewVisibility(R.id.widgetTodo2StarOn, View.INVISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo2StarOff, View.VISIBLE);
        }
        if(e.equals(TODO2_STAR_OFF)) {
            eventTableController.updateImportance("2", 1);
            mainViews.setViewVisibility(R.id.widgetTodo2StarOn, View.VISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo2StarOff, View.INVISIBLE);
        }
        if(e.equals(TODO2_SELECT)) {
            eventTableController.updateCompleteness("2", 0);
            mainViews.setViewVisibility(R.id.widgetTodo2Select, View.INVISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo2NoSelect, View.VISIBLE);
        }
        if(e.equals(TODO2_NO_SELECT)) {
            eventTableController.updateCompleteness("2", 1);
            mainViews.setViewVisibility(R.id.widgetTodo2Select, View.VISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo2NoSelect, View.INVISIBLE);
        }

        if(e.equals(TODO3_STAR_ON)) {
            eventTableController.updateImportance("3", 0);
            mainViews.setViewVisibility(R.id.widgetTodo3StarOn, View.INVISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo3StarOff, View.VISIBLE);
        }
        if(e.equals(TODO3_STAR_OFF)) {
            eventTableController.updateImportance("3", 1);
            mainViews.setViewVisibility(R.id.widgetTodo3StarOn, View.VISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo3StarOff, View.INVISIBLE);
        }
        if(e.equals(TODO3_SELECT)) {
            eventTableController.updateCompleteness("3", 0);
            mainViews.setViewVisibility(R.id.widgetTodo3Select, View.INVISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo3NoSelect, View.VISIBLE);
        }
        if(e.equals(TODO3_NO_SELECT)) {
            eventTableController.updateCompleteness("3", 1);
            mainViews.setViewVisibility(R.id.widgetTodo3Select, View.VISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo3NoSelect, View.INVISIBLE);
        }

        if(e.equals(TODO4_STAR_ON)) {
            eventTableController.updateImportance("4", 0);
            mainViews.setViewVisibility(R.id.widgetTodo4StarOn, View.INVISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo4StarOff, View.VISIBLE);
        }
        if(e.equals(TODO4_STAR_OFF)) {
            eventTableController.updateImportance("4", 1);
            mainViews.setViewVisibility(R.id.widgetTodo4StarOn, View.VISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo4StarOff, View.INVISIBLE);
        }
        if(e.equals(TODO4_SELECT)) {
            eventTableController.updateCompleteness("4", 0);
            mainViews.setViewVisibility(R.id.widgetTodo4Select, View.INVISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo4NoSelect, View.VISIBLE);
        }
        if(e.equals(TODO4_NO_SELECT)) {
            eventTableController.updateCompleteness("4", 1);
            mainViews.setViewVisibility(R.id.widgetTodo4Select, View.VISIBLE);
            mainViews.setViewVisibility(R.id.widgetTodo4NoSelect, View.INVISIBLE);
        }

        if(e.equals(SEE_MORE)) {
            /*
            Intent mainIntent = new Intent(Intent.ACTION_MAIN);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            mainIntent.setComponent(new ComponentName(context, MainActivity.class));
            PendingIntent mainPI = PendingIntent.getActivity(context, 0, mainIntent, 0);
            mainViews.setOnClickPendingIntent(R.id.widgetSeeMore, mainPI);
            */
            /*
            Log.i(TAG, "메인 화면 호출");
            Intent mainIntent = new Intent(context.getApplicationContext(), MainActivity.class);
            PendingIntent p = PendingIntent.getActivity(context.getApplicationContext(), 0, mainIntent, 0);
            try {
                p.send();
            } catch (PendingIntent.CanceledException er) {
                er.printStackTrace();
            }
            onDisabled(context);
            */
            /*
            Intent mainIntent = new Intent(Intent.ACTION_MAIN);
            mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
            mainIntent.setComponent(new ComponentName(context, MainActivity.class));
            context.startActivity(mainIntent);
            */
        }
        eventTableController.myDb.close();

        for(int i = 0; i < widgetIds.length; i++) {
            appWidgetManager.updateAppWidget(widgetIds[i], mainViews);
        }

        if(e.equals("android.appwidget.action.APPWIDGET_UPDATE")) {
            updateInfo(context, appWidgetManager, widgetIds);
        }
    }
}
