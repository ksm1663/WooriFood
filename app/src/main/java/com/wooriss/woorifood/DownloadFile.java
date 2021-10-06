package com.wooriss.woorifood;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Debug;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/*
 - 작성일 : 2021.10.03
 - 작성자 : 김성미
 - 기능 : 금융결제원이 제공하는 금융기관은행코드 파일 다운로드 받아와서 내부 데이터베이스 저장
 - 비고 :
 - 수정이력 :
    2021.10.04 ........... (수정자 : xxx)
*/

// 인자 : <Params, Progress, Result>
// Params : doInBackground 파라미터 타입. execute 메소드 인자 값
// Progress : doInBackground 작업 시 진행 단위 타입으로 onProgressUpdate 파라미터 타입
// Result : doInBackground 리턴 값으로 onPostExecute 파라미터 타압
public  class DownloadFile extends AsyncTask<String, Integer, Long> {


    //ProgressDialog progressDialog = new Process(ProgressDialogActivty.this);

    Intent intent = null;
    Context mContext;
    public DownloadFile (Context context) {
        mContext = context;
    }

    // 금융기관 코드 다운로드 경로
    private String urlStr = "https://www.kftc.or.kr/common/download1.jsp?filename=codefilex.text&sysfilename=codefilex.text&mode=direct";

    // 백그라운드 작업 실행 전 onPreExecute() 실행됨
    // 로딩 중 이미지를 띄워놓거나, 스레드 작업 이전에 수행할 동작 구현
    @Override
    protected void onPreExecute() {
        Log.d("디버그", getClass().getName() + " : 1111");
        //super.onPreExecute();

        // 로딩화면 부르기
        //intent = new Intent(mContext, LoadingActivity.class);
        //mContext.startActivity(intent);
    }

    // doInBackground() : 새로 만든 쓰레드에서 백그라운드 작업 수행. execute() 메소드를 호출할 때 사용된 파라미터 전달 받음
    // 중간 진행상태 UI 업데이트 하려면 publishProgress() 메소드 호출
    // publishProgress() 호출될 때 마다 자동으로 onProgressUpdate() 호출됨
    // doInBackground 작업이 끝나면 onPostExcuted()로 결과 파라미터를 리턴하면서 그 리턴값을 통해
    // 스레드 작업이 끝났을 때의 동작 구현
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected Long doInBackground(String... strings) { // 전달된 URL 사용 작업 strings[i]

        Log.d("디버그", getClass().getName() + " : doInBackground 들어옴");
        int total = 0;

        DBHelper helper = new DBHelper(mContext);

        Log.d("디버그", getClass().getName() + " : doInBackground 1111111");
        SQLiteDatabase db = helper.getWritableDatabase();

        //Log.d("디버그", "isTableExist : " + DBHelper.isTableExist(db, DBHelper.TABLE_NAME));
        Log.d("디버그", "isTableCreate : " + helper.isTableCreate());
        if (!helper.isTableCreate()) { // 기존 테이블 사용하는 경우 (최초 X, 업그레이드 X)
            db.close();
            return null;
        }

        Log.d("디버그", getClass().getName() + " 파일이 다운로드");
        HttpURLConnection conn = null;
        InputStream is = null;
        BufferedReader reader = null;

        try {
            URL url = new URL (urlStr);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");


            // 연결 요청 확인
            int httpRes = conn.getResponseCode();
            Log.d("디버그", getClass().getName() + "연결응답: " + httpRes);
            if (httpRes != HttpURLConnection.HTTP_OK)
                return null;

            // 요청한 URL 출력을 BufferedReader 로 받기
            is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader (is, "euc-kr"));




            // 츨력물 라인, 그 합에 대한 변수
            String line;

            // conn.getContentLength(); => 파일의 총 크기!

            // 라인 받아와서 합치기
            db.beginTransaction();
            while ((line=reader.readLine())!=null) {
                //sb.append (line + "\n");
                // 바로 sql 저장 테스트
                helper.insertToTable(db, line);
                total++;
                publishProgress(total);
            }
            Log.d("디버그", getClass().getName() + " 총 라인 수 : " + total);
            db.setTransactionSuccessful();


            reader.close();


        } catch (MalformedURLException e) { // for URL
            e.printStackTrace();
        } catch (IOException e) { // for openConnection()
            e.printStackTrace();
        } finally {
            if (conn !=null)
                conn.disconnect();
            if (db !=null)
                db.endTransaction();
        }

        db.close();
        return (long)total;
    }

    @Override
    protected void onProgressUpdate(Integer... values) { // 파일 다운로드 퍼센티지 표시 작업 values[i]

        //Log.d("디버그", getClass().getName() + " : onProgressUpdate");
        //super.onProgressUpdate(vawlues);

        ((LoadingActivity)mContext).setProgressBar(values[0]);

        ((LoadingActivity)mContext).setTextView(values[0].toString());
        //TextView tv = (TextView)findViewById(R.id.testText);
    }

    @Override
    protected void onPostExecute (Long result) { // doInbackground 에서 받아온 total 값 사용 장소

        ((LoadingActivity)mContext).setProgressBar(DBHelper.CNT_LINE);
        ((LoadingActivity)mContext).setTextView("끝");
        Log.d("디버그", getClass().getName() + " : onPostExecute : " + result);

        if (result == null){
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //Intent intent = new Intent (getBaseContext(), MainActivity.class);
                    //startActivity(intent);
                    LoadingActivity.loadingActivity.finish();
                }
            }, 2000);
        } else
            LoadingActivity.loadingActivity.finish();

    }


}


