package nnthien.com.myapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.github.library.bubbleview.BubbleTextView;
import com.quickblox.chat.QBChatService;

import java.util.List;

import nnthien.com.myapp.Holder.QBUsersHolder;
import nnthien.com.myapp.Models.ChatModel;
import nnthien.com.myapp.R;

/**
 * Created by T440S on 7/18/2017.
 */

public class CustomAdapter extends BaseAdapter {

    private List<ChatModel> list_chat_models;
    private Context context;
    private LayoutInflater layoutInflater;


    public CustomAdapter(List<ChatModel> list_chat_models,Context context){
        this.list_chat_models = list_chat_models;
        this.context = context;
        layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }
    @Override
    public int getCount() {
        return list_chat_models.size();
    }

    @Override
    public Object getItem(int position) {
        return list_chat_models.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            if (list_chat_models.get(position).isSend()){
                view = inflater.inflate(R.layout.list_send_message,null);
            }
            else {
                view = inflater.inflate(R.layout.list_recv_message,null);
            }
            BubbleTextView text_message = (BubbleTextView)view.findViewById(R.id.message_content);
            text_message.setText(list_chat_models.get(position).getMessage());
        }
        return view;
    }
}
