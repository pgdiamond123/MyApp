package nnthien.com.myapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.auth.session.QBSettings;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;

import nnthien.com.myapp.Adapter.ChatDialogsAdapters;

public class LoginActivity extends AppCompatActivity {

    Button btnLogin;
    TextView btnSignup;
    EditText edtUser,edtPassword;

    static final String APP_ID = "59588";
    static final String AUTH_KEY = "Pph9yvBpbchNYvk";
    static final String AUTH_SECRET = "Zj5YhWx-4Agyz7U";
    static final String ACCOUNT_KEY = "3e7LbZvyMU_f6KMtTdTN";
    static final int REQUEST_CODE = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btnLogin = (Button) findViewById(R.id.main_btnLogin);
        btnSignup = (TextView) findViewById(R.id.main_btnSignup);
        edtUser = (EditText) findViewById(R.id.main_editLogin);
        edtPassword = (EditText) findViewById(R.id.main_editPassword);

        initializeFramework();

        btnSignup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this,SignupActivity.class));
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String user = edtUser.getText().toString();
                final String password = edtPassword.getText().toString();
                QBUser qbUser = new QBUser(user,password);
                QBUsers.signIn(qbUser).performAsync(new QBEntityCallback<QBUser>() {
                    @Override
                    public void onSuccess(QBUser qbUser, Bundle bundle) {
                        Toast.makeText(getBaseContext(),"Login successfully",Toast.LENGTH_SHORT).show();

                        Intent intent = new Intent(LoginActivity.this,MainActivity.class);
                        intent.putExtra("user",user);
                        intent.putExtra("password",password);
                        startActivity(intent);

                        finish(); //Close login activity after logged
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
    private void initializeFramework() {
        QBSettings.getInstance().init(getApplicationContext(),APP_ID,AUTH_KEY,AUTH_SECRET);
        QBSettings.getInstance().setAccountKey(ACCOUNT_KEY);
    }
}
