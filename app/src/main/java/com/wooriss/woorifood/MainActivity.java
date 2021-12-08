package com.wooriss.woorifood;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDialog;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/*
 - 작성일 : 2021.10.03
 - 작성자 : 김성미
 - 기능 : 메인 화면
 - 비고 :
 - 수정이력 :
*/


//    https://material.io/components/bottom-navigation/android#using-bottom-navigation
//    https://developer.android.com/training/basics/fragments/pass-data-between?hl=ko#java

public class MainActivity extends AppCompatActivity implements UpdateDataService {

    // NavigationBarView 는 부모타입! (부모니까 자식객체 받기 가능)
    // 자식인 BottomNavigationView 로 하게 되면, 아이템선택이벤트가 deprecated 되어있음 ..
    private NavigationBarView navigationBarView;

//    private FragmentManager fManager; //androidx.fragment.app.FragmentManager
    private Button btnMain;

    public static FirebaseUser f_user;
    public static User user;

    private FirebaseDatabase mDatabase;
    private FirebaseFirestore fireStore;


    public static FoodLocation foodLocation;

    //private SQLiteDatabase sqlDb;

//    private HashMap<String, String> branch_info;


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        overridePendingTransition(R.anim.ani_fadein,R.anim.ani_fadeout);
        setContentView(R.layout.activity_main);

        initFirebase();

        findViews();

        addListenerToNavigationBar();

        transferTo(MainListFragment.newInstance());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private void initFirebase() {
        mDatabase = FirebaseDatabase.getInstance();
        fireStore = FirebaseFirestore.getInstance();

    }
    // 화면 구성요소 정의
    private void findViews() {

         navigationBarView = findViewById(R.id.bottom_navigation);

//        btnMain = findViewById(R.id.btn_main);
//        btnMain.setOnClickListener(this);
//
//        fManager = getSupportFragmentManager();
//        mainFragment = new MainFragment();




        Intent intent = getIntent();
        f_user = intent.getParcelableExtra("FUSER_INFO");
        user = (User) intent.getSerializableExtra("USER_INFO");
//        branch_info = (HashMap<String, String>) intent.getSerializableExtra("BRANCH_INFO");


    }


    @Override
    protected void onDestroy() {
        Log.d("plz", "MainActivity is Destroied!!!!!");
        super.onDestroy();
    }

