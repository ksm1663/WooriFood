package com.wooriss.woorifood;

import android.content.Context;
import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.UiThread;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.naver.maps.geometry.LatLng;
import com.naver.maps.map.CameraUpdate;
import com.naver.maps.map.MapView;
import com.naver.maps.map.NaverMap;
import com.naver.maps.map.OnMapReadyCallback;
import com.naver.maps.map.overlay.Marker;
import com.naver.maps.map.overlay.Overlay;
import com.naver.maps.map.util.MarkerIcons;

import java.util.List;


public class MapFragment extends Fragment implements OnMapReadyCallback {

    private Context context;
    private MapView mapView;
    private static NaverMap naverMap;

    private double branchX = 0;
    private double branchY = 0;


    private RecyclerView recyclerView;

    private int mapFlag;

    public MapFragment(int _mapFlag) {
        mapFlag = _mapFlag;
    }

    public static MapFragment newInstance(int _mapFlag) {
        MapFragment mapFragment = new MapFragment(_mapFlag);
        return mapFragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d("plz", "onCreateView of MapFragment");
        if(container!=null)
            context = container.getContext();

        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d("plz", "onViewCreated of MapFragment");

        //????????? ??????
        mapView = view.findViewById(R.id.map_container);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);

        recyclerView = view.findViewById(R.id.sikdangItemInMap);

        recyclerView.setLayoutManager(new LinearLayoutManager(context)
        {
            @Override
            public boolean canScrollVertically() { return false; }
            @Override
            public boolean canScrollHorizontally() { return false; }
        });


        if (mapFlag == Code.MapType.SEARCH_MAP) {
            recyclerView.setAdapter(SearchFragment.sikdangAdapter);
        }
        else {
            recyclerView.setAdapter(MainListFragment.reviewdSikdangAdapter);
        }

        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("plz", "MapFragment is dead!!!");
    }



    private String cur_branchName="";
    @UiThread
    @Override
    public void onMapReady(@NonNull NaverMap naverMap) {
        this.naverMap = naverMap;
        if (setCameraLocation()) { // ????????? ?????? ?????? ???????????? ?????? ?????? ???
            Log.d("plz", "onMapReaddy in setCameraLocation is true");


            cur_branchName = MainActivity.user.getBranch_name();

            if (mapFlag == Code.MapType.SEARCH_MAP) {
                if (SearchFragment.pageListSikdang != null) {
                    branchX = Double.valueOf(FoodLocation.tmpX);
                    branchY = Double.valueOf(FoodLocation.tmpY);
                    moveCamera(branchX, branchY);
                    setMarkerSikdangList(SearchFragment.pageListSikdang.body().getDocuments());
                }
            }
            else {
                if (MainListFragment.mainSikdangList != null) {
                    if (MainListFragment.branch_otherName.length()>0)
                        cur_branchName = MainListFragment.branch_otherName;
                    setMarkerSikdangList(MainListFragment.mainSikdangList);
                }

            }
            setMarkerBranch();
            addListenerToMap();
        }
    }

    public void setRecyclerViewSize (int code) {
        if (code == Code.ViewType.DEFAULT_SIKDANG)
        {
            recyclerView.getLayoutParams().height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 85, getResources().getDisplayMetrics());
            recyclerView.requestLayout();

        } else {
            recyclerView.getLayoutParams().height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 130, getResources().getDisplayMetrics());
            recyclerView.requestLayout();
        }

    }


    // ?????? ????????????????????? ?????? ?????? ????????? ????????? ?????? ??????
    private void setMarkerSikdangList(List<Sikdang> sikdangList) {

        int current_doc_cnt = sikdangList.size();
        Log.d("plz", "size() : " + current_doc_cnt);
        for (int i=0; i < current_doc_cnt; i++) {
            Sikdang sikdang = sikdangList.get(i);
            Marker marker = new Marker();
            marker.setPosition(new LatLng(Double.valueOf(sikdang.getY()), Double.valueOf(sikdang.getX())));
            marker.setIcon(MarkerIcons.BLACK);

            if (sikdangList.get(i).getViewType() == Code.ViewType.DEFAULT_SIKDANG)
                marker.setIconTintColor(Color.GREEN);
            else
                marker.setIconTintColor(Color.RED);

            marker.setCaptionText(sikdang.getPlace_name());
            marker.setTag(i);
            addListenerToMarker(marker);

            marker.setMap(naverMap);
        }
    }

