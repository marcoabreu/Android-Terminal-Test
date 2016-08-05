package com.marcoabreu.att.host;

import com.marcoabreu.att.profile.data.AttParameter;
import com.marcoabreu.att.profile.data.DynamicScript;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import bsh.EvalError;
import bsh.UtilEvalError;

/**
 * Helper to compile and execute an action on the host machine
 * Created by AbreuM on 03.08.2016.
 */
public class HostActionCompiler extends ActionCompiler {
    //private final DynamicInterpreter dynamicInterpreter;
    private final JavaInterpreter javaInterpreter;

    public HostActionCompiler(DynamicScript dynamicScript) throws EvalError, IOException, NoSuchMethodException, UtilEvalError, ClassNotFoundException {
        super(dynamicScript);
        this.javaInterpreter = new JavaInterpreter(dynamicScript.getMethod(), dynamicScript.getPath());
        //this.dynamicInterpreter = new DynamicInterpreter(dynamicScript.getMethod(), new File(dynamicScript.getPath()).getName(), getSourceFileContent(dynamicScript, true), dynamicScript.getPath());
    }

    /*@Override
    protected Object execute() throws EvalError, InvocationTargetException, IllegalAccessException {
        //Load runtime
        ScriptRuntimeContainer runtime = new ScriptRuntimeContainer();

        //Load parameters
        for(AttParameter param : this.dynamicScript.getParameters()) {
            runtime.addParameter(param.getKey(), param.getValue());
        }

        return dynamicInterpreter.execute(runtime);
    }*/

    @Override
    protected Object execute() throws EvalError, ExecutionException, InterruptedException, IOException, InvocationTargetException, IllegalAccessException {
        Map<String, Serializable> parameters = new HashMap<>();

        //Load parameters
        for(AttParameter param : this.dynamicScript.getParameters()) {
            parameters.put(param.getKey(), param.getValue());
        }

        javaInterpreter.executeVoid(parameters);

        return null;
    }
}
