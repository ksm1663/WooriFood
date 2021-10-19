package com.wooriss.woorifood;

import android.app.Dialog;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Debug;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.ListMenuItemView;

import java.util.ArrayList;

public class SearchBranchDialog  extends Dialog {

    private Context context;

    private ListView listView;
//    private ArrayAdapter listAdapter;
    private ArrayAdapter<String> listAdapter;
    private ArrayList branchList;

    public SearchBranchDialog(@NonNull Context context) {
        super(context);
        this.context = context;

        setCanceledOnTouchOutside(false);
        setCancelable(false);

        Log.d("plz", "다이얼로그 입장!");

        show();

    }

    @Override
    protected void onCreate (Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView (R.layout.dialog_searchbranch);

        findViews();


        //branchList = new ArrayList<Branch>();
        branchList = new ArrayList<String>();

        Cursor cursor = viewData();

        Log.d("plz", "셀렉트 결과 : " + cursor.getCount());

        listAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, branchList);
        listView.setAdapter(listAdapter);


        if (cursor!=null && cursor.moveToFirst()) {
            do {
//                Log.d("plz", cursor.getString(0) + "");
                //branchList.add(new Branch(cursor.getString(0), cursor.getString(2), cursor.getString(6)));
                branchList.add(cursor.getString(2));
            }while (cursor.moveToNext());
        }

        //Log.d("plz", branchList.size() + "");


        //listAdapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, branchList);

        //Log.d("plz", "hmmm : " + listAdapter.getCount() + "");
        //

    }

    private Cursor viewData () {
        Log.d("plz", "11111");
        SQLiteDatabase db = new DBHelper(context).getReadableDatabase();
        String query = "Select * from " + DBHelper.TABLE_NAME + " where name like \"우리%\"" + " and (code like \"020%\" or code like \"20%\")";
        Cursor cursor = db.rawQuery(query, null);
        return cursor;
    }

    private void findViews() {
        listView = findViewById(R.id.list_branch);
    }

}
