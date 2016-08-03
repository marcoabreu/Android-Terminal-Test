package com.marcoabreu.att.storage;

import com.marcoabreu.att.communication.PhysicalDevice;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by AbreuM on 01.08.2016.
 */
public class DataStorage {
    private static DataStorage instance = new DataStorage();

    public static DataStorage getInstance() {
        return instance;
    }

    public DataStorage() {
        applicationData = new HashMap<>();
        deviceData = new HashMap<>();
        deviceProfileData = new HashMap<>();
        profileData = new HashMap<>();
    }

    private Map<String, Object> applicationData;
    private Map<PhysicalDevice, Map<String, Object>> deviceData;
    private Map<PhysicalDevice, Map<String, Object>> deviceProfileData;
    private Map<String, Object> profileData;

    public void purgeApplicationData() {
        applicationData.clear();
    }

    public void purgeDeviceData(String device) {
        deviceData.remove(device);
        deviceProfileData.remove(device);
    }

    public void purgeProfileData() {
        deviceProfileData.clear();
        profileData.clear();
    }

    public <T> T getData(String key) {
        return getData(key, null);
    }

    public <T> T getData(String key, PhysicalDevice device) {
        Object data = null;

        if(data == null) {
            data = applicationData.get(key);
        }

        if(data == null) {
            data = profileData.get(key);
        }

        if(device != null) {
            if(data == null) {
                data = deviceData.get(device).get(key);
            }

            if(data == null) {
                data = deviceProfileData.get(device).get(key);
            }
        }

        return (T)data;
    }

    public <T> void saveData(String key, PhysicalDevice device, StorageScope scope, T data) throws DuplicateKeyException {
        //Ensure device is set if device-specific scope is selected
        if(device == null && (scope == StorageScope.DEVICE_PROFILE || scope == StorageScope.DEVICE )) {
            throw new IllegalArgumentException("device must be set for scope " + scope.toString());
        }

        //Check for duplicate key
        if(scope != StorageScope.APPLICATION) {
            if(applicationData.containsKey(key)) {
                throw new DuplicateKeyException(key, StorageScope.APPLICATION);
            }
        }

        if(scope != StorageScope.PROFILE) {
            if(profileData.containsKey(key)) {
                throw new DuplicateKeyException(key, StorageScope.PROFILE);
            }
        }

        if(device != null) {
            if(scope != StorageScope.DEVICE) {
                if (deviceData.get(device).containsKey(key)) {
                    throw new DuplicateKeyException(key, StorageScope.DEVICE);
                }
            }

            if(scope != StorageScope.DEVICE_PROFILE) {
                if (deviceProfileData.get(device).containsKey(key)) {
                    throw new DuplicateKeyException(key, StorageScope.DEVICE_PROFILE);
                }
            }
        }

        switch(scope) {
            case APPLICATION:
                applicationData.put(key, data);
                break;
            case DEVICE:
                deviceData.get(device).put(key, data);
                break;
            case DEVICE_PROFILE:
                deviceProfileData.get(device).put(key, data);
                break;
            case PROFILE:
                profileData.put(key, data);
                break;
            default:
                throw new RuntimeException("Undefined Scope " + scope.toString());
        }
    }
}
