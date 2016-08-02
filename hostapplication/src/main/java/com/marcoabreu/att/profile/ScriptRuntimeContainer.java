package com.marcoabreu.att.profile;

import com.marcoabreu.att.storage.DataStorageProxy;

import java.util.HashMap;
import java.util.Map;

/**
 * Container for data required during script execution
 * Created by AbreuM on 02.08.2016.
 */
public class ScriptRuntimeContainer {
    private final DataStorageProxy dataStorage;
    private Map<String, Object> parameters = new HashMap<>();

    public ScriptRuntimeContainer() {
        dataStorage = new DataStorageProxy();

    }

    public ScriptRuntimeContainer(String device) {
        dataStorage = new DataStorageProxy(device);
    }

    public void addParameter(String key, Object data) {
        parameters.put(key, data);
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public DataStorageProxy getDataStorage() {
        return dataStorage;
    }
}
