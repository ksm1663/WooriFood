package com.wooriss.woorifood;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.loader.content.CursorLoader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ReviewFragment extends Fragment implements RatingBar.OnRatingBarChangeListener, RadioGroup.OnCheckedChangeListener {

    private Context context;

    private FirebaseUser f_user;
    private User user;
    private Sikdang sikdang;
    private TextView textSikdangName;
//
//    private Button btnPicture;
    private Button btnUpload;
    private RatingBar ratingTaste;

    private RadioGroup radioPrice;
    private RadioGroup radioVisit;
    private RadioGroup radioComplex;
    private RadioGroup radioLuxury;



    private EditText editComment;


    private ImageView btngetImage;

    private RecyclerView imageListView;
    private ArrayList<ReviewFragment.ImageViewItem> imageList;
    private ReviewFragment.ImageViewAdapter imageViewAdapter;

    private int price = 0;
    private int visit = 0;
    private int complex = 0;
    private int luxury = 0;

    private String comment = "";

//    private List<Uri> imageList;

    private ActivityResultLauncher<Intent> resultLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    new ActivityResultCallback<ActivityResult>() {
                        @Override
                        public void onActivityResult(ActivityResult result) {
                            if (result.getResultCode() == Activity.RESULT_OK) {

                                if (result.getData().getExtras() != null) // ????????? ?????? ??????
                                {
                                    handleCameraCapture(result.getData());
                                }
                                else {// ??????????????? ?????? ??????
                                    handleGalleryImage(result.getData());
                                }
                            }
                        }
                    });

    public ReviewFragment() {}

    public static ReviewFragment newInstance(Bundle bundle) {
        ReviewFragment reviewFragment = new ReviewFragment();
        reviewFragment.setArguments(bundle);
        return reviewFragment;
    }

    @Override
    public void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {

            f_user = getArguments().getParcelable("f_user");
            user = (User)getArguments().getSerializable("user");
            sikdang = (Sikdang)getArguments().getSerializable("sikdang");

            Log.d("plz", "???????????? ?????????????????? ????????? sikdang : " + sikdang);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container != null)
            context = container.getContext();

        return inflater.inflate(R.layout.fragment_review, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (imageList == null)
            Log.d("plz", "imageList is null!" );
        else
            Log.d("plz", "imageList size : " + imageList.size());

        findViews(view);

        addListenerToPictureBtn();
        addListenerToUploadBtn();

        ratingTaste.setOnRatingBarChangeListener(this); // onRatingChanged

        radioPrice.setOnCheckedChangeListener(this); //onCheckedChanged
        radioVisit.setOnCheckedChangeListener(this);
        radioComplex.setOnCheckedChangeListener(this);
        radioLuxury.setOnCheckedChangeListener(this);

        textSikdangName.setText(sikdang.getPlace_name());

        requestPermissions();
//        chkCameraPermission();

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("plz", "ReviewFragment is dead");
    }

    // ?????? ???????????? ??????
    private void findViews(View v) {

        btngetImage = v.findViewById(R.id.imageUploadBtn);

        textSikdangName = v.findViewById(R.id.textSikdangName);
//        btnPicture = v.findViewById(R.id.btn_picture);
        btnUpload = v.findViewById(R.id.btn_upload);
        ratingTaste = v.findViewById(R.id.ratingTaste);

        radioPrice = v.findViewById(R.id.radioPrice);
        radioVisit = v.findViewById(R.id.radioVisit);
        radioComplex = v.findViewById(R.id.radioComplex);
        radioLuxury = v.findViewById(R.id.radioLuxury);
        editComment = v.findViewById(R.id.editComment);


        imageListView = v.findViewById(R.id.imageListView);

        imageList = new ArrayList<>();
        imageViewAdapter = new ImageViewAdapter(imageList);
        imageListView.setAdapter(imageViewAdapter);
        imageListView.setLayoutManager(new LinearLayoutManager(context, RecyclerView.HORIZONTAL, false));


    }

    // ?????? ?????? ??? : ?????????/?????? ?????? ??? ??????
    private String[] galleryOrCamera = {"?????????", "?????????"};

    private void addListenerToPictureBtn() {
        btngetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(requestPermissions()) {
                    new MaterialAlertDialogBuilder(view.getContext())
                            .setTitle("?????? ??????")
                            .setItems(galleryOrCamera, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    launchIntent(i);
                                }
                            }).show();
                }
            }
        });
    }

    private void launchIntent (int i) {
        Intent intent;
        switch (i) {
            case 0 : // ?????????
                intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // ???????????? ??????
                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);

                resultLauncher.launch(Intent.createChooser(intent, "Get Album"));
//                intent = new Intent(Intent.ACTION_PICK);
//                intent.setType(MediaStore.Images.Media.CONTENT_TYPE);
//                intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true); // ???????????? ??????
//                intent.setData(MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                intent.setType("image/*");
//                intent.setAction(Intent.ACTION_GET_CONTENT);
//                resultLauncher.launch(intent);

                break;

            case 1 : // ?????????
                intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                resultLauncher.launch(intent);
                break;
        }
    }

    // ??????????????? ?????? ???
    @Override
    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
        if (ratingBar == ratingTaste) {
            ratingTaste.setRating(v);
        }
    }

    // ??????????????? ?????? ?????? ???
    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int i) {

        if (radioGroup == radioPrice) {
            switch (i) {
                case R.id.radioPriceCheap:
                    price = Code.PriceType.CHEAP;
                    break;
                case R.id.radioPriceNormal:
                    price = Code.PriceType.NORMAL;
                    break;
                case R.id.radioPriceExpensive:
                    price = Code.PriceType.EXPENSIVE;
                    break;
            }
        } else if (radioGroup == radioVisit) {
            switch (i) {
                case R.id.radioVisitFirst:
                    visit = Code.VisitType.FIRST;
                    break;
                case R.id.radioVisitSecond:
                    visit = Code.VisitType.SECOND;
                    break;
                case R.id.radioVisitThird:
                    visit = Code.VisitType.THIRD;
                    break;
            }

        } else if (radioGroup == radioComplex) {
            switch (i) {
                case R.id.radioComplexCozy:
                    complex = Code.ComplexType.COZY;
                    break;
                case R.id.radioComplexNormal:
                    complex = Code.ComplexType.NORMAL;
                    break;
                case R.id.radioComplexBuzy:
                    complex = Code.ComplexType.BUZY;
                    break;
            }

        } else if (radioGroup == radioLuxury) {
            switch (i) {
                case R.id.radioLuxuryBad:
                    luxury = Code.LuxuryType.BAD;
                    break;
                case R.id.radioLuxuryNormal:
                    luxury = Code.LuxuryType.NORMAL;
                    break;
                case R.id.radioLuxuryGood:
                    luxury = Code.LuxuryType.GOOD;
                    break;
            }

        }

    }

    // ????????? ?????? ?????? : ???????????? ??????
    private void handleGalleryImage(Intent intent) {

        ClipData clipData = intent.getClipData();

        if (clipData == null) { // ????????? ????????? ???????????? ??????
            Uri uri = intent.getData();

            ImageViewItem item = new ImageViewItem();
            item.setImgUri(uri);
            imageList.add(item);
        }
        else {
            int size = clipData.getItemCount();

            Log.d("plz", "selected images : " + size);

            for (int i=0; i<size; i++) {
                Uri uri = clipData.getItemAt(i).getUri();
                ImageViewItem item = new ImageViewItem();
                item.setImgUri(uri);
                imageList.add(item);
            }
        }

//        imageViewAdapter.updateData(mainSikdangList);
        imageViewAdapter.notifyDataSetChanged();

    }

    // ???????????? ????????? ????????? ??????
    private void handleCameraCapture(Intent intent) {
        Bitmap bitmap = (Bitmap) intent.getExtras().get("data");
        if (bitmap != null) {
            String imageSaveUri = MediaStore.Images.Media.insertImage(((Activity)context).getContentResolver(),
                    bitmap, "?????? ??????", "?????? ????????? ?????????????????????.");
//            btnPicture.performClick();
            launchIntent (0);
        }
    }


    // ?????? ??????
    private boolean requestPermissions() {
        if ((ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                && ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
        {
            return true;
        }
        String[] permissions = {Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        ActivityCompat.requestPermissions(getActivity(), permissions, 0);

        return false;
    }


    // ????????? ?????? ?????? ?????????
    private Uri getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA };
        Cursor cursor = ((MainActivity)context).getContentResolver().query(contentUri, proj, null, null, null);
        cursor.moveToNext();
        @SuppressLint("Range") String path = cursor.getString(cursor.getColumnIndex(MediaStore.MediaColumns.DATA));
        Uri uri = Uri.fromFile(new File(path));

        cursor.close();
        Log.d("plz", "path : " + path);

        return uri;
//        return path;
    }

    // ???????????? ????????? ??? ????????? ?????????
    @Nullable
    public String createCopyAndReturnRealPath(@NonNull Uri uri) {
        String [] proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(context,uri,proj,null,null,null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(index);
    }





    // ?????? ??????!
    private void addListenerToUploadBtn() {
        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // ??????????????? ??????
                if(!chkValueInput()) {
                    Toast.makeText(context, "??????????????? ??????????????????!", Toast.LENGTH_SHORT).show();
                    Log.d("plz", "??????????????? ??? ??????");
                    return;
                }

                // ?????? ????????? ?????? : ????????????_yyyyMMddhhmmss_?????????uid
                // ???????????? ????????????????????? ??? ??????
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyyMMddhhmmss");
                String curTime = simpleDateFormat.format(new Date());
                String reviewDocName = sikdang.getId() + "_" + curTime + "_" + f_user.getUid();

                FirebaseFirestore fireStore = FirebaseFirestore.getInstance();
                //DocumentReference reviewRef = fireStore.collection("sikdangs").
                //       document(sikdang.getId()).collection("reviews").document(reviewDocName);

                DocumentReference sikdangRef = fireStore.collection("sikdangs")
                        .document(sikdang.getId());

                DocumentReference reviewRef = sikdangRef.collection("reviews").document(reviewDocName);


                callProgressOnOff(0);
                fireStore.runTransaction(new Transaction.Function<Void>() {
                    @Nullable
                    @Override
                    public Void apply(@NonNull Transaction transaction) throws FirebaseFirestoreException {
                        Sikdang _sikdang = transaction.get(sikdangRef).toObject(Sikdang.class);
                        if (_sikdang == null)
                            _sikdang = sikdang;

                        // Compute new number of ratings
                        int newNumRatings = _sikdang.getNumRatings() + 1;

                        // Compute new average rating (taste)
                        double oldTasteTotal = _sikdang.getAvgTaste() * _sikdang.getNumRatings();
                        double newAvgTaste = (oldTasteTotal + ratingTaste.getRating()) / newNumRatings;

                        float oldPriceTotal = _sikdang.getAvgPrice() * _sikdang.getNumRatings();
                        float newPriceTotal = (oldPriceTotal + price) / newNumRatings;

                        float oldLuxuryTotal = _sikdang.getAvgLuxury() * _sikdang.getAvgLuxury();
                        float newLuxuryTotal = (oldLuxuryTotal + luxury) / newNumRatings;

                        float oldComplexTotal;
                        float newComplexTotal;


                        int newNumComplex;
                        if (visit == Code.VisitType.FIRST) {
                            newNumComplex = _sikdang.getNumFirstComplex() + 1;
                            oldComplexTotal = _sikdang.getAvgFirstComplex() * _sikdang.getNumFirstComplex();
                            newComplexTotal = (oldComplexTotal + complex) / newNumComplex;
                            _sikdang.setAvgFirstComplex(newComplexTotal);
                            _sikdang.setNumFirstComplex(newNumComplex);

                        } else if (visit == Code.VisitType.SECOND) {
                            newNumComplex = _sikdang.getNumSecondComplex() + 1;
                            oldComplexTotal = _sikdang.getAvgSecondComplex() * _sikdang.getNumSecondComplex();
                            newComplexTotal = (oldComplexTotal + complex) / newNumComplex;
                            _sikdang.setAvgSecondComplex(newComplexTotal);
                            _sikdang.setNumSecondComplex(newNumComplex);

                        } else { // THIRD
                            newNumComplex = _sikdang.getNumThirdComplex() + 1;
                            oldComplexTotal = _sikdang.getAvgThirdComplex() * _sikdang.getNumThirdComplex();
                            newComplexTotal = (oldComplexTotal + complex) / newNumComplex;
                            _sikdang.setAvgThirdComplex(newComplexTotal);
                            _sikdang.setNumThirdComplex(newNumComplex);
                        }

                        // Set new sikdang info
                        _sikdang.setNumRatings(newNumRatings);
                        _sikdang.setAvgTaste(newAvgTaste);
                        _sikdang.setViewType(Code.ViewType.REVIEWED_SIKDANG);
                        _sikdang.setAvgPrice(newPriceTotal);
                        _sikdang.setAvgLuxury(newLuxuryTotal);

                        if (imageList.size() > 0)
                            _sikdang.setTitleImage(reviewDocName);

                        // Update sikdang
                        transaction.set(sikdangRef, _sikdang);

                        // Update rating (taste)
                        comment = editComment.getText().toString();
                        Review review = new Review(f_user.getUid(),  ratingTaste.getRating(), price, luxury, visit, complex, comment);
                        transaction.set(reviewRef, review, SetOptions.merge());

                        return null;
                    }
                }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("plz", "review added");

                        // ??????????????? ????????? ??????!
                        if (imageList.size() > 0)
                        {
                            uploadImage (curTime, reviewDocName);
                        } else {
                            callProgressOnOff(1);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d("plz", "error on collection group query: " + e.getMessage());
                    }
                });
            }
        });

    }

    // ???, ??????, ????????????, ???????????? ?????? ?????? ???
    private boolean chkValueInput () {
        if ((ratingTaste.getRating() > 0) && (price > 0) && (visit > 0) && (complex > 0) && (luxury > 0)) {
            return true;
        } else
            return false;
    }
    private void uploadImage(String filename, String pathname) {

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        // ?????? ???????????? ????????? ?????? 20191023142634
        //StorageReference imagRef = firebaseStorage.getReference("reviewImages/" + filename);
        StorageReference storageRef = firebaseStorage.getReference(); // ?????? (????????? ????????????) ?????? ?????????
        ReviewFragmentListener reviewFragmentListener = new ReviewFragmentListener(){
            @Override
            public void upLoadImages() {
                Log.d("plz", "?????? ????????? ????????? ?????? ??????!!");
                callProgressOnOff (1);
            }

            @Override
            public void uploadImageCallback(int cur, int end) {
                Log.d("plz", "uploadImageCallback");

                if (cur == end-1) {
                    Log.d("plz", "?????? ?????? ??? ?????????! ????????? ???????????? ?????? ?????? ?????????");
                    upLoadImages();
                }
            }
        };

        for (int i=0; i< imageList.size(); i++) {
//            StorageReference tmpRef = storageRef.child("reviewImages/" + pathname + imageList.get(i).getImgUri().getLastPathSegment());
            StorageReference tmpRef = storageRef.child("reviewImages/" + pathname +"/" + filename + "_" + i);
            Log.d("plz", "????????? ?????? ?????? : " + getRealPathFromURI(imageList.get(i).getImgUri()).getPath().toString());
            UploadTask uploadTask = tmpRef.putFile(getRealPathFromURI(imageList.get(i).getImgUri()));

            int tmp = i;
            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    reviewFragmentListener.uploadImageCallback(tmp, imageList.size());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    //
                }
            });
        }
    }

    private void callProgressOnOff(int flag) {

        if (flag == 0) // ??????
        {
            ((MainActivity) context).progressON(getActivity(), "?????? ???...");

        }else { // ???
            ((MainActivity) context).progressOFF();
            Toast.makeText(context, "?????? ??????!", Toast.LENGTH_SHORT).show();

            getActivity().getSupportFragmentManager().beginTransaction().remove(ReviewFragment.this).commit();
            getActivity().getSupportFragmentManager().popBackStack();
        }

    }




    public interface ReviewFragmentListener {
        void upLoadImages();
        void uploadImageCallback(int cur, int end);
    }




    // inner Class : ?????????????????? ?????? ??????????????? ?????????
    public class ImageViewAdapter extends RecyclerView.Adapter<ReviewFragment.ImageViewAdapter.ViewHolder> {
        private ArrayList<ReviewFragment.ImageViewItem> mList;

        public class ViewHolder extends RecyclerView.ViewHolder {
            ImageView imgView_item;
            public ViewHolder(@NonNull View itemView) {
                super(itemView);
                imgView_item = (ImageView) itemView.findViewById(R.id.imageItem);
            }
        }


        public ImageViewAdapter(ArrayList<ReviewFragment.ImageViewItem> mList) {
            this.mList = mList;
        }

        // ????????? ?????? ?????? ????????? ????????? ???????????? ??????
        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            Context context = parent.getContext();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = inflater.inflate(R.layout.item_image, parent, false);
            ReviewFragment.ImageViewAdapter.ViewHolder vh = new ReviewFragment.ImageViewAdapter.ViewHolder(view);
            return vh;
        }

        // position??? ???????????? ???????????? ???????????? ??????????????? ??????
        @Override
        public void onBindViewHolder(@NonNull ReviewFragment.ImageViewAdapter.ViewHolder holder, int position) {
            ReviewFragment.ImageViewItem item = mList.get(position);

//            holder.imgView_item.setImageResource(R.drawable.ic_launcher_background);   // ?????? ????????? ?????? ????????? ????????? ??????
            Glide.with(context)
                    .load(item.getImgUri())
                    .into(holder.imgView_item);
        }

        @Override
        public int getItemCount() {
            return mList.size();
        }
    }

    // inner Class : ?????????????????? ?????? ????????? ?????????
    public class ImageViewItem {
        private Uri mImageUri;

        public Uri getImgUri() {
            return mImageUri;
        }

        public void setImgUri(Uri mImageUri) {
            this.mImageUri = mImageUri;
        }
    }



}


