package com.wooriss.woorifood;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;

/*
*
* REST API : 7aea75b45c61b6eeb0df2f3762c857a1
*
* */

/*
<키워드로 장소 검색 (/v2/local/search/keyword.{format})>
category_group_code : 카테고리 그룹 코드
                        (MT1=대형마트 / CS2=편의점 / PS3=어린이집, 유치원 / SC4=학교 / AC5=학원 /
                         PK6=주차장 / OL7=주유소, 충전소 / SW8=지하철역 / BK9=은행 / CT1=문화시설 /
                         AG2=중개업소 / PO3=공공기관 / AT4=관광명소 / AD5=숙박 / FD6=음식점 /
                         CE7=카페 / HP8=병원 / PM9=약국), 결과를 카테고리로 필터링을 원하는 경우 사용
x, y                : 중심 좌표의 X값 혹은 longitude, 특정 지역을 중심으로 검색하려고 할 경우 radius와 함께 사용 가능
radius              : 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 중심좌표로 쓰일 x,y와 함께 사용. 단위 meter. 0~20000 사이의 값
page                : 결과 페이지 번호, 1-45 사이, 기본 값 1
size                : 한 페이지에 보여질 문서의 개수, 1~15 사이, 기본 값 15
sort                : 결과 정렬 순서, distance 정렬을 원할 때는 기준 좌표로 쓰일 x, y와 함께 사용, distance 또는 accuracy, 기본 accuracy

* */

public class FoodLocation {


    //////////////////////////////////////////////////////////////////////////////////////
    private static final String BASE_URL= "https://dapi.kakao.com/"; // HOST
    private static Retrofit retrofit;
    private static final String API_KEY_KAKAO = "KakaoAK 7aea75b45c61b6eeb0df2f3762c857a1"; //카카오 개발자 사이트에서 발급 받은 키를 REST API 키
//    private static final String QUERY_KAKAO = "김밥천국";


    private String x;
    private String y;
    private Context context;

    private String searchKey;

    private MainFragment mainfFragment;

//    public FoodLocation(Context _context, String addr, String _searchKey)  {
//        context = _context;
//        searchKey = _searchKey;
//        getPosition (addr); // 비동기 방식이라 이 안에서 식당 검색을 위한 API 호출
//    }

    public FoodLocation(MainFragment _context, String addr, String _searchKey)  {
        mainfFragment = _context;
        searchKey = _searchKey;
        getPosition (addr); // 비동기 방식이라 이 안에서 식당 검색을 위한 API 호출
    }


    // 넘어온 한글 주소로 x, y 좌표 확인
    private void getPosition(String addr) {
        Log.d("plz", "넘어온 주소 : " + addr);
        RetrofitService addrService = RetrofitFactory.create();

        Call<JsonObject> addrCall = addrService.getAddress(API_KEY_KAKAO, addr, 1, 1);

        addrCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                JsonObject j = response.body().getAsJsonArray("documents").get(0).getAsJsonObject();
                x = j.get("x").getAsString();
                y = j.get("y").getAsString();
                Log.d("plz", "x : " + x + "/ y : " + y);

                getSikdangList();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
            }
        });
    }


    // 구한 좌표로 검색된 식당 리스트 구하기
    private void getSikdangList() {
        RetrofitService findSikdangService = RetrofitFactory.create();
        Call<PageListSikdang> sikdangCall = findSikdangService.getSikdangList(API_KEY_KAKAO, searchKey,
                x, y, 5000, 1, "distance");

        sikdangCall.enqueue(new Callback<PageListSikdang>() {
            // 매개변수 call은 다음 페이지 호출 있을 때를 대비한 것. 반복 호출 시 해당 함수 바깥으로 빼면 더 깔끔하겠지!
            @Override
            public void onResponse(Call<PageListSikdang> call, Response<PageListSikdang> response) {
                Log.d("plz", response + "");
                Log.d("plz", "식당 결과 : " + response.body() + "");
//                ((MainActivity)context).setSikdangList(response); // 메인으로 결과 보내서 리스트 세팅!
                mainfFragment.setSikdangList(response); // 메인으로 결과 보내서 리스트 세팅!

                // MyAdapter adapter = new MyAdapter(response.body().articles);
                // recyclerView.setAdapter(adapter)
            }

            @Override
            public void onFailure(Call<PageListSikdang> call, Throwable t) {
            }
        });
    }


    // Retrofit 내 Response 익명클래스 바깥으로 뺀 버전
    retrofit2.Callback<PageListSikdang> callback = new Callback<PageListSikdang>() {
        @Override
        public void onResponse(Call<PageListSikdang> call, Response<PageListSikdang> response) {
            if (response.isSuccessful()) {
                Log.d("plz", "들어왔다 성공 1111111");
                Log.d("plz", response.body().getDocuments() + "");
                // enqueue 는 비동기실행!
            }
        }

        @Override
        public void onFailure(Call<PageListSikdang> call, Throwable t) {

        }
    };

}
