package com.wooriss.woorifood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.HashMap;
import java.util.List;

import javax.annotation.Nullable;

import retrofit2.Response;

public class MainListFragment extends Fragment {

    private Context context;
    public static MainListFragment mainListFragment;

    private ImageView mapBtn;
    private EditText branchEdit;
    private ImageView searchBranchBtn;
    private TextView infoText;
    private SwipeRefreshLayout swipeMainlist;
    private TextView nonText;

    private RecyclerView mainListRecyclerView;
    public static SikdangAdapter reviewdSikdangAdapter;

    private FragmentManager fManager;
    private MapFragment reviewedMapFragment;

    public static List<Sikdang> mainSikdangList;

    public MainListFragment(){}

    public static MainListFragment newInstance() { //(String param1, String param2) {
        if(mainListFragment ==null) {
            mainListFragment = new MainListFragment();
            MainActivity.foodLocation = new FoodLocation(mainListFragment, MainActivity.user.getBranch_addr());
            reviewdSikdangAdapter = new SikdangAdapter(null);
        }
        return mainListFragment;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container!=null)
            context = container.getContext();
        return inflater.inflate(R.layout.fragment_mainlist, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @androidx.annotation.Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d("plz", "i call UpdateService!");

        findViews(view);

        addListenerToSearchEdit();

        addListenerToSearchBtn();

        addListenerToMapBtn();

        addListenerToSwipeMainlist();


        if (FoodLocation.x != null)
            callSetMainList();
    }

    private void findViews(View v) {
        mapBtn = v.findViewById(R.id.btn_map);
        branchEdit = v.findViewById(R.id.edit_branch);
        branchEdit.setInputType(InputType.TYPE_NULL);
        searchBranchBtn = v.findViewById(R.id.btn_searchBranch);
        infoText = v.findViewById(R.id.text_info);
        nonText = v.findViewById(R.id.non_item);

        swipeMainlist = v.findViewById(R.id.swipeMainlist);

        mainListRecyclerView = v.findViewById(R.id.mainList);
        mainListRecyclerView.setLayoutManager(new LinearLayoutManager(context));

        mainListRecyclerView.setAdapter(reviewdSikdangAdapter);

        fManager = getParentFragmentManager();

        String userId = MainActivity.user.getUser_mail();
//        String userBranch = ((MainActivity)context).getF_user().getBranch_name();
        String userBranch = MainActivity.user.getBranch_name();
        String printMainText = userId +"(" + userBranch + ") " + MainActivity.user.getUser_position();
        infoText.setText(printMainText);

        branchEdit.setHint(userBranch);
    }

    private String branch_addr = "";
    public static String branch_otherName = "";
    private void addListenerToSearchEdit() {
        branchEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SearchBranchDialog(context, new SearchBranchDialog.ICustomDialogEventListener() {
                    @Override
                    public void customDialogEvent(HashMap<String, String> _branch_info) {
                        // Do something with the value here, e.g. set a variable in the calling activity
                        if (_branch_info != null) {
                            //branch_info = _branch_info;
                            branch_otherName = _branch_info.get("branch_name");
                            branchEdit.setText(branch_otherName);
                            branch_addr = _branch_info.get("branch_addr");
                        }
                    }
                });
            }
        });
    }
    private void addListenerToSearchBtn() {
//        private EditText branchEdit;
//        private ImageView searchBranchBtn;
        searchBranchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (branch_addr.length()>0) {
                    MainActivity.foodLocation = new FoodLocation(mainListFragment, branch_addr, 1);
                }

            }
        });
    }


    private void addListenerToSwipeMainlist() {
        swipeMainlist.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // swipe 시 진행할 동작
                callSetMainList();
                // 업데이트가 끝났음을 알림
                swipeMainlist.setRefreshing(false);
            }
        });
    }

    // 지도 버튼 눌렀을 때 이벤트
    private void addListenerToMapBtn() {
        mapBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                if (reviewedMapFragment == null) {
                    reviewedMapFragment = new MapFragment(Code.MapType.REVIEWED_MAP);
                    Log.d("plz", "new MapFragment");
                }*/
                reviewedMapFragment = MapFragment.newInstance(Code.MapType.REVIEWED_MAP);

                if (!reviewedMapFragment.isVisible()) {
                    //FragmentTransaction tf;
                    fManager.beginTransaction().replace(R.id.main_container, reviewedMapFragment).addToBackStack(null).commit();
                }
            }
        });
    }

    private void setNullResult (boolean flag) {
        if (flag == true) {
            nonText.setVisibility(View.VISIBLE);
            //if (mainSikdangList!=null)
                //mainSikdangList.clear();
        }
        else
            nonText.setVisibility(View.GONE);
    }

    public void callSetMainList() {
        Log.d("plz", "get ready!!!!");
        ((MainActivity)context).getReviewedSikdangs(FoodLocation.x, FoodLocation.y, 4000);

    }

    @SuppressLint("NotifyDataSetChanged")
    public void setReviewedSikdangList(List<Sikdang> results) {

        if (results.size() > 0)
            setNullResult (false);
        else
            setNullResult (true);


        mainSikdangList = results;
        reviewdSikdangAdapter.updateData(mainSikdangList);
        reviewdSikdangAdapter.notifyDataSetChanged();
    }



}
