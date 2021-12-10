package com.wooriss.woorifood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


import java.util.List;

import retrofit2.Response;

public class SearchFragment extends Fragment {

    private Context context;

    private TextView mainText;
    private EditText searchEdit;
    private ImageView searchBtn;
    private ImageView mapBtn;
    private TextView nonText;
    private ImageView nonImage;
    private SwipeRefreshLayout swipeSearchList;

    private static RecyclerView sikdangListRecyclerView;
    public static SikdangAdapter sikdangAdapter;
    private FragmentManager fManager;
    private MapFragment mapFragment;


    private static SearchFragment searchFragment;

//    public static FoodLocation foodLocation;

    public static Response<PageListSikdang> pageListSikdang;

    public SearchFragment() {}

    public static SearchFragment newInstance() { //(String param1, String param2) {
        if(searchFragment ==null) {
            searchFragment = new SearchFragment();
//            foodLocation = new FoodLocation(searchFragment, MainActivity.user.getBranch_addr());
            MainActivity.foodLocation.setSearchfFragment(searchFragment);

            sikdangAdapter = new SikdangAdapter(null);
        }
        return searchFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container!=null)
            context = container.getContext();
        return inflater.inflate(R.layout.fragment_search, container, false);
    }


    public static int hadSearched = 0;
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("plz", "i call UpdateService!");

        findViews(view);
        addListenerToSearchBtn();
        addListenerToMapBtn();
        addListenerToRecyclerView();

        addListenerToSwipeSearchList();

        if (sikdangListRecyclerView.getAdapter().getItemCount() <= 0) {
            setNullResult(true);
        }
        else
            setNullResult(false);

        if (hadSearched == 0) {
            nonImage.setVisibility(View.VISIBLE);
            setNullResult(false);
        } else
            nonImage.setVisibility(View.GONE);


    }




    // 화면 구성요소 정의
    private void findViews(View v) {

        mainText = v.findViewById(R.id.text_info);
        searchEdit = v.findViewById(R.id.edit_search);
        searchBtn = v.findViewById(R.id.btn_search);
        mapBtn = v.findViewById(R.id.btn_map);
        nonText = v.findViewById(R.id.non_item);
        nonImage = v.findViewById(R.id.non_item_img);

        swipeSearchList = v.findViewById(R.id.swipeSearchList);

        sikdangListRecyclerView = v.findViewById(R.id.sikdangList);
        sikdangListRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        sikdangListRecyclerView.setAdapter(sikdangAdapter);

        fManager = getParentFragmentManager();

//        String userId = ((MainActivity)context).getF_user().getUser_mail();
        String userId = MainActivity.user.getUser_mail();
//        String userBranch = ((MainActivity)context).getF_user().getBranch_name();
        String userBranch = MainActivity.user.getBranch_name();
        String printMainText = MainActivity.user.getUser_name() + " 님 안녕하세요!" + " (" + userBranch +")";
//        String printMainText = userId +"(" + userBranch + ") " + MainActivity.user.getUser_position();
        mainText.setText(printMainText);
    }


    private String searchSikdang;
    // 검색 버튼 눌렀을 때 이벤트
    private void addListenerToSearchBtn() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchSikdang = searchEdit.getText().toString();

                if (searchSikdang.length() == 0) {
                    Toast.makeText(context, "식당명을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
//                new FoodLocation(SearchFragment.this, ((MainActivity)context).getBranch().getAddr(), searchSikdang);
                if (FoodLocation.x != null)
                {
                    Log.d("plz", "call getSikdanList(addListenerToSearchBtn)");
                    MainActivity.foodLocation.getSikdangList(searchSikdang);
                }
                //new FoodLocation(SearchFragment.this, MainActivity.user.getBranch_addr(), searchSikdang);
            }
        });
    }

    // 지도 버튼 눌렀을 때 이벤트
    private void addListenerToMapBtn() {
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if (mapFragment == null) {
                    mapFragment = new MapFragment(Code.MapType.SEARCH_MAP);
                    Log.d("plz", "new MapFragment");
                }*/
                mapFragment = MapFragment.newInstance(Code.MapType.SEARCH_MAP);

                if (!mapFragment.isVisible()) {
                    //FragmentTransaction tf;
                    fManager.beginTransaction().replace(R.id.main_container, mapFragment).addToBackStack(null).commit();
                }
            }
        });
    }

    private void addListenerToSwipeSearchList() {
        swipeSearchList.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if ((searchSikdang != null)) {
                    // swipe 시 진행할 동작
                    MainActivity.foodLocation.getSikdangList(searchSikdang);
                }
                // 업데이트가 끝났음을 알림
                swipeSearchList.setRefreshing(false);
            }
        });

    }

    // 리사이클러뷰 페이징 처리 이벤트
    private void addListenerToRecyclerView() {
            sikdangListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override  // 최상단, 최하단 감지
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (sikdangListRecyclerView.getAdapter().getItemCount() > 0) {

                        if (!recyclerView.canScrollVertically((-1))) {
                            Log.d("plz", "리사이클러뷰 제일 위");
                            //foodLocation.callItemWithPaging(pageListSikdang.body().getMeta().total_count, 0);
                        }

                        if (!recyclerView.canScrollVertically((1))) {
                            Log.d("plz", "리사이클러뷰 제일 밑");
                            MainActivity.foodLocation.callItemWithPaging(pageListSikdang.body().getMeta().total_count, 1);
                        }
                    }
                }
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    // 현재 보이는 것 중 마지막 아이템 포지션 + 1
//                    int lastVisibleItemPosition = ((LinearLayoutManager)(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition() + 1;
                    // 전체 아이템 갯수
