package com.marcoabreu.att.communication.message;

import com.marcoabreu.att.communication.Opcode;

import java.io.Serializable;
import java.util.Map;

/**
 * Request the device to execute some dynamic code
 * Created by AbreuM on 03.08.2016.
 */
public class ExecuteActionMessage extends BaseMessage {
    private String methodName;
    private String scriptContent;
    private String path;
    private Map<String, Serializable> parameters;
    private boolean readReturnValue;
    public ExecuteActionMessage(String methodName, String scriptContent, String path, boolean readReturnValue, Map<String, Serializable> parameters) {
        super(Opcode.EXECUTE_ACTION_REQUEST);
        this.methodName = methodName;
        this.scriptContent = scriptContent;
        this.path = path;
        this.readReturnValue = readReturnValue;
        this.parameters = parameters;
    }

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public String getScriptContent() {
        return scriptContent;
    }

    public void setScriptContent(String scriptContent) {
        this.scriptContent = scriptContent;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public boolean isReadReturnValue() {
        return readReturnValue;
    }

    public void setReadReturnValue(boolean readReturnValue) {
        this.readReturnValue = readReturnValue;
    }

    public Map<String, Serializable> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Serializable> parameters) {
        this.parameters = parameters;
    }
}
