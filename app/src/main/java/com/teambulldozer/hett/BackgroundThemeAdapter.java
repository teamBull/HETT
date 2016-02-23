package com.teambulldozer.hett;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by GHKwon on 2016-02-17.
 */
public class BackgroundThemeAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<String> arrayList;
    private LayoutInflater layoutInflater;
    public BackgroundThemeAdapter(Context context, ArrayList<String> arrayList) {
        this.context=context;
        this.arrayList=arrayList;
        /*기존의 화면에 그리기 위해선,onCreate에서 화면을 그려주는데 화면에 등록한걸 inflater라 부른다.
        * 이걸 우리가 하나하나 그려줘야 하기 때문에 inflater를 빌려온다.*/
        this.layoutInflater = LayoutInflater.from(this.context);
    }
    @Override
    public int getCount() {
        return this.arrayList.size();
    }
    public String getItem(int position) {
        return this.arrayList.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    @Override
    public View getView(int position,View covertView,ViewGroup parent) {
        View itemLayout = layoutInflater.inflate(R.layout.list_setting_background_theme,null);
        TextView backgroundThemeName = (TextView)itemLayout.findViewById(R.id.backgroundThemeName);
        backgroundThemeName.setText(arrayList.get(position).toString());
        ImageView backgroundThemeOpenStatus = (ImageView)itemLayout.findViewById(R.id.backgroundThemeOpenStatus);
        backgroundThemeOpenStatus.setImageResource(R.drawable.select);
        if(arrayList.get(position).toString().equals("나무나무")) {
            ImageView imageView = (ImageView)itemLayout.findViewById(R.id.selectedBackgroundTheme);
            imageView.setImageResource(R.drawable.bg_pattern_tree_01);
            imageView.setVisibility(View.VISIBLE);
        }
        return itemLayout;
    }
}
