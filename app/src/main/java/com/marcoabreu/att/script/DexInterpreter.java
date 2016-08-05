package com.marcoabreu.att.script;

import android.content.Context;

import com.marcoabreu.att.MainActivity;
import com.marcoabreu.att.bridge.PairedHost;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

import dalvik.system.DexClassLoader;

/**
 * Interpreter to execute dynamic scripts on android
 * Created by AbreuM on 04.08.2016.
 */
public class DexInterpreter {
    //Inspired by http://android-developers.blogspot.cz/2011/07/custom-class-loading-in-dalvik.html
    private static final String DEX_FILENAME = "att-scripts.dex";
    private static final int BUFFER_SIZE = 8 * 1024;
    private static DexClassLoader dexClassLoader;
    private static Map<String, String> classpathMapping;

    private Class targetClass;
    private Method targetMethod;

    public DexInterpreter(String methodName, String path) throws ClassNotFoundException {
        if(!classpathMapping.containsKey(path)) {
            throw new NoSuchElementException("No class for path " + path + " loaded");
        }

        targetClass = dexClassLoader.loadClass(classpathMapping.get(path));

        targetMethod = null;
        for(Method method : targetClass.getMethods()) {
            if(method.getName().equals(methodName)) {
                targetMethod = method;
                break;
            }
        }

        //TODO: merge with DynamicInterpreter to have a base class which gets a class-object and does all the other heavy lifting. This class should only load the dex
    }

    private Object execute(PairedHost host, Map<String, Serializable> parameters) throws InvocationTargetException, IllegalAccessException {
        //Load runtime
        DeviceRuntimeContainer runtime = new DeviceRuntimeContainer(host);
        runtime.setAppContext(MainActivity.getAppContext());

        //Load parameters
        runtime.getParameters().putAll(parameters);



        return targetMethod.invoke(null, new Object[] { runtime });
    }

    public <T> T executeReturn(PairedHost host, Map<String, Serializable> parameters) throws InvocationTargetException, IllegalAccessException {
        return (T)execute(host, parameters);
    }

    public void executeVoid(PairedHost host, Map<String, Serializable> parameters) throws InvocationTargetException, IllegalAccessException {
        execute(host, parameters);
    }

    /**
     * Request the dex file and load it into the classpath
     */
    public static void init(Context context, byte[] dexFileContent, Map<String, String> classpathMapping) throws IOException, ExecutionException, InterruptedException {
        // Before the secondary dex file can be processed by the DexClassLoader,
        // it has to be first written to a storage location.
        final File dexInternalStoragePath = new File(context.getDir("dex", Context.MODE_PRIVATE), DEX_FILENAME);
        final File optimizedDexOutputPath = context.getDir("outdex", Context.MODE_PRIVATE);

        try(OutputStream dexWriter = new BufferedOutputStream(new FileOutputStream(dexInternalStoragePath));
            ByteArrayInputStream reader = new ByteArrayInputStream(dexFileContent)) {
            byte[] buf = new byte[BUFFER_SIZE];
            int len;
            while((len = reader.read(buf, 0, BUFFER_SIZE)) > 0) {
                dexWriter.write(buf, 0, len);
            }
        }

        dexClassLoader = new DexClassLoader(dexInternalStoragePath.getAbsolutePath(),
                optimizedDexOutputPath.getAbsolutePath(),
                null,
                context.getClassLoader());
        DexInterpreter.classpathMapping = classpathMapping;
    }
}
