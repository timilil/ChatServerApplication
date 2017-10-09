package com.example.timil.chatserverapplication;

/**
 * Created by TimiL on 03/10/2017.
 */

public class ChatMessages {
    public boolean chatUserMessageSide;
    public String message;

    public ChatMessages(boolean chatusermessageSide, String message) {
        super();
        this.chatUserMessageSide = chatusermessageSide;
        this.message = message;
    }
}
