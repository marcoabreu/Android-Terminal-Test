package com.marcoabreu.att.device;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import se.vidstige.jadb.AdbServerLauncher;
import se.vidstige.jadb.JadbConnection;
import se.vidstige.jadb.JadbDevice;
import se.vidstige.jadb.JadbException;
import se.vidstige.jadb.Transport;
import se.vidstige.jadb.managers.Package;
import se.vidstige.jadb.managers.PackageManager;

/**
 * Central element for device interaction
 * Created by AbreuM on 02.08.2016.
 */
public class DeviceManager {
    private static final int SERVER_PORT = 12022;
    private DeviceServer deviceServer;
    private Set<PairedDevice> pairedDevices;

    public DeviceManager() throws IOException {
        pairedDevices = new HashSet<>();
        deviceServer = new DeviceServer(this, SERVER_PORT);
        deviceServer.start();
    }

    /**
     * Get all physical android devices connected to the host
     * @return
     * @throws IOException
     * @throws InterruptedException
     * @throws JadbException
     */
    public List<JadbDevice> getConnectedDevices() throws IOException, InterruptedException, JadbException {
        new AdbServerLauncher().launch(); //TODO only request start if necessary - this is a hell of slow
        return createConnection().getDevices();
    }

    /**
     * Get all physical devices which have been paired successfully
     * @return
     */
    public List<PairedDevice> getPairedDevices() {
        throw new RuntimeException();
    }

    public void addPairedDevice(PairedDevice device) {
        this.pairedDevices.add(device);
    }

    /**
     * Start the pairing process with the device - this does not await the connection
     * @param device Device to pair
     */
    public void startPairing(JadbDevice device) throws IOException, JadbException {
        Transport transport = createConnection().createTransport();
        transport.send(String.format("host-serial:%s:forward:tcp:%d;tcp:%d", device.getSerial(), SERVER_PORT, SERVER_PORT));
        transport.verifyResponse();

        //Launch app
        PackageManager pm = new PackageManager(device);
        //TODO PackageManager install app if not installed and execute
        //adb shell pm grant com.your.package ****** to grant all permissions
        pm.launch(new Package("com.marcoabreu.att"));
    }

    private JadbConnection createConnection() throws IOException {
        return new JadbConnection();
    }
}
