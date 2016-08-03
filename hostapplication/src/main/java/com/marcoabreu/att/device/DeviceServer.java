package com.marcoabreu.att.device;

import com.marcoabreu.att.communication.BridgeEndpoint;
import com.marcoabreu.att.communication.BridgeMessageListener;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashSet;
import java.util.Set;

/**
 * Server for communication between devices and host
 * Created by AbreuM on 02.08.2016.
 */
public class DeviceServer implements Closeable, BridgeEndpoint {
    private final int port;
    private final DeviceManager deviceManager;
    private ServerSocket serverSocket;
    private Thread pairingThread;
    private Set<BridgeMessageListener> listeners;

    /**
     * Initialize a server to listen for connections on the specified port
     * @param port Port to listen on
     */
    public DeviceServer(DeviceManager deviceManager, int port) {
        this.deviceManager = deviceManager;
        this.port = port;
        this.listeners = new HashSet<>();
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        pairingThread = new Thread(new PairingListenerThread(serverSocket, this));
        pairingThread.setDaemon(true);
        pairingThread.start();

    }

    @Override
    public void close() throws IOException {
        stop();
    }

    public void stop() {
        //TODO: Close listener thread and close connection to all devices
    }

    @Override
    public void registerMessageListener(BridgeMessageListener listener) {
        listeners.add(listener);
    }

    @Override
    public void removeMessageListener(BridgeMessageListener listener) {
        listeners.remove(listener);
    }

    public void invokeOnMessage(PairedDevice device, com.marcoabreu.att.communication.message.BaseMessage message) {
        //TODO: Invoke in seperate tasks
        for(BridgeMessageListener listener : listeners) {
            listener.onMessage(device, message);
        }
    }



    /**
     * Main thread to listen for incoming connections of newly connected devices
     */
    private class PairingListenerThread implements Runnable {
        private final ServerSocket socket;
        private final DeviceServer deviceServer;
        public PairingListenerThread(ServerSocket socket, DeviceServer deviceServer) {
            this.socket = socket;
            this.deviceServer = deviceServer;
        }

        @Override
        public void run() {
            while (true) {
                try {
                    Socket socket = serverSocket.accept(); //new device attempting to connect

                    //out.flush() has to be done first to publish headers
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                    out.flush();
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());

                    PairedDevice pairedDevice = new PairedDevice(deviceServer, in, out);

                    //Start listener for incoming messages
                    Thread messageHandlerThread = new Thread(new DeviceMessageThread(pairedDevice, deviceServer));
                    messageHandlerThread.setDaemon(true);
                    pairedDevice.setMessageThread(messageHandlerThread);
                    messageHandlerThread.start();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Thread to handle incoming messages of the device
     */
    private class DeviceMessageThread implements Runnable {
        private final PairedDevice pairedDevice;
        private final DeviceServer deviceServer;
        public DeviceMessageThread(PairedDevice pairedDevice, DeviceServer deviceServer) {
            this.pairedDevice = pairedDevice;
            this.deviceServer = deviceServer;
        }

        @Override
        public void run() {
            try (PairedDevice device = pairedDevice) {
                //Finish handshake
                com.marcoabreu.att.communication.message.PairRequestMessage pairRequestMessage = device.readMessage();
                device.sendMessage(new com.marcoabreu.att.communication.message.PairResponseMessage(pairRequestMessage));

                deviceManager.addPairedDevice(device);

                //TODO: Notify a device has been paired

                while (true) {
                    com.marcoabreu.att.communication.message.BaseMessage message = device.readMessage();
                    deviceServer.invokeOnMessage(device, message);
                }
            } catch (IOException ex) {
                //TODO: Notify of disconnection
            }
        }
    }

}
