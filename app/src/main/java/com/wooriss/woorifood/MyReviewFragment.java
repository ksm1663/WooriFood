package com.wooriss.woorifood;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class MyReviewFragment extends Fragment {

    private Context context;

    private FirebaseUser f_user;

    private RecyclerView myReviewList;

    private List<DetailFragment.ReviewSet> reviewSetList;
    private DetailFragment.ReviewAdapter reviewAdapter;

    public MyReviewFragment() { }

    public static MyReviewFragment newInstance (Bundle bundle) {
        MyReviewFragment myReviewFragment = new MyReviewFragment();
        myReviewFragment.setArguments(bundle);
        return myReviewFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            f_user = getArguments().getParcelable("f_user");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(container!=null)
            context = container.getContext();
        return inflater.inflate(R.layout.fragment_myreview, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);

//        addListenerToEditBtn();
    }

    private void findViews(View v) {
        myReviewList = v.findViewById(R.id.myReviewList);
        reviewSetList = new ArrayList<>();
//        reviewAdapter = new DetailFragment.ReviewAdapter(reviewSetList);

        //아래구분선 세팅
//        reviewListInDetailView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));

//        reviewListInDetailView.setAdapter(reviewAdapter);
//        reviewListInDetailView.setLayoutManager(new LinearLayoutManager(context));
    }

}


