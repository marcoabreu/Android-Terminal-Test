package com.marcoabreu.att.script;

import android.content.Context;

import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.storage.DataStorageDeviceProxy;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by AbreuM on 03.08.2016.
 */
public class DeviceRuntimeContainer {
    //TODO: Remove huge android-22.jar from libs
    private final DataStorageDeviceProxy dataStorage;
    private Context appContext;
    private Map<String, Object> parameters = new HashMap<>();

    public DeviceRuntimeContainer(PhysicalDevice host) {
        this.dataStorage = new DataStorageDeviceProxy(host);
    }

    public void addParameter(String key, Object data) {
        parameters.put(key, data);
    }

    public Map<String, Object> getParameters() {
        return this.parameters;
    }

    public <T> T getParameter(String key) {
        return (T)parameters.get(key);
    }

    public DataStorageDeviceProxy getDataStorage() {
        return this.dataStorage;
    }

    public Context getAppContext() {
        return appContext;
    }

    public void setAppContext(Context appContext) {
        this.appContext = appContext;
    }
}
