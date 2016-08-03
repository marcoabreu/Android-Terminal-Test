package com.marcoabreu.att.communication.message;

import com.marcoabreu.att.communication.Opcode;

/**
 * Request data from the data storage on the host
 * Created by AbreuM on 03.08.2016.
 */
public class DataStorageGetRequest extends BaseMessage {
    private String key;
    public DataStorageGetRequest(String key) {
        super(Opcode.STORAGE_DATA_GET_REQUEST);

        this.key = key;
    }

    public String getKey() {
        return key;
    }
}
