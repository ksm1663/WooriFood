package com.wooriss.woorifood;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;


/*
 - 작성일 : 2021.10.12
 - 작성자 : 김성미
 - 기능 : 로그인을 위한 사용자정의 다이얼로그
 - 비고 :
 - 수정이력 :
*/
public class LoginDialog extends Dialog {
    private Context context;
    public TextView id;
    private Button btnLogin;

    public LoginDialog(@NonNull Context context) {
        super(context);
        this.context = context;

        setCanceledOnTouchOutside(false);
        setCancelable(false);

        show();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);

        btnLogin = findViewById(R.id.btn_login);
        id = findViewById(R.id.text_id);


        btnLogin.setOnClickListener(new View.OnClickListener() {

            // 로그인 버튼 눌렀을 때 이벤트
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "로그인 성공 : " + id.getText().toString(), Toast.LENGTH_LONG).show();

                LoadingActivity.loadingActivity.finish(); // 로딩화면종료
                dismiss();

            }
        });
    }









}
