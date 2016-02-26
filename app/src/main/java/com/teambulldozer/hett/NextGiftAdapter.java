package com.teambulldozer.hett;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by YUNSEON on 2016-02-26.
 */
public class NextGiftAdapter extends BaseAdapter {

    Context context;   // 현재 화면의 제어권자
    int layout;         // 한 행을 그려줄 레이아웃
    ArrayList al;       // 다량의 데이터
    LayoutInflater inf;     // layout xml 파일을 객체로 전환할때 필요
    Typeface NanumBarunGothic_R;

    public NextGiftAdapter(Context context, int layout, ArrayList al) {// 초기화
        this.context = context;
        this.layout = layout;
        this.al = al;
        this.inf = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        NanumBarunGothic_R = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothic_Regular.ttf");
    }
    @Override
    public int getCount() { // ListView 에서 사용할 데이터의 총개수
        return al.size();
    }
    @Override
    public Object getItem(int position) { // 해당 position번째의 데이터 값
        return al.get(position);
    }
    @Override
    public long getItemId(int position){// 해당 position번째의 유니크한id 값
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        //해당행 순서,   해당행 레이아웃,          리스트뷰
        // 한행의 화면을 셋팅하는 메서드 (가장 중요)
        if (convertView == null) {
            convertView = inf.inflate(layout, null);
            //xml파일로 레이아웃객체 생성
        }
        TextView tv = (TextView)convertView.findViewById(R.id.tvClTalkMore);
        tv.setText(al.get(position).toString()); // 해당번째의 값을 설정
        tv.setTypeface(NanumBarunGothic_R);
        convertView.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                // 여기서 이벤트를 막습니다.
                return true;
            }
        });

        return convertView;
    }

}
