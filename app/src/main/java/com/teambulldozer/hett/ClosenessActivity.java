package com.teambulldozer.hett;

import android.graphics.Typeface;
import android.graphics.drawable.ClipDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.Layout;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by YUNSEON on 2016-02-16.
 */
public class ClosenessActivity extends AppCompatActivity {

    NextGiftAdapter adapter;
    ArrayList al;
    RelativeLayout rlthemeEx;

    Button btnPrevCloseness;

    TextView tvTotalPoint;
    TextView tvTodayPoint;
    TextView tvClCloseness;
    TextView tvBtnClosenessOk;
    TextView tvClDesc;
    TextView tvCl1,tvCl2;
    TextView tvCl0dg, tvCl20dg, tvCl40dg, tvCl60dg, tvCl80dg, tvCl100dg;
    TextView tvClNext;
    TextView tvClGift, tvClGiftTitle;

    ImageView ivGiftBox0, ivGiftBox1, ivGiftBox2, ivGiftBox3, ivGiftBox4, ivGiftBox5;

    ListView lvGiftEx;

    double todayPoint;
    double totalPoint;

    // 폰트
    Typeface NanumSquare_B;
    Typeface NanumBarunGothic_R;

    //progress bar-------------------------------------
    private ClipDrawable mImageDrawable;

    // a field in your class
    private int mLevel = 0;
    private int fromLevel = 0;
    private int toLevel = 0;

    public static final int MAX_LEVEL = 10000;
    public static final int LEVEL_DIFF = 100;
    public static final int DELAY = 30;

    private Handler mUpHandler = new Handler();
    private Runnable animateUpImage = new Runnable() {

        @Override
        public void run() {
            doTheUpAnimation(fromLevel, toLevel);
        }
    };

