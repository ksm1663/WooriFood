package com.wooriss.woorifood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SikdangAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Sikdang> sikdangs;

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
//        Context context = parent.getContext();
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
            itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_reviewed_sikdang, parent, false);
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

        }
    }

    @Override
    public int getItemCount() {
        if (sikdangs != null)
            return sikdangs.size();
        else
            return 0;
    }

    // inner Class - 리뷰 이력 없는 리스트 아이템
    public class SikdangItemViewHolder extends RecyclerView.ViewHolder {
        private TextView itemPlaceNameView;
        private TextView itemRoadAddressNameView;
        private TextView itemDistanceView;

        SikdangItemViewHolder(View itemView) {
            super(itemView);
            itemPlaceNameView = itemView.findViewById(R.id.item_place_name);
            itemRoadAddressNameView = itemView.findViewById(R.id.item_road_address_name);
            itemDistanceView = itemView.findViewById(R.id.item_distance);

            // 리사이클뷰 내 아이템 클릭 시 이벤트
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
                                .setMessage("리뷰를 등록하시겠습니까?")
//                            .setIcon(android.R.drawable.ic_menu_save)
                                .setCancelable(false)
                                .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        ((MainActivity)view.getContext()).transferToReview(sikdangs.get(pos));
//                                        ((MainActivity)view.getContext()).insertReview(SearchFragment.pageListSikdang.body().getDocuments().get(pos));

                                    }})
                                .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        // 취소 시 처리 로직
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


    // inner Class - 리뷰 있는 리스트 아이템
    public class ReviewedSikdangItemViewHolder extends RecyclerView.ViewHolder {
        private TextView itemPlaceNameView;
        private TextView itemRoadAddressNameView;
        private TextView itemDistanceView;
        private TextView itemReviewCnt;
        private TextView itemAvgTaste;
        private RatingBar itemAvgTasteBar;

        private SeekBar itemAvgPrice;
        private SeekBar itemAvgLuxury;

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


            // 리사이클뷰 내 아이템 클릭 시 이벤트
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
    }
}