package com.wooriss.woorifood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;


/*
 - 작성일 : 2021.10.03
 - 작성자 : 김성미
 - 기능 : 메인 화면
 - 비고 :
 - 수정이력 :
*/
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private FragmentManager fManager; //androidx.fragment.app.FragmentManager
    private MainFragment mainFragment;
    private Button btnMain;


    private FirebaseUser user;
    private FirebaseDatabase mDatabase;
    private Branch userBracch;

    private SQLiteDatabase sqlDb;

    private User u;

    private HashMap<String, String> branch_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.ani_fadein,R.anim.ani_fadeout);
        setContentView(R.layout.activity_main);

        findViews();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // 데이터베이스 읽기  - 한번 읽기 (로드된 후 변경되지 않거나 능동적으로 수신대기할 필요 없는 경우 사용)
        // firebase 에서 로그인 한 유저 정보 가져오기
        mDatabase.getReference().child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                u = dataSnapshot.getValue(User.class);
                if (u==null)
                    Log.d("plz", "There is no user data");
                else {
                    Cursor c = getBranchInfo();
                    c.moveToNext();
                    String code = c.getString(c.getColumnIndexOrThrow("code")).replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");
                    String name = c.getString(c.getColumnIndexOrThrow("branch_name")).replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");
                    String addr = c.getString(c.getColumnIndexOrThrow("addr")).replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");

                    userBracch = new Branch (code, name, addr);

                    c.close();

                    Log.d("plz", "찾은 주소 : " + userBracch.getAddr());

                    btnMain.performClick();
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("plz", "onCancelled of addListenerForSingleValueEvent");
            }
        });
    }



    // 화면 구성요소 정의
    private void findViews() {

        btnMain = findViewById(R.id.btn_main);
        btnMain.setOnClickListener(this);

        fManager = getSupportFragmentManager();
        mainFragment = new MainFragment();


        mDatabase = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        user = intent.getParcelableExtra("USER_INFO");
        branch_info = (HashMap<String, String>) intent.getSerializableExtra("BRANCH_INFO");

        Log.d("plz", user.getEmail());

        if (branch_info == null) {
            Log.d("plz", "넘어온 지점 정보 없어서 디비에서 검색 필요 ");
        }
        else {
            Log.d("plz", branch_info.get("addr"));
        }
    }

    @Override
    public void onClick(View v) {
        //
        if (v == btnMain) {
            if (!mainFragment.isVisible()) {
                FragmentTransaction tf = fManager.beginTransaction();
                tf.replace(R.id.main_container, mainFragment);
                tf.commit();
            }
        }
    }



    // 유저의 소속 지점정보 찾기
    private Cursor getBranchInfo() {
        sqlDb = new DBHelper(this).getReadableDatabase();
        Log.d("plz", "getBranch :  : " + u.getBranch());
        String query = "Select * from " + DBHelper.TABLE_NAME + " " +
                "where branch_name like \'" + u.getBranch() + "%\' and name like \"우리%\"" + " and (code like \"020%\" or code like \"20%\")";
        Log.d("plz", query);
        return sqlDb.rawQuery(query, null);
    }


    @Override
    protected void onDestroy() {
        sqlDb.close();
        super.onDestroy();
    }

    public Branch getBranch() {
        return userBracch;
    }

    public User getUser() {
        return u;
    }


}
