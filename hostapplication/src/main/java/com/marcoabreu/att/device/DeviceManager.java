package com.marcoabreu.att.device;

import com.marcoabreu.att.ui.AssignDeviceDialog;
import org.apache.logging.log4j.Logger;
import se.vidstige.jadb.*;
import se.vidstige.jadb.managers.Package;
import se.vidstige.jadb.managers.PackageManager;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Central element for device interaction
 * Created by AbreuM on 02.08.2016.
 */
public class DeviceManager {
    private static final Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
    private static final int SERVER_PORT = 12022;
    private static final int CONNECTION_WATCHER_DELAY_MS = 2000;
    private static DeviceManager instance;
    private DeviceServer deviceServer;
    private Set<PairedDevice> pairedDevices;
    private Map<String, PairedDevice> assignedDevices;
    private Thread connectionThread;

    private Set<DeviceConnectionListener> deviceConnectionListeners;

    public static synchronized DeviceManager getInstance() {
        if(instance == null) {
            try {
                instance = new DeviceManager();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return instance;
    }

    private DeviceManager() throws IOException {
        pairedDevices = ConcurrentHashMap.newKeySet();
        assignedDevices = new ConcurrentHashMap<>();
        deviceServer = new DeviceServer(this, SERVER_PORT);
        deviceConnectionListeners = new HashSet<>();
    }

    public void start() throws IOException, InterruptedException {
        new AdbServerLauncher().launch();

        connectionThread = new Thread(new ConnectedDevicesThread());
        connectionThread.setDaemon(true);
        connectionThread.start();

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
        return createConnection().getDevices();
    }

    /**
     * Gets a physical device by its alias and asks the user to select a device in case it's not defined yet
     * @param alias alias like "device1", "device2" etc
     * @return
     */
    public PairedDevice getPairedDeviceByAlias(String alias) {
        PairedDevice device = assignedDevices.get(alias);
        if(device == null) {
            //Device not defined, ask the user
            showAssignDeviceDialog(alias);

            return getPairedDeviceByAlias(alias); //Recursive in case user enters rubbish or closes the dialog.
        } else {
            return device;
        }
    }

    private void showAssignDeviceDialog(String alias) {
        Set<PairedDevice> unassignedDevices = getPairedDevices();
        unassignedDevices.removeAll(assignedDevices.entrySet());
        AssignDeviceDialog dialog = new AssignDeviceDialog(alias, unassignedDevices);
        PairedDevice selectedDevice = dialog.showDialog();

        if(selectedDevice != null) {
            addDeviceAssignment(selectedDevice, alias);
        }
    }

    public synchronized void addDeviceAssignment(PairedDevice device, String alias) {
        LOG.info("Assigning " + device.toString() + " to alias " + alias);
        this.assignedDevices.put(alias, device);
        invokeOnDeviceAssigned(device, alias);
    }

    public void removeDeviceAssignment(String alias) {

        PairedDevice device = this.assignedDevices.remove(alias);

        if(device != null) {
            LOG.info("Unassigned " + device.toString() + " from alias " + alias);
            invokeOnDeviceUnassigned(device);
        }
    }

    /**
     * Get all physical devices which have been paired successfully
     * @return
     */
    public Set<PairedDevice> getPairedDevices() {
        return this.pairedDevices;
    }

    public void registerPairedDevice(PairedDevice device) {
        LOG.info("Paired successfully to device " + device.getSerial());
        this.pairedDevices.add(device);
        invokeOnDevicePaired(device);
    }

    public void addDevicePairedListener(DeviceConnectionListener listener) {
        this.deviceConnectionListeners.add(listener);
    }

    public void invokeOnDevicePaired(PairedDevice device) {
        deviceConnectionListeners.forEach(listener -> listener.onDevicePaired(device));
    }

    public void invokeOnDeviceUnpaired(PairedDevice device) {
        deviceConnectionListeners.forEach(listener -> listener.onDeviceUnpaired(device));
    }

    public void invokeOnDeviceConnected(JadbDevice device) {
        deviceConnectionListeners.forEach(listener -> listener.onDeviceConnected(device));
    }

    public void invokeOnDeviceDisconnected(JadbDevice device) {
        deviceConnectionListeners.forEach(listener -> listener.onDeviceDisconnected(device));
    }

    public void invokeOnDeviceNeedPermission(JadbDevice device) {
        deviceConnectionListeners.forEach(listener -> listener.onDeviceNeedPermission(device));
    }

    public void invokeOnDeviceAssigned(PairedDevice device, String alias) {
        deviceConnectionListeners.forEach(listener -> listener.onDeviceAssigned(device, alias));
    }

    public void invokeOnDeviceUnassigned(PairedDevice device) {
        deviceConnectionListeners.forEach(listener -> listener.onDeviceUnassigned(device));
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

    /**
     * Thread to watch devices connected via USB (TODO: consider if should be pairing automatically)
     */
    private class ConnectedDevicesThread implements Runnable {
        @Override
        public void run() {

            Set<JadbDevice> previouslyConnectedDevices = new HashSet<>();
            while(true) {
                try {
                    Set<JadbDevice> connectedDevices = new HashSet<>(getConnectedDevices());
                    Set<JadbDevice> finalPreviouslyConnectedDevices = previouslyConnectedDevices; //Required because of lambda
                    previouslyConnectedDevices = connectedDevices;

                    //Check for disconnected devices
                    finalPreviouslyConnectedDevices.stream().filter(device -> !connectedDevices.contains(device)).forEach(device -> {
                        invokeOnDeviceDisconnected(device);
                    });

                    //Check for newly connected devices
                    connectedDevices.stream().filter(device -> !finalPreviouslyConnectedDevices.contains(device)).forEach(device -> {
                        invokeOnDeviceConnected(device);

                        //TODO: Check if permission required
                        if(false) {
                            invokeOnDeviceNeedPermission(device);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (JadbException e) {
                    e.printStackTrace();
                }

                try {
                    Thread.sleep(CONNECTION_WATCHER_DELAY_MS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
