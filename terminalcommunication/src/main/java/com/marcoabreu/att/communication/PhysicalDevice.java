package com.marcoabreu.att.communication;

import com.marcoabreu.att.communication.message.BaseMessage;

import java.io.Closeable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Class representing a physical device like a phone or the host
 * Created by AbreuM on 03.08.2016.
 */
public class PhysicalDevice implements Closeable {
    private final BridgeEndpoint bridgeEndpoint;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private String serial = "unknown";

    public PhysicalDevice(BridgeEndpoint bridgeEndpoint, ObjectInputStream in, ObjectOutputStream out) {
        this.bridgeEndpoint = bridgeEndpoint;
        this.in = in;
        this.out = out;
    }

    /**
     * Send a message and retrieve a future to access the response
     * @param message
     * @return
     */
    public <T extends BaseMessage> FutureResponse<T> sendMessage(BaseMessage message) throws IOException {
        FutureResponse<T> futureResponse = new FutureResponse<>(message, bridgeEndpoint);

        out.writeObject(message);

        return futureResponse;
    }

    /**
     * Send a message without awaiting a response
     * @param message
     */
    public void sendResponse(BaseMessage message) throws IOException {
        if(message.getOccuredException() != null) {
            System.out.println("Sending faulty response: " + message.getClass().toString());
            message.getOccuredException().printStackTrace();
        }

        out.writeObject(message);
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

        //Thread is automatically closed due to closing of streams
    }
    
    public String getSerial() {
        return this.serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