    // 네비게이션 바 이벤트
    private void addListenerToNavigationBar() {
        // 메뉴 클릭되었을 때 이벤트
        navigationBarView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
//                Log.d("plz", "entry : " + getFragmentManager().getBackStackEntryCount());
                switch (item.getItemId()) {
                    //
                    case R.id.page_1:// Respond to navigation item 1 click
//                        transferTo(FavoritesFragment.newInstance("param1", "param2"));
                        transferTo(MainListFragment.newInstance());

                        return true;
                    case R.id.page_2:// Respond to navigation item 2 click
                        //transferTo(MusicFragment.newInstance("param1", "param2"));
                        transferTo(SearchFragment.newInstance());

                        return true;
                    case R.id.page_3:// Respond to navigation item 3 click
                        //transferTo(PlacesFragment.newInstance("param1", "param2"));
                        return true;
                    case R.id.page_4:// Respond to navigation item 4 click
                        //transferTo(NewsFragment.newInstance("param1", "param2"));
//                        transferTo(UserInfoFragment.newInstance());
                        Bundle bundle = new Bundle();
                        bundle.putParcelable("f_user", f_user);
                        bundle.putSerializable("user", user);

//                        UserInfoFragment userInfoFragment = new UserInfoFragment();
//                        userInfoFragment.setArguments(bundle);
                        UserInfoFragment userInfoFragment = UserInfoFragment.newInstance(bundle);
                        transferTo(userInfoFragment);

                        return true;
                    default:
                        return false;
                }
            }
        });

        // 똑같은 메뉴 또 클릭되었을 떄 이벤트 : 아무것도 안하기!
        navigationBarView.setOnItemReselectedListener(new NavigationBarView.OnItemReselectedListener() {
            @Override
            public void onNavigationItemReselected(@NonNull MenuItem item) {
            }
        });
    }

    private void transferTo(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    public void transferToReview(Sikdang sikdang) {
//        ReviewFragment reviewFragment = new ReviewFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("f_user", f_user);
        bundle.putSerializable("user", user);
        bundle.putSerializable("sikdang", sikdang);
        ReviewFragment reviewFragment = ReviewFragment.newInstance(bundle);
        if (!reviewFragment.isVisible()) {
//            getSupportFragmentManager().beginTransaction().add(R.id.main_container, reviewFragment).addToBackStack(null).commit();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.ani_slid_in_top, R.anim.ani_slid_out_top
                    , R.anim.ani_slid_in_top, R.anim.ani_slid_out_top).replace(R.id.main_container, reviewFragment).addToBackStack(null).commit();
        }
    }

    public void transferToDetail(Sikdang sikdang) {
//        DetailFragment detailFragment = new DetailFragment();

        Bundle bundle = new Bundle();
        bundle.putParcelable("f_user", f_user);
        bundle.putSerializable("user", user);
        bundle.putSerializable("sikdang", sikdang);
        DetailFragment detailFragment = DetailFragment.newInstance(bundle);
        if (!detailFragment.isVisible()) {
//            getSupportFragmentManager().beginTransaction().add(R.id.main_container, detailFragment).addToBackStack(null).commit();
            getSupportFragmentManager().beginTransaction().setCustomAnimations(R.anim.ani_slid_in_bottom, R.anim.ani_slid_out_bottom
            , R.anim.ani_slid_in_bottom, R.anim.ani_slid_out_bottom).add(R.id.main_container, detailFragment).addToBackStack(null).commit();
        }
    }



    // 유저 수정 정보 업데이트
    @Override
    public void updateUserInfo(String newBranchName, String newBranchAddr, String newUserPosition) {
        Log.d("plz", "called updateUserInfo!!!! in MainActivity");

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/users/" + f_user.getUid() + "/" + "branch_name", newBranchName);
        childUpdates.put("/users/" + f_user.getUid() + "/" + "branch_addr", newBranchAddr);
        childUpdates.put("/users/" + f_user.getUid() + "/" + "user_position", newUserPosition);

        mDatabase.getReference().updateChildren(childUpdates).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("plz", "save successed");
                Toast.makeText(MainActivity.this, "저장을 완료했습니다.", Toast.LENGTH_SHORT).show();

                user.setBranch_name(newBranchName);
                user.setBranch_addr(newBranchAddr);
                user.setUser_position(newUserPosition);

                MainActivity.foodLocation.getBranchPosition(newBranchAddr);

            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("plz", "save failed");
                Toast.makeText(MainActivity.this, "저장 실패, 다시 시도해주세요", Toast.LENGTH_SHORT).show();
            }
        });
    }

