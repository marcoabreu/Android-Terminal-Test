package com.marcoabreu.att.script;

import com.marcoabreu.att.bridge.PairedHost;
import com.marcoabreu.att.storage.DataStorageDeviceProxy;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AbreuM on 03.08.2016.
 */
public class DeviceRuntimeContainer {
    private final DataStorageDeviceProxy dataStorage;
    private Map<String, Object> parameters = new HashMap<>();

    public DeviceRuntimeContainer(PairedHost host) {
        this.dataStorage = new DataStorageDeviceProxy(host);
    }

    public void addParameter(String key, Object data) {
        parameters.put(key, data);
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public DataStorageDeviceProxy getDataStorage() {
        return this.dataStorage;
    }
}
