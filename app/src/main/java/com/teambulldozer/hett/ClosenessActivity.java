package com.teambulldozer.hett;

import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by YUNSEON on 2016-02-16.
 */
public class ClosenessActivity extends AppCompatActivity {

    Button btnPrevCloseness;
    TextView tvTotalPoint;
    TextView tvTodayPoint;
    TextView tvClCloseness;
    TextView tvBtnClosenessOk;
    TextView tvClDesc;
    TextView tvCl1;
    TextView tvCl2;

    float todayPoint;
    float totalPoint;

    // 폰트
    Typeface NanumSquare_B;
    Typeface NanumBarunGothic_R;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closeness);

        NanumSquare_B = Typeface.createFromAsset(getAssets(), "NanumSquare_Bold.ttf");
        NanumBarunGothic_R = Typeface.createFromAsset(getAssets(), "NanumBarunGothic_Regular.ttf");

        DatabaseHelper dbHelper = new DatabaseHelper.get(this);
        FriendDataManager dataManager = new FriendDataManager(this);

        btnPrevCloseness = (Button)findViewById(R.id.btnPrevCloseness);
        btnPrevCloseness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        todayPoint = (float)dbHelper.getCompletedDataSize()/dbHelper.numOfEntries();

        tvTodayPoint = (TextView)findViewById(R.id.tvTodayPoint);
        tvTodayPoint.setText(String.format("%.1f°", todayPoint));

        tvTotalPoint = (TextView)findViewById(R.id.tvTotalPoint);

        tvClCloseness = (TextView)findViewById(R.id.tvClCloseness);
        tvBtnClosenessOk = (TextView)findViewById(R.id.tvBtnClosenessOk);
        tvClDesc = (TextView)findViewById(R.id.tvClDesc);
        tvCl1 = (TextView)findViewById(R.id.tvCl1);
        tvCl2 = (TextView)findViewById(R.id.tvCl2);

        dataManager.updateTotalPoint(1, dataManager.getTotalPoint()+todayPoint);
        totalPoint = dataManager.getTotalPoint();

        tvTotalPoint.setText(String.format("%.1f°", totalPoint));

        setFont();
    }

    public void setFont(){
        tvClCloseness.setTypeface(NanumSquare_B);
        tvBtnClosenessOk.setTypeface(NanumSquare_B);
        tvClDesc.setTypeface(NanumBarunGothic_R);
        tvTotalPoint.setTypeface(NanumSquare_B);
        tvCl1.setTypeface(NanumSquare_B);
        tvCl2.setTypeface(NanumSquare_B);
        tvTodayPoint.setTypeface(NanumSquare_B);
    }
}