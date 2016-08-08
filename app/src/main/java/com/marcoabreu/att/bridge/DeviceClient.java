package com.marcoabreu.att.bridge;

import android.os.Build;
import android.util.Log;

import com.marcoabreu.att.communication.BridgeEndpoint;
import com.marcoabreu.att.communication.BridgeMessageListener;
import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.communication.message.BaseMessage;
import com.marcoabreu.att.communication.message.PairRequestMessage;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Client to connect to host application
 * Created by AbreuM on 03.08.2016.
 */
public class DeviceClient implements Closeable, BridgeEndpoint{
    private static final String TAG = "DeviceClient";
    private final int port;
    private Socket socket;
    private Thread bridgeThread;
    private Set<BridgeMessageListener> listeners;

    public DeviceClient(int port) {
        this.port = port;
        this.listeners = ConcurrentHashMap.newKeySet();
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
        listeners.add(listener);
    }

    @Override
    public void removeMessageListener(BridgeMessageListener listener) {
        listeners.remove(listener);
    }

    public void invokeOnMessage(PhysicalDevice device, BaseMessage message) {
        //TODO: Invoke in separate tasks
        for(BridgeMessageListener listener : listeners) {
            try {
                listener.onMessage(device, message);
            } catch (IOException e) {
                e.printStackTrace(); //TODO
            }
        }
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
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                        }
                    }
                }

                //TODO: Fire pairing event (to show the id of this device on the UI) Build.SERIAL

                //out.flush() has to be done first to publish headers
                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                PairedHost pairedHost = new PairedHost(deviceClient, in, out);

                //Send pairing request
                Log.d(TAG, "Attempting to pair");
                out.writeObject(new PairRequestMessage(Build.SERIAL));


                //Read messages in loop
                while(true) {
                    BaseMessage message = (BaseMessage) in.readObject();
                    if(message.getOccuredException() != null) {
                        onException(message.getOccuredException());
                    } else {
                        deviceClient.invokeOnMessage(pairedHost, message);
                    }
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

    private void onException(Exception ex) {
        //TODO handle properly
        ex.printStackTrace();
    }
}
