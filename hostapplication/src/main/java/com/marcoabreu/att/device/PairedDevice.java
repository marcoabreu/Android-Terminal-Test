package com.marcoabreu.att.device;

import com.marcoabreu.att.communication.PhysicalDevice;
import se.vidstige.jadb.JadbDevice;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Instance of a device which has been paired successfully. Offers tools to communicate with the device
 * Created by AbreuM on 02.08.2016.
 */
public class PairedDevice extends PhysicalDevice {
    private JadbDevice jadbDevice;
    private Thread messageThread;
    private String deviceModel;

    public PairedDevice(DeviceServer deviceServer, ObjectInputStream in, ObjectOutputStream out)  {
        super(deviceServer, in, out);
    }

    public JadbDevice getJadbDevice() {
        return jadbDevice;
    }

    public void setJadbDevice(JadbDevice jadbDevice) {
        this.jadbDevice = jadbDevice;
    }

    public Thread getMessageThread() {
        return messageThread;
    }

    public void setMessageThread(Thread messageThread) {
        this.messageThread = messageThread;
    }

    public String getDeviceModel() {
        return deviceModel;
    }

    public void setDeviceModel(String deviceModel) {
        this.deviceModel = deviceModel;
    }

    @Override
    public String toString() {
        return String.format("PairedDevice: %s", this.getSerial());
    }
}
