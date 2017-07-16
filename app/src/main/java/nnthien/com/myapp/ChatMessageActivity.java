package nnthien.com.myapp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AlertDialogLayout;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.bhargavms.dotloader.DotLoader;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.QBIncomingMessagesManager;
import com.quickblox.chat.QBRestChatService;
import com.quickblox.chat.exception.QBChatException;
import com.quickblox.chat.listeners.QBChatDialogMessageListener;
import com.quickblox.chat.listeners.QBChatDialogParticipantListener;
import com.quickblox.chat.listeners.QBChatDialogTypingListener;
import com.quickblox.chat.model.QBChatDialog;
import com.quickblox.chat.model.QBChatMessage;
import com.quickblox.chat.model.QBDialogType;
import com.quickblox.chat.model.QBPresence;
import com.quickblox.chat.request.QBDialogRequestBuilder;
import com.quickblox.chat.request.QBMessageGetBuilder;
import com.quickblox.chat.request.QBMessageUpdateBuilder;
import com.quickblox.content.QBContent;
import com.quickblox.content.model.QBFile;
import com.quickblox.core.QBEntityCallback;
import com.quickblox.core.exception.QBResponseException;
import com.quickblox.core.request.QBRequestBuilder;
import com.quickblox.core.request.QBRequestUpdateBuilder;
import com.squareup.picasso.Picasso;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smackx.muc.DiscussionHistory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import nnthien.com.myapp.Adapter.ChatMessageAdapter;
import nnthien.com.myapp.Common.Common;
import nnthien.com.myapp.Holder.QBChatMessagesHolder;

public class ChatMessageActivity extends AppCompatActivity implements QBChatDialogMessageListener {

    QBChatDialog qbChatDialog;
    ListView lstChatMessages;
    ImageButton submitButton,emojiButton;
    EmojIconActions emojIconActions;
    EmojiconEditText edtContent;

    RelativeLayout activity_main;

    Toolbar toolbar;

    ChatMessageAdapter adapter;


    //Update Online User

    ImageView img_online_count,dialog_avatar;
    TextView txt_online_count;

    DotLoader dotloader;



    //Variables for Edit/Delete Message
    int contextMenuIndexClicked = -1;

