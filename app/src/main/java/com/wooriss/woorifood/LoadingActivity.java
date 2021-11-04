package com.wooriss.woorifood;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import com.bumptech.glide.Glide;
import com.dinuscxj.progressbar.CircleProgressBar;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;


/*
 - 작성일 : 2021.10.03
 - 작성자 : 김성미
 - 기능 : 로딩화면, 다운받는 동안의 진행현황을 프로그래스바로 보여줌
         종료 후 MainActivty 호출 (로딩화면은 재사용될 경우 없으므로 destroy)
 - 비고 :
 - 수정이력 :
*/

public class LoadingActivity extends AppCompatActivity {
    private CircleProgressBar progressBar;
    private FirebaseUser u;

    private HashMap<String, String> branch_info;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView (R.layout.activity_loading);


        //FoodLocation f = new FoodLocation(this);

        //persent = findViewById(R.id.persentValue);
        progressBar = findViewById(R.id.progressBar);
        //private TextView persent;

        ImageView loadingImg = findViewById(R.id.gif_loading);


        Animation ani_circle;
        ani_circle = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.ani_loading_scale);
        ImageView loadCircle;
        loadCircle = findViewById(R.id.loading_circle);
        loadCircle.startAnimation(ani_circle);


        // 커스텀 프로그스바 세팅
//        Glide.with(this).load(R.drawable.loading_bee).override(50,50).into(loadingImg);
        Glide.with(this).load(R.drawable.loading_bee).into(loadingImg);


        // 파일다운로드 시작
        DownloadFile dlf = new DownloadFile(this);
        dlf.execute();
    }

    @Override
    protected void onPause() {
        super.onPause();

        // 액티비티 종료할 때 애니메이션 없애기
        overridePendingTransition(0,0);

        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("USER_INFO", u);
        intent.putExtra("BRANCH_INFO", branch_info);
        startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }


    public void setTextView() {
        String tmpPer = progressBar.getProgress() + "%";
        ((TextView)findViewById(R.id.persentValue)).setText(tmpPer);
    }

    public void setProgressBar(int all, int cur) {
        double tmp = (double)cur / (double) all * 100 ;
        progressBar.setProgress((int)tmp);
    }


    // 로그인 다이얼로그 생성
    public void showLoginDialog (View view) {
        new LoginDialog(this);
    }

    public void setUser (FirebaseUser u) {
        this.u = u;
    }

    public void setBranchInfo (HashMap<String, String> _branch_info) {
        this.branch_info = _branch_info;
    }

}
