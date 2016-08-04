package com.marcoabreu.att.host;

import com.marcoabreu.att.host.ActionCompiler;
import com.marcoabreu.att.profile.ScriptRuntimeContainer;
import com.marcoabreu.att.profile.data.AttParameter;
import com.marcoabreu.att.profile.data.DynamicScript;
import com.marcoabreu.att.script.DynamicInterpreter;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import bsh.EvalError;
import bsh.UtilEvalError;

/**
 * Helper to compile and execute an action on the host machine
 * Created by AbreuM on 03.08.2016.
 */
public class HostActionCompiler extends ActionCompiler {
    private final DynamicInterpreter dynamicInterpreter;

    public HostActionCompiler(DynamicScript dynamicScript) throws EvalError, IOException, NoSuchMethodException, UtilEvalError {
        super(dynamicScript);
        this.dynamicInterpreter = new DynamicInterpreter(dynamicScript.getMethod(), new File(dynamicScript.getPath()).getName(), getSourceFileContent(dynamicScript, true), dynamicScript.getPath());
    }

    @Override
    protected Object execute() throws EvalError, InvocationTargetException, IllegalAccessException {
        //Load runtime
        ScriptRuntimeContainer runtime = new ScriptRuntimeContainer();

        //Load parameters
        for(AttParameter param : this.dynamicScript.getParameters()) {
            runtime.addParameter(param.getKey(), param.getValue());
        }

        return dynamicInterpreter.execute(runtime);
    }
}
