package com.marcoabreu.att.storage;

import com.marcoabreu.att.communication.PhysicalDevice;

/**
 * Proxied view on the DataStorage for host
 * Created by AbreuM on 02.08.2016.
 */
public class DataStorageHostProxy {
    private PhysicalDevice device;

    /**
     * No device specific view
     */
    public DataStorageHostProxy() {
        this.device = null;
    }

    /**
     * Device specific view
     * @param device selected device
     */
    public DataStorageHostProxy(PhysicalDevice device) {
        this.device = device;
    }

    public <T> T getData(String key) {
        return DataStorage.getInstance().getData(key, device);
    }

    public <T> void saveData(String key, StorageScope scope, T data) throws DuplicateKeyException {
        DataStorage.getInstance().saveData(key, device, scope, data);
    }
}