/*
    // ?????? ????????????????????? ?????? ?????? ????????? ????????? ?????? ??????
    private void _setMarkerSikdangList() {
//        int total_count = SearchFragment.pageListSikdang.body().getMeta().total_count;
        int current_doc_cnt = SearchFragment.pageListSikdang.body().getDocuments().size();
        //Log.d("plz", "pageListSikdang.body().getMeta().total_count : " + total_count);
        Log.d("plz", "SearchFragment.pageListSikdang.body().getDocuments().size() : " + current_doc_cnt);
        for (int i=0; i < current_doc_cnt; i++) {
            Sikdang sikdang = SearchFragment.pageListSikdang.body().getDocuments().get(i);
            Marker marker = new Marker();
            marker.setPosition(new LatLng(Double.valueOf(sikdang.getY()), Double.valueOf(sikdang.getX())));
            marker.setIcon(MarkerIcons.BLACK);
            marker.setIconTintColor(Color.GREEN);
            marker.setCaptionText(sikdang.getPlace_name());
            marker.setTag(i);
            addListenerToMarker(marker);

            marker.setMap(naverMap);
        }
    }
*/


    // ?????? ???????????? ????????????????????? ???????????? ?????????
    private void addListenerToMarker(Marker marker) {
        marker.setOnClickListener(new Overlay.OnClickListener() {
            @Override
            public boolean onClick(@NonNull Overlay overlay) {
                Marker tmpMarker = (Marker)overlay;
                int markerPos = (int)tmpMarker.getTag();
                // ???????????? ?????????, ???????????? ??????(?????????) ????????? ????????? ?????????????????? ?????? ????????? ??????
                if (recyclerView.getAdapter().getItemViewType(markerPos) == Code.ViewType.DEFAULT_SIKDANG)
                    setRecyclerViewSize(Code.ViewType.DEFAULT_SIKDANG);
                else
                    setRecyclerViewSize(Code.ViewType.REVIEWED_SIKDANG);

                LinearLayoutManager llm = (LinearLayoutManager) recyclerView.getLayoutManager();
                llm.scrollToPositionWithOffset(markerPos,0);

                if (recyclerView.getVisibility()==View.GONE) {
                    recyclerView.setVisibility(View.VISIBLE);
                    recyclerView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.ani_slid_in_bottom));
                }
                return true;
            }
        });
    }


    // ??? ???????????? ????????? ??????
    private void addListenerToMap() {
         naverMap.setOnMapClickListener(new NaverMap.OnMapClickListener() {
             @Override
             public void onMapClick(@NonNull PointF pointF, @NonNull LatLng latLng) {
                 if (recyclerView.getVisibility() == View.VISIBLE) {
                     recyclerView.startAnimation(AnimationUtils.loadAnimation(context, R.anim.ani_slid_out_bottom));
                     recyclerView.setVisibility(View.GONE);
                 }

             }
         });
    }

    // ?????? ?????? ??????
    private void setMarkerBranch() {
        Marker marker = new Marker();
        marker.setPosition(new LatLng(branchY, branchX));
        marker.setIcon(MarkerIcons.BLACK);
        marker.setIconTintColor(Color.BLUE);
        marker.setCaptionText(cur_branchName);
        marker.setCaptionColor(Color.BLUE);
        marker.setMap(naverMap);
    }

    // ????????? ????????? ????????? ????????? ?????? ?????? ???????????? ??????
    private boolean setCameraLocation() {
        if (FoodLocation.x == null)
            return false;

        if (branchX == 0) { // ???????????? ?????? ?????? ??????  (?????? ?????? : ????????? ????????? ???????????? ?????? ???????????? ??????)
            branchX = Double.valueOf(FoodLocation.x);
            branchY = Double.valueOf(FoodLocation.y);
            Log.d("plz", "1111 : " + branchX + "/" + branchY);
            moveCamera(branchX, branchY);
            return true;

        } else {
            // ?????? ????????? ???????????????, ?????? ????????? ??????????????? ?????? ?????????, ?????? ?????? ??????????????? ?????? ??? ????????? ??????
            if ((branchX != Double.valueOf(FoodLocation.x)) && (branchY != Double.valueOf(FoodLocation.y))) {
                branchX = Double.valueOf(FoodLocation.x);
                branchY = Double.valueOf(FoodLocation.y);
                Log.d("plz", "2222 : " + branchX + "/" + branchY);
                moveCamera(branchX, branchY);
                return true;
            }
            else {
                moveCamera(branchX, branchY);
                return true;
            }
        }
    }

    public void moveCamera(double x, double y) {
        Log.d("plz", "moveCamera : x : " + x + ", y : " + y);
//        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(Double.valueOf(FoodLocation.y), Double.valueOf(FoodLocation.x)));
        CameraUpdate cameraUpdate = CameraUpdate.scrollTo(new LatLng(y, x));
        naverMap.moveCamera(cameraUpdate);
    }
}
