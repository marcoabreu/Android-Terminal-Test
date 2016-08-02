package com.marcoabreu.att.device;

import com.marcoabreu.att.communication.BaseMessage;
import com.marcoabreu.att.communication.PairRequestMessage;
import com.marcoabreu.att.communication.PairResponseMessage;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.concurrent.Future;

/**
 * Instance of a device which has been paired successfully. Offers tools to communicate with the device
 * Created by AbreuM on 02.08.2016.
 */
public class PairedDevice implements Closeable {
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private Thread messageThread;

    public PairedDevice(ObjectInputStream in, ObjectOutputStream out)  {
        this.in = in;
        this.out = out;
    }

    /**
     * Send a message and retrieve a future to access the response
     * @param message
     * @return
     */
    public <T extends BaseMessage> FutureResponse<T> sendMessage(BaseMessage message) throws IOException {
        out.writeObject(message);

        return new FutureResponse<>(message);
    }

    /**
     * Send a message without awaiting a response
     * @param message
     */
    public void sendResponse(BaseMessage message) {

    }

    /**
     * Blocking call to get the next message
     * @param <T>
     */
    public <T extends BaseMessage> T readMessage() throws IOException {
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
    }

    public Thread getMessageThread() {
        return messageThread;
    }

    public void setMessageThread(Thread messageThread) {
        this.messageThread = messageThread;
    }
}
