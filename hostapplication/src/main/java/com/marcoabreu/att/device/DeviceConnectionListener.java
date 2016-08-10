package com.marcoabreu.att.device;

import se.vidstige.jadb.JadbDevice;

/**
 * Listener to handle events regarding a device
 * Created by AbreuM on 08.08.2016.
 */
public interface DeviceConnectionListener {
    /**
     * Event thrown upon detecting a connected device via USB. It's now ready to pair
     * @param device
     */
    void onDeviceConnected(JadbDevice device);

    /**
     * Event thrown after a device has been disconnected from USB
     * @param device
     */
    void onDeviceDisconnected(JadbDevice device);

    /**
     * Event thrown when the permission dialog on the device has to be confirmed
     * @param device
     */
    void onDeviceNeedPermission(JadbDevice device);

    /**
     * Event thrown after a device has successfully paired with this application. It's now ready for operation
     * @param device
     */
    void onDevicePaired(PairedDevice device);

    /**
     * Event thrown after the pairing to a device was removed
     * @param device
     */
    void onDeviceUnpaired(PairedDevice device);

    /**
     * Event thrown after a device has been assigned to an alias
     * @param device
     */
    void onDeviceAssigned(PairedDevice device);

    /**
     * Event thrown upon removing the assignment of a device - it is still paired
     * @param device
     */
    void onDeviceUnassigned(PairedDevice device);
}
