package com.marcoabreu.att.profile;

import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.storage.DataStorageHostProxy;

import java.util.HashMap;
import java.util.Map;

/**
 * Container for data required during script execution
 * Created by AbreuM on 02.08.2016.
 */
public class ScriptRuntimeContainer {
    private final DataStorageHostProxy dataStorage;
    private Map<String, Object> parameters = new HashMap<>();

    public ScriptRuntimeContainer() {
        dataStorage = new DataStorageHostProxy();

    }

    public ScriptRuntimeContainer(PhysicalDevice device) {
        dataStorage = new DataStorageHostProxy(device);
    }

    public <T> void addParameter(String key, T data) {
        parameters.put(key, data);
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public <T> T getParameter(String key) {
        return (T)parameters.get(key);
    }

    public DataStorageHostProxy getDataStorage() {
        return dataStorage;
    }
}
