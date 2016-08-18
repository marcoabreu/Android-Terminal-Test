package com.marcoabreu.att.host.handler;

import com.marcoabreu.att.communication.BridgeMessageListener;
import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.communication.message.BaseMessage;
import com.marcoabreu.att.communication.message.DataStorageSaveRequest;
import com.marcoabreu.att.communication.message.DataStorageSaveResponse;
import com.marcoabreu.att.storage.DataStorage;

import java.io.IOException;

/**
 * Handler to save data in the storage requested by a device
 * Created by AbreuM on 03.08.2016.
 */
public class DataStorageSaveHandler implements BridgeMessageListener {
    @Override
    public void onMessage(PhysicalDevice device, BaseMessage message) throws IOException {
        if(message instanceof DataStorageSaveRequest) {
            DataStorageSaveRequest saveRequest = (DataStorageSaveRequest) message;
            DataStorageSaveResponse response = new DataStorageSaveResponse(saveRequest);

            try {
                DataStorage.getInstance().saveData(saveRequest.getKey(), device, saveRequest.getScope(), saveRequest.getData());
            } catch (Exception ex) {
                response.setOccurredException(ex);
            }

            device.sendResponse(response);
        }
    }
}
