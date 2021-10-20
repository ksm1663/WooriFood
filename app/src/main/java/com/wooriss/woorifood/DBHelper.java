package com.wooriss.woorifood;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 - 작성일 : 2021.10.03
 - 작성자 : 김성미
 - 기능 : 다운받은 금융기관 코드 파일을 데이터베이스 (sql) 저장
 - 비고 : LoadingActivity 가 호출
 - 수정이력 :
*/

public class DBHelper extends SQLiteOpenHelper {


    public static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "CODE.DB";
    public static final String TABLE_NAME = "TB_CODE";
    private static boolean isTableCreate = false;


    private static final String SQL_CREATE_ENTRIES =
            "create table " + TABLE_NAME + " " +
                    "(code text primary key, " + // 금융기관 고유 코드
                    "name text," + // 금융기관명
                    "branch_name text," + // 금융기관지점명
                    "tel text," + // 전화번호
                    "fax text," + // 팩스
                    "zip text," + // 우편번호
                    "addr text)"; // 주소

    private static final String SQL_DELETE_ENTRIES =
            "drop table if exists " +TABLE_NAME;


    public DBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); // 두번째 인자는 DB 파일명
    }

    // 앱 설치 후 SQLiteOpenHelper가 최초로 이용되는 순간 한 번 호출출
    // 전체 앱 내에서 가장 처음 한 번만 수행하면 되는 코드 작성 (보통 테이블 생성하는 코드 작성)
    // 만약 테이블 생성 잘못해서 수정해도 한번만 호출되므로 수정부분 반영안됨
    @Override
    public void onCreate(SQLiteDatabase db) {
        isTableCreate = true;
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    // 데이터베이스 버전이 변경될 때만 호출
    // 본 클래스 생성자에 전달되는 버전 변경될 때마다 호출, 테이블 스키마 변경하기 위한 용도
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("plz", " curVer : " + oldVersion + " , newVer : " + newVersion);
        Log.d("plz", " drop table start!! ");
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    // 테이블에 데이터 insert
    // 한 줄 씩 바로 insert 하는 방식 (현재 미사용)
    /*

    public void insertToTable (SQLiteDatabase db, String str) {
        String strTmp;
        String[] splitTmp;

        splitTmp = str.split("\\|");

        db.execSQL("insert into " + DBHelper.TABLE_NAME + " (code, name, branch_name, tel, fax, zip, addr) values (?,?,?,?,?,?,?)",
                splitTmp);
    }
    */

    // 테이블에 데이터 insert
    // 500 라인 씩 받아서 union 사용하여 한번에 insert 방식 (더 빠르다고 해서 해봄.. 빠른거 같기도하고...똑같은거 같기도..)
    private StringBuilder sql = null;
    public void insertToTable2 (SQLiteDatabase db, String str, int cur, boolean isLast) {
        if (isLast == false) {
            String [] splitTmp = str.split("\\|");
            if (cur == 1) {
                sql = new StringBuilder();
                sql.append("INSERT INTO " + DBHelper.TABLE_NAME +
                                        " (code, name, branch_name, tel, fax, zip, addr) ");
            }


            if (cur % 500 == 0) {
                db.execSQL(sql.toString().replaceFirst("UNION ", ""));
                sql.setLength(0);
                sql.append("INSERT INTO " + DBHelper.TABLE_NAME +
                        " (code, name, branch_name, tel, fax, zip, addr) ");
            }

            StringBuilder strTmp = new StringBuilder();
            for (String tmp : splitTmp) {
                strTmp.append(" '").append(tmp).append("',");
            }
            strTmp = new StringBuilder(strTmp.substring(0, strTmp.length() - 1));
            sql.append("UNION SELECT ").append(strTmp);
        } else
        {
            db.execSQL(sql.toString().replaceFirst("UNION ", ""));
            sql.setLength(0);
            sql.append("INSERT INTO " + DBHelper.TABLE_NAME +
                    " (code, name, branch_name, tel, fax, zip, addr) ");
        }
    }


    public boolean isTableCreate () { return isTableCreate; }
}
