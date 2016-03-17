package com.teambulldozer.hett;

import android.app.Activity;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by flecho on 2016. 2. 18..
 */
public class HattToast extends Toast {

    /*
    * Main Activity의 코드가 너무 길어져서;
    * 그리 고토스트 날리려고 만든, customized toast.
    * */

    Context mContext;

    public HattToast(Context context) {
        super(context);
        mContext = context;
    }

    public void showToast(String body, int duration){

        LayoutInflater inflater;
        View v;
        if(false){
            Activity act = (Activity)mContext;
            inflater = act.getLayoutInflater();
            v = inflater.inflate(R.layout.toast_layout, null);
        }else{  // same
            inflater = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.toast_layout, null);
        }
        TextView text = (TextView) v.findViewById(R.id.toast_text);
        text.setText(body);

        show(this,v,duration);
    }

    private void show(Toast toast, View v, int duration){
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 0);
        toast.setDuration(duration);
        toast.setView(v);
        toast.show();
    }

}

