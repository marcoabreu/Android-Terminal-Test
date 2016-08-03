package com.marcoabreu.att.bridge;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Created by AbreuM on 03.08.2016.
 */
public class PairedHost {
    private final DeviceClient deviceClient;
    private final ObjectInputStream in;
    private final ObjectOutputStream out;
    private Thread messageThread;

    public PairedHost(DeviceClient deviceClient, ObjectInputStream in, ObjectOutputStream out)  {
        this.deviceClient = deviceClient;
        this.in = in;
        this.out = out;
    }
}
