package com.wooriss.woorifood;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import retrofit2.Response;

public class MainFragment extends Fragment {

    private Context context;

    private TextView mainText;
    private EditText searchEdit;
    private Button searchBtn;

    private RecyclerView recyclerView;
    private SikdangAdapter adapter;


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if(container!=null)
            context = container.getContext();
        return inflater.inflate(R.layout.fragment_main, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);
        addListenerToSearchBtn();
    }


    // 화면 구성요소 정의
    private void findViews(View v) {

        mainText = v.findViewById(R.id.text_info);
        searchEdit = v.findViewById(R.id.edit_search);
        searchBtn = v.findViewById(R.id.btn_search);

        recyclerView = v.findViewById(R.id.sikdangList);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));

        adapter = new SikdangAdapter(null);
        recyclerView.setAdapter(adapter);

        String userId = ((MainActivity)context).getUser().getUserId();
        String userBranch = ((MainActivity)context).getUser().getBranch();
        String printMainText = userId +"(" + userBranch + ")";
        mainText.setText(printMainText);
    }

    private void addListenerToSearchBtn() {
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchSikdang = searchEdit.getText().toString();

                if (searchSikdang.length() == 0) {
                    Toast.makeText(context, "식당명을 입력하세요.", Toast.LENGTH_SHORT).show();
                    return;
                }
                new FoodLocation(MainFragment.this, ((MainActivity)context).getBranch().getAddr(), searchSikdang);
            }
        });
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setSikdangList (Response<PageListSikdang> response) {
        Log.d("plz", "짠");
//        adapter.notifyDataSetChanged();
        //adapter = new SikdangAdapter(response.body().getDocuments());
        //recyclerView.setAdapter(adapter);

        adapter.updateData(response.body().getDocuments());
        adapter.notifyDataSetChanged();

    }




    // inner class
    private class SikdangAdapter extends RecyclerView.Adapter<ItemViewHolder> {
        private List<Sikdang> sikdangs;

        SikdangAdapter(List<Sikdang> sikdangs) {
            if (sikdangs == null)
                this.sikdangs = sikdangs;
        }

        private void updateData(List<Sikdang> sikdangs) {
            this.sikdangs = sikdangs;
        }


        @NonNull
        @Override
        public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_sikdang, parent, false);
            return new ItemViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull ItemViewHolder holder, int position) {
            Sikdang sikdang = sikdangs.get(position);
            String place_name = sikdang.place_name;
            String road_address_name = sikdang.road_address_name;
            String distance = sikdang.distance;

            holder.getItemPlaceNameView().setText(place_name);
            holder.getItemRoadAddressNameView().setText(road_address_name);
            holder.getItemDistanceView().setText(distance);
        }

        @Override
        public int getItemCount() {
            if (sikdangs != null)
                return sikdangs.size();
            else
                return 0;
        }
    }


    // inner class
    private class ItemViewHolder extends RecyclerView.ViewHolder {
        private TextView itemPlaceNameView;
        private TextView itemRoadAddressNameView;
        private TextView itemDistanceView;

        ItemViewHolder(View itemView) {
            super(itemView);

            itemPlaceNameView = itemView.findViewById(R.id.item_place_name);
            itemRoadAddressNameView = itemView.findViewById(R.id.item_road_address_name);
            itemDistanceView = itemView.findViewById(R.id.item_distance);
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

}


