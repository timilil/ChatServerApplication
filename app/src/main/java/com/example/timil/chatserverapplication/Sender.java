package com.example.timil.chatserverapplication;

import java.io.PrintStream;
import java.util.concurrent.BlockingQueue;

/**
 * Created by TimiL on 01/10/2017.
 */

public class Sender implements Runnable {
    private PrintStream messageForCommandInterpreter;
    private BlockingQueue<String> outGoingMessageQueue;
    private boolean stop = false;
    private MainActivity mainActivity;
    private String user = null;

    public Sender(PrintStream messageForCommandInterpreter, BlockingQueue<String> outGoingMessageQueue, MainActivity mainActivity){
        this.outGoingMessageQueue = outGoingMessageQueue;
        this.messageForCommandInterpreter = messageForCommandInterpreter;
        this.mainActivity = mainActivity;
    }

    public void stop() {
        stop = true;
    }

    @Override
    public void run() { //Thread for sending to stream
        while (!stop){
            try {
                String message = outGoingMessageQueue.take();
                String[] splitted = message.split("\\s+");
                if (splitted.length >= 1 && splitted[0].toLowerCase().equals(":user")) {
                    if (splitted.length >= 2) {
                        user = splitted[1];
                        mainActivity.setSenderUserName(user);
                    }
                }
                messageForCommandInterpreter.println(message);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
