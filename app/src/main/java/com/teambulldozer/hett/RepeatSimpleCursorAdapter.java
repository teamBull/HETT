package com.teambulldozer.hett;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.StringTokenizer;

/**
 * Created by SEONGBONG on 2016-02-26.
 */
public class RepeatSimpleCursorAdapter extends SimpleCursorAdapter{
    private Context mContext; // In order to call MainActivity's method
    RepeatEventController repeatEventCtr;
    Typeface NanumSquare_B;
    Typeface NanumBarunGothic_R;
    ViewHolder viewHolder;

    private static String maxDate=null;
    public static boolean isOnEditMenu = true;

    /*1) context : ListView의 context
    2) layout : list의 Layout
    3) c : DB에서 가져온 Data를 가리키는 Cursor.
    4) from : DB 필드 이름
    5) to : DB 필드에 대응되는 component의 id*/
    public RepeatSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags,RepeatEventController repeatEventCtr) {
        super(context, layout, c, from, to, flags);
        this.mContext = context;
        this.repeatEventCtr = repeatEventCtr;
        NanumSquare_B = Typeface.createFromAsset(context.getAssets(), "NanumSquare_Bold.ttf");
        NanumBarunGothic_R = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothic_Regular.ttf");
    }
    private static class ViewHolder {
        public RelativeLayout repeatlistItem;
        public ImageView startBtn;
        public TextView memoContent;
        public TextView dayOfWeek;
        public ImageView repeatDeleteBtn;
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        viewHolder = new ViewHolder();
        LayoutInflater li = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.list_item_repeat, parent, false);

        viewHolder.repeatlistItem = (RelativeLayout)view.findViewById(R.id.list_item_repeat);

        viewHolder.startBtn = (ImageView)view.findViewById(R.id.repeat_star_btn);

        viewHolder.memoContent = (TextView)view.findViewById(R.id.repeat_memo_content);
        viewHolder.memoContent.setTypeface(NanumBarunGothic_R);

        viewHolder.dayOfWeek = (TextView)view.findViewById(R.id.repeat_dayofweek_textview);
        viewHolder.dayOfWeek.setTypeface(NanumBarunGothic_R);

        viewHolder.repeatDeleteBtn = (ImageView)view.findViewById(R.id.repeat_delete_Btn);
        view.setTag(viewHolder);

        initializeAllButtons();

        return view;
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        RelativeLayout.LayoutParams relativeLayoutPrams;
        ViewHolder holder = (ViewHolder)view.getTag();

        holder.startBtn.setTag(cursor.getString(cursor.getColumnIndex("_id")));
        if(cursor.getInt(cursor.getColumnIndex("IMPORTANCE")) == 1 ){
            holder.startBtn.setImageResource(R.drawable.star_on);
        }else{
            holder.startBtn.setImageResource(R.drawable.star_off);
        }
        holder.memoContent.setText(cursor.getString(cursor.getColumnIndex("MEMO")));
        holder.dayOfWeek.setText(cursor.getString(cursor.getColumnIndex("DAY_OF_WEEK")));
        holder.repeatDeleteBtn.setTag(cursor.getString(cursor.getColumnIndex("_id")));

        super.bindView(view, context, cursor);
    }

    private void initializeAllButtons(){
        if(isOnEditMenu){

            viewHolder.dayOfWeek.setVisibility(View.VISIBLE);
            viewHolder.repeatDeleteBtn.setVisibility(View.GONE);//-버튼 삭제
        }else{
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,330, mContext.getResources().getDisplayMetrics());
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.CENTER_VERTICAL);
            params.addRule(RelativeLayout.RIGHT_OF,R.id.repeat_star_btn);
            params.addRule(RelativeLayout.LEFT_OF,R.id.repeat_delete_Btn);
            viewHolder.memoContent.setLayoutParams(params);
            viewHolder.dayOfWeek.setVisibility(View.GONE);
            viewHolder.repeatDeleteBtn.setVisibility(View.VISIBLE);//-버튼 생성
        }
        viewHolder.repeatDeleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (repeatEventCtr.deleteData(String.valueOf(v.getTag())) == 1) {
                    swapCursor(repeatEventCtr.getEventRepeatData());
                    notifyDataSetChanged();
                    maxDate = null;
                } else {
                    Toast.makeText(mContext, "Data Not Deleted", Toast.LENGTH_LONG).show();
                }
            }
        });

        viewHolder.startBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("id값", String.valueOf(v.getTag()));
                if (repeatEventCtr.updateImportances(String.valueOf(v.getTag()),repeatEventCtr.getEventImportance(String.valueOf(v.getTag()))) == 1) {
                    swapCursor(repeatEventCtr.getEventRepeatData());
                    notifyDataSetChanged();
                    maxDate = null;
                } else {
                    Toast.makeText(mContext, "Data Not Deleted", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
    public String dayConverter(String dateInfo){
        Calendar calendar = Calendar.getInstance();
        String year = null,month = null, date = null,hour=null,min=null,sec=null;
        StringTokenizer st = new StringTokenizer(dateInfo,"/");
        while(st.hasMoreElements()){
            year = st.nextElement().toString();
            month = st.nextElement().toString();
            date = st.nextElement().toString();
            hour = st.nextElement().toString();
            min = st.nextElement().toString();
            sec = st.nextElement().toString();
        }

        calendar.set(Calendar.YEAR,Integer.parseInt(year));
        calendar.set(Calendar.MONTH,Integer.parseInt(month));
        calendar.set(Calendar.DATE,Integer.parseInt(date));

        String dayOfWeek="";
        switch (calendar.get(Calendar.DAY_OF_WEEK)){
            case 1:
                Log.i("ca",String.valueOf(calendar.get(Calendar.DAY_OF_WEEK)));
                dayOfWeek = "토";
                break;
            case 2:
                dayOfWeek = "일";
                break;
            case 3:
                dayOfWeek = "월";
                break;
            case 4:
                dayOfWeek = "화";
                break;
            case 5:
                dayOfWeek = "수";
                break;
            case 6:
                dayOfWeek = "목";
                break;
            case 7:
                dayOfWeek = "금";
                break;
        }
        return month + "월 " + date + "일 " + "(" + dayOfWeek + ")";
    }
    public static void setMaxDate(String maxDate) {
        RepeatSimpleCursorAdapter.maxDate = maxDate;
    }
}
