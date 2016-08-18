package com.marcoabreu.att.host;

import com.marcoabreu.att.communication.FutureResponse;
import com.marcoabreu.att.communication.PhysicalDevice;
import com.marcoabreu.att.communication.message.AbortActionMessage;
import com.marcoabreu.att.communication.message.ExecuteActionMessage;
import com.marcoabreu.att.communication.message.ExecuteActionResponse;
import com.marcoabreu.att.profile.data.AttParameter;
import com.marcoabreu.att.profile.data.DynamicScript;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Helper to compile and execute an action on the device
 * Created by AbreuM on 03.08.2016.
 */
public class DeviceActionCompiler extends ActionCompiler {
    private final PhysicalDevice device;
    private final String methodName;
    private final String className;
    private final String scriptContent;
    private final String scriptPath;

    public DeviceActionCompiler(PhysicalDevice device, DynamicScript dynamicScript) throws IOException {
        super(dynamicScript);
        this.device = device;
        this.methodName = dynamicScript.getMethod();
        this.className = new File(dynamicScript.getPath()).getName();
        this.scriptContent = getSourceFileContent(dynamicScript, false);
        this.scriptPath = dynamicScript.getPath();
    }

    @Override
    protected Serializable execute() throws ExecutionException, InterruptedException, IOException {
        Map<String, Serializable> parameters = new HashMap<>();

        //Load parameters
        for(AttParameter param : this.dynamicScript.getParameters()) {
            parameters.put(param.getKey(), param.getValue());
        }

        //String methodName, String scriptContent, String path, boolean readReturnValue, Map<String, Serializable> parameters
        ExecuteActionMessage request = new ExecuteActionMessage(this.methodName, this.className, this.scriptContent, this.scriptPath, true, parameters);
        FutureResponse<ExecuteActionResponse> response = this.device.sendMessage(request);
        return response.get().getReturnValue();
    }

    @Override
    public void finish() throws IOException {
        AbortActionMessage request = new AbortActionMessage();
        this.device.sendMessage(request);
    }
}
