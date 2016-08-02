package com.marcoabreu.att.device;

import com.marcoabreu.att.communication.BaseMessage;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by AbreuM on 02.08.2016.
 */
public class FutureResponse<T extends BaseMessage> implements Future<T> {
    private final Object lockObject = new Object();
    private BaseMessage response = null;

    public FutureResponse(BaseMessage sentMessage, DeviceServer deviceServer){
        deviceServer.registerMessageListener(new DeviceMessageListener() {
            @Override
            public void onMessage(PairedDevice device, BaseMessage message) {
                if(message.getTransactionId() == sentMessage.getTransactionId()) {
                    setResponse(message);

                    //Unregister handler
                    deviceServer.removeMessageListener(this);
                }
            }
        });
    }

    private void setResponse(BaseMessage response) {
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
