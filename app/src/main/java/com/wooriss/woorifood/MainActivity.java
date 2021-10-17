package com.wooriss.woorifood;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

/*
 - 작성일 : 2021.10.03
 - 작성자 : 김성미
 - 기능 : 메인 화면
 - 비고 :
 - 수정이력 :
*/

public class MainActivity extends AppCompatActivity {

    private TextView mainText;
    private Context loginContext;

    private String id;

    FirebaseAuth auth;
    FirebaseUser user;
    FirebaseDatabase fdb;
    DatabaseReference dbr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        mainText = findViewById(R.id.mainText);
        mainText.setText(id);



        //auth = FirebaseAuth.getInstance();
        //auth.getCurrentUser();


        //user = auth.getCurrentUser();


        dbr = FirebaseDatabase.getInstance().getReference();//fdb.getReference();
        //String tmp  = fdb.getReference().child("data-sikdang").child("039230").child("name").se;

        dbr.child("data-sikdang").child("039230").child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    String tmp = String.valueOf(task.getResult().getValue());
                    mainText.setText(tmp);

                }
            }
        });

/*
        if(user!=null) {
            Log.d("plz", "원래 있던애!!!");

        } else {
            auth.signInAnonymously().addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {

                        user = task.getResult().getUser();
                        Log.d("plz", "성공! : " + user.getUid());

                    } else {
                        Log.d("plz", "실패!!!");
                    }
                }
            });
        }
        */

    }


}
