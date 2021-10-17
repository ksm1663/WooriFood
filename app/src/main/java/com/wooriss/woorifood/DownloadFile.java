package com.wooriss.woorifood;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Handler;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;

/*
 - 작성일 : 2021.10.03
 - 작성자 : 김성미
 - 기능 : 금융결제원이 제공하는 금융기관은행코드 파일 다운로드 받아옴
 - 비고 : 1번 연결 재시도 후에도 연결 안될 경우에는 ? (아직 그런적없음)
 - 수정이력 :
    2021.10.04 다운 중 금결원 서버 끊기는 문제 발생 시 최대 1번 연결 재시도하도록 수정  (수정 : 김성미)
*/

// 인자 : <Params, Progress, Result>
// Params : doInBackground 파라미터 타입. execute 메소드 인자 값
// Progress : doInBackground 작업 시 진행 단위 타입으로 onProgressUpdate 파라미터 타입
// Result : doInBackground 리턴 값으로 onPostExecute 파라미터 타
public  class DownloadFile extends AsyncTask<String, Integer, Long> {

    private int total_line = 1;
    private Context mContext;

    // 생성
    public DownloadFile (Context context) {
        mContext = context;
    }


    // 백그라운드 작업 실행 전 onPreExecute() 실행됨
    // 로딩 중 이미지를 띄워놓거나, 스레드 작업 이전에 수행할 동작 구현
    @Override
    protected void onPreExecute() {
        Log.d("디버그", getClass().getName() + " : 1111");
    }

    // doInBackground() : 새로 만든 쓰레드에서 백그라운드 작업 수행. execute() 메소드를 호출할 때 사용된 파라미터 전달 받음
    // 중간 진행상태 UI 업데이트 하려면 publishProgress() 메소드 호출
    // publishProgress() 호출될 때 마다 자동으로 onProgressUpdate() 호출됨
    // doInBackground 작업이 끝나면 onPostExcuted()로 결과 파라미터 리턴, 그 리턴값으 스레드 작업 끝났을 때의 동작 구현
    @Override
    protected Long doInBackground(String... strings) { // 전달된 URL 사용 작업 strings[i]

        // 금융기관 코드 다운로드 경로
        String urlStr = "https://www.kftc.or.kr/common/download1.jsp?filename=codefilex.text&sysfilename=codefilex.text&mode=direct";
        Log.d("디버그", getClass().getName() + " : doInBackground Start");
        int total = 0;

        DBHelper helper = new DBHelper(mContext);
        SQLiteDatabase db = helper.getWritableDatabase();

        Log.d("plz", "isTableCreate : " + helper.isTableCreate());
        // 기존 저장되어 있는 테이블이 있거나, 업데이트 할 필요 없는 경우에는 디비연결 종료 후 onPostExcuted로 이
        if (!helper.isTableCreate()) {
            db.close();
            return null;
        }

        Log.d("plz", getClass().getName() + "Start Download");
        HttpURLConnection conn = null;
        InputStream is ;
        BufferedReader reader ;

        try {
            URL url = new URL (urlStr);
            conn = (HttpURLConnection) url.openConnection();

            // 데이터베이스 insert 속도 개선 위해 트랜잭션 시작
            db.beginTransaction();

            // 연결 요청 확인
            int httpRes = -1;
            try {
                httpRes = conn.getResponseCode();
            } catch (SocketException e) {
                // 다운 중 서버 연결 끊면 재시도
                Log.d("plz", getClass().getName() + "Fail to download, Let's go ReConnect");
                conn.disconnect();
                conn = (HttpURLConnection) url.openConnection();
                httpRes = conn.getResponseCode();
            } finally {
                Log.d("plz", getClass().getName() + "HttpURLConnection : " + httpRes);
                if (httpRes != HttpURLConnection.HTTP_OK)
                    return null;
            }
            // 요청한 URL에서 오는 파일데이터를 BufferedReader 로 받기
            is = conn.getInputStream();
            reader = new BufferedReader(new InputStreamReader (is, "euc-kr"));


            // 파일의 라인 하나씩 받아와서 데이터베이스 넣는 작업 시작
            // 파일 총크기, 첫 라인길이로 전체 라인수를 알아내서 프로그래스바 현황 업데이트시 활용
            String line;
            int flag = 0;
            while ((line=reader.readLine())!=null) {
                if(flag == 0) {
                    int file_size = conn.getContentLength(); // 파일의 총 크기 (21.10.10 기준 5868294)
                    int cnt_line = line.getBytes("euc-kr").length;
                    total_line = file_size/cnt_line;
                    flag = 1;
                }
                total++;

                // sql insert 시작
                helper.insertToTable2(db, line, total_line, total);
                // 프로그래스바 ui 업데이트
                publishProgress(total);
            }
            Log.d("plz", getClass().getName() + " total line count : " + total); // (21.10.10 기준 30309)
            db.setTransactionSuccessful();
            reader.close();

        } catch (IOException e) { // for openConnection()
            e.printStackTrace();
        } finally {
            if (conn !=null)
                conn.disconnect();

            if (db.inTransaction())
                db.endTransaction();
        }
        if (db != null)
            db.close();
        return (long)total;
    }

    // 파일 다운로드 퍼센티지 표시 작업 values[i]
    @Override
    protected void onProgressUpdate(Integer... values) {
        // 프로그래스바에 값 전달 (전체 값, 현재 값)
        ((LoadingActivity)mContext).setProgressBar(total_line, values[0]);
        ((LoadingActivity)mContext).setTextView();
    }

    // AsyncTask 종료 시 호출
    // doInbackground 에서 받아온 total 값 사용 장소
    @Override
    protected void onPostExecute (Long result) {
        Log.d("plz", getClass().getName() + " : result : " + result);
        // 완료된 값 ui 표시
        ((LoadingActivity)mContext).setProgressBar(total_line, total_line);
        ((LoadingActivity)mContext).setTextView();

        // 로딩작업 끝난 후 다음 단계 진행
        if (result == null){ // 다운로드 할 내용이 없었어도 로딩화면 2초 보여주다가 로딩화면 종료
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    //LoadingActivity.loadingActivity.finish(); // 로딩화면종료
                    ((LoadingActivity)mContext).showLoginDialog(null); // 로그인 다이얼로그 호출
                }
            }, 2000);
        } else if (result == 0 ) { // 다운 받았는데 데이터가 0인 경우,, 아직 없음
        } else {
            //LoadingActivity.loadingActivity.finish(); // 로딩화면 종료
            ((LoadingActivity)mContext).showLoginDialog(null); // 로그인 다이얼로그 호출
        }

    }

}


