package com.marcoabreu.att.script;

import com.marcoabreu.att.MainActivity;
import com.marcoabreu.att.bridge.PairedHost;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import bsh.EvalError;
import bsh.UtilEvalError;

/**
 * Helper to execute dynamic scripts on the device
 * Created by AbreuM on 03.08.2016.
 */
public class DeviceInterpreter {
    private final DynamicInterpreter dynamicInterpreter;

    public DeviceInterpreter(String methodName, String className, String scriptContent, String path) throws IOException, EvalError, NoSuchMethodException, UtilEvalError {
        this.dynamicInterpreter = new DynamicInterpreter(methodName, className, scriptContent, path);
    }

    private Object execute(PairedHost host, Map<String, Serializable> parameters) throws EvalError, InvocationTargetException, IllegalAccessException {
        //Load runtime
        DeviceRuntimeContainer runtime = new DeviceRuntimeContainer(host);
        runtime.setAppContext(MainActivity.getAppContext());

        //Load parameters
        runtime.getParameters().putAll(parameters);

        return dynamicInterpreter.execute(runtime);
    }

    public <T> T executeReturn(PairedHost host, Map<String, Serializable> parameters) throws EvalError, InvocationTargetException, IllegalAccessException {
        return (T)execute(host, parameters);
    }

    public void executeVoid(PairedHost host, Map<String, Serializable> parameters) throws EvalError, InvocationTargetException, IllegalAccessException {
        execute(host, parameters);
    }
}
