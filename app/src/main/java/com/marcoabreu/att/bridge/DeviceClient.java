package com.marcoabreu.att.bridge;

import android.util.Log;

import com.marcoabreu.att.communication.BridgeEndpoint;
import com.marcoabreu.att.communication.BridgeMessageListener;
import com.marcoabreu.att.communication.message.BaseMessage;
import com.marcoabreu.att.communication.message.PairRequestMessage;
import com.marcoabreu.att.communication.message.PairResponseMessage;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 * Client to connect to host application
 * Created by AbreuM on 03.08.2016.
 */
public class DeviceClient implements Closeable, BridgeEndpoint{
    private static final String TAG = "DeviceClient";
    private final int port;
    private Socket socket;
    private Thread bridgeThread;

    public DeviceClient(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        socket = new Socket();
        bridgeThread = new Thread(new BridgeThread(socket, this));
        bridgeThread.setDaemon(true);
        bridgeThread.start();
    }

    public void stop() {
        //TODO
    }

    @Override
    public void close() throws IOException {
        stop();
    }

    @Override
    public void registerMessageListener(BridgeMessageListener listener) {
        //TODO
    }

    @Override
    public void removeMessageListener(BridgeMessageListener listener) {
        //TODO
    }

    private class BridgeThread implements Runnable {
        private final Socket socket;
        private final DeviceClient deviceClient;

        public BridgeThread(Socket socket, DeviceClient deviceClient) {
            this.socket = socket;
            this.deviceClient = deviceClient;
        }

        @Override
        public void run() {
            try {
                //Try to connect to host
                while(!socket.isConnected()) {
                    try {
                        socket.connect(new InetSocketAddress(InetAddress.getLocalHost(), port));
                        Log.d(TAG, "Connected to host");
                    } catch(IOException ex) {
                        Log.e(TAG, "Unable to connect to host application, retrying...", ex);
                    }
                }

                String identifyingString = "blabla"; //TODO

                //TODO: Fire pairing event (to show the id of this device on the UI)

                //out.flush() has to be done first to publish headers
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());


                PairedHost pairedHost = new PairedHost(deviceClient, in, out);

                //Send pairing request
                Log.d(TAG, "Attempting to pair");
                out.writeObject(new PairRequestMessage(identifyingString));

                //Read pairing response
                PairResponseMessage pairResponseMessage = (PairResponseMessage) in.readObject();
                Log.d(TAG, "Pairing successful");

                //TODO: Fire connected event (show we've successfully connected)

                //Read messages in loop
                while(true) {
                    BaseMessage message = (BaseMessage) in.readObject();
                    Log.d(TAG, "Received message");
                    //TODO: Invoke message handler
                }
            } catch (IOException e) {
                e.printStackTrace();
                //TODO: Fire disconnect event
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }
}
