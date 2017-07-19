package nnthien.com.myapp;


import android.app.ProgressDialog;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import nnthien.com.myapp.Common.Common;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends android.support.v4.app.Fragment {


    EditText edtPassword,edtOldPassword,edtConfirmPassword;
    Button btnChange;
    public ChangePasswordFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View itemview = inflater.inflate(R.layout.fragment_change_password, container, false);

        initViews(itemview);

        btnChange.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String password = edtPassword.getText().toString();
                String oldpassword = edtOldPassword.getText().toString();
                String confirmpassword = edtConfirmPassword.getText().toString();

                QBUser user = new QBUser();
                user.setId(QBChatService.getInstance().getUser().getId());
                if(!Common.isNullOrEmptyString(oldpassword)
                        && !Common.isNullOrEmptyString(password)
                        && !Common.isNullOrEmptyString(confirmpassword)){
                    if(!confirmpassword.equals(password)){
                        Toast.makeText(getActivity(),"Comfirm Password error",Toast.LENGTH_SHORT).show();
                    }
                    else {
                        user.setOldPassword(oldpassword);
                        user.setPassword(password);
                        final ProgressDialog mDialog = new ProgressDialog(getActivity());

                        mDialog.setMessage("Please waiting...");
                        mDialog.show();

                        QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                            @Override
                            public void onSuccess(QBUser qbUser, Bundle bundle) {
                                Toast.makeText(getActivity(),"Password changed",Toast.LENGTH_SHORT).show();
                                mDialog.dismiss();
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Toast.makeText(getActivity(),"Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
                else {
                    if(Common.isNullOrEmptyString(password)){
                        edtPassword.requestFocus();
                    }
                    else if(Common.isNullOrEmptyString(oldpassword)){
                        edtOldPassword.requestFocus();
                    }
                    else if(Common.isNullOrEmptyString(confirmpassword)){
                        edtConfirmPassword.requestFocus();
                    }
                }
            }
        });

        return itemview;
    }
    private void initViews(View itemview) {
        btnChange = (Button)itemview.findViewById(R.id.change_btnChange);
        edtOldPassword = (EditText)itemview.findViewById(R.id.change_editOldPassword);
        edtPassword = (EditText)itemview.findViewById(R.id.change_editPassword);
        edtConfirmPassword = (EditText)itemview.findViewById(R.id.change_editConfirmPassword);

    }
}
