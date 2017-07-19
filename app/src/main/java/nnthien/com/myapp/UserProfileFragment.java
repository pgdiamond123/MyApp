package nnthien.com.myapp;


import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.app.Fragment;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.quickblox.chat.QBChatService;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.users.QBUsers;
import com.quickblox.users.model.QBUser;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import nnthien.com.myapp.Common.Common;
import nnthien.com.myapp.Holder.QBUsersHolder;


/**
 * A simple {@link Fragment} subclass.
 */
public class UserProfileFragment extends android.support.v4.app.Fragment {


    EditText edtFullName,edtEmail,edtPhone;
    Button btnUpdate;

    ImageView user_avatar;

    TextView tvFullName;

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.user_update_menu,menu);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.user_update_log_out:
                logOut();
                break;
            default: break;
        }
        return true;
    }

    private void logOut() {
        QBUsers.signOut().performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                QBChatService.getInstance().logout(new QBEntityCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid, Bundle bundle) {
                        Toast.makeText(getActivity(),"You are logout!!!",Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(),MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        getActivity().finish();;
                    }

                    @Override
                    public void onError(QBResponseException e) {

                    }
                });
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == Activity.RESULT_OK){
            if (requestCode == Common.SELECT_PICTURE){
                Uri selectedImageUri = data.getData();
                final ProgressDialog mDialog = new ProgressDialog(getActivity());
                mDialog.setMessage("Please waiting...");
                mDialog.setCancelable(false);
                mDialog.show();

                try {
                    InputStream in = getActivity().getContentResolver().openInputStream(selectedImageUri);
                    final Bitmap bitmap = BitmapFactory.decodeStream(in);
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.PNG,100,bos);
                    File file = new File(Environment.getExternalStorageDirectory()+"/image.png");
                    FileOutputStream fileOut = new FileOutputStream(file);
                    fileOut.write(bos.toByteArray());
                    fileOut.flush();
                    fileOut.close();
                    int imageSizeKb = (int)file.length()/1024;
                    if(imageSizeKb >= (1024*100)){
                        Toast.makeText(getActivity(),"ERROR size",Toast.LENGTH_SHORT).show();
                        return;
                    }
                    //Upload file
                    QBContent.uploadFileTask(file,true,null).performAsync(new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            QBUser user = new QBUser();
                            user.setId(QBChatService.getInstance().getUser().getId());
                            user.setFileId(Integer.parseInt(qbFile.getId().toString()));

                            QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                                @Override
                                public void onSuccess(QBUser qbUser, Bundle bundle) {
                                    mDialog.dismiss();
                                    user_avatar.setImageBitmap(bitmap);
                                }

                                @Override
                                public void onError(QBResponseException e) {

                                }
                            });
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });
                }catch (FileNotFoundException e){
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadUserProfile() {
        //Load avatar
        QBUsers.getUser(QBChatService.getInstance().getUser().getId()).performAsync(new QBEntityCallback<QBUser>() {
            @Override
            public void onSuccess(final QBUser qbUser, Bundle bundle) {
                String email = qbUser.getEmail();
                String phone = qbUser.getPhone();
                String fullname = qbUser.getFullName();
                edtPhone.setText(phone);
                edtFullName.setText(fullname);
                edtEmail.setText(email);
                tvFullName.setText(fullname);
                QBUsersHolder.getInstance().putUser(qbUser);
                if(qbUser.getFileId() !=null){
                    int profilePictureId = qbUser.getFileId();
                    QBContent.getFile(profilePictureId).performAsync(new QBEntityCallback<QBFile>() {
                        @Override
                        public void onSuccess(QBFile qbFile, Bundle bundle) {
                            String fileURL = qbFile.getPublicUrl();
                            Picasso.with(getActivity().getBaseContext()).load(fileURL).resize(50,50).centerCrop().into(user_avatar);


                        }
                        @Override
                        public void onError(QBResponseException e) {
                            Log.e("ERROR_IMAGE",""+e.getMessage());
                        }
                    });
                }
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });


    }

    private void initViews(View itemview) {
        btnUpdate = (Button)itemview.findViewById(R.id.update_user_btn_update);
        edtEmail = (EditText)itemview.findViewById(R.id.update_edt_email);
        edtFullName = (EditText)itemview.findViewById(R.id.update_edt_full_name);
        edtPhone = (EditText)itemview.findViewById(R.id.update_edt_phone);
        tvFullName = (TextView) itemview.findViewById(R.id.profile_fullname);

        user_avatar = (ImageView)itemview.findViewById(R.id.user_avatar);
    }

    public UserProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View itemview = inflater.inflate(R.layout.fragment_user_profile, container, false);
        initViews(itemview);

        loadUserProfile();

        user_avatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent selectImage = new Intent();
                selectImage.setType("image/*");
                selectImage.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(selectImage,"Select Picture"), Common.SELECT_PICTURE);
            }
        });

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                Log.e("me", "me.clicked");

                String email = edtEmail.getText().toString();
                String phone = edtPhone.getText().toString();
                String fullname = edtFullName.getText().toString();

                QBUser user = new QBUser();
                user.setId(QBChatService.getInstance().getUser().getId());
                if(!Common.isNullOrEmptyString(fullname)
                        && !Common.isNullOrEmptyString(phone)
                        && !Common.isNullOrEmptyString(email)){
                    user.setFullName(fullname);
                    user.setPhone(phone);
                    user.setEmail(email);

                    final ProgressDialog mDialog = new ProgressDialog(getActivity());

                    mDialog.setMessage("Please waiting...");
                    mDialog.show();

                    QBUsers.updateUser(user).performAsync(new QBEntityCallback<QBUser>() {
                        @Override
                        public void onSuccess(QBUser qbUser, Bundle bundle) {
                            Toast.makeText(getActivity(),"User:"+qbUser.getLogin()+" updated",Toast.LENGTH_SHORT).show();
                            mDialog.dismiss();
                        }

                        @Override
                        public void onError(QBResponseException e) {
                            Toast.makeText(getActivity(),"Error:"+e.getMessage(),Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else {
                    if(Common.isNullOrEmptyString(fullname)){
                        edtFullName.requestFocus();
                    }
                    else if(Common.isNullOrEmptyString(phone)){
                        edtPhone.requestFocus();
                    }
                    else if(Common.isNullOrEmptyString(email)){
                        edtPhone.requestFocus();
                    }
                }

            }
        });
        // Inflate the layout for this fragment
        return itemview;
    }

}
