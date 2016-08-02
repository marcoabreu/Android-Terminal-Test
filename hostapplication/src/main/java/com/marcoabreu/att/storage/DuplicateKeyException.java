package com.marcoabreu.att.storage;

/**
 * Exception thrown when tried to save data, which is already mapped to the same key but in a different scope
 * Created by AbreuM on 02.08.2016.
 */
public class DuplicateKeyException extends RuntimeException{
    public DuplicateKeyException() {
        super();
    }

    public DuplicateKeyException(String key, StorageScope scope) {
        super("The key '" + key + "' is already mapped under the scope " + scope.toString());
    }
}
