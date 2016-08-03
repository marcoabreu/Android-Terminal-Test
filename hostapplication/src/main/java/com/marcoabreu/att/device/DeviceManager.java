package com.marcoabreu.att.device;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    private static DeviceManager instance;
    private DeviceServer deviceServer;
    private Set<PairedDevice> pairedDevices;
    private Map<String, PairedDevice> assignedDevices;

    public static DeviceManager getInstance() {
        //TODO make it nice
        if(instance == null) {
            try {
                instance = new DeviceManager();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return instance;
    }

    public DeviceManager() throws IOException {
        pairedDevices = ConcurrentHashMap.newKeySet();
        assignedDevices = new HashMap<>();
        deviceServer = new DeviceServer(this, SERVER_PORT);

    }

    public void start() throws IOException, InterruptedException {
        new AdbServerLauncher().launch();
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
        //new AdbServerLauncher().launch(); //TODO only request start if necessary - this is a hell of slow
        return createConnection().getDevices();
    }

    /**
     * Gets a physical device by its synonym and asks the user to select a device in case it's not defined yet
     * @param synonym synonym like "device1", "device2" etc
     * @return
     */
    public PairedDevice getPairedDeviceBySynonym(String synonym) {
        PairedDevice device = assignedDevices.get(synonym);

        if(device == null) {
            //Device not defined, ask the user
            //TODO: Ask the user

            assignedDevices.put(synonym, getPairedDevices().iterator().next());

            return getPairedDeviceBySynonym(synonym); //Recursive in case user enters rubbish or closes the dialog. TODO: Reconsider this way
        } else {
            return device;
        }

    }

    /**
     * Get all physical devices which have been paired successfully
     * @return
     */
    public Set<PairedDevice> getPairedDevices() {
        return this.pairedDevices;
    }

    public void addPairedDevice(PairedDevice device) {
        this.pairedDevices.add(device);
    }

    /**
     * Start the pairing process with the device - this does not await the connection
     * @param device Device to pair
     */
    public void startPairing(JadbDevice device) throws IOException, JadbException {
        //Transport transport = createConnection().createTransport();
        Transport transport = device.getTransport();
        //transport.send(String.format("host-serial:%s:forward:tcp:%d;tcp:%d", device.getSerial(), SERVER_PORT, SERVER_PORT));
        //transport.send(String.format("host-serial:%s:forward:tcp:%d;tcp:%d", device.getSerial(), SERVER_PORT, SERVER_PORT));
        transport.send(String.format("reverse:forward:tcp:%d;tcp:%d", SERVER_PORT, SERVER_PORT));
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

    public DeviceServer getDeviceServer() {
        return deviceServer;
    }
}
