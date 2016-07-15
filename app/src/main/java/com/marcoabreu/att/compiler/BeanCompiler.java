package com.marcoabreu.att.compiler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import bsh.EvalError;
import bsh.Interpreter;

/**
 * Created by AbreuM on 14.07.2016.
 */
public class BeanCompiler {
    private String codeToExecute = "";
    private String pathToSource = "";
    private HashMap<String, Object> parameters = new HashMap<>();

    public BeanCompiler() {

    }

    /**
     * Clean up compiler and reset to starting state
     */
    public void clear() {
        codeToExecute = "";
        pathToSource = "";
        parameters.clear();
    }

    /**
     * Prepare code to execute
     * @param codeToExecute Code to execute
     */
    public void loadCode(String codeToExecute) {
        this.codeToExecute = codeToExecute;
    }

    public void loadSource(String pathToFile) {
        this.pathToSource = pathToFile;
    }

    /**
     * Load a parameter which gets passed to the executing code
     * @param name parameter name used in the code
     * @param value parameter value
     */
    public void loadParameter(String name, Object value) {
        parameters.put(name, value);
    }

    public void execute() throws EvalError, CompilerException, IOException {
        if(this.codeToExecute.isEmpty() && this.pathToSource.isEmpty()) {
            throw new CompilerException("No code to execute defined");
        }

        Interpreter interpreter = new Interpreter();
        interpreter.setStrictJava(true);

        if(!this.pathToSource.isEmpty()) {
            interpreter.source(this.pathToSource);
        }


        for(Map.Entry<String, Object> param : parameters.entrySet()) {
            interpreter.set(param.getKey(), param.getValue());
        }

        interpreter.eval(this.codeToExecute);
    }
}
