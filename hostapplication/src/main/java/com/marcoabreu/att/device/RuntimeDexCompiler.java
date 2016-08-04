package com.marcoabreu.att.device;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Class to turn java source code into a dex file ready to be loaded by the device on runtime
 * Created by AbreuM on 04.08.2016.
 */
public class RuntimeDexCompiler {
    private static final String SOURCE_FILENAME = "sources.txt";
    private static final String[] DEPENDENCIES = new String[] {
            //Android library
            //Shared library
    };

    private final String sourceDirectory;
    private File tempDir = null;

    public RuntimeDexCompiler(String sourceDirectory) {
        this.sourceDirectory = sourceDirectory;
    }

    private synchronized File getTemporaryDirectory() {

        if(tempDir == null) {
            tempDir = com.google.common.io.Files.createTempDir();
        }

        return tempDir;
    }

    public void work() {
        try {
            compileToClass();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Compile java source code into class-file
     */
    private void compileToClass() throws IOException {
        //Find all java files and write to sources.txt
        try(PrintWriter writer = new PrintWriter(new FileWriter(new File(getTemporaryDirectory(), SOURCE_FILENAME)))) {
            for(File sourceFile : FileUtils.listFiles(new File(this.sourceDirectory), new String[] { "java" }, true)      ) {
                writer.println(sourceFile.getAbsolutePath());
            }
        }


    }

    /**
     * Compile class-file into jar
     */
    private void compileToJar() {

    }

    /**
     * Convert Jar into android-specific dex-file
     */
    private void convertToDex() {

    }
}
