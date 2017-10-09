package com.example.timil.chatserverapplication;

import android.annotation.SuppressLint;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedTransferQueue;

/**
 * Created by TimiL on 01/10/2017.
 */

public class Receiver implements Runnable {
    private Socket clientSocket;
    private BlockingQueue<String> outGoingMessageQueue;
    private MainActivity mainActivity;
    private Sender sender = null;
    private boolean stop = false;

    //Fixes error: LinkedTransferQueue call requires api lvl 21, current min is 15
    @SuppressLint("NewApi")

    public Receiver(MainActivity mainActivity, Socket socket){
        this.mainActivity = mainActivity;
        outGoingMessageQueue = new LinkedTransferQueue<>();
        this.clientSocket = socket;
    }

    public void stop() {
        stop = true;
        if(sender != null) {
            sender.stop();
        }
    }

    public void add(String messageToQueue) {
        outGoingMessageQueue.add(messageToQueue);
    }

    @Override
    public void run() { //Thread for receiving stream
        try {
            sender = new Sender(new PrintStream(clientSocket.getOutputStream()), outGoingMessageQueue, mainActivity);
            new Thread (sender).start();
            InputStream inputstream = (clientSocket.getInputStream());
            Scanner scanner = new Scanner(inputstream);
            while (!stop) {
                String message = null;
                //returns true if there is a line in scanner
                if(scanner.hasNext()) {
                    message = scanner.nextLine();
                    String[] splitted = message.split("\\s+");
                    if (splitted.length >= 2) {
                        mainActivity.setReceiverUserName(splitted[1]);
                    }
                    if (splitted.length >= 1 && splitted[0].equals("Users:")) {
                        mainActivity.setReceiverUserName(null);
                    }
                    if (splitted.length >= 1 && splitted[0].equals("History")) {
                        mainActivity.setReceiverUserName(null);
                    }
                } else if(scanner.nextLine().isEmpty()) {
                    message = "\r\n";
                }
                if(null != message) {
                    mainActivity.sendChatMessage(message);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
