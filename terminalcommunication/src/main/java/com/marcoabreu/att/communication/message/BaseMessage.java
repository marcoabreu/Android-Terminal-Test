package com.marcoabreu.att.communication.message;

import com.marcoabreu.att.communication.Opcode;

import java.io.Serializable;
import java.util.UUID;

/**
 * Created by AbreuM on 02.08.2016.
 */
public abstract class BaseMessage implements Serializable {
    private final UUID transactionId;
    private final Opcode opcode;
    private Exception occuredException = null;

    /**
     * Generate a new message
     * @param opcode Message opcode
     */
    public BaseMessage(Opcode opcode) {
        this.opcode = opcode;
        this.transactionId = UUID.randomUUID();
    }

    /**
     * Generate a response to a previous message
     */
    public BaseMessage(Opcode opcode, BaseMessage previousMessage) {
        this.opcode = opcode;
        this.transactionId = previousMessage.transactionId;
    }

    /**
     * Always call this method to validate a response
     * @throws Exception Exception thrown by the other party while serving the response
     */
    public void checkValidity() throws Exception {
        if(occuredException != null) {
            throw occuredException;
        }
    }

    public Exception getOccuredException() {
        return occuredException;
    }

    public void setOccuredException(Exception occuredException) {
        this.occuredException = occuredException;
    }

    public UUID getTransactionId() {
        return transactionId;
    }

    public Opcode getOpcode() {
        return opcode;
    }
}
