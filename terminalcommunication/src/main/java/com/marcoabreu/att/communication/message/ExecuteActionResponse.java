package com.marcoabreu.att.communication.message;

import com.marcoabreu.att.communication.Opcode;

/**
 * Response of the dynamic script execution
 * Created by AbreuM on 03.08.2016.
 */
public class ExecuteActionResponse extends BaseMessage {
    private Object returnValue;

    public ExecuteActionResponse(ExecuteActionMessage request) {
        super(Opcode.EXECUTE_ACTION_RESPONSE, request);

    }

    public Object getReturnValue() {
        return returnValue;
    }

    public void setReturnValue(Object returnValue) {
        this.returnValue = returnValue;
    }
}
