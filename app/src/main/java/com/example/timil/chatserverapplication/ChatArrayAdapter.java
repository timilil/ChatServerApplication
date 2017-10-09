package com.example.timil.chatserverapplication;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TimiL on 03/10/2017.
 */

public class ChatArrayAdapter extends ArrayAdapter<ChatMessages> {
    private TextView chatText;
    private List<ChatMessages> chatMessageList = new ArrayList<ChatMessages>();
    private Context context;

    @Override
    public void add(ChatMessages object) {
        chatMessageList.add(object);
        super.add(object);
    }

    public ChatArrayAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
        this.context = context;
    }

    //returns the count of items in adapter
    public int getCount() {
        return this.chatMessageList.size();
    }

    //get the data item associated with the specified position in the data set
    public ChatMessages getItem(int index) {
        return this.chatMessageList.get(index);
    }

    //getView() is called for each item in the list that is passed to adapter. It is called when adapter is set
    public View getView(int position, View convertView, ViewGroup parent) {
        ChatMessages chatMessageObj = getItem(position);
        View row = convertView;
        LayoutInflater inflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (chatMessageObj.chatUserMessageSide) {
            row = inflater.inflate(R.layout.myuser, parent, false);
            chatText = (TextView) row.findViewById(R.id.myuser);
            chatText.setText(chatMessageObj.message);
        }else {
            row = inflater.inflate(R.layout.chatusers, parent, false);
            chatText = (TextView) row.findViewById(R.id.chatuser);
            chatText.setText(chatMessageObj.message);
        }
        return row;
    }
}
