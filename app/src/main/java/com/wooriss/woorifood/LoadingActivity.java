package com.wooriss.woorifood;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.dinuscxj.progressbar.CircleProgressBar;

/*
 - 작성일 : 2021.10.03
 - 작성자 : 김성미
 - 기능 : 로딩화면, 다운받는 동안의 진행현황을 프로그래스바로 보여줌
 - 비고 : 종료 후 MainActivty 호출 (로딩화면은 재사용될 경우 없으므로 destroy)
 - 수정이력 :
*/

public class LoadingActivity extends AppCompatActivity {

    public static Activity loadingActivity;

    CircleProgressBar progressBar;
    TextView persent;
    ImageView loadingImg;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView (R.layout.activity_loading);

        loadingActivity = LoadingActivity.this;

        persent = findViewById(R.id.persentValue);
        progressBar = findViewById(R.id.progressBar);
        loadingImg = findViewById(R.id.gif_loading);

        // 커스텀 프로그스바 세팅
        Glide.with(this).load(R.drawable.loading).override(50,50).into(loadingImg);

        // 파일다운로드 시작
       DownloadFile dlf = new DownloadFile(this);
        dlf.execute();

    }


    @Override
    protected void onPause() {
        super.onPause();
        // 액티비티 종료할 때 애니메이션 없애기
        overridePendingTransition(0,0);

        // 액티비티 불러올 때 애니메이션 없애기
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);

        // 메인 액티비티 호출
        startActivity(intent);

    }


    public void setTextView(String txt) {
        ((TextView)findViewById(R.id.persentValue)).setText(txt);
    }

    public void setProgressBar(int all, int cur) {
        double tmp = (double)cur / (double) all * 100 ;
        progressBar.setProgress((int)tmp);
    }

}
