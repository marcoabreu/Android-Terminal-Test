package com.marcoabreu.att.communication.message;

import com.marcoabreu.att.communication.Opcode;

import java.io.Serializable;

/**
 * Response of the dynamic script execution
 * Created by AbreuM on 03.08.2016.
 */
public class ExecuteActionResponse extends BaseMessage {
    private Serializable returnValue;

    public ExecuteActionResponse(ExecuteActionMessage request) {
        super(Opcode.EXECUTE_ACTION_RESPONSE, request);

    }

    public Serializable getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Serializable returnValue) {
        this.returnValue = returnValue;
    }
}
