package com.marcoabreu.att.device;

import com.marcoabreu.att.communication.BaseMessage;
import com.marcoabreu.att.communication.PairRequestMessage;
import com.marcoabreu.att.communication.PairResponseMessage;

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
public class DeviceServer {
    private final int port;
    private final DeviceManager deviceManager;
    private ServerSocket serverSocket;
    private Thread pairingThread;
    private Set<DeviceMessageListener> listeners;

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
        pairingThread.start();

    }

    public void stop() {
        //TODO: Close listener thread and close connection to all devices
    }

    public void registerMessageListener(DeviceMessageListener listener) {
        listeners.add(listener);
    }

    public void removeMessageListener(DeviceMessageListener listener) {
        listeners.remove(listener);
    }

    public void invokeOnMessage(PairedDevice device, BaseMessage message) {
        for(DeviceMessageListener listener : listeners) {
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

                    //TODO: Switch to communication via objects (ObjectStream)
                    ObjectInputStream in = new ObjectInputStream(socket.getInputStream());
                    ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());

                    PairedDevice pairedDevice = new PairedDevice(in, out);

                    //Start listener for incoming messages
                    Thread messageHandlerThread = new Thread(new DeviceMessageThread(pairedDevice, deviceServer));
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
                PairRequestMessage pairRequestMessage = device.readMessage();
                device.sendMessage(new PairResponseMessage(pairRequestMessage));

                deviceManager.addPairedDevice(device);

                while (true) {
                    BaseMessage message = device.readMessage();
                    deviceServer.invokeOnMessage(device, message);
                }
            } catch (IOException ex) {
                //TODO: Notify of disconnection
            }
        }
    }

}
