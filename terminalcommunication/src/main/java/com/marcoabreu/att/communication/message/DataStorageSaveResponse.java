package com.marcoabreu.att.communication.message;

import com.marcoabreu.att.communication.Opcode;

/**
 * Response the data storage get request
 * Created by AbreuM on 03.08.2016.
 */
public class DataStorageSaveResponse extends BaseMessage {
    private String key;

    public DataStorageSaveResponse(DataStorageSaveRequest request) {
        super(Opcode.STORAGE_DATA_SAVE_RESPONSE, request);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
