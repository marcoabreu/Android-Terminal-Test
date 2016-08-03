package com.marcoabreu.att.script;

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
 * Created by AbreuM on 03.08.2016.
 */
public class DynamicInterpreter {
    private Interpreter interpreter;
    private BshMethod targetMethod;

    /**
     * Load a dynamic interpreter and preload the script.
     * @param methodName Method to be executed later
     * @param scriptContent Script which contains the requested method
     * @param path Debug information to display a better error location, maybe be empty
     * @throws EvalError
     * @throws IOException
     */
    public DynamicInterpreter(String methodName, String scriptContent, String path) throws EvalError, IOException {
        this.interpreter = new Interpreter();
        this.interpreter.setStrictJava(true);

        //Basically extract the newly added methods
        List<BshMethod> oldMethods = Arrays.asList(interpreter.getNameSpace().getMethods());
        interpreter.eval(scriptContent); //Load Action
        Set<BshMethod> newMethodsSet = new HashSet<>(Arrays.asList(interpreter.getNameSpace().getMethods()));
        newMethodsSet.removeAll(oldMethods);

        //Search for the method we're looking for - we simply set the constraint that method names must be unique within a script and dont bother about parameters
        targetMethod = null;
        for(BshMethod method : newMethodsSet) {
            if(method.getName().equals(methodName)) {
                targetMethod = method;
                break;
            }
        }

        if(targetMethod == null) {
            throw new NoSuchElementException(String.format("Method %s not found in %s", methodName, path));
        }

        //TODO: Validate method signature
    }

    /**
     * Execute the dynamically compiled method
     * @param contextContainer Container containing context informations
     * @return Return value of the called method, may be a void type
     * @throws EvalError
     */
    public Object execute(Object contextContainer) throws EvalError {
        return targetMethod.invoke(new Object[] { contextContainer }, interpreter);
    }

    /**
     * Execute the dynamically compiled method and return the return value
     * @param contextContainer Container containing context information
     * @param <T> Type of the return value
     * @return Return value of the method
     * @throws EvalError
     */
    public <T> T executeReturn(Object contextContainer) throws EvalError {
        return (T)execute(contextContainer);
    }

    /**
     * Execute the dynamically compiled method as a void without return value
     * @param contextContainer Container containing context information
     * @throws EvalError
     */
    public void executeVoid(Object contextContainer) throws EvalError {
        execute(contextContainer);
    }
}
