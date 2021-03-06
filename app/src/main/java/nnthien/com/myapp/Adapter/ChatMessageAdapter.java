package nnthien.com.myapp.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.github.library.bubbleview.BubbleTextView;
import com.quickblox.chat.QBChatService;
import com.quickblox.chat.model.QBChatMessage;

import java.util.ArrayList;

import nnthien.com.myapp.Holder.QBUsersHolder;
import nnthien.com.myapp.R;

/**
 * Created by T440S on 7/13/2017.
 */

public class ChatMessageAdapter extends BaseAdapter {

    private Context context;
    private ArrayList<QBChatMessage> qbChatMessages;

    public ChatMessageAdapter(Context context, ArrayList<QBChatMessage> qbChatMessages) {
        this.context = context;
        this.qbChatMessages = qbChatMessages;
    }

    @Override
    public int getCount() {
        return qbChatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return  qbChatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private ColorGenerator generator = ColorGenerator.MATERIAL;
    private int randomColor = generator.getRandomColor();
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater)context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            if (qbChatMessages.get(position).getSenderId().equals(QBChatService.getInstance().getUser().getId())){
                view = inflater.inflate(R.layout.list_send_message,null);
                BubbleTextView bubbleTextView = (BubbleTextView)view.findViewById(R.id.message_content);
                bubbleTextView.setText(qbChatMessages.get(position).getBody());
            }
            else {
                view = inflater.inflate(R.layout.list_recv_message,null);
                BubbleTextView bubbleTextView = (BubbleTextView)view.findViewById(R.id.message_content);
                bubbleTextView.setText(qbChatMessages.get(position).getBody());
                TextView txtName = (TextView)view.findViewById(R.id.message_user);
                ImageView user_avatar = (ImageView)view.findViewById(R.id.simsimi_image);

                TextDrawable.IBuilder builder = TextDrawable.builder().beginConfig().withBorder(4).endConfig().round();

                //GET TEXT ImageView
                TextDrawable drawable = builder.build(QBUsersHolder.getInstance().getUserById(qbChatMessages.get(position).getSenderId()).getFullName().toString().substring(0, 1).toUpperCase(), randomColor);

                user_avatar.setImageDrawable(drawable);
                txtName.setText(QBUsersHolder.getInstance().getUserById(qbChatMessages.get(position).getSenderId()).getFullName());
            }
        }
        return view;
    }
}
