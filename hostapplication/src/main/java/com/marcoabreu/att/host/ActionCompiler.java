package com.marcoabreu.att.host;

import com.marcoabreu.att.profile.data.AttParameter;
import com.marcoabreu.att.profile.data.DynamicScript;
import com.marcoabreu.att.utilities.FileHelper;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.ExecutionException;

/**
 * Helper to compile and execute an action on the host machine
 * Created by AbreuM on 01.08.2016.
 */
public abstract class ActionCompiler {
    private static final String SCRIPTS_BASE_PATH = "scripts";
    protected final DynamicScript dynamicScript;

    public ActionCompiler(DynamicScript dynamicScript) throws IOException {
        this.dynamicScript = dynamicScript;

        //Init parameters
        this.dynamicScript.getParameters().forEach(AttParameter::init);
    }

    protected String getSourceFileContent(DynamicScript dynamicScript, boolean isHostFile) throws IOException {
        String dirType = "";

        if(isHostFile) {
            dirType += "host\\";
        } else {
            dirType += "device\\";
        }

        return FileUtils.readFileToString(FileUtils.getFile(FileHelper.getApplicationPath().toUri().getPath(), SCRIPTS_BASE_PATH, dirType, dynamicScript.getPath() + ".java"));
    }

    protected abstract Object execute() throws ExecutionException, InterruptedException, IOException, InvocationTargetException, IllegalAccessException;

    public <T> T executeReturn() throws InterruptedException, ExecutionException, IOException, InvocationTargetException, IllegalAccessException {
        return (T)execute();
    }

    public void executeVoid() throws InterruptedException, ExecutionException, IOException, InvocationTargetException, IllegalAccessException {
        execute();
    }
}
