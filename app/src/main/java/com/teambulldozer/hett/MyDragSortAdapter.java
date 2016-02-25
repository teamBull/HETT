package com.teambulldozer.hett;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobeta.android.dslv.SimpleDragSortCursorAdapter;

/**
 * Created by flecho on 2016. 2. 24..
 */
public class MyDragSortAdapter extends SimpleDragSortCursorAdapter {

        /*
    * ListView 속도 문제 개선을 위해, Adapter를 Customize하였다.
    * 최적화를 위해, getView() 메소드(Scroll을 움직일 때마다 계속해서 호출되는)를 최대한 가볍게 만드는 것이 관건.
    * getView 내에서 ViewHolder pattern을 이용하였고, 불필요하게 object를 생성하는 일이 없도록 함.
    * 폰트는 처음 생성자에서 한 번 만든 것을 계속 가져다가 씀.
    * */


    private Context mContext; // In order to call MainActivity's method
    EventTableController myEventController;
    Typeface NanumSquare_B;
    Typeface NanumBarunGothic_R;
    //int currentBorderline;

    public static boolean isUserOnTyping;
    public static boolean isOnEditMenu = true;

    public MyDragSortAdapter(Context context, int layout, Cursor c, String[] from, int[] to, EventTableController controller){
        super(context, layout, c, from, to, 0);
        mContext = context;
        myEventController = controller;
        NanumSquare_B = Typeface.createFromAsset(context.getAssets(), "NanumSquare_Bold.ttf");
        NanumBarunGothic_R = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothic_Regular.ttf");
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder1;
        final int position_sync = position + 1;


        if (convertView == null) {

            LayoutInflater li = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
            convertView = li.inflate(R.layout.list_item_memo, parent, false);

            holder1 = new ViewHolder();
            holder1.starButton = (ImageView) convertView.findViewById(R.id.starButton);
            holder1.nextButton = (ImageView) convertView.findViewById(R.id.nextButton);
            holder1.deleteButton = (ImageView) convertView.findViewById(R.id.deleteButton);
            holder1.orderButton = (ImageView) convertView.findViewById(R.id.orderButton);
            holder1.finishLine1 = (ImageView) convertView.findViewById(R.id.finishLine1);
            holder1.finishLine2 = (ImageView) convertView.findViewById(R.id.finishLine2);
            holder1.borderline = (ImageView) convertView.findViewById(R.id.borderline);
            holder1.list_item = (RelativeLayout) convertView.findViewById(R.id.list_item);
            holder1.text = (TextView) convertView.findViewById(R.id.memoContent);
            holder1.text.setTypeface(NanumBarunGothic_R);

            // 이렇게 하면 getView 내에서 불필요하게 findViewById를 많이 할 필요가 없고,
            // 나눔 바른고딕체를 매번 재생성할 필요 없이 재활용할 수 있음.

            convertView.setTag(holder1);

        } else {
            holder1 = (ViewHolder) convertView.getTag();
        }

        respondToStarBtnClicked(holder1, position_sync); // 별표 클릭에 반응하기.
        ifClickedDeleteData(holder1, position_sync); // 딜리트버튼을 클릭하면 해당 데이터를 지워준다!
        initializeAllButtons(holder1); // 처음에 모든 버튼을 모두 보이게 해놨다가, 조건에 의해 필요없는 것은 끄는 방식.
        ifIsOnBorder(holder1, position_sync);
        ifNextButtonClicked(holder1);

        if(isOnEditMenu){ // 일반적인 상태
            setEditCondition(holder1);  // More general Condition;
            ifImportant_fillStar(position_sync, holder1.starButton);
            ifFinished_drawFinishLine(position_sync, holder1);
            ifFinished_giveOpacity(position_sync, holder1);
            setMemo(position_sync, holder1.text);
            ifUserOnTyping(holder1);
        } else { // 편집버튼 눌렀을 때,
            setEditCondition(holder1);  // More general Condition;
            ifFinished_drawFinishLine(position_sync, holder1);
            ifFinished_giveOpacity(position_sync, holder1);
            setMemo(position_sync, holder1.text);
        }


        return convertView;
    }

    private static class ViewHolder {
        public ImageView starButton;
        public ImageView nextButton;
        public ImageView deleteButton;
        public ImageView orderButton;
        public TextView text;
        public ImageView finishLine1;
        public ImageView finishLine2;
        public RelativeLayout list_item;
        public ImageView borderline;

    }


