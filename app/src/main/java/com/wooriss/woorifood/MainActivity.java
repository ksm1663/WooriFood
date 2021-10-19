package com.wooriss.woorifood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/*
 - 작성일 : 2021.10.03
 - 작성자 : 김성미
 - 기능 : 메인 화면
 - 비고 :
 - 수정이력 :
*/

public class MainActivity extends AppCompatActivity {

    private TextView mainText;

    private FirebaseUser user;
    private FirebaseDatabase mDatabase;

    private User u;


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
        mDatabase.getReference().child("users").child(user.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                u = dataSnapshot.getValue(User.class);
                if (u==null)
                    Log.d("plz", "There is no user data");
                else {
                    mainText.setText(u.getBranch());
                    //for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //    Log.d("MainActivity", "Single ValueEventListener : " + snapshot.getValue());
                    //}
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("plz", "onCancelled of addListenerForSingleValueEvent");
            }
        });

/*        // java.lang.Exception: Client is offline 가 자주 발생하네...
        mDatabase.getReference().child("users").child(user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d("plz", "Error getting data", task.getException());
                }
                else {
                    u = task.getResult().getValue(User.class);
                    mainText.setText(u.getBranch());
                }
            }
        });*/


    }


    // 화면 구성요소 정의
    private void findViews() {
        mainText = findViewById(R.id.mainText);

        mDatabase = FirebaseDatabase.getInstance();

        Intent intent = getIntent();
        user = intent.getParcelableExtra("USER_INFO");
    }


}
