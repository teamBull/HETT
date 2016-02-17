package com.teambulldozer.hett;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by flecho on 2016. 2. 14..
 */
public class MySimpleCursorAdapter extends SimpleCursorAdapter {

    /*
    * ListView 속도 문제 개선을 위해, Adapter를 Customize하였다.
    * 최적화를 위해, getView() 메소드(Scroll을 움직일 때마다 계속해서 호출되는)를 최대한 가볍게 만드는 것이 관건.
    * getView 내에서 ViewHolder pattern을 이용하였고, 불필요하게 object를 생성하는 일이 없도록 함.
    * 폰트는 처음 생성자에서 한 번 만든 것을 계속 가져다가 씀.
    * */


    private Context mContext; // In order to call MainActivity's method
    DatabaseHelper myDb;
    Typeface NanumSquare_B;
    Typeface NanumBarunGothic_R;
    //int currentBorderline;

    public static boolean isUserOnTyping;
    public static boolean isOnEditMenu = true;


    public MySimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags, DatabaseHelper db) {
        super(context, layout, c, from, to, flags);
        mContext = context;
        myDb = db;
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
            holder1.finishLine = (ImageView) convertView.findViewById(R.id.finishLine);
            holder1.list_item = (RelativeLayout) convertView.findViewById(R.id.list_item);
            holder1.text = (TextView) convertView.findViewById(R.id.memoContent);
            holder1.text.setTypeface(NanumBarunGothic_R);

            // 이렇게 하면 getView 내에서 불필요하게 findViewById를 많이 할 필요가 없고,
            // 나눔 바른고딕체를 매번 재생성할 필요 없이 재활용할 수 있음.

                convertView.setTag(holder1);

        } else {
            holder1 = (ViewHolder) convertView.getTag();
        }

        holder1.starButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Cursor cursor = myDb.getImportanceData();
                cursor.moveToFirst();

                try {
                    do {
                        if (Integer.toString(position_sync).equals(cursor.getString(0))) {
                            if (cursor.getString(1).equals("true")) {
                                holder1.starButton.setImageResource(R.drawable.star_off);
                                myDb.updateData(Integer.toString(position_sync), null, "false", "0");
                                break; // In order to avoid unnecessary loop.
                            } else {
                                holder1.starButton.setImageResource(R.drawable.star_on);
                                myDb.updateData(Integer.toString(position_sync), null, "true", "0");
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
        ifClickedDeleteData(holder1, position_sync);
        initializeAllButtons(holder1);
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
        public ImageView finishLine;
        public RelativeLayout list_item;
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
        viewHolder.finishLine.setVisibility(View.VISIBLE);
    }

    public void setMemo(int position_sync, TextView textView){
        Cursor cursor = myDb.getMemoData();
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
        Cursor cursor = myDb.getImportanceData();
        cursor.moveToFirst();

        try{
            do {
                if (Integer.toString(position_sync).equals(cursor.getString(0))) {
                    if (cursor.getString(1).equals("true")) {
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

        if(myDb.isCompleted(position_sync)){
            viewHolder.starButton.setVisibility(View.INVISIBLE);
            viewHolder.nextButton.setVisibility(View.INVISIBLE);
            viewHolder.orderButton.setVisibility(View.INVISIBLE);
        } else {
            viewHolder.finishLine.setVisibility(View.INVISIBLE);
        }

        return;
    }

    public void ifFinished_giveOpacity(int position_sync, ViewHolder viewHolder){

        if(myDb.isCompleted(position_sync)){

            viewHolder.text.setTextColor(ContextCompat.getColor(mContext, R.color.hatt_completedTaskColor));
            viewHolder.list_item.setBackgroundColor(ContextCompat.getColor(mContext, R.color.hatt_completedItemColor));
        } else {
            viewHolder.text.setTextColor(ContextCompat.getColor(mContext, R.color.hatt_memocolor));
            viewHolder.list_item.setBackgroundColor(ContextCompat.getColor(mContext, R.color.hatt_white));
        }

        return;
    }

}
