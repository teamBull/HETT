package com.teambulldozer.hett;

import android.content.Context;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;



/**
 * Created by GHKwon on 2016-02-17.
 */
public class BackgroundThemeAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<BackgroundThemeDTO> arrayList;
    private LayoutInflater layoutInflater;
    public ArrayList<View> arrayListView;
    public ViewHolder disableTargetViewHolder;
    public static int isSelected;
    public int isPermission;
    private static Typeface NanumBarunGothic_R;

    public BackgroundThemeAdapter(Context context, ArrayList<BackgroundThemeDTO> arrayList) {
        this.context=context;
        this.arrayList=arrayList;
        /*기존의 화면에 그리기 위해선,onCreate에서 화면을 그려주는데 화면에 등록한걸 inflater라 부른다.
        * 이걸 우리가 하나하나 그려줘야 하기 때문에 inflater를 빌려온다.*/
        this.layoutInflater = LayoutInflater.from(this.context);
        this.arrayListView = new ArrayList<View>();
        NanumBarunGothic_R = Typeface.createFromAsset(context.getAssets(), "NanumBarunGothic_Regular.ttf");
    }
    @Override
    public int getCount() {
        return this.arrayList.size();
    }
    public BackgroundThemeDTO getItem(int position) {
        return this.arrayList.get(position);
    }
    @Override
    public long getItemId(int position){
        return position;
    }
    private class ViewHolder {
        TextView backgroundThemeName;
        ImageView backgroundThemeOpenStatus;
        ImageView selectedBackgroundTheme;
        boolean isReapeat=false;
    }
    @Override
    public View getView(final int position,View covertView,ViewGroup parent) {

        final ViewHolder viewHolder;
        final View itemLayout = layoutInflater.inflate(R.layout.list_setting_background_theme, null);
        if(covertView==null) {
            viewHolder = new ViewHolder();
            arrayListView.add(itemLayout);//error가 나면 이 코드를 위로 올릴 것.
            viewHolder.backgroundThemeName = (TextView) itemLayout.findViewById(R.id.backgroundThemeName); // 백그라운드 테마 객체를 받아옴.
            viewHolder.backgroundThemeOpenStatus = (ImageView) itemLayout.findViewById(R.id.backgroundThemeOpenStatus); // status를 표시해 줄 ImageView를 받아옴.
            viewHolder.selectedBackgroundTheme = (ImageView) itemLayout.findViewById(R.id.selectedBackgroundTheme);
            covertView = layoutInflater.inflate(R.layout.list_setting_background_theme,parent,false);
            covertView.setTag(viewHolder);
        } else {
            viewHolder=(ViewHolder)covertView.getTag();
        }
        //TextView backgroundThemeName =
        viewHolder.backgroundThemeName.setText(arrayList.get(position).getBackgroundThemeName()); // 테마 명 set.
        viewHolder.backgroundThemeName.setTypeface(NanumBarunGothic_R);

        if(arrayList.get(position).getIsBackgroundPermission()==1) {
            viewHolder.backgroundThemeOpenStatus = (ImageView) itemLayout.findViewById(R.id.backgroundThemeOpenStatus);
            viewHolder.backgroundThemeOpenStatus.setImageResource(R.drawable.white_select_image);
            viewHolder.backgroundThemeName.setTextColor(context.getResources().getColor(R.color.permissionBackgroundColor));
        }
        if(arrayList.get(position).getIsSelected()==1) { //선택된 애라면
            disableTargetViewHolder=viewHolder;
            setImageByClick(position, viewHolder); // 이미지 클릭메소드 호출!
            viewHolder.backgroundThemeOpenStatus.setImageResource(R.drawable.select);
        }
        isPermission = arrayList.get(position).getIsBackgroundPermission();

        if(isPermission==1) {
            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //리스트 뷰가 클릭 된 거니까 포문을 이용해서 모두 비선택으로 변경 해준뒤 클릭 한 놈만 선택하는 방향으로 변경함.

                    final int checkInt = position;//arrayList.get(position).getBackgroundCode();
                    /*Handler handler = new Handler() {
                        @Override
                        public void handleMessage(Message msg) {*/
                    setImageByClick(checkInt, viewHolder);
                        /*}
                    };
                    handler.obtainMessage().sendToTarget();*/
                    disableTargetViewHolder=viewHolder; // isEnable에서 enable할 애.
                    isEnabled(isSelected);
                    Log.d("number",isSelected+"");
                    isSelected=checkInt;

                }
            });//리스너 종료.
        }
        return itemLayout;
    }
    public void selectTheme(ViewHolder viewHolder){
        disableTargetViewHolder.selectedBackgroundTheme.setVisibility(View.GONE);
        viewHolder.selectedBackgroundTheme.setVisibility(View.VISIBLE);
        viewHolder.backgroundThemeOpenStatus.setImageResource(R.drawable.select);
        viewHolder.backgroundThemeName.setTextColor(context.getResources().getColor(R.color.hatt_cyan));
    }
    public void setImageByClick(int checkInt,ViewHolder viewHolder) {
        isSelected=checkInt; //선택된 얘의 번호를 ..ㄱㄱ
        switch (checkInt) {
            case 0 : {
                if(viewHolder.isReapeat)
                    break;
                viewHolder.isReapeat=true;
                viewHolder.selectedBackgroundTheme.setImageResource(R.drawable.white_select_image);
                break;
            }
            case 1 : {
                if(viewHolder.isReapeat)
                    break;
                viewHolder.isReapeat=true;
                viewHolder.selectedBackgroundTheme.setImageResource(R.drawable.bg_pattern_tree_01);
                break;
            }
            case 2 : {
                if(viewHolder.isReapeat)
                    break;
                viewHolder.isReapeat=true;
                viewHolder.selectedBackgroundTheme.setImageResource(R.drawable.background_theme_stripe);
                break;
            }
            case 3 : {
                if(viewHolder.isReapeat)
                    break;
                viewHolder.isReapeat=true;
                viewHolder.selectedBackgroundTheme.setImageResource(R.drawable.space_dir_width);
                break;
            }
            case 4 : {
                if(viewHolder.isReapeat)
                    break;
                viewHolder.isReapeat=true;
                viewHolder.selectedBackgroundTheme.setImageResource(R.drawable.bg_pattern_snow);
                break;
            }
            /*case 5 : {
                ((ImageView) itemLayout.findViewById(R.id.selectedBackgroundTheme)).setImageResource(R.drawable.bg_pattern_snow);
                selectTheme(itemLayout);
                break;
            }
            case 6 : {
                ((ImageView) itemLayout.findViewById(R.id.selectedBackgroundTheme)).setImageResource(R.drawable.bg_pattern_tree_01);
                selectTheme(itemLayout);
                break;
            }*/
        }
        selectTheme(viewHolder);
    }

    @Override
    public boolean isEnabled(int position) {
        //arrayListView.get(position).findViewById(R.id.selectedBackgroundTheme).setVisibility(View.GONE);


        disableTargetViewHolder.backgroundThemeOpenStatus.setImageResource(R.drawable.white_select_image);
        disableTargetViewHolder.backgroundThemeName.setTextColor(context.getResources().getColor(R.color.hatt_gray));
        return super.isEnabled(position);
    }
}
