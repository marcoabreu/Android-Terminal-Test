package com.marcoabreu.att.script;

import com.marcoabreu.att.MainActivity;
import com.marcoabreu.att.bridge.PairedHost;

import java.io.IOException;
import java.io.Serializable;
import java.util.Map;

import bsh.EvalError;

/**
 * Helper to execute dynamic scripts on the device
 * Created by AbreuM on 03.08.2016.
 */
public class DeviceInterpreter {
    private final DynamicInterpreter dynamicInterpreter;

    public DeviceInterpreter(String methodName, String scriptContent, String path) throws IOException, EvalError {
        this.dynamicInterpreter = new DynamicInterpreter(methodName,scriptContent,path);
    }

    private Object execute(PairedHost host, Map<String, Serializable> parameters) throws EvalError {
        //Load runtime
        DeviceRuntimeContainer runtime = new DeviceRuntimeContainer(host);
        runtime.setAppContext(MainActivity.getAppContext());

        //Load parameters
        runtime.getParameters().putAll(parameters);

        return dynamicInterpreter.execute(runtime);
    }

    public <T> T executeReturn(PairedHost host, Map<String, Serializable> parameters) throws EvalError {
        return (T)execute(host, parameters);
    }

    public void executeVoid(PairedHost host, Map<String, Serializable> parameters) throws EvalError {
        execute(host, parameters);
    }
}
