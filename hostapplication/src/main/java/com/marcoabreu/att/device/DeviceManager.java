package com.marcoabreu.att.device;

import com.marcoabreu.att.ui.AssignDeviceDialog;
import com.marcoabreu.att.utilities.FileHelper;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashSet;
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
    private static final Logger LOG = org.apache.logging.log4j.LogManager.getLogger();
    private static final String APP_PERMISSION_FILE = "/scripts/device/permissions.txt";
    private static final String APK_FILE_PATH = "/apk/app-release.apk";
    private static final String APK_PACKAGE_NAME = "com.marcoabreu.att";
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
            device = showAssignDeviceDialog(alias);

            if(device != null) {
                addDeviceAssignment(device, alias);
            } else {
                throw new RuntimeException("User canceled device selection");
            }
        }

        return device;
    }

    private PairedDevice showAssignDeviceDialog(String alias) {
        Set<PairedDevice> unassignedDevices = getPairedDevices();
        unassignedDevices.removeAll(assignedDevices.entrySet());
        AssignDeviceDialog dialog = new AssignDeviceDialog(alias, unassignedDevices);
        PairedDevice selectedDevice = dialog.showDialog();

        return selectedDevice;
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
        //Start tunnel
        Transport transport = device.getTransport();
        transport.send(String.format("reverse:forward:tcp:%d;tcp:%d", SERVER_PORT, SERVER_PORT));
        transport.verifyResponse();

        //Close app if it was opened
        device.executeShell("am force-stop " + APK_PACKAGE_NAME);

        //Start app
        PackageManager pm = new PackageManager(device);
        checkInstallApp(pm);

        grantPermissions(device);

        pm.launch(new Package(APK_PACKAGE_NAME));
    }

    private void checkInstallApp(PackageManager packageManager) throws IOException, JadbException {
        if(!packageManager.getPackages().stream().anyMatch(aPackage -> aPackage.toString().equals(APK_PACKAGE_NAME))) {
            LOG.info("Client application not found, installing...");
            File apk = FileUtils.getFile(FileHelper.getApplicationPath().toUri().getPath(), APK_FILE_PATH);
            packageManager.forceInstall(apk);
        } else {
            LOG.debug("Client application found");
        }
    }

    private void grantPermissions(JadbDevice device) throws IOException, JadbException {
        try (BufferedReader br = new BufferedReader(new FileReader(FileUtils.getFile(FileHelper.getApplicationPath().toUri().getPath(), APP_PERMISSION_FILE)))) {
            String permission;
            while ((permission = br.readLine()) != null) {
                if(permission.startsWith("//") || permission.isEmpty()) {
                    continue;
                }

                LOG.info("Granting permission " + permission);
                device.executeShell("pm grant " + APK_PACKAGE_NAME + " " + permission);
            }
        }
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
            try {
                while (true) {
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
                            if (false) {
                                invokeOnDeviceNeedPermission(device);
                            }
                        });
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JadbException e) {
                        e.printStackTrace();
                    }

                    Thread.sleep(CONNECTION_WATCHER_DELAY_MS);
                }
            } catch (InterruptedException e) {
                LOG.error("ConnectedDevicesThread interrupted. Stopping");
            }
        }
    }
}
