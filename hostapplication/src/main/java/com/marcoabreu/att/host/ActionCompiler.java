package com.marcoabreu.att.host;

import com.marcoabreu.att.profile.ScriptRuntimeContainer;
import com.marcoabreu.att.profile.data.AttParameter;
import com.marcoabreu.att.profile.data.DynamicScript;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;

import bsh.BshMethod;
import bsh.EvalError;
import bsh.Interpreter;

/**
 * Helper to compile and execute an action
 * Created by AbreuM on 01.08.2016.
 */
public class ActionCompiler {
    private final DynamicScript dynamicScript;
    private Interpreter interpreter;
    private BshMethod targetMethod;

    public ActionCompiler(DynamicScript dynamicScript) throws EvalError, IOException {
        this.dynamicScript = dynamicScript;
        this.interpreter = new Interpreter();
        this.interpreter.setStrictJava(true);

        //Basically extract the newly added methods
        List<BshMethod> oldMethods = Arrays.asList(interpreter.getNameSpace().getMethods());
        interpreter.eval(getSourceFileContent(dynamicScript)); //Load Action
        Set<BshMethod> newMethodsSet = new HashSet<>(Arrays.asList(interpreter.getNameSpace().getMethods()));
        newMethodsSet.removeAll(oldMethods);

        //Search for the method we're looking for - we simply set the constraint that method names must be unique within a script and dont bother about parameters
        targetMethod = null;
        for(BshMethod method : newMethodsSet) {
            if(method.getName().equals(dynamicScript.getMethod())) {
                targetMethod = method;
                break;
            }
        }

        //Init parameters
        this.dynamicScript.getParameters().forEach(AttParameter::init);

        if(targetMethod == null) {
            throw new NoSuchElementException(String.format("Method %s not found in %s", dynamicScript.getMethod(), dynamicScript.getPath()));
        }
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

        return targetMethod.invoke(new Object[] { runtime }, interpreter);
    }

    public <T> T executeReturn() throws EvalError {
        return (T)execute();
    }

    public void executeVoid() throws EvalError {
        execute();
    }
}
