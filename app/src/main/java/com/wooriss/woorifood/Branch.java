package com.wooriss.woorifood;

import android.widget.BaseAdapter;

import java.security.PrivateKey;

public class Branch {
    private String code;
    private String name;
    private String addr;

    public  Branch(){}

    public Branch (String code, String name, String addr) {
        this.code = code;
        this.name = name;
        this.addr = addr;
    }

    public String getAddr() {
        return addr;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

}
