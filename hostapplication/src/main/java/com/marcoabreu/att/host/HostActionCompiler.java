package com.marcoabreu.att.host;

import com.marcoabreu.att.profile.data.AttParameter;
import com.marcoabreu.att.profile.data.DynamicScript;

import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * Helper to compile and execute an action on the host machine
 * Created by AbreuM on 03.08.2016.
 */
public class HostActionCompiler extends ActionCompiler {
    private final JavaInterpreter javaInterpreter;

    public HostActionCompiler(DynamicScript dynamicScript) throws IOException, NoSuchMethodException, ClassNotFoundException {
        super(dynamicScript);
        this.javaInterpreter = new JavaInterpreter(dynamicScript.getMethod(), dynamicScript.getPath());
    }

    @Override
    protected Object execute() throws ExecutionException, InterruptedException, IOException, InvocationTargetException, IllegalAccessException {
        Map<String, Serializable> parameters = new HashMap<>();

        //Load parameters
        for(AttParameter param : this.dynamicScript.getParameters()) {
            parameters.put(param.getKey(), param.getValue());
        }

        javaInterpreter.executeVoid(parameters);

        return null;
    }
}
