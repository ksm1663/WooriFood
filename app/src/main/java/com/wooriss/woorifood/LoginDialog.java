package com.wooriss.woorifood;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import androidx.transition.Slide;
import androidx.transition.Transition;
import androidx.transition.TransitionManager;

import java.util.Arrays;

/*
 - 작성일 : 2021.10.12
 - 작성자 : 김성미
 - 기능 : 로그인을 위한 사용자정의 다이얼로그 (LoadingActivty 애서 생성)
 - 비고 :
 - 수정이력 :
*/
public class LoginDialog extends Dialog{

    private final Context context;

    private LinearLayout layerMain;
    private LinearLayout layerBranch;
    private LinearLayout layerLoginJoin;
    private LinearLayout layerRealJoin;

    private EditText editId;
    private EditText editPw;
    private EditText editBranch;

    private Spinner spinDomain;

    private Button btnLogin;
    private Button btnJoin;
    private Button btnRealJoin;
    private Button btnBack;

    private FirebaseAuth mAuth ; // 인증기능을 가지고 있는 객
    private FirebaseDatabase mDatabase;


    public LoginDialog(@NonNull Context context) {
        super(context);
        this.context = context; // LoadingActivity
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        show();
    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_login);
        // 화면 정의
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        findViews();

        addListenerToLoginBtn();
        addListenerToJoinBtn();
        addListenerToRealJoinBtn();
        addListenerToBackBtn();
        addListenerToBranchEdit();

