package com.marcoabreu.att.communication;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Object allowing access to the response of a message as soon as it arrives
 * Created by AbreuM on 02.08.2016.
 */
public class FutureResponse<T extends com.marcoabreu.att.communication.message.BaseMessage> implements Future<T> {
    private final Object lockObject = new Object();
    private com.marcoabreu.att.communication.message.BaseMessage response = null;

    public FutureResponse(com.marcoabreu.att.communication.message.BaseMessage sentMessage, BridgeEndpoint bridgeEndpoint){
        bridgeEndpoint.registerMessageListener(new BridgeMessageListener() {
            @Override
            public void onMessage(PhysicalDevice device, com.marcoabreu.att.communication.message.BaseMessage message) {
                if(message.getTransactionId() == sentMessage.getTransactionId()) {
                    setResponse(message);

                    //Unregister handler
                    bridgeEndpoint.removeMessageListener(this);
                }
            }
        });
    }

    private void setResponse(com.marcoabreu.att.communication.message.BaseMessage response) {
        synchronized (lockObject) {
            this.response = response;
            lockObject.notifyAll();
        }
    }

    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new IllegalAccessError("Can not be canceled");
    }

    @Override
    public boolean isCancelled() {
        return false;
    }

    @Override
    public boolean isDone() {
        return response != null;
    }

    @Override
    public T get() throws InterruptedException, ExecutionException {
        synchronized (lockObject) {
            while(response == null) {
                lockObject.wait();
            }

            return (T)response;
        }
    }

    @Override
    public T get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        synchronized (lockObject) {
            while(response == null) {
                lockObject.wait(unit.toMillis(timeout));
            }

            return (T)response;
        }
    }
}
