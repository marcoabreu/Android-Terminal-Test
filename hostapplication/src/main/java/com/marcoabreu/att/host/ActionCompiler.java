package com.marcoabreu.att.host;

import com.marcoabreu.att.profile.ScriptRuntimeContainer;
import com.marcoabreu.att.profile.data.AttParameter;
import com.marcoabreu.att.profile.data.DynamicScript;
import com.marcoabreu.att.script.DynamicInterpreter;

import org.apache.commons.io.FileUtils;

import java.io.IOException;

import bsh.BshMethod;
import bsh.EvalError;
import bsh.Interpreter;

/**
 * Helper to compile and execute an action on the host machine
 * Created by AbreuM on 01.08.2016.
 */
public class ActionCompiler {
    private final DynamicInterpreter dynamicInterpreter;
    private final DynamicScript dynamicScript;
    private Interpreter interpreter;
    private BshMethod targetMethod;

    public ActionCompiler(DynamicScript dynamicScript) throws EvalError, IOException {
        this.dynamicScript = dynamicScript;
        this.dynamicInterpreter = new DynamicInterpreter(dynamicScript.getMethod(), getSourceFileContent(dynamicScript), dynamicScript.getPath());

        //Init parameters
        this.dynamicScript.getParameters().forEach(AttParameter::init);
    }

    private String getSourceFileContent(DynamicScript dynamicScript) throws IOException {
        //TODO: Turn into variable path and stuff - this is just for me
        final String baseDirectory = "C:\\Users\\AbreuM\\AndroidStudioProjects\\AndroidTerminalTest\\hostapplication\\res\\scripts\\host\\";

        return FileUtils.readFileToString(FileUtils.getFile(baseDirectory, dynamicScript.getPath() + ".java"));
    }

    private Object execute() throws EvalError {
        //Load runtime
        ScriptRuntimeContainer runtime = new ScriptRuntimeContainer();

        //Load parameters
        for(AttParameter param : this.dynamicScript.getParameters()) {
            runtime.addParameter(param.getKey(), param.getValue());
        }

        return dynamicInterpreter.execute(runtime);
    }

    public <T> T executeReturn() throws EvalError {
        return (T)execute();
    }

    public void executeVoid() throws EvalError {
        execute();
    }
}