        mAuth = FirebaseAuth.getInstance(); // 인증기능 수행할 일
        mDatabase = FirebaseDatabase.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        // 활동 초기화할 때 유저가 이미 로그인 되어 있는지 확인
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null)  { // 이미 가입된 회원일 때 처리 => 아이디만 미리 세팅해주기
            String mail = currentUser.getEmail();
            String[] tmp = mail.split("@");

            Log.d("plz", getClass().getName() + " 기인증 회원 (Uid): " + tmp[0] + "/" + tmp[1] );
            editId.setText(tmp[0]);
            spinDomain.setSelection(Arrays.asList(context.getResources().getStringArray(R.array.domains)).indexOf("@" + tmp[1]));
       }

    }

    // 화면 구성요소 정의
    private void findViews() {
        btnLogin = findViewById(R.id.btn_login);
        btnJoin = findViewById(R.id.btn_join);
        btnRealJoin = findViewById(R.id.btn_realJoin);
        btnBack = findViewById(R.id.btn_back);

        editId = findViewById(R.id.text_id);
        editPw = findViewById(R.id.text_pw);
        editBranch = findViewById(R.id.text_branch);

        layerBranch = findViewById(R.id.layer_branch);
        layerLoginJoin = findViewById(R.id.layer_loginJoin);
        layerMain = findViewById(R.id.layer_main);
        layerRealJoin = findViewById(R.id.layer_realJoin);

        spinDomain = findViewById(R.id.spin_domain);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, context.getResources().getStringArray(R.array.domains)
        );
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinDomain.setAdapter(adapter);
    }

    // 로그인 버튼 이벤트 정의
    private void addListenerToLoginBtn() {
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = getEmail();
                String pw = editPw.getText().toString();

                Log.d("plz", "id : " + mail + " / pw : " + pw);

                if (mail.length() == 0 || pw.length() == 0) {
                    Toast.makeText(context, "아이디/비밀번호를 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                mAuth.signInWithEmailAndPassword(mail, pw)
                        .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {   // 로그인 성공
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    Toast.makeText((Activity) context, "!!반갑습니다!!", Toast.LENGTH_SHORT).show();
                                    showMainActivity(user);

                                } else {                    // 로그인 실패
                                    // If sign in fails, display a message to the user.
                                    if(task.getException()==null) {
                                        Toast.makeText((Activity) context, "로그인 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Log.d("plz", " 로그인 실패 : " + task.getException());
                                    if (task.getException().toString().contains("FirebaseAuthInvalidUserException"))
                                        Toast.makeText((Activity) context, "존재하지 않는 사용자입니다.", Toast.LENGTH_SHORT).show();
                                    else if (task.getException().toString().contains("FirebaseAuthInvalidCredentialsException"))
                                        Toast.makeText((Activity) context, "비밀번호가 틀렸습니다.", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText((Activity) context, "로그인 실패. 다시 시도해주세요.", Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

                //Toast.makeText(context, "로그인 성공 : " + id.getText().toString(), Toast.LENGTH_LONG).show();

            }
        });
    }

    // 조인(회원가입) 버튼 이벤트 정의
    private void addListenerToJoinBtn() {
        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cleanView ();
                // 다이얼로그 화면 조정
                Transition transition = new Slide(Gravity.BOTTOM);
                transition.setDuration(600);
                transition.addTarget(layerBranch);
                transition.addTarget(layerRealJoin);

                TransitionManager.beginDelayedTransition(layerMain, transition);

                layerBranch.setVisibility(View.VISIBLE);

                layerRealJoin.setVisibility(View.VISIBLE);
                layerLoginJoin.setVisibility(View.GONE);


            }
        });
    }

    // 신규등록 -> 등록 버튼 이벤트 정의 (실제 신규등록 처리)
    private void addListenerToRealJoinBtn() {
        btnRealJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String mail = getEmail();
                String pw = editPw.getText().toString();
                String branch = editBranch.getText().toString();

                if (mail.length() == 0 || pw.length() <= 6 || branch.length() == 0) {
                    Toast.makeText(context, "아이디/비밀번호/지점을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }

                mAuth.createUserWithEmailAndPassword(mail, pw)
                        .addOnCompleteListener((Activity) context, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {      // 계정 생성 성공
                                    // Sign in success, update UI with the signed-in user's information
                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // 계정 정보 DB에 저장
                                    String uid = user.getUid();
                                    User u = new User(uid, mail, branch);
                                    mDatabase.getReference().child("users").child(uid).setValue(u);

                                    Log.d("plz", "계정 생성 성공!");
                                    Toast.makeText(context, "생성 완료! 반갑습니다!!", Toast.LENGTH_SHORT).show();
                                    showMainActivity (user);

                                } else {                        // 계정 생성 실패
                                    // If sign in fails, display a message to the user.
                                    if(task.getException()==null) {
                                        Toast.makeText(context, "신규등록 실패! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();
                                        return;
                                    }
                                    Log.d("plz", "계정 생성 실패!", task.getException());
                                    if (task.getException().toString().contains("FirebaseAuthUserCollisionException"))
                                        Toast.makeText(context, "이미 존재하는 회원입니다!!", Toast.LENGTH_SHORT).show();
                                    else
                                        Toast.makeText(context, "신규등록 실패! 다시 시도해주세요.", Toast.LENGTH_SHORT).show();

                                    //showMainActivity (null);
                                    //updateUI(null);
                                }
                            }
                        });
            }
        });
    }

    // 신규등록 -> 뒤로 버튼 이벤트 정의
    private void addListenerToBackBtn() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                cleanView ();

                // 다이얼로그 화면 재조정
                layerRealJoin.setVisibility(View.GONE);

                Transition transition = new Slide(Gravity.BOTTOM);
                transition.setDuration(600);
                transition.addTarget(layerLoginJoin);
                TransitionManager.beginDelayedTransition(layerMain, transition);

                layerLoginJoin.setVisibility(View.VISIBLE);
                layerBranch.setVisibility(View.GONE);

            }
        });
    }

    // 브랜치 텍스트 박스 눌렀을 때 이벤트 정의
    private void addListenerToBranchEdit() {
        editBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("plz", "뭐지뭐지");
                new SearchBranchDialog(context, new SearchBranchDialog.ICustomDialogEventListener() {
                    @Override
                    public void customDialogEvent(String test1, String test2) {
                        // Do something with the value here, e.g. set a variable in the calling activity
                        editBranch.setText(test1);
                    }
                });
            }
        });
        /*
        editBranch.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if (b) {
                    Log.d("plz", "뭐지뭐지");
                    new SearchBranchDialog(context, new SearchBranchDialog.ICustomDialogEventListener() {
                        @Override
                        public void customDialogEvent(String test1, String test2) {
                            // Do something with the value here, e.g. set a variable in the calling activity
                            editBranch.setText(test1);
                        }
                    });
                }
            }
        });*/
    }

    private void showMainActivity (FirebaseUser user) {

        ((LoadingActivity)context).setUser(user);

        ((LoadingActivity) context).finish(); // 로딩화면종료

        dismiss();
    }


    private String getEmail() {
        return editId.getText().toString() + spinDomain.getSelectedItem().toString();
    }

    private void cleanView () {
        editBranch.setText("");
        editPw.setText("");
        editId.setText("");
    }



}
