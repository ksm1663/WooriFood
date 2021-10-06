package com.wooriss.woorifood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dinuscxj.progressbar.CircleProgressBar;

public class LoadingActivity extends Activity {

    public static Activity loadingActivity;

    CircleProgressBar progressBar;
    //    ProgressBar progressBar;
    TextView persent;
    ImageView loadingImg;


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView (R.layout.activity_loading);

        loadingActivity = LoadingActivity.this;

        persent = findViewById(R.id.persentValue); // 코드에서 R.string.id 는 int형임. string으로 가져오려면 아래와 같이! (xml에서는 @string/string_name으로 사용)
        progressBar = findViewById(R.id.progressBar);

        loadingImg = findViewById(R.id.gif_loading);
        Glide.with(this).load(R.drawable.loading).override(50,50).into(loadingImg);


        DownloadFile dlf = new DownloadFile(this);
        dlf.execute();

        //startLoading();
    }


    public void setTextView(String txt) {
        ((TextView)findViewById(R.id.persentValue)).setText(txt);
    }

    public void setProgressBar(int val) {
        double tmp = (double)val / (double) DBHelper.CNT_LINE * 100 ;
        progressBar.setProgress((int)tmp);
    }



    // 로딩 화면 나타난 후 2초 대기
    public void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Intent intent = new Intent (getBaseContext(), MainActivity.class);
                //startActivity(intent);
                finish();
            }
        }, 2000);
    }

}
