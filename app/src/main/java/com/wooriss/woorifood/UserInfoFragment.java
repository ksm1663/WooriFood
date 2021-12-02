package com.wooriss.woorifood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseUser;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UserInfoFragment extends Fragment {

//    private static UserInfoFragment userInfoFragment;

    private Context context;
//
//    private static final String ARG_PARAM1 = "param1";
//    private static final String ARG_PARAM2 = "param2";

    private FirebaseUser f_user;
    private User user;

    private TextView textUsername;
    private TextView textUserJoinDate;
    private TextView textUserRecentDate;
    private EditText editBranch;
    private TextView textUserBranchAddr;
    private Spinner spinPosition;
    private Button btnEdit;




    public UserInfoFragment() { }

    public static UserInfoFragment newInstance (Bundle bundle) {
        UserInfoFragment userInfoFragment = new UserInfoFragment();
        userInfoFragment.setArguments(bundle);
        return userInfoFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            f_user = getArguments().getParcelable("f_user");
            user = (User) getArguments().getSerializable("user");
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if(container!=null)
            context = container.getContext();
        return inflater.inflate(R.layout.fragment_user_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        findViews(view);
        setInfoView();

        addListenerToEditBtn();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("plz", "UserInfoFragment is destroy");
    }

    private void setInfoView() {

        textUsername.setText(user.getUser_name() + "(" + user.getUser_mail() + ")");

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd (HH:mm:ss)");
        textUserJoinDate.setText(format.format(new Date(f_user.getMetadata().getCreationTimestamp())));
        textUserRecentDate.setText(format.format(new Date(f_user.getMetadata().getLastSignInTimestamp())));

        editBranch.setText(user.getBranch_name());
        textUserBranchAddr.setText(user.getBranch_addr());


    }

    private void findViews(View v) {

        textUsername = v.findViewById(R.id.text_username);
        textUserJoinDate = v.findViewById(R.id.text_userJoinDate);
        textUserRecentDate = v.findViewById(R.id.text_userRecentDate);
        editBranch = v.findViewById(R.id.edit_branch);
//        editBranch.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
        editBranch.setInputType(InputType.TYPE_NULL);
        textUserBranchAddr = v.findViewById(R.id.text_userBranchAddr);
        spinPosition = v.findViewById(R.id.spin_position);
        btnEdit = v.findViewById(R.id.btn_edit);

        ArrayAdapter<String> adapter_pos = new ArrayAdapter<>(
                context, android.R.layout.simple_spinner_item, context.getResources().getStringArray(R.array.positions)
        );
        adapter_pos.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinPosition.setAdapter(adapter_pos);

        // 스피너 값은 여기서 세팅
        spinPosition.setSelection(adapter_pos.getPosition(user.getUser_position()));

        addListenerToBranchEdit();
    }

    // 수정버튼 클릭 시
    private void addListenerToEditBtn() {
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isUserInfoChanged())
                    new AlertDialog.Builder(context)
                            .setTitle("정보 수정")
                            .setMessage("저장하시겠습니까?")
//                            .setIcon(android.R.drawable.ic_menu_save)
                            .setCancelable(false)
                            .setPositiveButton("예", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    ((MainActivity)context).updateUserInfo(editBranch.getText().toString(),
                                                            textUserBranchAddr.getText().toString(), spinPosition.getSelectedItem().toString());

                                }})
                            .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // 취소 시 처리 로직
                                }
                            })
                            .show();
                else
                    Toast.makeText(context, "변경내역이 없습니다!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 기존 데이터와 변경 된 것이 있는지 확인 (변경가능 데이터 : 지점, 직급)
    private boolean isUserInfoChanged() {

        if ((editBranch.getText().toString().equals(user.getBranch_name())) &&
                (spinPosition.getSelectedItem().toString().equals(user.getUser_position()))) {

            Log.d("plz", "in ifff");
            return false;
        }
        Log.d("plz", "not in iffff");
        return true;
    }

    // 브랜치 텍스트 박스 눌렀을 때 이벤트 정의
    private void addListenerToBranchEdit() {
        editBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SearchBranchDialog(context, new SearchBranchDialog.ICustomDialogEventListener() {
                    @Override
                    public void customDialogEvent(HashMap<String, String> _branch_info) {
                        // Do something with the value here, e.g. set a variable in the calling activity
                        if (_branch_info != null) {
                            //branch_info = _branch_info;
                            editBranch.setText(_branch_info.get("branch_name"));
                            textUserBranchAddr.setText(_branch_info.get("branch_addr"));
                        }
                    }
                });
            }
        });
    }
}