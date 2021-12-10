package com.wooriss.woorifood;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class MyReviewFragment extends Fragment {

    private Context context;

    private FirebaseUser f_user;

    private RecyclerView myReviewList;

    private List<ReviewSet> reviewSetList;
    private ReviewAdapter reviewAdapter;

    private TextView textCount;

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

        downloadMyReview();

//        addListenerToEditBtn();
    }

    private void findViews(View v) {
        myReviewList = v.findViewById(R.id.myReviewList);
        reviewSetList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewSetList, context);

        textCount = v.findViewById(R.id.textCount);

        //아래구분선 세팅
        myReviewList.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));

        myReviewList.setAdapter(reviewAdapter);
        myReviewList.setLayoutManager(new LinearLayoutManager(context));
    }

    private Sikdang sikdang;
    private void downloadMyReview() {
        Log.d("plz", "is here???");
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        Log.d("plz", "hmmm : " + firebaseFirestore);
        firebaseFirestore.collectionGroup("reviews").orderBy("timestamp", Query.Direction.DESCENDING).whereEqualTo("reviewerUid", f_user.getUid()).get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                Log.d("plz", "is here??? queryDocumentSnapshots queryDocumentSnapshots");
                Log.d("plz", queryDocumentSnapshots.getDocuments().toString());
                List<Review> reviews = queryDocumentSnapshots.toObjects(Review.class);
                textCount.setText("총 " + reviews.size() + "건");
                for (int i=0; i<reviews.size(); i++) {


                    ReviewSet reviewSet = new ReviewSet();
                    reviewSet.images = new ArrayList<>();
                    reviewSet.user = new User();
//                                        _tmpUser = new User();
//                                        reviewSet.user = _tmpUser;


                    // sikdangs 에서 해당 식당코드로 리뷰정보 가져오기
                    String[] sikdangIds = queryDocumentSnapshots.getDocuments().get(i).getId().toString().split("_");
                    firebaseFirestore.collection("sikdangs").document(sikdangIds[0]).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                            if (task.isSuccessful()) {
                                DocumentSnapshot document = task.getResult();
                                if (document.exists()) {
                                    Log.d("plz", "DocumentSnapshot data: " + document.getData());
                                    // 식당의 종합정보 세팅
                                    sikdang = document.toObject(Sikdang.class);
                                    reviewSet.getUser().setUser_name(sikdang.getPlace_name());
                                    reviewSet.getUser().setUser_position("");
                                    reviewSet.getUser().setBranch_name(sikdang.getAddress_name());

                                } else {
                                    Log.d("plz", "No such document");
                                }
                            } else {
                                Log.d("plz", "get failed with ", task.getException());
                            }
                        }
                    });



                    reviewSet.review = reviews.get(i);
                    reviewSetList.add(reviewSet);

                    // 해당 리뷰의 등록자 정보 가져와서 저장
                    setReviewerInfo(reviewSetList.size() - 1, reviews.get(i).getReviewerUid());

                    // 해당 리뷰의 사진 가져오기
                    downloadImages(reviewSet.images, queryDocumentSnapshots.getDocuments().get(i).getId());







                }
            }
        });
    }


    // 리뷰 등록한 사람 정보 가져와서 세팅
    private void setReviewerInfo (int pos, String uid) {
        DatabaseReference mDatabase;
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("users").child(uid).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.d("plz", "Error getting data", task.getException());
                }
                else {
                    Log.d("plz", "gogogogogogogo");
                    reviewSetList.get(pos).user = task.getResult().getValue(User.class);
//                    _user = task.getResult().getValue(User.class);
//                    u[0] = task.getResult().getValue(User.class);
//                    user = (User)tmpUser.clone();
                    Log.d("plz", "user : " + reviewSetList.get(pos).user.getUser_name());
                    reviewAdapter.notifyDataSetChanged();
//                    Log.d("plz", "_user : " + _user.getUser_name());
                }
            }
        });

    }

    // 리뷰 이미지 가져오기
    private void downloadImages(List<Uri> images, String id) {
        // Get a default Storage bucket
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        StorageReference listRef = storageRef.getReference().child("reviewImages/" + id);

        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        Log.d("plz", "onSuccess in downloadImage");
                        for (StorageReference item : listResult.getItems()) {
//                            LinearLayout layout = (LinearLayout) findViews(R.id.);
//                            Log.d("plz", "item.getPath() : " + item.getPath());
                            // reference 의 아이템(이미지) url 받아오기
                            item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
//                                        Log.d("plz", "task.getResult() : " + task.getResult() );
                                        images.add(task.getResult());
                                        ((ReviewAdapter)myReviewList.getAdapter()).notifyDetailImageViewAdaper();
                                        reviewAdapter.notifyDataSetChanged();
                                    } else {
                                        // url 가져오지 못함
                                    }
                                }
                            });
                        }
                    } // end onSuccess
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //
            }
        });
    }

}


