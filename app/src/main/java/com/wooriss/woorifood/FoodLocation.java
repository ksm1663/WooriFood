package com.wooriss.woorifood;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;

import java.io.IOException;
import java.util.List;

public class FoodLocation {

    private Context mContext;

    public FoodLocation(Context context) {
        mContext = context;

        Geocoder geocoder = new Geocoder(context);

        List<Address> list = null;

        Log.d("plz", "11111");
        //String adr = "새창로 6길 35";
        String adr = "김밥천국";

        try {
            list = geocoder.getFromLocationName(adr, 10000);
            Log.d("plz", "2222222");
        }catch (IOException e) {
            e.printStackTrace();
            Log.d("plz", "서버에서 주소변환 시 에러 발생");
        }

        Log.d("plz", "333333 : " + list.size());

        for (Address e: list
             ) {
            Log.d("plz", "44444");
            Log.d("plz", e.getLatitude() + "/" + e.getLongitude());
        }
    }


}