    boolean isEditMode = false;
    QBChatMessage editMessage;

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.chat_group_edit_name:
                editNameGroup();
                break;
            case R.id.chat_group_add_user:
                addUser();
                break;
            case R.id.chat_group_remove_user:
                removeUser();
                break;
        }

        return true;
    }

    private void removeUser() {
        Intent intent = new Intent(this,ListUsersActivity.class);
        intent.putExtra(Common.UPDATE_DIALOG_EXTRA,qbChatDialog);
        intent.putExtra(Common.UPDATE_MODE,Common.UPDATE_REMOVE_MODE);

        startActivity(intent);
    }

    private void addUser() {
        Intent intent = new Intent(this,ListUsersActivity.class);
        intent.putExtra(Common.UPDATE_DIALOG_EXTRA,qbChatDialog);
        intent.putExtra(Common.UPDATE_MODE,Common.UPDATE_ADD_MODE);

        startActivity(intent);
    }

    private void editNameGroup() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.dialog_edit_group_layout,null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setView(view);

        final EditText newName = (EditText)view.findViewById(R.id.edt_new_group_name);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                qbChatDialog.setName(newName.getText().toString());

                QBDialogRequestBuilder requestBuilder = new QBDialogRequestBuilder();
                QBRestChatService.updateGroupChatDialog(qbChatDialog,requestBuilder).performAsync(new QBEntityCallback<QBChatDialog>() {
                    @Override
                    public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                        Toast.makeText(ChatMessageActivity.this,"Group name edited",Toast.LENGTH_SHORT).show();
                        toolbar.setTitle(qbChatDialog.getName());
                    }

                    @Override
                    public void onError(QBResponseException e) {
                        Toast.makeText(getBaseContext(),""+e.getMessage(),Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });


        AlertDialog alertDialog = alertDialogBuilder.create();

        alertDialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if(qbChatDialog.getType() == QBDialogType.GROUP || qbChatDialog.getType() == QBDialogType.PUBLIC_GROUP){
            getMenuInflater().inflate(R.menu.chat_message_group_menu,menu);
        }
        return true;
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
        contextMenuIndexClicked = info.position;
        switch (item.getItemId()){
            case R.id.chat_message_update_message:
                UpdateMessage();
                break;
            case R.id.chat_message_delete_message:
                DeleteMessage();
                break;
        }

        return true;
    }

    private void UpdateMessage() {
        editMessage = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(qbChatDialog.getDialogId()).get(contextMenuIndexClicked);
        edtContent.setText(editMessage.getBody());
        isEditMode = true;

    }
    private void DeleteMessage() {

        final ProgressDialog deleteDialog = new ProgressDialog(ChatMessageActivity.this);
        deleteDialog.setMessage("Please waiting...");
        deleteDialog.show();

        editMessage = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(qbChatDialog.getDialogId()).get(contextMenuIndexClicked);
        QBRestChatService.deleteMessage(editMessage.getId(),false).performAsync(new QBEntityCallback<Void>() {
            @Override
            public void onSuccess(Void aVoid, Bundle bundle) {
                retrieveMessage();
                deleteDialog.dismiss();
            }

            @Override
            public void onError(QBResponseException e) {

            }
        });
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        getMenuInflater().inflate(R.menu.chat_mesage_context_menu,menu);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        qbChatDialog.removeMessageListrener(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        qbChatDialog.removeMessageListrener(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_message);

        initViews();

        initChatDialogs();

        retrieveMessage();

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!edtContent.getText().toString().isEmpty()) {
                    if (!isEditMode) {
                        QBChatMessage chatMessage = new QBChatMessage();
                        chatMessage.setBody(edtContent.getText().toString());
                        chatMessage.setSenderId(QBChatService.getInstance().getUser().getId());
                        chatMessage.setSaveToHistory(true);

                        try {
                            qbChatDialog.sendMessage(chatMessage);
                        } catch (SmackException.NotConnectedException e) {
                            e.printStackTrace();
                        }
                        // Fix private chat don't show message
                        if (qbChatDialog.getType() == QBDialogType.PRIVATE) {
                            QBChatMessagesHolder.getInstance().putMessage(qbChatDialog.getDialogId(), chatMessage);
                            ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(chatMessage.getDialogId());
                            adapter = new ChatMessageAdapter(getBaseContext(), messages);
                            lstChatMessages.setAdapter(adapter);
                            adapter.notifyDataSetChanged();
                        }
                        edtContent.setText("");
                        edtContent.setFocusable(true);
                    } else {
                        final ProgressDialog updateDialog = new ProgressDialog(ChatMessageActivity.this);
                        updateDialog.setMessage("Please waiting...");
                        updateDialog.show();

                        QBMessageUpdateBuilder messageUpdateBuilder = new QBMessageUpdateBuilder();
                        messageUpdateBuilder.updateText(edtContent.getText().toString()).markDelivered().markRead();
                        QBRestChatService.updateMessage(editMessage.getId(), qbChatDialog.getDialogId(), messageUpdateBuilder).performAsync(new QBEntityCallback<Void>() {
                            @Override
                            public void onSuccess(Void aVoid, Bundle bundle) {
                                //Refresh data
                                retrieveMessage();
                                isEditMode = false;
                                edtContent.setText("");
                                updateDialog.dismiss();
                            }

                            @Override
                            public void onError(QBResponseException e) {
                                Toast.makeText(getBaseContext(), "" + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                }
            }
        });
    }

    private void retrieveMessage() {
        QBMessageGetBuilder messageGetBuilder = new QBMessageGetBuilder();
        messageGetBuilder.setLimit(500); //get Limit 500 messages

        if(qbChatDialog !=null){
            QBRestChatService.getDialogMessages(qbChatDialog,messageGetBuilder).performAsync(new QBEntityCallback<ArrayList<QBChatMessage>>() {
                @Override
                public void onSuccess(ArrayList<QBChatMessage> qbChatMessages, Bundle bundle) {
                    //Put message to cache

                    QBChatMessagesHolder.getInstance().putMessages(qbChatDialog.getDialogId(),qbChatMessages);
                    adapter = new ChatMessageAdapter(getBaseContext(),qbChatMessages);
                    lstChatMessages.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }

                @Override
                public void onError(QBResponseException e) {
                    Log.e("ERROR", e.getMessage());
                }
            });
        }
    }

    private void initChatDialogs() {
        qbChatDialog = (QBChatDialog)getIntent().getSerializableExtra(Common.DIALOG_EXTRA);

        qbChatDialog.initForChat(QBChatService.getInstance());

        //Register listener Incoming Message
        QBIncomingMessagesManager incomingMessage = QBChatService.getInstance().getIncomingMessagesManager();
        incomingMessage.addDialogMessageListener(new QBChatDialogMessageListener() {
            @Override
            public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
            }

            @Override
            public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
            }
        });

        if (qbChatDialog.getType() == QBDialogType.PUBLIC_GROUP || qbChatDialog.getType() == QBDialogType.GROUP){
            DiscussionHistory discussionHistory = new DiscussionHistory();
            discussionHistory.setMaxStanzas(0);

            qbChatDialog.join(discussionHistory, new QBEntityCallback() {
                @Override
                public void onSuccess(Object o, Bundle bundle) {

                }

                @Override
                public void onError(QBResponseException e) {
                    Log.d("ERROR",""+e.getMessage());
                }
            });
        }

        final QBChatDialogParticipantListener participantListener = new QBChatDialogParticipantListener() {
            @Override
            public void processPresence(String dialogId, QBPresence qbPresence) {
                if(dialogId== qbChatDialog.getDialogId()){
                    QBRestChatService.getChatDialogById(dialogId).performAsync(new QBEntityCallback<QBChatDialog>() {
                        @Override
                        public void onSuccess(QBChatDialog qbChatDialog, Bundle bundle) {
                            try{
                                Collection<Integer> onlineList = qbChatDialog.getOnlineUsers();
                                TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig().withBorder(4).endConfig().round();
                                TextDrawable online = builder.build("", Color.RED);
                                img_online_count.setImageDrawable(online);
                                txt_online_count.setText(String.format("%d/%d online",onlineList.size(),qbChatDialog.getOccupants().size()));
                            }catch (XMPPException e){
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(QBResponseException e) {

                        }
                    });

                }
            }
        };
        qbChatDialog.addParticipantListener(participantListener);
        qbChatDialog.addMessageListener(this);

        //Set title for toolbar
        toolbar.setTitle(qbChatDialog.getName());
        setSupportActionBar(toolbar);

        registerTypingForChatDialog(qbChatDialog);


    }

    private void registerTypingForChatDialog(QBChatDialog qbChatDialog) {

        QBChatDialogTypingListener typingListener = new QBChatDialogTypingListener() {
            @Override
            public void processUserIsTyping(String s, Integer integer) {
                if (dotloader.getVisibility() != View.VISIBLE){
                    dotloader.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void processUserStopTyping(String s, Integer integer) {
                if (dotloader.getVisibility() != View.INVISIBLE){
                    dotloader.setVisibility(View.INVISIBLE);
                }
            }
        };

        qbChatDialog.addIsTypingListener(typingListener);
    }

    private void initViews() {

        dotloader = (DotLoader)findViewById(R.id.dot_loader);
        lstChatMessages = (ListView)findViewById(R.id.list_of_message);
        submitButton = (ImageButton)findViewById(R.id.send_button);
        emojiButton = (ImageButton)findViewById(R.id.emoji_button);
        activity_main = (RelativeLayout)findViewById(R.id.activity_main);
        edtContent = (EmojiconEditText)findViewById(R.id.edt_content);
        emojIconActions = new EmojIconActions(getApplicationContext(),activity_main,emojiButton,edtContent);
        emojIconActions.ShowEmojicon();
        edtContent.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                try {
                    qbChatDialog.sendIsTypingNotification();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    qbChatDialog.sendStopTypingNotification();
                } catch (XMPPException e) {
                    e.printStackTrace();
                } catch (SmackException.NotConnectedException e) {
                    e.printStackTrace();
                }
            }
        });

        img_online_count = (ImageView)findViewById(R.id.img_online_count);
        txt_online_count = (TextView)findViewById(R.id.txt_online_count);



        //Add context menu
        registerForContextMenu(lstChatMessages);

        toolbar = (Toolbar)findViewById(R.id.chat_message_toolbar);

    }


    @Override
    public void processMessage(String s, QBChatMessage qbChatMessage, Integer integer) {
        QBChatMessagesHolder.getInstance().putMessage(qbChatMessage.getDialogId(),qbChatMessage);
        ArrayList<QBChatMessage> messages = QBChatMessagesHolder.getInstance().getChatMessagesByDialogId(qbChatMessage.getDialogId());
        adapter = new ChatMessageAdapter(getBaseContext(),messages);
        lstChatMessages.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

    @Override
    public void processError(String s, QBChatException e, QBChatMessage qbChatMessage, Integer integer) {
        Log.e("ERROR",""+e.getMessage());
    }
}
