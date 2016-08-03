package com.marcoabreu.att.communication.message;

import com.marcoabreu.att.communication.Opcode;
import com.marcoabreu.att.storage.StorageScope;

import java.io.Serializable;

/**
 * Request data from the data storage on the host
 * Created by AbreuM on 03.08.2016.
 */
public class DataStorageSaveRequest extends BaseMessage {
    private String key;
    private StorageScope scope;
    private Object data;

    public <T extends Serializable> DataStorageSaveRequest(String key, StorageScope scope, T data) {
        super(Opcode.STORAGE_DATA_SAVE_REQUEST);

        this.key = key;
        this.scope = scope;
        this.data = data;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public StorageScope getScope() {
        return scope;
    }

    public void setScope(StorageScope scope) {
        this.scope = scope;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
