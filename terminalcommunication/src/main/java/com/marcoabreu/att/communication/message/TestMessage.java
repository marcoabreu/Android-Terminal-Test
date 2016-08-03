package com.marcoabreu.att.communication.message;

import com.marcoabreu.att.communication.Opcode;

/**
 * Created by AbreuM on 03.08.2016.
 */
public class TestMessage extends BaseMessage {
    private final String message;

    public TestMessage(String message) {
        super(Opcode.TEST);
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }
}
