package nnthien.com.myapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.quickblox.auth.QBAuth;
import com.quickblox.auth.session.QBSession;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

public class SignupActivity extends AppCompatActivity {

    Button btnSignup,btnCancel;
    EditText edtUser,edtPassword,edtFullname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        registerSession();

        btnCancel = (Button)findViewById(R.id.signup_btnCancel);
        btnSignup = (Button)findViewById(R.id.signup_btnSignup);
        edtUser = (EditText)findViewById(R.id.signup_editLogin);
        edtPassword = (EditText) findViewById(R.id.signup_editPassword);
        edtFullname = (EditText) findViewById(R.id.signup_editfullname);
        btnCancel.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user = edtUser.getText().toString();
                String password = edtPassword.getText().toString();
                QBUser qbUser = new QBUser(user,password);
                qbUser.setFullName(edtFullname.getText().toString());
                QBUsers.signUp(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(getBaseContext(),"Signup successfully",Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void registerSession() {
        QBAuth.createSession().performAsync(new QBEntityCallback<QBSession>() {
            @Override
            public void onSuccess(QBSession qbSession, Bundle bundle) {

            }

            @Override
            public void onError(QBResponseException e) {
                Log.e("ERROR",e.getMessage());
            }
        });
    }
}
