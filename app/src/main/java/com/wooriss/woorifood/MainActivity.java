package com.wooriss.woorifood;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.dinuscxj.progressbar.CircleProgressBar;


public class MainActivity extends AppCompatActivity {

    CircleProgressBar progressBar;
//    ProgressBar progressBar;
    TextView persent;
    ImageView loadingImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 로딩화면 호출
        Intent intent = new Intent(this, LoadingActivity.class);
        startActivity(intent);

   //     persent = findViewById(R.id.persentValue); // 코드에서 R.string.id 는 int형임. string으로 가져오려면 아래와 같이! (xml에서는 @string/string_name으로 사용)
   //     progressBar = findViewById(R.id.progressBar);

   //     loadingImg = findViewById(R.id.gif_loading);
    //    Glide.with(this).load(R.drawable.loading).override(50,50).into(loadingImg);
//        GlideDrawableImageViewTarget gifImg = new GlideDrawableImageViewTarget(loadingImg);
//        Glide.with(this).load(R.drawable.loading).into(gifImg);


//        DownloadFile dlf = new DownloadFile(this);
//        dlf.execute();



        //Toast.makeText(getApplicationContext(), "다운로드가 완료됨!!뀨뀨뀨", Toast.LENGTH_LONG).show();

    }
/*
    public void setTextView(String txt) {
        ((TextView)findViewById(R.id.persentValue)).setText(txt);
    }

    public void setProgressBar(int val) {
        double tmp = (double)val / (double) DBHelper.CNT_LINE * 100 ;
        progressBar.setProgress((int)tmp);
    }*/


}
