package nnthien.com.myapp;


import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Fragment;
import android.support.design.widget.FloatingActionButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;
import nnthien.com.myapp.Adapter.ChatMessageAdapter;
import nnthien.com.myapp.Adapter.CustomAdapter;
import nnthien.com.myapp.Helper.HttpDataHandler;
import nnthien.com.myapp.Models.ChatModel;
import nnthien.com.myapp.Models.SimsimiModel;


/**
 * A simple {@link Fragment} subclass.
 */
public class SimsimiFragment extends android.support.v4.app.Fragment {

    ListView listView;
    List<ChatModel> list_chat = new ArrayList<>();
    EmojiconEditText editText;
    ImageButton btn_send_message;
    public SimsimiFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View itemview = inflater.inflate(R.layout.fragment_simsimi, container, false);
        listView = (ListView)itemview.findViewById(R.id.simsimi_list_of_message);
        editText = (EmojiconEditText)itemview.findViewById(R.id.simsimi_edt_content);
        btn_send_message = (ImageButton) itemview.findViewById(R.id.simsimi_send_button);



        btn_send_message.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = editText.getText().toString();
                ChatModel model = new ChatModel(text,true);
                list_chat.add(model);
                new SimsimiAPI().execute(list_chat);
                editText.setText("");
            }
        });

        return  itemview;
    }

    private class SimsimiAPI extends AsyncTask<List<ChatModel>,Void,String> {
        String stream = null;
        List<ChatModel> models;
        String text = editText.getText().toString();

        @Override
        protected String doInBackground(List<ChatModel>... params) {
            String url = String.format("http://sandbox.api.simsimi.com/request.p?key=%s&lc=en&ft=1.0&text=%s",getString(R.string.simsimi_api),text);
            models = params[0];
            HttpDataHandler httpDataHandler = new HttpDataHandler();
            stream = httpDataHandler.getHTTPData(url);
            return stream;
        }

        @Override
        protected void onPostExecute(String s) {
            Gson gson = new Gson();
            SimsimiModel response = gson.fromJson(s,SimsimiModel.class);

            ChatModel chatModel = new ChatModel(response.getResponse(),false); // get response from simsimi
            models.add(chatModel);
            CustomAdapter adapter = new CustomAdapter(models,getActivity().getApplicationContext());
            listView.setAdapter(adapter);
        }
    }
}
