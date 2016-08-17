package com.marcoabreu.att.host;

import com.marcoabreu.att.device.CompilerException;
import com.marcoabreu.att.device.RuntimeCompiler;
import com.marcoabreu.att.profile.ScriptRuntimeContainer;
import com.marcoabreu.att.utilities.FileHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

/**
 * /**
 * Interpreter to execute dynamic scripts on a java machine
 * Created by AbreuM on 04.08.2016.
 */
public class JavaInterpreter {
    //Inspired by http://android-developers.blogspot.cz/2011/07/custom-class-loading-in-dalvik.html
    private static final String SCRIPTS_HOST_BASE_PATH = "scripts/host";
    private static final String SCRIPT_LIBS_HOST_BASE_PATH = "scripts/host/libs";
    private static final String DEX_FILENAME = "att-scripts.dex";
    private static final int BUFFER_SIZE = 8 * 1024;
    private static URLClassLoader urlClassLoader;
    private static Map<String, String> classpathMapping;

    private Class targetClass;
    private Method targetMethod;

    public JavaInterpreter(String methodName, String path) throws ClassNotFoundException {
        if(!classpathMapping.containsKey(path)) {
            throw new NoSuchElementException("No class for path " + path + " loaded");
        }

        targetClass = urlClassLoader.loadClass(classpathMapping.get(path));

        targetMethod = null;
        for(Method method : targetClass.getMethods()) {
            if(method.getName().equals(methodName)) {
                targetMethod = method;
                break;
            }
        }

        //TODO: merge with DynamicInterpreter and DexInterpreter to have a base class which gets a class-object and does all the other heavy lifting. This class should only load the dex
    }

    private Object execute(Map<String, Serializable> parameters) throws InvocationTargetException, IllegalAccessException {
        //Load runtime
        ScriptRuntimeContainer runtime = new ScriptRuntimeContainer();

        //Load parameters
        runtime.getParameters().putAll(parameters);

        return targetMethod.invoke(null, new Object[] { runtime });
    }

    public <T> T executeReturn(Map<String, Serializable> parameters) throws InvocationTargetException, IllegalAccessException {
        return (T)execute(parameters);
    }

    public void executeVoid( Map<String, Serializable> parameters) throws InvocationTargetException, IllegalAccessException {
        execute(parameters);
    }

    /**
     * Load the dynamic script files
     */
    public static void init(/*File classDirectory, Map<String, String> classpathMapping*/) throws IOException, ExecutionException, InterruptedException, CompilerException {
        String basePath = FileUtils.getFile(FileHelper.getApplicationPath().toUri().getPath(), SCRIPTS_HOST_BASE_PATH).getPath();
        String libPath = FileUtils.getFile(FileHelper.getApplicationPath().toUri().getPath(), SCRIPT_LIBS_HOST_BASE_PATH).getPath();
        RuntimeCompiler compiler = new RuntimeCompiler(basePath);
        Pair<File, Map<String, String>> compiledClasses = compiler.convertClass(new File(libPath));

        Map<String, String> classpathMapping = new HashMap<>();
        for(Map.Entry<String, String> entry : compiledClasses.getRight().entrySet()) {
            String replaced = entry.getKey().replace(basePath, "").replace(".java", "").replace("\\", "/");
            replaced = replaced.substring(1); //Remove starting slash
            classpathMapping.put(replaced, entry.getValue());
        }

        JavaInterpreter.urlClassLoader = new URLClassLoader(new URL[] {compiledClasses.getLeft().toURI().toURL()});
        JavaInterpreter.classpathMapping = classpathMapping;
    }
}