/*
    private Task<Void> addRating(DocumentReference sikdangRef, float taste, Sikdang _sikdang) {
        // Create reference for new rating, for use inside the transaction
        DocumentReference ratingRef = sikdangRef.collection("reviews").document();
        Log.d("plz", ratingRef + "");
        // In a transaction, add the new rating and update the aggregate totals
        return fireStore.runTransaction(new Transaction.Function<Void>() {
            @Nullable
            @Override
            public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                Sikdang sikdang = transaction.get(sikdangRef).toObject(Sikdang.class);
                if (sikdang == null)
                    sikdang = _sikdang;

                // Compute new number of ratings
                int newNumRatings = sikdang.getNumRatings() + 1;

                // Compute new average rating (taste)
                double oldTasteTotal = sikdang.getAvgTaste() * sikdang.getNumRatings();
                double newAvgTaste = (oldTasteTotal + taste) / newNumRatings;

                // Set new sikdang info
                sikdang.setNumRatings(newNumRatings);
                sikdang.setAvgTaste(newAvgTaste);
                sikdang.setViewType(Code.ViewType.REVIEWED_SIKDANG);

                // Update sikdang
                transaction.set(sikdangRef, sikdang);

                // Update rating (taste)
                Review review = new Review(f_user.getUid(), taste);
                transaction.set(ratingRef, review, SetOptions.merge());

                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d("plz", "review added");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("plz", "error on collection group query: " + e.getMessage());
            }
        });
    }

    public void insertReview(Sikdang _sikdang) {
        addRating(fireStore.collection("sikdangs").document(_sikdang.getId()), 2, _sikdang);
    }
*/

    @Override
    public void getReviewedSikdangs (String x, String y, int innerMeter) {
        List<Sikdang> reviewedSikdangList = new ArrayList<>();
        fireStore.collection("sikdangs").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                QuerySnapshot result = task.getResult();
                List<DocumentSnapshot> documents = result.getDocuments();

                for (DocumentSnapshot doc: documents) {
                    Sikdang sikdang = doc.toObject(Sikdang.class);
                    int distance = distance(Double.valueOf(y), Double.valueOf(x), Double.valueOf(sikdang.getY()), Double.valueOf(sikdang.getX()));
//                    Log.d("plz", sikdang.getPlace_name() + "(" + sikdang.getId() + ") : " + distance + "m");
                    if ( distance <= innerMeter) {
                        sikdang.setDistance(Integer.toString(distance));
                        reviewedSikdangList.add(sikdang);
                        //MainListFragment.reviewdSikdangAdapter.notifyDataSetChanged();
//                        Log.d("plz", sikdang.getPlace_name() + "(" + sikdang.getId() + ") : " + distance + "m");
                    }
                }

                MainListFragment.mainListFragment.setReviewedSikdangList(reviewedSikdangList);
            }
        });
    }

    // 카메라권한 요청 결과 이벤트
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        Log.d("plz", "onRequestPermissionsResult");
        switch (requestCode){
            case Code.REQUEST_PERMISSION_CODE:
                Log.d("plz", "Code.REQUEST_PERMISSION_CODE");
                if (grantResults.length > 0) { // 요청한 권한 허용/거부 상태 한번에 체크
                    boolean isAllGranted = true;
                    for (int i=0; i <grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            isAllGranted = false;
                            break;
                        }
                    }

                    if (isAllGranted) { // 요청한 권한 모두 허용 시
                        // 다음 스탭
                    } else { // 허용하지 않은 권한이 있음. 필수/선택권한 여부에 따라 별도 처리 필요
                        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)
                        || !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        || !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                            // 다시 묻지 않기 체크하면서 권한 거부 됨 -> 앱 설정 화면으로 이동해 권한 직접 풀도록 유도
                            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).setData (Uri.parse("package:" +
                                    BuildConfig.APPLICATION_ID));
                            startActivity(intent);
                        } else {
                            // 접근 권한 거부 하였음.
                            Toast.makeText(this,"기능 사용을 위한 권한 동의가 필요합니다.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
//                if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
//                    //동의 했을 경우 .....
//                    Log.d("plz", "agreed CAMERA!!");
//                }else{
//                    //거부했을 경우
//                    Log.d("plz", "disagreed CAMERA!!");
//                    Toast.makeText(this,"기능 사용을 위한 권한 동의가 필요합니다.", Toast.LENGTH_SHORT).show();
//                }
//                break;
        }
    }

    public void chkReviewedSikdang(MyCallback mycallback, String sikdangId, int pos) {
//        Log.d("plz", "sikdangId of readData : " + sikdangId);
        DocumentReference docRef = fireStore.collection("sikdangs").document(sikdangId);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Sikdang sikdang = documentSnapshot.toObject(Sikdang.class);
//                int numRating = sikdang.getNumRatings();
//                Log.d("plz", "numRating of " + sikdangId + " : " + numRating);
                mycallback.onCallback(sikdang, sikdangId, pos);
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //
            }
        });
    }


    /**
     * 두 지점간의 거리 계산
     *
     * @param lat1 지점 1 위도
     * @param lon1 지점 1 경도
     * @param lat2 지점 2 위도
     * @param lon2 지점 2 경도
     * @return
     */
    private int distance(double lat1, double lon1, double lat2, double lon2) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        dist = dist * 1609.344;

        return (int)dist;
    }

    // This function converts decimal degrees to radians
    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }



    private Dialog progressDialog;
    public void progressON(Activity activity, String message) {

        if (activity == null || activity.isFinishing()) {
            return;
        }
        if (progressDialog != null && progressDialog.isShowing()) {
            //progressSET(message);
        } else {
            progressDialog = new AppCompatDialog(activity);
            progressDialog.setCancelable(false);
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.show();
        }


        final ImageView img_loading_frame = (ImageView) progressDialog.findViewById(R.id.iv_frame_loading);
        Glide.with(this).load(R.drawable.loading_spinner).into(img_loading_frame);

        TextView tv_progress_message = (TextView) progressDialog.findViewById(R.id.tv_progress_message);
        if (!TextUtils.isEmpty(message)) {
            tv_progress_message.setText(message);
        }
    }
    public void progressOFF() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
    }


    // wEhYv5XJ0Oq5z3cxtPqKhWBWQ0o=
    /*private void getAppKeyHash() {
        try {
            PackageInfo info = getPackageManager().getPackageInfo(getPackageName(), PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            Log.e("name not found", e.toString());
        }
    }*/

}
