package com.marcoabreu.att.device;

import com.marcoabreu.att.communication.PhysicalDevice;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Instance of a device which has been paired successfully. Offers tools to communicate with the device
 * Created by AbreuM on 02.08.2016.
 */
public class PairedDevice extends PhysicalDevice {
    private Thread messageThread;

    public PairedDevice(DeviceServer deviceServer, ObjectInputStream in, ObjectOutputStream out)  {
        super("TODO", deviceServer, in, out); //TODO get the unique ID
    }



    public Thread getMessageThread() {
        return messageThread;
    }

    public void setMessageThread(Thread messageThread) {
        this.messageThread = messageThread;
    }
}
