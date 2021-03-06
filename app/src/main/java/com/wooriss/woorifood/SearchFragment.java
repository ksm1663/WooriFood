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




    // ?????? ???????????? ??????
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
        String printMainText = MainActivity.user.getUser_name() + " ??? ???????????????!" + " (" + userBranch +")";
//        String printMainText = userId +"(" + userBranch + ") " + MainActivity.user.getUser_position();
        mainText.setText(printMainText);
    }


    private String searchSikdang;
    // ?????? ?????? ????????? ??? ?????????
    private void addListenerToSearchBtn() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchSikdang = searchEdit.getText().toString();

                if (searchSikdang.length() == 0) {
                    Toast.makeText(context, "???????????? ???????????????.", Toast.LENGTH_SHORT).show();
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

    // ?????? ?????? ????????? ??? ?????????
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
                    // swipe ??? ????????? ??????
                    MainActivity.foodLocation.getSikdangList(searchSikdang);
                }
                // ??????????????? ???????????? ??????
                swipeSearchList.setRefreshing(false);
            }
        });

    }

    // ?????????????????? ????????? ?????? ?????????
    private void addListenerToRecyclerView() {
            sikdangListRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override  // ?????????, ????????? ??????
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);

                    if (sikdangListRecyclerView.getAdapter().getItemCount() > 0) {

                        if (!recyclerView.canScrollVertically((-1))) {
                            Log.d("plz", "?????????????????? ?????? ???");
                            //foodLocation.callItemWithPaging(pageListSikdang.body().getMeta().total_count, 0);
                        }

                        if (!recyclerView.canScrollVertically((1))) {
                            Log.d("plz", "?????????????????? ?????? ???");
                            MainActivity.foodLocation.callItemWithPaging(pageListSikdang.body().getMeta().total_count, 1);
                        }
                    }
                }
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    // ?????? ????????? ??? ??? ????????? ????????? ????????? + 1
//                    int lastVisibleItemPosition = ((LinearLayoutManager)(recyclerView.getLayoutManager())).findLastCompletelyVisibleItemPosition() + 1;
                    // ?????? ????????? ??????
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
                        if (sikdang != null) {  // ?????? ?????? ????????? ??????
                            //                        int numRating = sikdang.getNumRatings();
                            Log.d("plz", "numRating of " + sikdang.getId() + ", pos : " + pos);
                            sikdangList.get(pos).setViewType(Code.ViewType.REVIEWED_SIKDANG);
                            sikdangList.get(pos).setAvgTaste(sikdang.getAvgTaste());
                            sikdangList.get(pos).setNumRatings(sikdang.getNumRatings());

                            sikdangList.get(pos).setAvgPrice(sikdang.getAvgPrice());
                            sikdangList.get(pos).setAvgLuxury(sikdang.getAvgLuxury());
                            sikdangList.get(pos).setTitleImage(sikdang.getTitleImage());

                        } else {                    // ?????? ?????? ????????? ??????
                            Log.d("plz", "null is mean default item set!! : " + baseSikdangId + ", pos" + pos);
                            sikdangList.get(pos).setViewType(Code.ViewType.DEFAULT_SIKDANG);
                        }

                        // ???????????? ?????? ??????????????? ??? ??????
                        if (sikdangList.size() == cntSetViewItem) {
                            Log.d("plz", "???????????????????????????????????????????????????");
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


