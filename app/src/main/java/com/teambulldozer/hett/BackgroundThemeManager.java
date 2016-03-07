package com.teambulldozer.hett;

import android.content.Context;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by GHKwon on 2016-03-03.
 */
public class BackgroundThemeManager {
    private Context context;
    private static final BackgroundThemeManager backgroundThemeManager;
    public int selectedBackgroundThemeCodeNo;
    static {
        backgroundThemeManager = new BackgroundThemeManager();
    }
    private BackgroundThemeManager(){

    }
    public static BackgroundThemeManager getInstance() {
        return backgroundThemeManager;
    }
    public void setBackground(Context context,View view) {
        this.context=context;
        selectedBackgroundThemeCodeNo = DrawerTableController.getInstance(context).searchByBackgroundThemeCodeNo();
        switch (selectedBackgroundThemeCodeNo) {
            case 0 :whiteSelectImage(view);break;
            case 1 :bgPatternTree(view); break;
            case 2 :Stripe(view); break;
            case 3 :space(view); break;
            case 4 :snow(view);break;
            case 5 :break;

        }
    }
    public void whiteSelectImage(View view) {
        view.setBackgroundResource(R.color.hatt_background);

    }
    public void bgPatternTree(View view) {
        view.setBackground(context.getResources().getDrawable(R.drawable.bg_pattern_tree_01));
        if(view.findViewById(R.id.myLayout)!=null)
            changeOwnTextColorByMainActivity(view);
    }
    public void Stripe(View view) {
        view.setBackground(context.getResources().getDrawable(R.drawable.background_theme_stripe));
        if(view.findViewById(R.id.myLayout)!=null)
            changeOwnTextColorByMainActivity(view);
    }
    public void space(View view ) {
        view.setBackground(context.getResources().getDrawable(R.drawable.space));
        if(view.findViewById(R.id.myLayout)!=null)
            changeTextColorByLightByMainActivity(view);
    }
    public void snow(View view) {
        view.setBackground(context.getResources().getDrawable(R.drawable.snow_1));
        if(view.findViewById(R.id.myLayout)!=null)
            changeOwnTextColorByMainActivity(view);
    }
    public void changeTextColorByLightByMainActivity(View view) {
        ((TextView)view.findViewById(R.id.todayBar)).setTextColor(context.getResources().getColor(R.color.hatt_white));
        ((TextView)view.findViewById(R.id.dateBar)).setTextColor(context.getResources().getColor(R.color.hatt_white));
        ((TextView)view.findViewById(R.id.finishMenu)).setTextColor(context.getResources().getColor(R.color.hatt_white));
        ((TextView)view.findViewById(R.id.editMenu)).setTextColor(context.getResources().getColor(R.color.hatt_white));
        ((ImageView)view.findViewById(R.id.friendBtn)).setImageDrawable(context.getResources().getDrawable(R.drawable.white_menu));

    }
    public void changeOwnTextColorByMainActivity(View view) {
        ((TextView)view.findViewById(R.id.todayBar)).setTextColor(context.getResources().getColor(R.color.hatt_date));
        ((TextView)view.findViewById(R.id.dateBar)).setTextColor(context.getResources().getColor(R.color.hatt_date));
        ((TextView)view.findViewById(R.id.finishMenu)).setTextColor(context.getResources().getColor(R.color.hatt_date));
        ((TextView)view.findViewById(R.id.editMenu)).setTextColor(context.getResources().getColor(R.color.hatt_date));
        ((ImageView)view.findViewById(R.id.friendBtn)).setImageDrawable(context.getResources().getDrawable(R.drawable.menu));
    }
}
