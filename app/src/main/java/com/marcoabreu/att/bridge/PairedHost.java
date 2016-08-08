package com.marcoabreu.att.bridge;

import com.marcoabreu.att.communication.PhysicalDevice;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by AbreuM on 03.08.2016.
 */
public class PairedHost extends PhysicalDevice {
    public PairedHost(DeviceClient deviceClient, ObjectInputStream in, ObjectOutputStream out)  {
        super(deviceClient, in, out); //TODO whatever we want to do here with the id
    }
}
