package com.marcoabreu.att.storage;

/**
 * Proxied view on the DataStorage
 * Created by AbreuM on 02.08.2016.
 */
public class DataStorageProxy {
    private String device;

    /**
     * No device specific view
     */
    public DataStorageProxy() {
        this.device = null;
    }

    /**
     * Device specific view
     * @param device selected device
     */
    public DataStorageProxy(String device) {
        this.device = device;
    }

    public <T> T getData(String key) {
        return DataStorage.getInstance().getData(key, device);
    }

    public <T> void saveData(String key, StorageScope scope, T data) throws DuplicateKeyException {
        DataStorage.getInstance().saveData(key, device, scope, data);
    }
}
