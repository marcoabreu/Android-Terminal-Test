package com.marcoabreu.att.communication.message;

import com.marcoabreu.att.communication.Opcode;

/**
 * Created by AbreuM on 18.08.2016.
 */
public class AbortActionMessage extends BaseMessage {
    public AbortActionMessage() {
        super(Opcode.ABORT_ACTION_REQUEST);
    }
}
