package com.example.timil.chatserverapplication;

import android.database.DataSetObserver;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.AbsListView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.view.View;
import java.io.IOException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private String messageToQueue;
    private ChatArrayAdapter chatArrayAdapter;
    private ImageButton myButton;
    private ImageButton clearButton;
    private ListView listview;
    private EditText editText;
    private Socket socket;
    private Receiver receiver;
    private boolean chatTextSide;
    private String senderUser;
    private String receiverUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final MainActivity mainActivity = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //chatarrayadapter is made, adapter is needed for making a view for each item in the data set
        chatArrayAdapter = new ChatArrayAdapter(getApplicationContext(), R.layout.myuser);
        listview = (ListView) findViewById(R.id.listView);
        listview.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        //in order to display messages in the listview, an adapter is associated for the listview
        listview.setAdapter(chatArrayAdapter);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    socket = new Socket("10.0.2.2", 4321);
                    receiver = new Receiver(mainActivity, socket);
                    new Thread (receiver).start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        editText = (EditText) findViewById(R.id.writeMessage);
        myButton = (ImageButton) findViewById(R.id.sendButton);
        myButton.setOnClickListener(new View.OnClickListener() {
            String messageToQueue;
            @Override
            public void onClick(View v) {
                messageToQueue = editText.getText().toString();
                receiver.add(messageToQueue);
                editText.setText("");
            }
        });
        clearButton = (ImageButton) findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editText.setText("");
            }
        });
        editText.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // If the event is a key-down event on the "enter" button
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    messageToQueue = editText.getText().toString();
                    receiver.add(messageToQueue);
                    editText.setText("");
                    return true;
                }
                return false;
            }
        });

        //scroll the list view to bottom on data change
        chatArrayAdapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                listview.setSelection(chatArrayAdapter.getCount()-1);
            }
        });
    }

    public boolean sendChatMessage(final String message) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //compares the usernames so the adapter know which side the message is supposed to go
                chatTextSide = compareUser(getSenderUserName(),getReceiverUserName());
                //adds an object to the chatmessagelist in the adapter
                chatArrayAdapter.add(new ChatMessages(chatTextSide, message));
            }
        });return true;
    }

    @Override
    protected void onStop(){
        super.onStop();
        //stops the scanning of the message stream in receiver and printing to server in sender
        receiver.stop();
    }

    public synchronized void setSenderUserName(String user){
        this.senderUser = user;
    }

    public String getSenderUserName(){
        return senderUser;
    }

    public synchronized void setReceiverUserName(String user){
        this.receiverUser = user;
    }

    public String getReceiverUserName(){
        return receiverUser;
    }

    public boolean compareUser(String senderUser, String receiverUser){
        if (null != senderUser && null != receiverUser && senderUser.equals(receiverUser)){
            return true;
        }
        else {
            return false;
        }
    }
}


