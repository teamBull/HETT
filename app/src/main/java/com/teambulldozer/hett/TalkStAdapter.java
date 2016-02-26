package com.teambulldozer.hett;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by YUNSEON on 2016-02-16.
 */
public class TalkStAdapter extends BaseExpandableListAdapter {
    private ArrayList<String> groupList = null;
    private ArrayList<ArrayList<String>> childList = null;
    private LayoutInflater inflater = null;
    private ViewHolder viewHolder = null;

    Typeface NanumBarunGothic_R;

    DatabaseHelper helper;
    FriendDto friendData;
    FriendDataManager friendDataManager;
    DisplayMetrics dm;

    public TalkStAdapter(Context c, ArrayList<String> groupList, ArrayList<ArrayList<String>> childList) {
        super();
        this.inflater = LayoutInflater.from(c);
        this.groupList = groupList;
        this.childList = childList;
        NanumBarunGothic_R = Typeface.createFromAsset(c.getAssets(), "NanumBarunGothic_Regular.ttf");
        helper = DatabaseHelper.get(c);
        friendDataManager = FriendDataManager.get(c);
        friendData = friendDataManager.getFriend();
        Log.i("dddd", friendData.getTotalPoint()+""+friendData.getFriendName());
        dm = c.getResources().getDisplayMetrics();
    }

    // 그룹 포지션을 반환한다.
    @Override
    public String getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    // 그룹 사이즈를 반환한다.
    @Override
    public int getGroupCount() {
        return groupList.size();
    }

    // 그룹 ID를 반환한다.
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    // 그룹뷰 각각의 ROW
    @Override
    public View getGroupView(final int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.list_talk_style_group, parent, false);
            viewHolder.tv_groupName = (TextView) v.findViewById(R.id.tvTalkStyle);
            viewHolder.ivTalkStCheck = (ImageView) v.findViewById(R.id.ivTalkStCheck);
            viewHolder.ivTalkStLock = (ImageView) v.findViewById(R.id.ivTalkStLock);
            viewHolder.tv_groupName.setTypeface(NanumBarunGothic_R);
            viewHolder.dividerTop = (View)v.findViewById(R.id.adapter_divider_top);

            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        /* 그룹을 펼칠때와 닫을때 아이콘을 변경해 준다.
        그룹을 펼칠 때 childview와 groupview의 경계선을 보여준다.
        그룹을 펼치면 해당 position의 groupview의 text 색을 바꾼다.
        그룹이 닫히면 다시 text 색을 원상태로 바꾼다.*/
        viewHolder.ivTalkStCheck.setVisibility(View.INVISIBLE);
        viewHolder.ivTalkStLock.setVisibility(View.INVISIBLE);
        viewHolder.tv_groupName.setTextColor(Color.parseColor("#585858"));
        if (isExpanded) {
                viewHolder.ivTalkStCheck.setVisibility(View.VISIBLE);
                viewHolder.ivTalkStLock.setVisibility(View.INVISIBLE);
                viewHolder.dividerTop.setVisibility(View.VISIBLE);
                viewHolder.tv_groupName.setTextColor(Color.parseColor("#1bbbbf"));
            Log.i("ddd", friendData.getTotalPoint()+"");
        }
        else{
            //기본테마
            if(groupPosition == 0 && friendData.getTotalPoint() >= 0){
                viewHolder.ivTalkStCheck.setVisibility(View.INVISIBLE);
                viewHolder.ivTalkStLock.setVisibility(View.INVISIBLE);
                viewHolder.tv_groupName.setTextColor(Color.parseColor("#585858"));
                v.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        return false;
                    }
                });
            }

            //연서복
            else if (groupPosition == 1  && (int)friendData.getTotalPoint() <= 19){
                viewHolder.ivTalkStCheck.setVisibility(View.INVISIBLE);
                viewHolder.ivTalkStLock.setVisibility(View.VISIBLE);
                viewHolder.tv_groupName.setTextColor(Color.parseColor("#585858"));
                v.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    // 여기서 이벤트를 막습니다.
                    return true;
                }
                });
            }


            //한쿸어 어려훠효
            else if (groupPosition == 2 && (int)friendData.getTotalPoint() <= 59){
                viewHolder.ivTalkStCheck.setVisibility(View.INVISIBLE);
                viewHolder.ivTalkStLock.setVisibility(View.VISIBLE);
                viewHolder.tv_groupName.setTextColor(Color.parseColor("#585858"));
                v.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        // 여기서 이벤트를 막습니다.
                        return true;
                    }
                });
            }

            //극존칭
            else if(groupPosition == 3 && (int)friendData.getTotalPoint() <= 79){
                viewHolder.ivTalkStCheck.setVisibility(View.INVISIBLE);
                viewHolder.ivTalkStLock.setVisibility(View.VISIBLE);
                viewHolder.tv_groupName.setTextColor(Color.parseColor("#585858"));
                v.setOnTouchListener(new View.OnTouchListener() {
                    public boolean onTouch(View v, MotionEvent event) {
                        // 여기서 이벤트를 막습니다.
                        return true;
                    }
                });
            }

        }

        viewHolder.tv_groupName.setText(getGroup(groupPosition));

        return v;
    }

    // 차일드뷰를 반환한다.
    @Override
    public String getChild(int groupPosition, int childPosition) {
        return childList.get(groupPosition).get(childPosition);
    }

    // 차일드뷰 사이즈를 반환한다.
    @Override
    public int getChildrenCount(int groupPosition) {
        return childList.get(groupPosition).size();
    }

    // 차일드뷰 ID를 반환한다.
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    // 차일드뷰 각각의 ROW
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            viewHolder = new ViewHolder();
            v = inflater.inflate(R.layout.list_talk_style_child, null);
            viewHolder.tv_childName = (TextView) v.findViewById(R.id.tvMoreTalk);
            viewHolder.tv_childName.setTypeface(NanumBarunGothic_R);
            v.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) v.getTag();
        }

        viewHolder.tv_childName.setText(getChild(groupPosition, childPosition));

        // space between child and group
        if (childPosition == getChildrenCount(groupPosition)-1) {
            v.setPadding(Math.round(12 * dm.density), 0, 0, Math.round(13 * dm.density));
        } else if(childPosition == 0) {
            v.setPadding(Math.round(12 * dm.density), Math.round(13 * dm.density), 0, Math.round(8 * dm.density));
        }else{
            v.setPadding(Math.round(12 * dm.density), 0, 0, Math.round(8 * dm.density));
        }

        return v;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    class ViewHolder {

        public TextView tv_groupName;
        public TextView tv_childName;
        public ImageView ivTalkStCheck;
        public ImageView ivTalkStLock;
        public View dividerTop;
    }
}