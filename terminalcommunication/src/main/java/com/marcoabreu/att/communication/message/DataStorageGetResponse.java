package com.marcoabreu.att.communication.message;

import com.marcoabreu.att.communication.Opcode;

import java.io.Serializable;

/**
 * Response the data storage get request
 * Created by AbreuM on 03.08.2016.
 */
public class DataStorageGetResponse extends BaseMessage {
    private String key;
    private Object value;

    public <T extends Serializable> DataStorageGetResponse(DataStorageGetRequest request) {
        super(Opcode.STORAGE_DATA_GET_RESPONSE, request);
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}