    private Handler mDownHandler = new Handler();
    private Runnable animateDownImage = new Runnable() {

        @Override
        public void run() {
            doTheDownAnimation(fromLevel, toLevel);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_closeness);

        NanumSquare_B = Typeface.createFromAsset(getAssets(), "NanumSquare_Bold.ttf");
        NanumBarunGothic_R = Typeface.createFromAsset(getAssets(), "NanumBarunGothic_Regular.ttf");

        DatabaseHelper dbHelper = DatabaseHelper.get(this);
        FriendDataManager dataManager = FriendDataManager.get(this);
        EventTableController controller = EventTableController.get(this);

        btnPrevCloseness = (Button)findViewById(R.id.btnPrevCloseness);
        btnPrevCloseness.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        todayPoint = dataManager.getTodayPoint();
        totalPoint = dataManager.getTotalPoint();


        tvTodayPoint = (TextView)findViewById(R.id.tvTodayPoint);
        tvTodayPoint.setText(String.format("%.1f°", todayPoint));

        tvTotalPoint = (TextView)findViewById(R.id.tvTotalPoint);
        tvTotalPoint.setText(String.format("%.1f°", totalPoint));

        // 폰트 적용을 위해
        tvClCloseness = (TextView)findViewById(R.id.tvClCloseness);
        tvBtnClosenessOk = (TextView)findViewById(R.id.tvBtnClosenessOk);
        tvClDesc = (TextView)findViewById(R.id.tvClDesc);
        tvCl1 = (TextView)findViewById(R.id.tvCl1);
        tvCl2 = (TextView)findViewById(R.id.tvCl2);
        tvCl0dg = (TextView)findViewById(R.id.tvCl0dg);
        tvCl20dg = (TextView)findViewById(R.id.tvCl20dg);
        tvCl40dg = (TextView)findViewById(R.id.tvCl40dg);
        tvCl60dg = (TextView)findViewById(R.id.tvCl60dg);
        tvCl80dg = (TextView)findViewById(R.id.tvCl80dg);
        tvCl100dg = (TextView)findViewById(R.id.tvCl100dg);
        tvClNext = (TextView)findViewById(R.id.tvClNext);
        tvClGift = (TextView)findViewById(R.id.tvClGift);
        tvClGiftTitle = (TextView)findViewById(R.id.tvClGiftTitle);

        //gift box
        ivGiftBox0 = (ImageView)findViewById(R.id.ivGiftBox0);
        ivGiftBox1 = (ImageView)findViewById(R.id.ivGiftBox1);
        ivGiftBox2 = (ImageView)findViewById(R.id.ivGiftBox2);
        ivGiftBox3 = (ImageView)findViewById(R.id.ivGiftBox3);
        ivGiftBox4 = (ImageView)findViewById(R.id.ivGiftBox4);
        ivGiftBox5 = (ImageView)findViewById(R.id.ivGiftBox5);

        ArrayList al = new ArrayList();
        rlthemeEx = (RelativeLayout)findViewById(R.id.rlthemeEx);
        //gift box 켰다 끄기, 보상 미리보기
        if(totalPoint <= 0){
            ivGiftBox0.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));
            tvClGift.setText("말투");
            tvClGiftTitle.setText("연서복");
            al.add("울 애긔~ㅎ안녕?ㅎ");
            al.add("어빠가 울 애긔 일 좀 도와줄까~ㅎ");
            al.add("오눌도 히믈내요! 자랄쑤이쏘!");
            al.add("넝담~ㅎ");
        }else if (totalPoint < 20){
            tvClGift.setText("배경");
            tvClGiftTitle.setText("나무나무");
            rlthemeEx.setBackground((getResources().getDrawable(R.drawable.bg_pattern_tree)));
            ivGiftBox0.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));

        }else if (totalPoint < 40){
            tvClGift.setText("배경");
            tvClGiftTitle.setText("스트라이프");
            rlthemeEx.setBackground((getResources().getDrawable(R.drawable.pattern_bg_stripe)));
            ivGiftBox0.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));
            ivGiftBox1.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));

        }else if (totalPoint < 60){
            tvClGift.setText("말투");
            tvClGiftTitle.setText("한쿸어 어려훠효");
            ivGiftBox0.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));
            ivGiftBox1.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));
            ivGiftBox2.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));
            al.add("아뇽하세효");
            al.add("이룬 자라고이찌요?");
            al.add("오눌도 히믈내요! 자랄쑤이쏘!");
            al.add("같치 파이팅!!");
        }else if (totalPoint < 80){
            tvClGift.setText("말투");
            tvClGiftTitle.setText("극존칭");
            al.add("안녕하시옵니까");
            al.add("약조들을 잊지 않고 계시온지요?");
            al.add("통촉하여!!주시옵소서!!");
            ivGiftBox0.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));
            ivGiftBox1.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));
            ivGiftBox2.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));
            ivGiftBox3.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));

        }else if (totalPoint < 100){
            ivGiftBox0.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));
            ivGiftBox1.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));
            ivGiftBox2.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));
            ivGiftBox3.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));
            ivGiftBox4.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));
            ivGiftBox5.setImageDrawable(getResources().getDrawable(R.drawable.giftbox_now));

        }


        //다음 보상 보여주는 것 1. 리스트뷰로 말투 보여주기 2. 배경 보여주기
        lvGiftEx = (ListView)findViewById(R.id.lvGiftEx);



        adapter = new NextGiftAdapter (
                getApplicationContext(), // 현재 화면의 제어권자
                R.layout.list_closeness_gift_ex, // 한행을 담당할 Layout
                al); // 데이터

        lvGiftEx.setAdapter(adapter);

        ImageView img = (ImageView) findViewById(R.id.imageView1);
        mImageDrawable = (ClipDrawable) img.getDrawable();
        mImageDrawable.setLevel(0);

        progressBar();
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
        tvCl0dg.setTypeface(NanumSquare_B);
        tvCl20dg.setTypeface(NanumSquare_B);
        tvCl40dg.setTypeface(NanumSquare_B);
        tvCl60dg.setTypeface(NanumSquare_B);
        tvCl80dg.setTypeface(NanumSquare_B);
        tvCl100dg.setTypeface(NanumSquare_B);
        tvClNext.setTypeface(NanumSquare_B);
        tvClGift.setTypeface(NanumBarunGothic_R);
        tvClGiftTitle.setTypeface(NanumBarunGothic_R);
    }


    //progress bar를 위한 함수들

    private void doTheUpAnimation(int fromLevel, int toLevel) {
        mLevel += LEVEL_DIFF;
        mImageDrawable.setLevel(mLevel);
        if (mLevel <= toLevel) {
            mUpHandler.postDelayed(animateUpImage, DELAY);
        } else {
            mUpHandler.removeCallbacks(animateUpImage);
            ClosenessActivity.this.fromLevel = toLevel;
        }
    }

    private void doTheDownAnimation(int fromLevel, int toLevel) {
        mLevel -= LEVEL_DIFF;
        mImageDrawable.setLevel(mLevel);
        if (mLevel >= toLevel) {
            mDownHandler.postDelayed(animateDownImage, DELAY);
        } else {
            mDownHandler.removeCallbacks(animateDownImage);
            ClosenessActivity.this.fromLevel = toLevel;
        }
    }

    public void progressBar() {
        int temp_level = (int)totalPoint*MAX_LEVEL/100;

        if (toLevel == temp_level || temp_level > MAX_LEVEL) {
            return;
        }
        toLevel = (temp_level <= MAX_LEVEL) ? temp_level : toLevel;
        if (toLevel > fromLevel) {
            // cancel previous process first
            mDownHandler.removeCallbacks(animateDownImage);
            ClosenessActivity.this.fromLevel = toLevel;

            mUpHandler.post(animateUpImage);
        } else {
            // cancel previous process first
            mUpHandler.removeCallbacks(animateUpImage);
            ClosenessActivity.this.fromLevel = toLevel;
            mDownHandler.post(animateDownImage);
        }
    }

}