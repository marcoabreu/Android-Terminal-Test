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
import java.util.Collections;
import java.util.HashMap;
import java.util.Set;

/**
 * Client to connect to host application
 * Created by AbreuM on 03.08.2016.
 */
public class DeviceClient implements Closeable, BridgeEndpoint{
    private static final String TAG = "DeviceClient";
    private final int port;
    private PairedHost pairedHost;
    private Socket socket;
    private Thread bridgeThread;
    private Set<BridgeMessageListener> bridgeMessageListeners;
    private Set<BridgeEventListener> bridgeEventListeners;


    public DeviceClient(int port) {
        this.port = port;
        this.bridgeMessageListeners = Collections.newSetFromMap(new HashMap<>());
        this.bridgeEventListeners = Collections.newSetFromMap(new HashMap<>());
    }

    public void start() throws IOException {
        socket = new Socket();
        bridgeThread = new Thread(new BridgeThread(socket, this));
        bridgeThread.setDaemon(true);
        bridgeThread.start();
    }

    public void stop() {
        try {
            socket.close();
        } catch (IOException e) {
        }
    }

    @Override
    public void close() throws IOException {
        stop();
    }

    @Override
    public void registerMessageListener(BridgeMessageListener listener) {
        bridgeMessageListeners.add(listener);
    }

    @Override
    public void removeMessageListener(BridgeMessageListener listener) {
        bridgeMessageListeners.remove(listener);
    }

    public void invokeOnMessage(PhysicalDevice device, BaseMessage message) {
        //TODO: Invoke in separate tasks
        for(BridgeMessageListener listener : bridgeMessageListeners) {
            try {
                listener.onMessage(device, message);
            } catch (IOException e) {
                e.printStackTrace();
                //Some connection issues, they will be handled by the thread
            }
        }
    }

    public void registerBridgeListener(BridgeEventListener listener) {
        bridgeEventListeners.add(listener);
    }

    public void removeBridgeListener(BridgeEventListener listener) {
        bridgeEventListeners.remove(listener);
    }

    public void invokeOnHostPaired(PairedHost host) {
        for(BridgeEventListener listener : bridgeEventListeners) {
            listener.onHostPaired(host);
        }
    }

    public void invokeOnHostUnpaired(PairedHost host) {
        for(BridgeEventListener listener : bridgeEventListeners) {
            listener.onHostUnpaired(host);
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

                ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                out.flush();
                ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                PairedHost pairedHost = new PairedHost(deviceClient, in, out);
                deviceClient.pairedHost = pairedHost;

                //Send pairing request
                Log.d(TAG, "Attempting to pair");
                out.writeObject(new PairRequestMessage(Build.SERIAL, Build.MANUFACTURER + " - " + Build.MODEL));


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
                if(deviceClient.pairedHost != null) {
                    deviceClient.invokeOnHostUnpaired(deviceClient.pairedHost);
                }
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        }
    }

    private void onException(Exception ex) {
        //TODO handle properly, but so far I see no reason to handle them on the device
        ex.printStackTrace();
    }
}
