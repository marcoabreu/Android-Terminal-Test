package com.marcoabreu.att.storage;

import com.marcoabreu.att.bridge.PairedHost;
import com.marcoabreu.att.communication.FutureResponse;
import com.marcoabreu.att.communication.message.DataStorageGetRequest;
import com.marcoabreu.att.communication.message.DataStorageGetResponse;
import com.marcoabreu.att.communication.message.DataStorageSaveRequest;
import com.marcoabreu.att.communication.message.DataStorageSaveResponse;

import java.io.Serializable;

/**
 * Device proxy view to the DataStorage
 * Created by AbreuM on 03.08.2016.
 */
public class DataStorageDeviceProxy {
    //We request the data from the host over the network and keep it all at one place instead of mirroring it the whole time
    private final PairedHost host;

    public DataStorageDeviceProxy(PairedHost host) {
        this.host = host;
    }


    public <T extends Serializable> T getData(String key) {
        DataStorageGetRequest request = new DataStorageGetRequest(key);
        FutureResponse<DataStorageGetResponse> futureResponse = host.sendMessage(request);

        try {
            return (T)futureResponse.get().getValue();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public <T extends Serializable> void saveData(String key, StorageScope scope, T data) throws DuplicateKeyException {
        DataStorageSaveRequest request = new DataStorageSaveRequest(key, scope, data);
        FutureResponse<DataStorageSaveResponse> futureResponse = host.sendMessage(request);

        try {
            futureResponse.get();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
