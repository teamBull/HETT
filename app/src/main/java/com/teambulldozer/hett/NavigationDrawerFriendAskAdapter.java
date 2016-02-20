package com.teambulldozer.hett;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by GHKwon on 2016-02-10.
 */
public class NavigationDrawerFriendAskAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<String> arrayList;
    private LayoutInflater layoutInflater;
    public NavigationDrawerFriendAskAdapter(Context context, ArrayList<String> arrayList) {
        this.context=context;
        this.arrayList=arrayList;
        /*기존의 화면에 그리기 위해선,onCreate에서 화면을 그려주는데 화면에 등록한걸 inflater라 부른다.
        * 이걸 우리가 하나하나 그려줘야 하기 때문에 inflater를 빌려온다.*/
        this.layoutInflater = LayoutInflater.from(this.context);
    }

    /**
     * 리스트뷰에서 자식 아이템 개수를 결정하기 위해서 호출함.
     * @return 어탭터가 가지고 있는 데이터 갯수를 리턴.
     */
    @Override
    public int getCount() {
        return this.arrayList.size();
    }

    /**
     * 리스트뷰에서 특정 아이템 위치에 표시할 데이터와 데이터 위치를 요구하는 경우가 있어서. 이를 위해 어댑터는 리스트뷰가 요구하는 아이템의 위치를 데이터와 위치를 전달해야 함.
     * @param position 특정 데이터의 위치.
     * @return 특정 데이터의 위치.
     */
    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public String getItem(int position) {
        return this.arrayList.get(position);
    }

    /**
     * 리스트뷰는 사용자에게 당장 보여줘야 할 데이터가 있다면 해당 함수를 통해 요구한다.
     * 그러므로 어댑터는 리스트뷰가 요구하는 위치의 아이템을 생성하고 전달해야 한다.
     * 여기서 아이템은 리스트뷰의 자식 뷰에 해당
     * @param position 당장 보여줘야 할 데이터의 위치
     * @param covertView 음.. 이걸 왜받지?
     * @param parent 부모의 VIewGroup.(여기선 RelativeLayout)
     * @return 해당하는 데이터.
     */
    @Override
    public View getView(int position,View covertView,ViewGroup parent) {
        View itemLayout = layoutInflater.inflate(R.layout.drawer_friend_ask,null);
        TextView friend_ask = (TextView)itemLayout.findViewById(R.id.friend_ask);
        friend_ask.setText(arrayList.get(position).toString());
        return itemLayout;
    }
}