    // 태훈이 합치는 부분.
    public void ifNextButtonClicked(ViewHolder viewHolder){
        viewHolder.nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(mContext, AlarmMain.class);
                mContext.startActivity(i);

            }
        });

    }


    public void respondToStarBtnClicked(final ViewHolder viewHolder, final int position_sync){
        viewHolder.starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = myEventController.getImportanceData();
                cursor.moveToFirst();

                try {
                    do {
                        if (Integer.toString(position_sync).equals(cursor.getString(0))) {
                            if (cursor.getString(1).equals("1")) {
                                viewHolder.starButton.setImageResource(R.drawable.star_off); // 이 라인 지우면 안돼;
                                myEventController.updateImportance(Integer.toString(position_sync), 0);
                                break; // In order to avoid unnecessary loop.
                            } else {
                                viewHolder.starButton.setImageResource(R.drawable.star_on);
                                myEventController.updateImportance(Integer.toString(position_sync), 1);
                                break;
                            }
                        }

                    } while (cursor.moveToNext());
                } finally {
                    if (cursor != null)
                        cursor.close();
                }
            }
        });
    }


    public void ifClickedDeleteData(final ViewHolder viewHolder, final int position_sync){
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!((MainActivity)mContext).deleteRow(Integer.toString(position_sync)))
                    Toast.makeText(mContext, "Data Not Deleted", Toast.LENGTH_LONG).show();
            }
        });

    }

    public void ifIsOnBorder(ViewHolder viewHolder, int position_sync){

        if(position_sync < 2)
            return;

        int position_before = position_sync-1;
        int position_after = position_sync;

        if((!myEventController.isCompleted(position_before)) && myEventController.isCompleted(position_after)){
            ViewGroup.LayoutParams layoutParams = viewHolder.borderline.getLayoutParams();
            layoutParams.height = ((MainActivity) mContext).pixelToDP(8);
            viewHolder.borderline.setLayoutParams(layoutParams);
        } else{
            ViewGroup.LayoutParams layoutParams = viewHolder.borderline.getLayoutParams();
            layoutParams.height = ((MainActivity) mContext).pixelToDP(0);
            viewHolder.borderline.setLayoutParams(layoutParams);
        }


    }

    public void setEditCondition(ViewHolder viewHolder){

        if(isOnEditMenu){
            viewHolder.deleteButton.setVisibility(View.INVISIBLE);
            viewHolder.orderButton.setVisibility(View.INVISIBLE);

        } else {
            viewHolder.starButton.setVisibility(View.INVISIBLE);
            viewHolder.nextButton.setVisibility(View.INVISIBLE);
        }

        return;

    }

    public void initializeAllButtons(ViewHolder viewHolder){
        viewHolder.starButton.setVisibility(View.VISIBLE);
        viewHolder.nextButton.setVisibility(View.VISIBLE);
        viewHolder.deleteButton.setVisibility(View.VISIBLE);
        viewHolder.orderButton.setVisibility(View.VISIBLE);
        viewHolder.finishLine2.setVisibility(View.VISIBLE);
    }

    public void setMemo(int position_sync, TextView textView){
        Cursor cursor = myEventController.getMemoData();
        cursor.moveToFirst();
        try{
            do {
                if (Integer.toString(position_sync).equals(cursor.getString(0))) {
                    textView.setText(cursor.getString(1));
                    break;
                }

            } while (cursor.moveToNext());
        } finally {
            if(cursor != null)
                cursor.close();
        }


    }

    public void ifUserOnTyping(ViewHolder viewHolder){
        if(isUserOnTyping){
            viewHolder.starButton.setVisibility(View.INVISIBLE);
            viewHolder.nextButton.setVisibility(View.INVISIBLE);
        }
    }


    public void ifImportant_fillStar(int position_sync, ImageView starButton) {
        Cursor cursor = myEventController.getImportanceData();
        cursor.moveToFirst();

        try{
            do {
                if (Integer.toString(position_sync).equals(cursor.getString(0))) {
                    if (cursor.getString(1).equals("1")) {
                        starButton.setImageResource(R.drawable.star_on);
                        break;
                    } else {
                        starButton.setImageResource(R.drawable.star_off);
                        break;
                    }
                }

            } while (cursor.moveToNext());

        } finally {
            if(cursor != null)
                cursor.close();
        }

    }

    public void ifFinished_drawFinishLine(int position_sync, ViewHolder viewHolder){

        if(myEventController.isCompleted(position_sync)){
            viewHolder.starButton.setVisibility(View.INVISIBLE);
            viewHolder.nextButton.setVisibility(View.INVISIBLE);
            viewHolder.orderButton.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.finishLine2.setVisibility(View.INVISIBLE);
        }

        return;
    }

    public void ifFinished_giveOpacity(int position_sync, ViewHolder viewHolder){

        if(myEventController.isCompleted(position_sync)){

            viewHolder.text.setTextColor(ContextCompat.getColor(mContext, R.color.hatt_completedTaskColor));
            viewHolder.list_item.setBackgroundColor(ContextCompat.getColor(mContext, R.color.hatt_completedItemColor));
        } else {
            viewHolder.text.setTextColor(ContextCompat.getColor(mContext, R.color.hatt_memocolor));
            viewHolder.list_item.setBackgroundColor(ContextCompat.getColor(mContext, R.color.hatt_white));
        }

        return;
    }



}


