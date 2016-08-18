package com.marcoabreu.att.host.handler;

import com.marcoabreu.att.communication.BridgeMessageListener;
import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.communication.message.BaseMessage;
import com.marcoabreu.att.communication.message.DataStorageGetRequest;
import com.marcoabreu.att.communication.message.DataStorageGetResponse;
import com.marcoabreu.att.storage.DataStorage;

import java.io.IOException;

/**
 * Handler to save data in the storage requested by a device
 * Created by AbreuM on 03.08.2016.
 */
public class DataStorageGetHandler implements BridgeMessageListener {
    @Override
    public void onMessage(PhysicalDevice device, BaseMessage message) throws IOException {
        if(message instanceof DataStorageGetRequest) {
            DataStorageGetRequest getRequest = (DataStorageGetRequest) message;
            DataStorageGetResponse response = new DataStorageGetResponse(getRequest);

            try {
                response.setValue(DataStorage.getInstance().getData(getRequest.getKey(), device));
            } catch (Exception ex) {
                response.setOccurredException(ex);
            }

            device.sendResponse(response);
        }
    }
}
