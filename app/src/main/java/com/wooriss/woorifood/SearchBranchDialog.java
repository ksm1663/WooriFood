package com.wooriss.woorifood;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;


/*
 - 작성일 : 2021.10.19
 - 작성자 : 김성미
 - 기능 : 지점명 검색하는 다이얼로그
 - 비고 :
 - 수정이력 :
*/

public class SearchBranchDialog  extends Dialog {

    private Context context;

    private ListView listView;
    private EditText editSearch;
    private ImageButton btnCancel;
    private ImageButton btnBack;

    private ArrayList<HashMap<String, String>> branchList;
    private ArrayList<HashMap<String, String>> backUpList;
    private SimpleAdapter listAdapter;

    private SQLiteDatabase db;

    public interface ICustomDialogEventListener {
        void customDialogEvent(HashMap<String, String> branch_info);
    }
    private ICustomDialogEventListener onCustomDialogEventListener;

    // In the constructor, you set the callback
    public SearchBranchDialog(@NonNull Context context, ICustomDialogEventListener onCustomDialogEventListener) {
        super(context);
        this.onCustomDialogEventListener = onCustomDialogEventListener;
        this.context = context;

        setCanceledOnTouchOutside(false);
        setCancelable(false);

        show();

    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView (R.layout.dialog_searchbranch);

        findViews();

        addListenerToEditSearch();
        addListenerToListItem();
        addListenerToCancelBtn();
        addListenerToBackBtn();

        Cursor cursor = viewData();

        Log.d("plz", "cursor Size : "+ cursor.getCount());
        // SELECT 결과값 리스트에 저장
        // String code = c.getString(c.getColumnIndexOrThrow("code")).replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");


        while (cursor.moveToNext()) {
            HashMap<String, String> map = new HashMap<>();
            String branch_name = cursor.getString(2).replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");
            String branch_addr = cursor.getString(6).replaceAll("(^\\p{Z}+|\\p{Z}+$)", "");

            map.put("branch_name", branch_name);
            map.put("branch_addr", branch_addr);
            branchList.add(map);
        }

        // 리스트뷰에 지점명어댑터 연결
        listAdapter = new SimpleAdapter(context,
                branchList,
                android.R.layout.simple_list_item_1,
                new String[]{"branch_name"},
                new int[]{android.R.id.text1});

        listView.setAdapter(listAdapter);


        Log.d("plz", "저장된 지점 수 : " + branchList.size());
        // 검색기능에 필요한 리스트 원본 복사본
        backUpList.addAll(branchList);
    }

    // 쿼리 요청 후 반환 : 우리은행 지점만 찾아옴
    private Cursor viewData () {
        db = new DBHelper(context).getReadableDatabase();
        String query = "Select * from " + DBHelper.TABLE_NAME + " where name like \"우리%\"" + " and (code like \"020%\" or code like \"20%\")";
        return db.rawQuery(query, null);
    }

    private void findViews() {

        listView = findViewById(R.id.list_branch);
        editSearch = findViewById(R.id.edit_search);
        // SPAN_EXCLUSIVE_EXCLUSIVE spans cannot have a zero length 땜에 넣었는데 안없어지넹 (삼성키보드-안드로이드 버그)
        editSearch.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
//        editSearch.setInputType(InputType.TYPE_NULL);
        btnCancel = findViewById(R.id.btn_cancel);
        btnBack = findViewById(R.id.btn_search_back);

        branchList = new ArrayList<>(); // HashMap<String, String>
        backUpList = new ArrayList<>(); // <String>

    }

    // 검색창에 문자 입력될 때마다 검색 수행 후 리스트뷰 갱신
    private void addListenerToEditSearch() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void afterTextChanged(Editable editable) {
                // input창에 문자를 입력할때마다 호출된다.
                // search 메소드를 호출한다.
                String text = editSearch.getText().toString();
                searchBranch(text);
            }
        });
    }

    // 리스트뷰에서 아이템 클릭했을 때 이벤트
    private void addListenerToListItem () {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Log.d("plz", "/ i : " + i + " / l : " + l);
                HashMap<String, String> hm = (HashMap<String, String>) adapterView.getItemAtPosition(i);
                String selBranchName = hm.get("branch_name");
                editSearch.setText(selBranchName);
//                editSearch.setText(adapterView.getItemAtPosition(i).toString().replaceAll("\\s",""));
                closeDialog(hm);
            }
        });
    }

    // 취소버튼 누르면 검색텍스트창 클리어
    private void addListenerToCancelBtn () {
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editSearch.setText("");
            }
        });
    }

    // 뒤로버튼 누르면 검색 다이얼로그 닫기 (전달 값 없음)
    private void addListenerToBackBtn() {
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                closeDialog(null);
            }
        });

    }

    // 검색어 입력할 때마다 리스트뷰 검색해서 해당되는 리스트로 갱신
    private void searchBranch(String str) {
        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        branchList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (str.length() == 0) {
            branchList.addAll(backUpList);
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < backUpList.size(); i++) {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (Objects.requireNonNull(backUpList.get(i).get("branch_name")).toLowerCase().contains(str)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    branchList.add(backUpList.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        listAdapter.notifyDataSetChanged();
    }

    private void closeDialog(HashMap<String,String> branch_info) {
        onCustomDialogEventListener.customDialogEvent(branch_info);
        db.close();
        dismiss();
    }



}
