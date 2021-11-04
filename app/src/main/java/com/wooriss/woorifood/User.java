package com.wooriss.woorifood;

import android.os.Parcelable;

/*
 - 작성일 : 2021.10.19
 - 작성자 : 김성미
 - 기능 : 사용자 정보 클래스
 - 비고 :
 - 수정이력 :
*/
public class User  {
    private String uid;
    private String userId;
    private String branch;

    public  User() {}

    public User(String uid, String userId, String branch) {
        this.uid = uid;
        this.userId = userId;
        this.branch = branch;
    }

    public String getUid() {
        return uid;
    }

    public String getUserId() {
        return userId;
    }

    public String getBranch() { return branch;
    }
}
