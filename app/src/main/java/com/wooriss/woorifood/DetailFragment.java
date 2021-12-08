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
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
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

    private SeekBar sikdangPriceAvgSeek;
    private SeekBar sikdangFirstComplexSeek;
    private SeekBar sikdangSecondComplexSeek;
    private SeekBar sikdangThirdComplexSeek;

    private BarChart sikdangComplexChart;

    private RecyclerView reviewListInDetailView;
    private List<ReviewSet> reviewSetList;
    private ReviewAdapter reviewAdapter;


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

            Log.d("plz", "상세정보 프래그먼트로 넘어온 sikdang : " + sikdang);
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



    }


    // 화면 구성요소 정의
    private void findViews(View v) {

        reviewUploadLay = v.findViewById(R.id.lay_reviewUpload);
        sikdangNameText = v.findViewById(R.id.textSikdangName);
        sikdangAddrText = v.findViewById(R.id.textSikdangAddr);
        sikdangCategoryText = v.findViewById(R.id.textSikdangCategory);
        sikdangCategoryGroupText = v.findViewById(R.id.textSikdangCategoryGroup);
        sikdangPhoneText = v.findViewById(R.id.textSikdangPhone);
        sikdangTasteAvgRating = v.findViewById(R.id.ratingSikdangTasteAvg);

        sikdangPriceAvgSeek = v.findViewById(R.id.seekPrice);
        sikdangFirstComplexSeek = v.findViewById(R.id.seekFirstComplex);
        sikdangSecondComplexSeek = v.findViewById(R.id.seekSecondComplex);
        sikdangThirdComplexSeek = v.findViewById(R.id.seekThirdComplex);

        sikdangComplexChart = v.findViewById(R.id.sikdangComplexChart);


        reviewListInDetailView = v.findViewById(R.id.reviewListInDetailView);
        reviewSetList = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(reviewSetList);

        //아래구분선 세팅
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


    public class MyValueFormatter extends ValueFormatter {

        private  float cutoff;
        private  DecimalFormat format;
        String strs[] = {"-","여유","보통","혼잡"};

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

        sikdangPriceAvgSeek.setProgress((int)(sikdang.getAvgPrice()*10));
        sikdangFirstComplexSeek.setProgress((int)(sikdang.getAvgFirstComplex()*10));
        sikdangSecondComplexSeek.setProgress((int)(sikdang.getAvgSecondComplex()*10));
        sikdangThirdComplexSeek.setProgress((int)(sikdang.getAvgThirdComplex()*10));

        sikdangPriceAvgSeek.setOnTouchListener(this);


        List<BarEntry> barEntryList = new ArrayList<>();

        barEntryList.add(new BarEntry(1f,0));
        barEntryList.add(new BarEntry(1f,sikdang.getAvgFirstComplex()*10));
        barEntryList.add(new BarEntry(2f,sikdang.getAvgSecondComplex()*10));
        barEntryList.add(new BarEntry(3f,sikdang.getAvgThirdComplex()*10));
        barEntryList.add(new BarEntry(3f,30));

//        Log.d("plz", "sikdang.getAvgFirstComplex() : " + sikdang.getAvgFirstComplex());
//        Log.d("plz", barEntryList.get(0).getY()+"");
//        Log.d("plz", "sikdang.getAvgSecondComplex() : " + sikdang.getAvgSecondComplex());
//        Log.d("plz", barEntryList.get(1).getY()+"");
//        Log.d("plz", "sikdang.getAvgThirdComplex() : " + sikdang.getAvgThirdComplex());
//        Log.d("plz", barEntryList.get(2).getY()+"");

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

        sikdangComplexChart.getDescription().setEnabled(false); // 차트 옆에 표기되는 desc
        sikdangComplexChart.setMaxVisibleValueCount(5); // 최대 보이는 그래프 개수
        sikdangComplexChart.setPinchZoom(false); // 두손가락으로 줌인, 줌아웃
        sikdangComplexChart.setDrawBarShadow(false); // 그래프 그림자
        sikdangComplexChart.setDrawGridBackground(true); // 격자구조
//        sikdangComplexChart.setEnabled(false);
        sikdangComplexChart.animateY(1000);
        sikdangComplexChart.getLegend().setEnabled(false);
        sikdangComplexChart.setTouchEnabled(false);

        // 30 3 4
//      sikdangComplexChart.getAxisLeft().setDrawZeroLine(true);
        sikdangComplexChart.getAxisLeft().setLabelCount(4, false);
        sikdangComplexChart.getAxisLeft().mAxisMaximum = 30;
        sikdangComplexChart.getAxisLeft().setGranularity(0.1f); // 1 단위마다 선 그리기
        sikdangComplexChart.getAxisLeft().setGranularityEnabled(true);

        sikdangComplexChart.getAxisLeft().setDrawLabels(true);
//        sikdangComplexChart.getAxisLeft().setSpaceBottom(100);
//        sikdangComplexChart.getAxisLeft().setCenterAxisLabels(true);
        sikdangComplexChart.getAxisLeft().setValueFormatter(new MyValueFormatter(0));



//        int max = findMax
//        sikdangComplexChart.getAxisLeft().setLabelCount(4, true);
//        sikdangComplexChart.getAxisLeft().mAxisMinimum = 0;
//        sikdangComplexChart.getAxisLeft().mAxisMaximum = 5; // 3 위치에 선 그리기 위해 4로 맥시멈 설정
//        sikdangComplexChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return String.valueOf(value);
////                return super.getFormattedValue(value);
//            }
//        });
//        sikdangComplexChart.getAxisLeft().setGranularity(1); // 1 단위마다 선 그리기
//        sikdangComplexChart.getAxisLeft().setGranularityEnabled(true);
//        sikdangComplexChart.getAxisLeft().setEnabled(true);
//        sikdangComplexChart.getAxisLeft().setDrawGridLines(true);
//        sikdangComplexChart.getAxisLeft().setDrawAxisLine(true);
//        sikdangComplexChart.getAxisLeft().setDrawLabels(true); // 값 적는거 허용안함 (1,2,3)
//        sikdangComplexChart.getAxisLeft().setValueFormatter(new LargeValueFormatter());

//        sikdangComplexChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
//            private String[] gubun = {"-", "여유", "보통", "혼집"};
//            @Override
//            public String getAxisLabel(float value, AxisBase axis) {
//                Log.d("plz", "Yvalue : " + value);
//                return gubun[Math.round(value)];
//            }
//        });

//        sikdangComplexChart.getAxisLeft().setDrawGridLines(false); // 격자 라인
//        sikdangComplexChart.getAxisLeft().setDrawAxisLine(false); // 축 그리기


        sikdangComplexChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        sikdangComplexChart.getXAxis().setGranularity(1f); // 간격
        sikdangComplexChart.getXAxis().setDrawGridLines(false); // 격자
        sikdangComplexChart.getXAxis().setDrawAxisLine(true); // 축 그림
        sikdangComplexChart.getXAxis().setDrawLabels(true); // 라벨

        sikdangComplexChart.getXAxis().setValueFormatter(new ValueFormatter() {
            private String[] gubun = {"1차", "2차", "3차", "", ""};
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
        // sikdangs 에서 해당 식당코드로 리뷰정보 가져오기
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseFirestore.collection("sikdangs").document(sikdang.getId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d("plz", "DocumentSnapshot data: " + document.getData());
                        // 식당의 종합정보 세팅
                        sikdang = document.toObject(Sikdang.class);
                        setSikdangInfo();
                        sikdangComplexChart.notifyDataSetChanged();
                        sikdangComplexChart.invalidate();

                        Log.d("plz", "[" + sikdang.getPlace_name() + "] 의 평균 맛평점 : " + sikdang.getAvgTaste());

                        document.getReference().collection("reviews").orderBy("timestamp", Query.Direction.ASCENDING).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                if (task.isSuccessful()) {
                                    // 식당의 리뷰들 가져오기
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

                                        // 해당 리뷰의 등록자 정보 가져와서 저장
                                        setReviewerInfo (reviewSetList.size()-1, review.getReviewerUid());

                                        // 해당 리뷰의 사진 가져오기
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
                    reviewSetList.get(pos).user = task.getResult().getValue(User.class);;
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
                            Log.d("plz", "item.getPath() : " + item.getPath());
                            // reference 의 아이템(이미지) url 받아오기
                            item.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("plz", "task.getResult() : " + task.getResult() );
                                        images.add(task.getResult());
                                        ((ReviewAdapter)reviewListInDetailView.getAdapter()).notifyDetailImageViewAdaper();
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

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        return true;
    }


    // inner Class : 리뷰아이템을 구성하는 클래스
    public class ReviewSet {
        private Review review;
        private User user;
        private List<Uri> images;

        ReviewSet() {
        }

        public ReviewSet(Review review, User user, List<Uri> images) {
            this.review = review;
            this.user = user;
            this.images = images;
        }

        public List<Uri> getImages() {
            return images;
        }

        public Review getReview() {
            return review;
        }

        public User getUser() {
            return user;
        }
    }


    // inner Class : 디테일 프래그먼트의 리뷰 리스트에 넣을 리사이클러 어댑터
    public class ReviewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<ReviewSet> reviewList;
        private DetailImageViewAdapter detailImageViewAdapter;

        public ReviewAdapter(List<ReviewSet> reviewList) {
            this.reviewList = reviewList;
        }

        public void notifyDetailImageViewAdaper() {
            if (detailImageViewAdapter != null)
                detailImageViewAdapter.notifyDataSetChanged();
        }


        // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_detail_sikdang, parent, false);

            return new ReviewAdapter.ReviewItemHolder(view);
        }


        // position 에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            ReviewSet reviewSet = reviewList.get(position);

            Timestamp uploadDate = reviewSet.getReview().getTimestamp();
            User user = reviewSet.getUser();
//            String uploadUser = reviewSet.getUser().getUser_name();
            double ratingTaste = reviewSet.getReview().getTaste();
            List<Uri> userImages = reviewSet.getImages();

             detailImageViewAdapter = new DetailImageViewAdapter(userImages);
//            ((ReviewItemHolder) holder).getImageListInDetailView().setHasFixedSize(true);
            ((ReviewItemHolder) holder).getImageListInDetailView().setLayoutManager(new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false));
            ((ReviewItemHolder) holder).getImageListInDetailView().setAdapter(detailImageViewAdapter);

            DateFormat format = new SimpleDateFormat("yyyy-MM-dd (HH:mm:ss)");
            ((ReviewItemHolder) holder).getTextUploadDate().setText(format.format(uploadDate.toDate()));
            String reviewerInfo = user.getUser_name() + " " + user.getUser_position() + " (" + user.getBranch_name() + ")";
            ((ReviewItemHolder) holder).getTextUploadUser().setText(reviewerInfo);
            ((ReviewItemHolder) holder).getRatingTasteAvg().setRating((float) ratingTaste);
//            ((ReviewItemHolder)holder).getImageListInDetailView().setAdapter();
        }


        @Override
        public int getItemCount() {
            if (reviewList != null)
                return reviewList.size();
            else
                return 0;
        }

        // inner or inner Class : 이미지리스트 안에 들어갈 아이템
        public class ReviewItemHolder extends RecyclerView.ViewHolder {
            private TextView textUploadDate;
            private TextView textUploadUser;
            private RatingBar ratingTaste;
            private RecyclerView imageListInDetailView;


            ReviewItemHolder(View itemView) {
                super(itemView);
                textUploadDate = itemView.findViewById(R.id.textUploadDate);
                textUploadUser = itemView.findViewById(R.id.textUploadUser);
                ratingTaste = itemView.findViewById(R.id.ratingTaste);
                imageListInDetailView = itemView.findViewById(R.id.imageListInDetailView);

                //클릭 이벤트 달고 싶으면 itemView.setOnClickListener
            }

            public TextView getTextUploadDate() {
                return textUploadDate;
            }

            public TextView getTextUploadUser() {
                return textUploadUser;
            }

            public RatingBar getRatingTasteAvg() {
                return ratingTaste;
            }

            public RecyclerView getImageListInDetailView() {
                return imageListInDetailView;
            }
        }
    }





    // inner of inner Class : 리뷰 내 이미지리스트 넣을 리사이클러 어댑터
    public class DetailImageViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private List<Uri> uriList;

        public DetailImageViewAdapter(List<Uri> uriList) {
            this.uriList = uriList;
        }

        // 아이템 뷰를 위한 뷰홀더 객체를 생성하여 리턴
        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.item_image, parent, false);

            ImageView ima_bigger_frame = (ImageView) view.findViewById(R.id.imageItem);
            ima_bigger_frame.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics());
            ima_bigger_frame.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 110, getResources().getDisplayMetrics());;
            ima_bigger_frame.requestLayout();

            return new DetailImageViewAdapter.ImageItemHolder(view);
        }


        // position 에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시
        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
            Uri item = uriList.get(position);

            Glide.with(context)
                    .load(item)
                    .into(((DetailImageViewAdapter.ImageItemHolder) holder).getImageView());
        }

        @Override
        public int getItemCount() {
            if (uriList != null)
                return uriList.size();
            else
                return 0;
        }


        // inner or inner Class : 이미지리스트 안에 들어갈 아이템
        public class ImageItemHolder extends RecyclerView.ViewHolder {

            private ImageView imageView;

            ImageItemHolder(View itemView) {
                super(itemView);
                imageView = itemView.findViewById(R.id.imageItem);
                //클릭 이벤트 달고 싶으면 itemView.setOnClickListener
            }
            public ImageView getImageView() {
                return imageView;
            }
        }
    }

}