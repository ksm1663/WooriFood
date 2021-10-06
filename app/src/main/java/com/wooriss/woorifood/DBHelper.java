package com.wooriss.woorifood;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 44;
    private static final String DATABASE_NAME = "CODE.DB";
    public static final String TABLE_NAME = "TB_CODE";

    public static final int CNT_LINE = 30300; // 하드코딩 ㅠㅠ !!! (로딩 프로그래스바용)
    private boolean isTableCreate = false;


    private static final String SQL_CREATE_ENTRIES =
            "create table " + TABLE_NAME + " " +
                    //"(_id integer primary key autoincrement," +
                    "(code integer primary key, " + // 금융기관 고유 코드
                    "name text," + // 금융기관명
                    "branch_name text," + // 금융기관지점명
                    "tel text," + // 전화번호
                    "fax text," + // 팩스
                    "zip text," + // 우편번호
                    "addr text)"; // 주소

    private static final String SQL_DELETE_ENTRIES =
            "drop table if exists " +TABLE_NAME;



//    Context mContext;
    public DBHelper (Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION); // 두번째 인자는 DB파일명
        Log.d("디버그", " DBHelper 생성자 호출");
//        mContext = context;
    }

    // 앱 설치 후 SQLiteOpenHelper가 최초로 이용되는 순간 한 번 호출출
    // 전체 앱 내에서 가장 처음 한 번만 수행하면 되는 코드 작성 (보통 테이블 생성하는 코드 작성)
    // 만약 테이블 생성 잘못해서 수정해도 한번만 호출되므로 수정부분 반영안됨
    @Override
    public void onCreate(SQLiteDatabase db) {
        isTableCreate = true;
        Log.d("디버그", getClass().getName() + " 테이블 생성");
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    // 데이터베이스 버전이 변경될 때마다 호출
    // 당 클래서 생성자에 전달되는 버전 변경될 때마다 호출, 테이블 스키마 변경하기 위한 용도
    // 버전 변경되지 않으면 미호
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("디버그", " 현재 버전 : " + oldVersion + " / 뉴 버전 : " + newVersion);
        Log.d("디버그", " 테이블 지우고 다시 만들기");
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }

    // 데이터 입력
    public void insertToTable (SQLiteDatabase db, String str) {
        String strTmp;
        String[] splitTmp;

        splitTmp = str.split("\\|");

        db.execSQL("insert into " + DBHelper.TABLE_NAME + " (code, name, branch_name, tel, fax, zip, addr) values (?,?,?,?,?,?,?)",
                splitTmp);

    }


    // 테이블 있는지 여부 확인
    public static boolean isTableExist (SQLiteDatabase db, String tableName) {
        boolean b = false;
        Cursor csr = db.query("sqlite_master", null, "name=? and type='table'", new String[]{tableName},null,null,null);
        Log.d("디버그", "테이블 수 : " + csr.getCount());
        if (csr.getCount() > 0 )
            b = true;
        csr.close();
        return b;
    }

    public boolean isTableCreate () {
        return isTableCreate;
    }
}