//                    int itemTotalCount = recyclerView.getAdapter().getItemCount();
                }
            });
    }

    public void setNullResult (boolean flag) {

        if (hadSearched == 1)
            nonImage.setVisibility(View.GONE);

        if (flag == true) {
            nonText.setVisibility(View.VISIBLE);
        }
        else
            nonText.setVisibility(View.GONE);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSikdangList (Response<PageListSikdang> _response, int _curPageNum) {
        SearchFragmentListener listener = new SearchFragmentListener() {
            private Response<PageListSikdang> response = _response;
            private int curPageNum = _curPageNum;

            @Override
            //public void loadPage(Response<PageListSikdang> response, int curPageNum) {
            public void setList() {
                if (curPageNum <= 1)
                {
                    pageListSikdang = response;


                }
                else {
                    pageListSikdang.body().getDocuments().addAll(response.body().getDocuments());
                }

                if (pageListSikdang != null) {
                    sikdangAdapter.updateData(pageListSikdang.body().getDocuments());
                    sikdangAdapter.notifyDataSetChanged();
                }

                Log.d("plz", "set List successed");
            }
        };

        setViewType(listener, _response.body().getDocuments());
    }

    private int cntSetViewItem = 0;
    public void setViewType (SearchFragmentListener listener, List<Sikdang> sikdangList) {
        Log.d("plz", "sikdangList.size() : " + sikdangList.size() );
        cntSetViewItem = 0;
        if (sikdangList.size() == 0) {
            Log.d("plz", "sikdangList.size() == 0");
            listener.setList();
        }
        else {
            Log.d("plz", "sikdangList.size() != 0");
            for (int i = 0; i < sikdangList.size(); i++) {
                Log.d("plz", " i : " + i + ", (" + sikdangList.get(i).getPlace_name() + ")");


                ((MainActivity) context).chkReviewedSikdang(new MyCallback() {
                    @Override
                    public void onCallback(Sikdang sikdang, String baseSikdangId, int pos) {
                        Log.d("plz", "in onCallback , pos : " + pos);
                        cntSetViewItem++;
                        if (sikdang != null) {  // 리뷰 있는 식당인 경우
                            //                        int numRating = sikdang.getNumRatings();
                            Log.d("plz", "numRating of " + sikdang.getId() + ", pos : " + pos);
                            sikdangList.get(pos).setViewType(Code.ViewType.REVIEWED_SIKDANG);
                            sikdangList.get(pos).setAvgTaste(sikdang.getAvgTaste());
                            sikdangList.get(pos).setNumRatings(sikdang.getNumRatings());

                        } else {                    // 리뷰 없는 식당인 경우
                            Log.d("plz", "null is mean default item set!! : " + baseSikdangId + ", pos" + pos);
                            sikdangList.get(pos).setViewType(Code.ViewType.DEFAULT_SIKDANG);
                        }

                        // 마지막건 처리 완료되었을 때 세팅
                        if (sikdangList.size() == cntSetViewItem) {
                            Log.d("plz", "끄으으으으으으으으으으으으으으으읏");
                            cntSetViewItem = 0;
                            listener.setList();
                        }
                    }
                }, sikdangList.get(i).getId(), i);
            }
        }


    }

    public interface SearchFragmentListener {
        void setList();
    }


}


