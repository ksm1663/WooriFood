package com.wooriss.woorifood;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;

import java.util.ArrayList;


/*
 - 작성일 : 2021.10.19
 - 작성자 : 김성미
 - 기능 : 지점명 검색하는 뷰
 - 비고 :
 - 수정이력 :
*/

public class SearchBranchDialog  extends Dialog {

    private Context context;

    private ListView listView;
    private EditText editSearch;

    private ArrayAdapter<String> listAdapter;
    private ArrayList<Branch> branchList;
    private ArrayList<String> branchNameList;
    private ArrayList<String> tmpList;

    public SearchBranchDialog(@NonNull Context context) {
        super(context);
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

        Cursor cursor = viewData();

        // 리스트뷰에 지점명어댑터 연결
        listAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, branchNameList);
        listView.setAdapter(listAdapter);

        // SELECT 결과값 리스트에 저장
        if (cursor!=null && cursor.moveToFirst()) {
            do {
                Branch b = new Branch(cursor.getString(0), cursor.getString(2), cursor.getString(6));
                branchList.add(b);
                branchNameList.add(b.getName());
            }while (cursor.moveToNext());
        }
        
        Log.d("plz", "저장된 지점 수 : " + branchList.size());

        // 검색기능에 필요한 리스트 원본 복사본
        tmpList.addAll(branchNameList);

    }

    private Cursor viewData () {
        Log.d("plz", "11111");
        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        String query = "Select * from " + DBHelper.TABLE_NAME + " where name like \"우리%\"" + " and (code like \"020%\" or code like \"20%\")";
        return db.rawQuery(query, null);
    }

    private void findViews() {

        listView = findViewById(R.id.list_branch);
        editSearch = findViewById(R.id.edit_search);

        branchList = new ArrayList<>(); // <Branch>
        branchNameList = new ArrayList<>(); // <String>
        tmpList = new ArrayList<>(); // <String>
    }

    // 검색창에 문자 입력될 때마다 검색 수행 후 리스트뷰 갱신
    private void addListenerToEditSearch() {
        editSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

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
//                editSearch.setText(tmpList.get(i));
//                Log.d("plz : ", i + " / " + adapterView.getSelectedItemPosition());
            }
        });
    }

    private void searchBranch(String str) {
        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        branchNameList.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (str.length() == 0) {
            branchNameList.addAll(tmpList);
        }
        // 문자 입력을 할때..
        else {
            // 리스트의 모든 데이터를 검색한다.
            for (int i = 0; i < tmpList.size(); i++) {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (tmpList.get(i).toLowerCase().contains(str)) {
                    // 검색된 데이터를 리스트에 추가한다.
                    branchNameList.add(tmpList.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        listAdapter.notifyDataSetChanged();
    }

}
