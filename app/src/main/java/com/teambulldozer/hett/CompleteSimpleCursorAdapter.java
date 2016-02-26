package com.teambulldozer.hett;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.teambulldozer.hett.DatabaseHelper;
import com.teambulldozer.hett.R;

/**
 * Created by SEONGBONG on 2016-02-23.
 */
public class CompleteSimpleCursorAdapter extends SimpleCursorAdapter{
    private Context mContext; // In order to call MainActivity's method
    CompleteEventTableController completeEventCtr;
    Typeface NanumSquare_B;
    Typeface NanumBarunGothic_R;
    ViewHolder viewHolder;

    public static boolean isOnEditMenu = true;

    /*1) context : ListView의 context
    2) layout : list의 Layout
    3) c : DB에서 가져온 Data를 가리키는 Cursor.
    4) from : DB 필드 이름
    5) to : DB 필드에 대응되는 component의 id*/
    public CompleteSimpleCursorAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags,CompleteEventTableController completeEventCtr) {
        super(context, layout, c, from, to, flags);
        this.mContext = context;
        this.completeEventCtr = completeEventCtr;
        NanumSquare_B = Typeface.createFromAsset(context.getAssets(), "NanumSquare_Bold.ttf");
        NanumBarunGothic_R = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothic_Regular.ttf");
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        viewHolder = new ViewHolder();
        LayoutInflater li = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
        View view = li.inflate(R.layout.list_item_complete, parent, false);
        viewHolder.borderline = (ImageView) view.findViewById(R.id.border_line);
        viewHolder.deleteButton = (ImageView) view.findViewById(R.id.delete_btn);
        viewHolder.completelistItem = (RelativeLayout) view.findViewById(R.id.list_item_complete);
        viewHolder.memoContent = (TextView) view.findViewById(R.id.memo_content);
        viewHolder.memoContent.setTypeface(NanumBarunGothic_R);
        view.setTag(viewHolder);

        initializeAllButtons();
        //((CompleteActivity)mContext).getLoaderManager().restartLoader()
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.i("Adapter", "bindView");
        ViewHolder holder = (ViewHolder)view.getTag();
        holder.memoContent.setText(cursor.getString(cursor.getColumnIndex("MEMO")));
        holder.deleteButton.setTag(cursor.getInt(cursor.getColumnIndex("_id")));
        super.bindView(view, context, cursor);
    }

    private static class ViewHolder {
        public ImageView borderline;
        public RelativeLayout completelistItem;
        public ImageView deleteButton;
        public TextView memoContent;
    }
    private void initializeAllButtons(){
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(mContext,String.valueOf(v.getTag()),Toast.LENGTH_SHORT).show();
                //completeEventCtr.deleteData(String.valueOf(v.getTag()));
                //completeEventCtr.rearrangeData(String.valueOf(v.getTag()));
                //changeCursor(getCursor());
                if (!((CompleteActivity)mContext).deleteRow(String.valueOf(v.getTag())))
                    Toast.makeText(mContext, "Data Not Deleted", Toast.LENGTH_LONG).show();
            }
        });
        if(isOnEditMenu){
            viewHolder.deleteButton.setVisibility(View.INVISIBLE);
        }else{
            viewHolder.deleteButton.setVisibility(View.VISIBLE);
        }
    }
}
