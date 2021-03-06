package com.wooriss.woorifood;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class DetailFragment extends Fragment implements View.OnTouchListener{

    private Context context;

    private FirebaseUser f_user;
    private User user;
    private Sikdang sikdang;


    private LinearLayout reviewUploadLay;

    private TextView sikdangNameText;
    private TextView sikdangAddrText;
    private TextView sikdangCategoryText;
    private TextView sikdangCategoryGroupText;
    private TextView sikdangPhoneText;
    private RatingBar sikdangTasteAvgRating;

    private SeekBar seekPriceAvg;
    private SeekBar seekLuxuryAvg;

    private BarChart sikdangComplexChart;

    private RecyclerView reviewListInDetailView;
    private List<ReviewSet> reviewSetList;
    private ReviewAdapter reviewAdapter;


    private LinearLayout laySikdangInfo;
    private ImageView btnClickMinimal;

    public DetailFragment() {
    }


    public static DetailFragment newInstance(Bundle bundle) {
        DetailFragment detailFragment = new DetailFragment();
        detailFragment.setArguments(bundle);
        return detailFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (container != null)
            context = container.getContext();

        if (getArguments() != null) {
            f_user = getArguments().getParcelable("f_user");
            user = (User) getArguments().getSerializable("user");
            sikdang = (Sikdang) getArguments().getSerializable("sikdang");

            Log.d("plz", "???????????? ?????????????????? ????????? sikdang : " + sikdang);
        }

        return inflater.inflate(R.layout.fragment_detail, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);
        addListenerToUploadLay();
        downloadReviews();

        //setSikdangInfo();

        addListenerToPhoneText();
        addListenerToMinimalBtn();



    }


    // ?????? ???????????? ??????
    private void findViews(View v) {

        reviewUploadLay = v.findViewById(R.id.lay_reviewUpload);
        sikdangNameText = v.findViewById(R.id.textSikdangName);
        sikdangAddrText = v.findViewById(R.id.textSikdangAddr);
        sikdangCategoryText = v.findViewById(R.id.textSikdangCategory);
        sikdangCategoryGroupText = v.findViewById(R.id.textSikdangCategoryGroup);
        sikdangPhoneText = v.findViewById(R.id.textSikdangPhone);
        sikdangTasteAvgRating = v.findViewById(R.id.ratingSikdangTasteAvg);

        laySikdangInfo = v.findViewById(R.id.laySikdangInfo);
        btnClickMinimal = v.findViewById(R.id.btnClickMinimal);


        seekPriceAvg = v.findViewById(R.id.seekPrice);
        seekLuxuryAvg = v.findViewById(R.id.seekLuxury);



        sikdangComplexChart = v.findViewById(R.id.sikdangComplexChart);


        reviewListInDetailView = v.findViewById(R.id.reviewListInDetailView);
        reviewSetList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewSetList, getContext());

        //??????????????? ??????
        reviewListInDetailView.addItemDecoration(new DividerItemDecoration(getActivity().getApplicationContext(), DividerItemDecoration.VERTICAL));

        reviewListInDetailView.setAdapter(reviewAdapter);
        reviewListInDetailView.setLayoutManager(new LinearLayoutManager(context));



    }

    private void addListenerToPhoneText() {
        sikdangPhoneText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phoneNumber = sikdangPhoneText.getText().toString().replaceAll("-", "");
                Intent call = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"+ phoneNumber));
                startActivity(call);
            }
        });
    }

    private boolean isMinimal = false;
    private void addListenerToMinimalBtn() {
        btnClickMinimal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isMinimal) {
                    isMinimal = true;
                    laySikdangInfo.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());
                }
                else {
                    isMinimal = false;
                    laySikdangInfo.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, getResources().getDisplayMetrics());
                }

                laySikdangInfo.requestLayout();
            }
        });
    }

    public class MyValueFormatter extends ValueFormatter {

        private  float cutoff;
        private  DecimalFormat format;
        String strs[] = {"-","??????","??????","??????"};

        public MyValueFormatter(float cutoff) {
            this.cutoff = cutoff;
            this.format = new DecimalFormat("###,###,###,##0");
        }

        @Override
        public String getFormattedValue(float value) {
            if (value < cutoff) {
                return "-";
            }
//            Log.d("plz", "format.format(value) : " + format.format(value));
            return strs[(Integer.parseInt(format.format(value))/10)];//format.format(value);
        }
    }

    //
    private void setSikdangInfo() {
        sikdangNameText.setText(sikdang.getPlace_name());
        sikdangAddrText.setText(sikdang.getAddress_name());
        sikdangCategoryText.setText(sikdang.getCategory_name());
        sikdangCategoryGroupText.setText(sikdang.getCategory_group_name());
        sikdangPhoneText.setText(sikdang.getPhone());
        sikdangTasteAvgRating.setRating((float) sikdang.getAvgTaste());

        seekPriceAvg.setProgress((int)(sikdang.getAvgPrice()*10));
        seekPriceAvg.setOnTouchListener(this);
        seekLuxuryAvg.setProgress((int)(sikdang.getAvgLuxury()*10));
        seekLuxuryAvg.setOnTouchListener(this);

        List<BarEntry> barEntryList = new ArrayList<>();

        barEntryList.add(new BarEntry(1f,0));
        barEntryList.add(new BarEntry(1f,sikdang.getAvgFirstComplex()*10));
        barEntryList.add(new BarEntry(2f,sikdang.getAvgSecondComplex()*10));
        barEntryList.add(new BarEntry(3f,sikdang.getAvgThirdComplex()*10));
        barEntryList.add(new BarEntry(3f,30));


        BarDataSet barDataSet = new BarDataSet(barEntryList,"");
//        barDataSet.setColor(R.color.yello);
        barDataSet.setColors(Color.TRANSPARENT, Color.parseColor("#FF3F72AF"),
                Color.parseColor("#FF3F72AF"), Color.parseColor("#FF3F72AF"), Color.TRANSPARENT);
//        barDataSet.setBarBorderWidth(0.1f);
//        barDataSet.setColors(ColorTemplate.PASTEL_COLORS);
        BarData barData = new BarData(barDataSet);
        barData.setBarWidth(0.4f);

        sikdangComplexChart.notifyDataSetChanged();
        sikdangComplexChart.invalidate();
        barData.setDrawValues(true);


        sikdangComplexChart.getDescription().setEnabled(false); // ?????? ?????? ???????????? desc
        sikdangComplexChart.setMaxVisibleValueCount(5); // ?????? ????????? ????????? ??????
        sikdangComplexChart.setPinchZoom(false); // ?????????????????? ??????, ?????????
        sikdangComplexChart.setDrawBarShadow(false); // ????????? ?????????
        sikdangComplexChart.setDrawGridBackground(true); // ????????????
//        sikdangComplexChart.setEnabled(false);
        sikdangComplexChart.animateY(1000);
        sikdangComplexChart.getLegend().setEnabled(false);
        sikdangComplexChart.setTouchEnabled(false);

        // 30 3 4
//      sikdangComplexChart.getAxisLeft().setDrawZeroLine(true);
        sikdangComplexChart.getAxisLeft().setLabelCount(4, false);
        sikdangComplexChart.getAxisLeft().mAxisMaximum = 30;
        sikdangComplexChart.getAxisLeft().setGranularity(0.1f); // 1 ???????????? ??? ?????????
        sikdangComplexChart.getAxisLeft().setGranularityEnabled(true);
        sikdangComplexChart.getAxisLeft().setTextSize(13);

        sikdangComplexChart.getAxisLeft().setDrawLabels(true);
//        sikdangComplexChart.getAxisLeft().setSpaceBottom(100);
//        sikdangComplexChart.getAxisLeft().setCenterAxisLabels(true);
        sikdangComplexChart.getAxisLeft().setValueFormatter(new MyValueFormatter(0));



//        int max = findMax
//        sikdangComplexChart.getAxisLeft().setLabelCount(4, true);
//        sikdangComplexChart.getAxisLeft().mAxisMinimum = 0;
//        sikdangComplexChart.getAxisLeft().mAxisMaximum = 5; // 3 ????????? ??? ????????? ?????? 4??? ????????? ??????
//        sikdangComplexChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return String.valueOf(value);
////                return super.getFormattedValue(value);
//            }
//        });
//        sikdangComplexChart.getAxisLeft().setGranularity(1); // 1 ???????????? ??? ?????????
//        sikdangComplexChart.getAxisLeft().setGranularityEnabled(true);
//        sikdangComplexChart.getAxisLeft().setEnabled(true);
//        sikdangComplexChart.getAxisLeft().setDrawGridLines(true);
//        sikdangComplexChart.getAxisLeft().setDrawAxisLine(true);
//        sikdangComplexChart.getAxisLeft().setDrawLabels(true); // ??? ????????? ???????????? (1,2,3)
//        sikdangComplexChart.getAxisLeft().setValueFormatter(new LargeValueFormatter());

//        sikdangComplexChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
//            private String[] gubun = {"-", "??????", "??????", "??????"};
//            @Override
//            public String getAxisLabel(float value, AxisBase axis) {
//                Log.d("plz", "Yvalue : " + value);
//                return gubun[Math.round(value)];
//            }
//        });

//        sikdangComplexChart.getAxisLeft().setDrawGridLines(false); // ?????? ??????
//        sikdangComplexChart.getAxisLeft().setDrawAxisLine(false); // ??? ?????????
        sikdangComplexChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        sikdangComplexChart.getXAxis().setGranularity(1f); // ??????
        sikdangComplexChart.getXAxis().setDrawGridLines(false); // ??????
        sikdangComplexChart.getXAxis().setDrawAxisLine(true); // ??? ??????
        sikdangComplexChart.getXAxis().setDrawLabels(true); // ??????
        sikdangComplexChart.getXAxis().setTextSize(12);

        sikdangComplexChart.getXAxis().setValueFormatter(new ValueFormatter() {
            private String[] gubun = {"1???", "2???", "3???", "", ""};
            @Override
            public String getAxisLabel(float value, AxisBase axis) {
                return gubun[(int)value-1];
            }
        });

        sikdangComplexChart.getAxisRight().setEnabled(false);


        sikdangComplexChart.setFitBars(true);
        sikdangComplexChart.setDrawGridBackground(false);

        sikdangComplexChart.setData(barData);
        sikdangComplexChart.animateY(1000);





    }

    private void cacluateMinMax(BarDataSet chart, BarChart sikdangComplexChart, int labelCount) {
        float maxValue = chart.getYMax();
        float minValue = chart.getYMin();

        if ((maxValue - minValue) < labelCount) {
            float diff = labelCount - (maxValue - minValue);
            maxValue = maxValue + diff;
            sikdangComplexChart.getAxisLeft().setAxisMaximum(maxValue);
            sikdangComplexChart.getAxisLeft().setAxisMinimum(minValue);
        }
    }

    private void addListenerToUploadLay() {
        reviewUploadLay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((MainActivity) view.getContext()).transferToReview(sikdang);
            }
        });
    }

    private void downloadReviews() {
        // sikdangs ?????? ?????? ??????????????? ???????????? ????????????
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("sikdangs").document(sikdang.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("plz", "DocumentSnapshot data: " + document.getData());
                        // ????????? ???????????? ??????
                        sikdang = document.toObject(Sikdang.class);
                        setSikdangInfo();
                        sikdangComplexChart.notifyDataSetChanged();
                        sikdangComplexChart.invalidate();

                        Log.d("plz", "[" + sikdang.getPlace_name() + "] ??? ?????? ????????? : " + sikdang.getAvgTaste());

                        document.getReference().collection("reviews").orderBy("timestamp", Query.Direction.DESCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // ????????? ????????? ????????????
                                    for (QueryDocumentSnapshot document : task.getResult()) {
//                                        Log.d("plz", document.getId() + " => " + document.getData());
                                        Review review = document.toObject(Review.class);

                                        ReviewSet reviewSet = new ReviewSet();
                                        reviewSet.images = new ArrayList<>();
                                        reviewSet.user = new User();
//                                        _tmpUser = new User();
//                                        reviewSet.user = _tmpUser;
                                        reviewSet.review = review;
                                        reviewSetList.add(reviewSet);

                                        // ?????? ????????? ????????? ?????? ???????????? ??????
                                        setReviewerInfo (reviewSetList.size()-1, review.getReviewerUid());

                                        // ?????? ????????? ?????? ????????????
                                        downloadImages(reviewSet.images, document.getId());
                                    }

                                    reviewAdapter.notifyDataSetChanged();

                                } else {
                                    Log.d("plz", "Error getting documents: ", task.getException());
                                }

                            }
                        });
                    } else {
                        Log.d("plz", "No such document");
                    }
                } else {
                    Log.d("plz", "get failed with ", task.getException());
                }
            }
        });
    }

    private User _tmpUser;
    // ?????? ????????? ?????? ?????? ???????????? ??????
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

    // ?????? ????????? ????????????
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
                            // reference ??? ?????????(?????????) url ????????????
                            item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
//                                        Log.d("plz", "task.getResult() : " + task.getResult() );
                                        images.add(task.getResult());
                                        ((ReviewAdapter)reviewListInDetailView.getAdapter()).notifyDetailImageViewAdaper();
                                        reviewAdapter.notifyDataSetChanged();
                                    } else {
                                        // url ???????????? ??????
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }


    // inner Class : ?????????????????? ???????????? ?????????



    // inner Class : ????????? ?????????????????? ?????? ???????????? ?????? ??????????????? ?????????



    // inner of inner Class : ?????? ??? ?????????????????? ?????? ??????????????? ?????????


}