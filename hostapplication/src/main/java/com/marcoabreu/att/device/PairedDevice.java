package com.marcoabreu.att.device;

import com.marcoabreu.att.communication.*;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Instance of a device which has been paired successfully. Offers tools to communicate with the device
 * Created by AbreuM on 02.08.2016.
 */
public class PairedDevice extends PhysicalDevice implements Closeable {
    private final DeviceServer deviceServer;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private Thread messageThread;

    public PairedDevice(DeviceServer deviceServer, ObjectInputStream in, ObjectOutputStream out)  {
        super("TODO"); //TODO get the unique ID
        this.deviceServer = deviceServer;
        this.in = in;
        this.out = out;
    }

    /**
     * Send a message and retrieve a future to access the response
     * @param message
     * @return
     */
    public <T extends com.marcoabreu.att.communication.message.BaseMessage> com.marcoabreu.att.communication.FutureResponse<T> sendMessage(com.marcoabreu.att.communication.message.BaseMessage message) throws IOException {
        out.writeObject(message);

        return new com.marcoabreu.att.communication.FutureResponse<>(message, deviceServer);
    }

    /**
     * Send a message without awaiting a response
     * @param message
     */
    public void sendResponse(com.marcoabreu.att.communication.message.BaseMessage message) throws IOException {
        out.writeObject(message);
    }

    /**
     * Blocking call to get the next message
     * @param <T>
     */
    public <T extends com.marcoabreu.att.communication.message.BaseMessage> T readMessage() throws IOException {
        try {
            return (T)in.readObject();
        }catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void close() throws IOException {
        try {
            in.close();
        } catch (Exception ex) {
        }

        try {
            out.close();
        } catch (Exception ex) {
        }

        //Thread is automatically closed due to closing of streams
    }

    public Thread getMessageThread() {
        return messageThread;
    }

    public void setMessageThread(Thread messageThread) {
        this.messageThread = messageThread;
    }
}
