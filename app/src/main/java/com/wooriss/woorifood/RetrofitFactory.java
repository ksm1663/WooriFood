package com.wooriss.woorifood;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

// 라이브러리가 구현해놓은 인터페이스에 대한 클래스를 자동 구현해줌 ! Retrofit!
// 인터페이스를 주면 모듈이 클래스 구현해줌.
public class RetrofitFactory {
    private static String BASE_URL_KAKAO = "https://dapi.kakao.com";

    public static RetrofitService create() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL_KAKAO)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(RetrofitService.class);
    }

}
