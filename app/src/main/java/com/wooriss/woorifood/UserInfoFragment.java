package com.wooriss.woorifood;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class UserInfoFragment extends Fragment {

    private Context context;

    private FirebaseUser f_user;
    private User user;

    private TextView textTitle;
    private TextView textUsername;
    private TextInputLayout textUserMail;
    private TextView textUserJoinDate;
    private TextView textUserRecentDate;

    private EditText editBranch;
    private TextInputLayout textUserBranchAddr;

    private AutoCompleteTextView classTextView;

    private Button btnEdit;


    public UserInfoFragment() { }

    public static UserInfoFragment newInstance (Bundle bundle) {
        UserInfoFragment userInfoFragment2 = new UserInfoFragment();
        userInfoFragment2.setArguments(bundle);
        return userInfoFragment2;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            f_user = getArguments().getParcelable("f_user");
            user = (User) getArguments().getSerializable("user");
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


    private void setInfoView() {

        textTitle.setText(user.getUser_name()+"???\n???????????????!");
        textUsername.setText(user.getUser_name());
        textUserMail.setHelperText( user.getUser_mail());

        DateFormat format = new SimpleDateFormat("yyyy-MM-dd (HH:mm:ss)");
        textUserJoinDate.setText(format.format(new Date(f_user.getMetadata().getCreationTimestamp())));
        textUserRecentDate.setText(format.format(new Date(f_user.getMetadata().getLastSignInTimestamp())));

        editBranch.setText(user.getBranch_name());
        textUserBranchAddr.setHelperText(user.getBranch_addr());

        // false ?????? Filtering ?????? ??????
        classTextView.setText(user.getUser_position(),false);
    }

    private void findViews(View v) {

        textTitle = v.findViewById(R.id.text_title);
        textUsername = v.findViewById(R.id.name_edit_text);
        textUserMail = v.findViewById(R.id.name_text_input);
        textUserJoinDate = v.findViewById(R.id.text_userJoinDate);
        textUserRecentDate = v.findViewById(R.id.text_userRecentDate);

        // branch_text_input
        editBranch = v.findViewById(R.id.branch_edit_text);
        editBranch.setInputType(InputType.TYPE_NULL);
        textUserBranchAddr = v.findViewById(R.id.branch_text_input);

        btnEdit = v.findViewById(R.id.save_button);

        classTextView = v.findViewById(R.id.class_text_view);

        // DropDown Setting
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                context,
                R.layout.dropdown_menu_item,
                context.getResources().getStringArray(R.array.positions)
        );
        classTextView.setAdapter(adapter);

//        ArrayAdapter<String> adapter_pos = new ArrayAdapter<>(
//                context,
//                android.R.layout.simple_spinner_item,
//                context.getResources().getStringArray(R.array.positions)
//        );
//        adapter_pos.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
//        spinPosition.setAdapter(adapter_pos);
//
//        // ????????? ?????? ????????? ??????
//        spinPosition.setSelection(adapter_pos.getPosition(user.getUser_position()));

        addListenerToBranchEdit();
    }

    // ???????????? ?????? ???
    private void addListenerToEditBtn() {
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (isUserInfoChanged())
                    new AlertDialog.Builder(context)
                            .setTitle("?????? ??????")
                            .setMessage("?????????????????????????")
//                            .setIcon(android.R.drawable.ic_menu_save)
                            .setCancelable(false)
                            .setPositiveButton("???", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {

                                    ((MainActivity)context).updateUserInfo(
                                            editBranch.getText().toString(),
                                            textUserBranchAddr.getHelperText().toString(),
                                            classTextView.getText().toString());
                                    //spinPosition.getSelectedItem().toString());

                                }})
                            .setNegativeButton("?????????", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    // ?????? ??? ?????? ??????
                                }
                            })
                            .show();
                else
                    Toast.makeText(context, "??????????????? ????????????!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ?????? ???????????? ?????? ??? ?????? ????????? ?????? (???????????? ????????? : ??????, ??????)
    private boolean isUserInfoChanged() {

        if ((editBranch.getText().toString().equals(user.getBranch_name())) &&
                (classTextView.getText().toString().equals(user.getUser_position()))) {

            Log.d("plz", "in ifff");
            return false;
        }
        Log.d("plz", "not in iffff");
        return true;
    }

    // ????????? ????????? ?????? ????????? ??? ????????? ??????
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
                            textUserBranchAddr.setHelperText(_branch_info.get("branch_addr"));
                        }
                    }
                });
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("plz", "UserInfoFragment is destroy");
    }

}
