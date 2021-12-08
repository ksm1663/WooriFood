package com.wooriss.woorifood;

import android.content.Context;
import android.util.Log;
import com.google.gson.JsonObject;

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
    private static final String BASE_URL= "https://dapi.kakao.com/"; // HOST
    private static Retrofit retrofit;
    private static final String API_KEY_KAKAO = "KakaoAK 7aea75b45c61b6eeb0df2f3762c857a1"; //카카오 개발자 사이트에서 발급 받은 키를 REST API 키
    private static final int NEAR_KILOMETER = 4000; // 인근 4KM 이내 식당 검색
    private static final int MAX_LIST_SIZE = 15; // 한 페이지에 보여질 문서 수
//    private static final String QUERY_KAKAO = "김밥천국";


    public static String x;
    public static String y;

    public static String tmpX;
    public static String tmpY;


    private Context context;

    private static String searchKey;

    private SearchFragment searchfFragment;
    private  MainListFragment mainListFragment;


    private static int cur_paging = 1; // 페이징을 위한 현재 페이지 번호
//    public FoodLocation(Context _context, String addr, String _searchKey)  {
//        context = _context;
//        searchKey = _searchKey;
//        getPosition (addr); // 비동기 방식이라 이 안에서 식당 검색을 위한 API 호출
//    }


//    public FoodLocation(SearchFragment _context, String branchAddr) {
//        searchfFragment = _context;
//        getBranchPosition (branchAddr);
//    }

    private int flag_otherBranch = 0;
    public FoodLocation(MainListFragment _context, String branchAddr) {
        mainListFragment = _context;
        getBranchPosition (branchAddr);
    }

    public FoodLocation(MainListFragment _context, String branchAddr, int flag) {
        flag_otherBranch = flag;
        mainListFragment = _context;
        getBranchPosition (branchAddr);
    }

    public void setSearchfFragment (SearchFragment searchfFragment) {
        this.searchfFragment = searchfFragment;
    }

    public void getBranchPosition(String _branchAddr) {
        Log.d("plz", "넘어온 주소(getBranchPosition) : " + _branchAddr);
        RetrofitService addrService = RetrofitFactory.create();

        Call<JsonObject> addrCall = addrService.getAddress(API_KEY_KAKAO, _branchAddr, 1, 1);

        addrCall.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                int firstFlag = 0;
                if (x == null)
                    firstFlag = 1;
                JsonObject j = response.body().getAsJsonArray("documents").get(0).getAsJsonObject();
                x = j.get("x").getAsString();
                y = j.get("y").getAsString();
                Log.d("plz", "지점주소 불러오기 완료! x : " + x + "/ y : " + y);

                if (flag_otherBranch == 0) {
                    tmpX = x;
                    tmpY = y;
                }

                if ((firstFlag == 1) || (flag_otherBranch ==1))
                    mainListFragment.callSetMainList();
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Log.d("plz", "지점 주소 불러오기 실패 ");
            }
        });
    }

    // 구한 좌표로 검색된 식당 리스트 구하기
    public void getSikdangList(String _searchKey) {
        searchKey = _searchKey;
        cur_paging = 1;

        RetrofitService findSikdangService = RetrofitFactory.create();
        Call<PageListSikdang> sikdangCall = findSikdangService.getSikdangList(API_KEY_KAKAO, searchKey,
                tmpX, tmpY, NEAR_KILOMETER, 1, "distance");
        sikdangCall.enqueue(callback);
    }

    // 리사이클뷰 페이징
    public void callItemWithPaging(int total_cnt, int upAndDown) {
        RetrofitService findSikdangService = RetrofitFactory.create();
        Call<PageListSikdang> sikdangCall;

        if ((upAndDown == 0) && (cur_paging > 1)) { // 스르롤 위로 && 현재 페이지가 1보다 큰 경우
//            sikdangCall = findSikdangService.getSikdangList(API_KEY_KAKAO, searchKey,
//                    x, y, NEAR_KILOMETER, --cur_paging, "distance");
//
//            Log.d("plz", "스크롤 젤위! 앞 페이지 불러오기");
//
//            sikdangCall.enqueue(callback);
        }else if (upAndDown == 1) { // 스르콜 아래로
            // 47 / 15 => 3.xx => 3 => 마지막 페이지는 4 => 현재가 4페이지면 그만되어야
            if (((total_cnt / MAX_LIST_SIZE) + 1) >= ++cur_paging) {
                sikdangCall = findSikdangService.getSikdangList(API_KEY_KAKAO, searchKey,
                        x, y, NEAR_KILOMETER, cur_paging, "distance");
                Log.d("plz", "스크롤 제일 밑! 다음 페이지 불러오기");
                sikdangCall.enqueue(callback);
            }
        }
    }


    // Retrofit 내 Response 익명클래스 바깥으로 뺀 버전
    retrofit2.Callback<PageListSikdang> callback = new Callback<PageListSikdang>() {
        @Override
        public void onResponse(Call<PageListSikdang> call, Response<PageListSikdang> response) {
            if (response.isSuccessful()) {
                Log.d("plz", response + "");
                Log.d("plz", "식당 결과 수: " + response.body().getMeta().total_count + "");
                if ((searchfFragment != null) &&(response.body().getMeta().total_count > 0))
                    searchfFragment.setNullResult(false);

                else
                    searchfFragment.setNullResult(true);
                searchfFragment.setSikdangList(response, cur_paging); // 메인으로 결과 보내서 리스트 세팅!

            }
        }
        @Override
        public void onFailure(Call<PageListSikdang> call, Throwable t) {
        }
    };

}
