package com.marcoabreu.att.host;

import com.marcoabreu.att.profile.data.AttParameter;
import com.marcoabreu.att.profile.data.DynamicScript;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import bsh.EvalError;

/**
 * Helper to compile and execute an action on the host machine
 * Created by AbreuM on 01.08.2016.
 */
public abstract class ActionCompiler {
    protected final DynamicScript dynamicScript;

    public ActionCompiler(DynamicScript dynamicScript) throws EvalError, IOException {
        this.dynamicScript = dynamicScript;

        //Init parameters
        this.dynamicScript.getParameters().forEach(AttParameter::init);
    }

    protected String getSourceFileContent(DynamicScript dynamicScript, boolean isHostFile) throws IOException {
        //TODO: Turn into variable path and stuff - this is just for me
        String baseDirectory = "C:\\Users\\AbreuM\\AndroidStudioProjects\\AndroidTerminalTest\\hostapplication\\res\\scripts\\";

        if(isHostFile) {
            baseDirectory += "host\\";
        } else {
            baseDirectory += "device\\";
        }

        return FileUtils.readFileToString(FileUtils.getFile(baseDirectory, dynamicScript.getPath() + ".java"));
    }

    protected abstract Object execute() throws EvalError, ExecutionException, InterruptedException, IOException;

    public <T> T executeReturn() throws EvalError, InterruptedException, ExecutionException, IOException {
        return (T)execute();
    }

    public void executeVoid() throws EvalError, InterruptedException, ExecutionException, IOException {
        execute();
    }
}
