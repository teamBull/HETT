package com.teambulldozer.hett;

import android.content.Context;
import android.graphics.Typeface;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by GHKwon on 2016-02-17.
 */
public class BackgroundThemeAdapter extends BaseAdapter{
    private Context context;
    private ArrayList<BackgroundThemeDTO> arrayList;
    private LayoutInflater layoutInflater;
    public static ArrayList<View> arrayListView;
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
    @Override
    public View getView(final int position,View covertView,ViewGroup parent) {
        final View itemLayout = layoutInflater.inflate(R.layout.list_setting_background_theme,null);
        arrayListView.add(itemLayout);
        TextView backgroundThemeName = (TextView)itemLayout.findViewById(R.id.backgroundThemeName); // 백그라운드 테마 객체를 받아옴.
        backgroundThemeName.setText(arrayList.get(position).getBackgroundThemeName()); // 테마 명 set.
        backgroundThemeName.setTypeface(NanumBarunGothic_R);
        ImageView backgroundThemeOpenStatus = (ImageView)itemLayout.findViewById(R.id.backgroundThemeOpenStatus); // status를 표시해 줄 ImageView를 받아옴.

        if(arrayList.get(position).getIsBackgroundPermission()==1) {
            ImageView imageView = (ImageView) itemLayout.findViewById(R.id.backgroundThemeOpenStatus);
            imageView.setImageResource(R.drawable.white_select_image);
        }
        if(arrayList.get(position).getIsSelected()==1) { //선택된 애라면
            
            setImageByClick(position, itemLayout); // 이미지 클릭메소드 호출!
            backgroundThemeOpenStatus.setImageResource(R.drawable.select);
        }
        isPermission = arrayList.get(position).getIsBackgroundPermission();

        if(isPermission==1) {
            itemLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //리스트 뷰가 클릭 된 거니까 포문을 이용해서 모두 비선택으로 변경 해준뒤 클릭 한 놈만 선택하는 방향으로 변경함.
                    for (int i = 0; i < arrayList.size(); i++) {
                        View tempView = arrayListView.get(i);
                        ImageView imageView;
                        imageView = (ImageView) tempView.findViewById(R.id.selectedBackgroundTheme);
                        imageView.setVisibility(View.GONE);
                        if(arrayList.get(i).getIsBackgroundPermission()==1) {
                            imageView = (ImageView) tempView.findViewById(R.id.backgroundThemeOpenStatus);
                            imageView.setImageResource(R.drawable.white_select_image);
                            ((TextView)tempView.findViewById(R.id.backgroundThemeName)).setTextColor(tempView.getResources().getColor(R.color.hatt_gray));
                        }

                    }
                    int checkInt = arrayList.get(position).getBackgroundCode();
                    setImageByClick(checkInt, itemLayout);
                }
            });//리스너 종료.

        }


        return itemLayout;
    }
    public void selectTheme(View itemLayout){
        ImageView imageView ;
        imageView = (ImageView)itemLayout.findViewById(R.id.selectedBackgroundTheme);
        imageView.setVisibility(View.VISIBLE);
        imageView = (ImageView)itemLayout.findViewById(R.id.backgroundThemeOpenStatus);
        imageView.setImageResource(R.drawable.select);
        TextView backgroundThemeName = (TextView)itemLayout.findViewById(R.id.backgroundThemeName);
        backgroundThemeName.setTextColor(itemLayout.getResources().getColor(R.color.hatt_cyan));
    }
    public void setImageByClick(int checkInt,View itemLayout) {
        isSelected=checkInt; //선택된 얘의 번호를 ..ㄱㄱ
        switch (checkInt) {
            case 0 : {
                ((ImageView) itemLayout.findViewById(R.id.selectedBackgroundTheme)).setImageResource(R.drawable.white_select_image);
                selectTheme(itemLayout);
                break;
            }
            case 1 : {
                ((ImageView) itemLayout.findViewById(R.id.selectedBackgroundTheme)).setImageResource(R.drawable.bg_pattern_tree_01);
                selectTheme(itemLayout);
                break;
            }
            case 2 : {
                ((ImageView) itemLayout.findViewById(R.id.selectedBackgroundTheme)).setImageResource(R.drawable.background_theme_stripe);
                selectTheme(itemLayout);
                break;
            }
            case 3 : {

                ((ImageView) itemLayout.findViewById(R.id.selectedBackgroundTheme)).setImageResource(R.drawable.bg_pattern_tree_01);
                selectTheme(itemLayout);
                break;
            }
            case 4 : {
                ((ImageView) itemLayout.findViewById(R.id.selectedBackgroundTheme)).setImageResource(R.drawable.bg_pattern_tree_01);
                selectTheme(itemLayout);
                break;
            }
            case 5 : {
                ((ImageView) itemLayout.findViewById(R.id.selectedBackgroundTheme)).setImageResource(R.drawable.bg_pattern_tree_01);
                selectTheme(itemLayout);
                break;
            }
            case 6 : {
                ((ImageView) itemLayout.findViewById(R.id.selectedBackgroundTheme)).setImageResource(R.drawable.bg_pattern_tree_01);
                selectTheme(itemLayout);
                break;
            }
        }
    }
}
