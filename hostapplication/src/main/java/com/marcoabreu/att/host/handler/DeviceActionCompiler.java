package com.marcoabreu.att.host.handler;

import com.marcoabreu.att.communication.FutureResponse;
import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.communication.message.ExecuteActionMessage;
import com.marcoabreu.att.communication.message.ExecuteActionResponse;
import com.marcoabreu.att.host.ActionCompiler;
import com.marcoabreu.att.profile.data.AttParameter;
import com.marcoabreu.att.profile.data.DynamicScript;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import bsh.EvalError;

/**
 * Helper to compile and execute an action on the device
 * Created by AbreuM on 03.08.2016.
 */
public class DeviceActionCompiler extends ActionCompiler {
    private final PhysicalDevice device;
    private final String methodName;
    private final String scriptContent;
    private final String scriptPath;

    public DeviceActionCompiler(PhysicalDevice device, DynamicScript dynamicScript) throws EvalError, IOException {
        super(dynamicScript);
        this.device = device;
        this.methodName = dynamicScript.getMethod();
        this.scriptContent = getSourceFileContent(dynamicScript, false);
        this.scriptPath = dynamicScript.getPath();
    }

    @Override
    protected Serializable execute() throws EvalError, ExecutionException, InterruptedException, IOException {
        Map<String, Serializable> parameters = new HashMap<>();

        //Load parameters
        for(AttParameter param : this.dynamicScript.getParameters()) {
            parameters.put(param.getKey(), param.getValue());
        }

        //String methodName, String scriptContent, String path, boolean readReturnValue, Map<String, Serializable> parameters
        ExecuteActionMessage request = new ExecuteActionMessage(this.methodName, this.scriptContent, this.scriptPath, true, parameters);
        FutureResponse<ExecuteActionResponse> response = this.device.sendMessage(request);
        return response.get().getReturnValue();
    }
}
