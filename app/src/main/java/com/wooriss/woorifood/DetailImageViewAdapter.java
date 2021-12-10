package com.wooriss.woorifood;

import android.content.Context;
import android.net.Uri;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.ceylonlabs.imageviewpopup.ImagePopup;

import java.util.List;

public class DetailImageViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Uri> uriList;
    private Context context;


    public DetailImageViewAdapter(List<Uri> uriList, Context context) {
        this.context = context;
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
        ima_bigger_frame.getLayoutParams().height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, context.getResources().getDisplayMetrics());
        ima_bigger_frame.getLayoutParams().width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 90, context.getResources().getDisplayMetrics());;
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

        ImagePopup imagePopup = new ImagePopup(context);
        imagePopup.initiatePopupWithGlide(item.toString());
//        imagePopup.initiatePopup(((DetailImageViewAdapter.ImageItemHolder) holder).getImageView().getDrawable()); // Load Image from Drawable
//        클릭 이벤트 달고 싶으면 itemView.setOnClickListener
        ((DetailImageViewAdapter.ImageItemHolder) holder).getImageView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                imagePopup.viewPopup();
            }
        });

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

        }
        public ImageView getImageView() {
            return imageView;
        }
    }
}
