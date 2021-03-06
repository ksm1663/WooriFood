package com.wooriss.woorifood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.ViewTarget;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.ListResult;
import com.google.firebase.storage.StorageReference;

import java.util.List;

public class SikdangAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Sikdang> sikdangs;
    private Uri titleUri;
    private Context context;

    SikdangAdapter(List<Sikdang> sikdangs) {
        if (sikdangs == null)
            this.sikdangs = sikdangs;

    }

    public void updateData(List<Sikdang> sikdangs) {
        this.sikdangs = sikdangs;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view;
         context = parent.getContext();
//        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        View itemView;
        Log.d("plz", "Code.ViewType : " + viewType);
        if (viewType == Code.ViewType.DEFAULT_SIKDANG) {
            Log.d("plz", "Code.ViewType.DEFAULT_SIKDANG" );
//            view = inflater.inflate(R.layout.item_sikdang, parent, false);
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sikdang, parent, false);
            return new SikdangItemViewHolder(itemView);
        } else
        {
            Log.d("plz", "Code.ViewType.REVIEWED_SIKDANG" );
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reviewed_sikdang_ver2, parent, false);
            return new ReviewedSikdangItemViewHolder(itemView);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return sikdangs.get(position).getViewType();
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        Sikdang sikdang = sikdangs.get(position);
        if (viewHolder instanceof SikdangItemViewHolder) {
            String place_name = sikdang.getPlace_name();
            String road_address_name = sikdang.getRoad_address_name();
            String distance = sikdang.getDistance();

            ((SikdangItemViewHolder)viewHolder).getItemPlaceNameView().setText(place_name);
            ((SikdangItemViewHolder)viewHolder).getItemRoadAddressNameView().setText(road_address_name);
            ((SikdangItemViewHolder)viewHolder).getItemDistanceView().setText(distance + "m");
        }
        else {
            String place_name = sikdang.getPlace_name();
            String road_address_name = sikdang.getRoad_address_name();
            String distance = sikdang.getDistance();

            String titleImage = sikdang.getTitleImage();

            int numRatings = sikdang.getNumRatings();
            double avgTaste = sikdang.getAvgTaste();
            float avgPrice = sikdang.getAvgPrice();
            float avgLuxury = sikdang.getAvgLuxury();

            Log.d("plz", "why?????? : " + avgTaste + "  /  " + numRatings);

            // Math.round(a * 10) / 10.0
            ((ReviewedSikdangItemViewHolder)viewHolder).getItemPlaceNameView().setText(place_name);
            ((ReviewedSikdangItemViewHolder)viewHolder).getItemRoadAddressNameView().setText(road_address_name);
            ((ReviewedSikdangItemViewHolder)viewHolder).getItemDistanceView().setText(distance + "m");
            ((ReviewedSikdangItemViewHolder)viewHolder).getItemReviewCnt().setText(numRatings+"");
            ((ReviewedSikdangItemViewHolder)viewHolder).getItemAvgTaste().setText(Math.round(sikdang.getAvgTaste()*10)/10.0 +"");
            ((ReviewedSikdangItemViewHolder)viewHolder).getItemAvgTasteBar().setRating((float) avgTaste);

            //(int)(sikdang.getAvgPrice()*10)
            ((ReviewedSikdangItemViewHolder)viewHolder).getItemAvgPrice().setProgress((int)(avgPrice*10));
            ((ReviewedSikdangItemViewHolder)viewHolder).getItemAvgLuxury().setProgress((int)(avgLuxury*10));

//            ((ReviewedSikdangItemViewHolder)viewHolder).getImgSikdangTitle().setImage
            Log.d("plz", "NOW : " + sikdang.getPlace_name() + " / ");
            if (titleImage != null) {
                Log.d("plz", "NOW : " + sikdang.getPlace_url() + " / ");
                getImgSikdangTitle(titleImage, ((ReviewedSikdangItemViewHolder) viewHolder).getImgSikdangTitle());
            }
            else
                ((ReviewedSikdangItemViewHolder) viewHolder).getImgSikdangTitle().setImageResource(R.drawable.ic_full);


        }
    }


    private void getImgSikdangTitle(String docName, ImageView imageView) {
        FirebaseStorage storageRef = FirebaseStorage.getInstance();
        StorageReference listRef = storageRef.getReference("reviewImages").child(docName);

        Log.d("plz", "listRef : " + listRef);
        listRef.listAll()
                .addOnSuccessListener(new OnSuccessListener<ListResult>() {
                    @Override
                    public void onSuccess(ListResult listResult) {
                        Log.d("plz", "onSuccess in downloadImage : " + listResult.getItems().size());
                        if (listResult.getItems().size() > 0) {
                            listResult.getItems().get(0).getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {

                                    if (task.isSuccessful()) {
                                        imageView.setImageURI(task.getResult());
                                        RequestOptions requestOptions = new RequestOptions();
                                        requestOptions = requestOptions.transform(new CenterCrop(), new RoundedCorners(55));
                                        Glide.with(context)
                                                .load(task.getResult())
                                                .apply(requestOptions)
//                                                .circleCrop()
                                                .into(imageView);
                                    }else {

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
    public int getItemCount() {
        if (sikdangs != null)
            return sikdangs.size();
        else
            return 0;
    }

    // inner Class - ?????? ?????? ?????? ????????? ?????????
    public class SikdangItemViewHolder extends RecyclerView.ViewHolder {
        private TextView itemPlaceNameView;
        private TextView itemRoadAddressNameView;
        private TextView itemDistanceView;

        SikdangItemViewHolder(View itemView) {
            super(itemView);
            itemPlaceNameView = itemView.findViewById(R.id.item_place_name);
            itemRoadAddressNameView = itemView.findViewById(R.id.item_road_address_name);
            itemDistanceView = itemView.findViewById(R.id.item_distance);

            // ??????????????? ??? ????????? ?????? ??? ?????????
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    //int pos = getAdapterPosition();
                    int pos = getAbsoluteAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {

                        Log.d("plz", SearchFragment.pageListSikdang.body().getDocuments().get(pos).getDistance());
                        Log.d("plz", SearchFragment.pageListSikdang.body().getDocuments().get(pos).getId());

                        new AlertDialog.Builder(view.getContext())
                                .setTitle(itemPlaceNameView.getText().toString())
                                .setMessage("????????? ?????????????????????????")
//                            .setIcon(android.R.drawable.ic_menu_save)
                                .setCancelable(false)
                                .setPositiveButton("???", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ((MainActivity)view.getContext()).transferToReview(sikdangs.get(pos));
//                                        ((MainActivity)view.getContext()).insertReview(SearchFragment.pageListSikdang.body().getDocuments().get(pos));

                                    }})
                                .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // ?????? ??? ?????? ??????
                                    }
                                })
                                .show();
                    }
                }
            });

        }
        public TextView getItemDistanceView() {
            return itemDistanceView;
        }
        public TextView getItemPlaceNameView() {
            return itemPlaceNameView;
        }
        public TextView getItemRoadAddressNameView() {
            return itemRoadAddressNameView;
        }
    }


    // inner Class - ?????? ?????? ????????? ?????????
    public class ReviewedSikdangItemViewHolder extends RecyclerView.ViewHolder implements View.OnTouchListener{
        private TextView itemPlaceNameView;
        private TextView itemRoadAddressNameView;
        private TextView itemDistanceView;
        private TextView itemReviewCnt;
        private TextView itemAvgTaste;
        private RatingBar itemAvgTasteBar;

        private SeekBar itemAvgPrice;
        private SeekBar itemAvgLuxury;

        private ImageView imgSikdangTitle;

        ReviewedSikdangItemViewHolder(View itemView) {
            super(itemView);
            itemPlaceNameView = itemView.findViewById(R.id.item_place_name);
            itemRoadAddressNameView = itemView.findViewById(R.id.item_road_address_name);
            itemDistanceView = itemView.findViewById(R.id.item_distance);
            itemReviewCnt = itemView.findViewById(R.id.item_reviewCnt);
            itemAvgTaste = itemView.findViewById(R.id.item_avgTaste);
            itemAvgTasteBar = itemView.findViewById(R.id.item_avgTasteBar);

            itemAvgPrice = itemView.findViewById(R.id.seekPrice_);
            itemAvgLuxury = itemView.findViewById(R.id.seekLuxury_);

            imgSikdangTitle = itemView.findViewById(R.id.imgSikdangTitle);

            itemAvgPrice.setOnTouchListener(this);
            itemAvgLuxury.setOnTouchListener(this);


            // ??????????????? ??? ????????? ?????? ??? ?????????
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //int pos = getAdapterPosition();
                    int pos = getAbsoluteAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {

                        // for test
                        if (MainListFragment.mainListFragment.isVisible()) {
                            if (MainListFragment.mainSikdangList != null) {
                                Log.d("plz", "pos : " + pos + ", mainSikdangList : " + MainListFragment.mainSikdangList.size());
                                Log.d("plz", MainListFragment.mainSikdangList.get(pos).getPlace_name());
                                Log.d("plz", MainListFragment.mainSikdangList.get(pos).getId());
                            }

                        }
                        else {
                            if  (SearchFragment.pageListSikdang != null) {
                                Log.d("plz", "pos : " + pos + ", searchSikdangList : " + SearchFragment.pageListSikdang.body().getDocuments().size());

                                Log.d("plz", SearchFragment.pageListSikdang.body().getDocuments().get(pos).getPlace_name());
                                Log.d("plz", SearchFragment.pageListSikdang.body().getDocuments().get(pos).getId());
                            }
                        }
                        // end for test

                        ((MainActivity)view.getContext()).transferToDetail(sikdangs.get(pos));
                    }
                }
            });

        }

        public TextView getItemDistanceView() {
            return itemDistanceView;
        }
        public TextView getItemPlaceNameView() {
            return itemPlaceNameView;
        }
        public TextView getItemRoadAddressNameView() {
            return itemRoadAddressNameView;
        }
        public RatingBar getItemAvgTasteBar() {
            return itemAvgTasteBar;
        }

        public SeekBar getItemAvgPrice() {
            return itemAvgPrice;
        }

        public SeekBar getItemAvgLuxury() {
            return itemAvgLuxury;
        }

        public TextView getItemAvgTaste() {
            return itemAvgTaste;
        }
        public TextView getItemReviewCnt() {
            return itemReviewCnt;
        }


        public ImageView getImgSikdangTitle() {
            return imgSikdangTitle;
        }


        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return true;
        }
    }
}