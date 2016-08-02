package com.marcoabreu.att.communication;

import java.util.UUID;

/**
 * Created by AbreuM on 02.08.2016.
 */
public abstract class BaseMessage {
    private final UUID transactionId;
    private final Opcode opcode;

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

    public UUID getTransactionId() {
        return transactionId;
    }

    public Opcode getOpcode() {
        return opcode;
    }
}
