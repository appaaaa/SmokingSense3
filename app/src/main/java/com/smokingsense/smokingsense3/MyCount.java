package com.smokingsense.smokingsense3;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mikhaellopez.circularimageview.CircularImageView;

import org.w3c.dom.Text;

public class MyCount extends AppCompatActivity {

    private TextView todayCountTextView;
    private Button plusButton;
    private Button minusButton;
    private TextView totalCountTextView;
    private TextView totalexpenseTextView;
    private TextView totalLifeDecreaseTextView;

    private Integer countToday;
    private Integer countAll;
    private Integer countMoney;
    private Integer countLife;
    private LinearLayout background;

    String mUserName;
    String mUserImage;

    CircularImageView mCircularImageView;
    TextView mUserNameTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_count);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        mUserName = intent.getStringExtra("username");
        mUserImage = intent.getStringExtra("userimage");


        mCircularImageView = (CircularImageView)findViewById(R.id.user_imageView2);
        mUserNameTextView = (TextView)findViewById(R.id.login_and_username2);

        Glide.with(this)
                .load(mUserImage)
                .into(mCircularImageView);

        mUserNameTextView.setText(mUserName);

        Toast.makeText(this, mUserName + "님의 흡연정보", Toast.LENGTH_SHORT).show();
        Log.v("get Image URL : ", mUserImage.toString());


        background = (LinearLayout)findViewById(R.id.activity_main);
        background.setAlpha((float)0.9);
        todayCountTextView = (TextView)findViewById(R.id.textview_today_count);
        plusButton = (Button)findViewById(R.id.button_plus);
        minusButton = (Button)findViewById(R.id.button_minus);

        totalCountTextView = (TextView)findViewById(R.id.textview_total_count);
        totalexpenseTextView = (TextView)findViewById(R.id.textview_total_expense);
        totalLifeDecreaseTextView = (TextView)findViewById(R.id.textview_total_life_decrease);

        countToday = Integer.parseInt(todayCountTextView.getText().toString());
        countAll = countToday;
        countMoney = countAll * 225;
        countLife = countAll * 330;

        totalCountTextView.setText(countAll.toString()+" 개피");
        totalexpenseTextView.setText(countMoney.toString()+ "원");
        totalLifeDecreaseTextView.setText(timeCount(countLife));

        plusButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                countToday++;

                todayCountTextView.setText(countToday.toString());
                countAll = countToday;
                countMoney = countAll * 225;
                countLife = countAll * 330;

                totalCountTextView.setText(countAll.toString()+" 개피");
                totalexpenseTextView.setText(countMoney.toString()+ "원");
                totalLifeDecreaseTextView.setText(timeCount(countLife));
            }
        });

        minusButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if(countToday>0)
                    countToday--;

                todayCountTextView.setText(countToday.toString());
                countAll = countToday;
                countMoney = countAll * 225;
                countLife = countAll * 330;

                totalCountTextView.setText(countAll.toString()+" 개피");
                totalexpenseTextView.setText(countMoney.toString()+ "원");
                totalLifeDecreaseTextView.setText(timeCount(countLife));
            }
        });

    }

    private String timeCount(Integer time){
        Integer day = time/(60*60*24);
        Integer hour = (time-day*60*60*24)/(60*60);
        Integer minute = (time-day*60*60*24-hour*3600)/60;
        Integer second = time%60;


        String s = day + "일 " + hour + "시간 " + minute + "분 " + second + "초";

        return s;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
