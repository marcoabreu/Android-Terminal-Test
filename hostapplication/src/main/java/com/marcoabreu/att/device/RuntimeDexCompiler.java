package com.marcoabreu.att.device;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Class to turn java source code into a dex file ready to be loaded by the device on runtime
 * Created by AbreuM on 04.08.2016.
 */
//TODO: Add error handling, Runtime.exec does not return any messages
public class RuntimeDexCompiler {
    //Inspired by http://stackoverflow.com/questions/29348327/creating-a-dex-file-from-java-source-code
    private static final String SOURCE_FILENAME = "sources.txt";
    private static final String BUILD_DIRNAME = "build";
    private static final String DEX_FILENAME = "classes.dex";

    private static final String[] DEPENDENCIES = new String[] {
            //TODO: relative paths
            "ANDROID_HOME/platforms/android-22/android.jar", // Android library
            //Android app
            "C:\\Users\\AbreuM\\AndroidStudioProjects\\AndroidTerminalTest\\terminalcommunication\\build\\libs\\terminalcommunication.jar" //Shared communication library
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

    /**
     * Convert the input directory to a dex file and return the mapping from local filepaths to resulting classpaths
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    public Pair<File, Map<String, String>> convert() throws IOException, InterruptedException, CompilerException {
        Map<String, String> mapping = compileToClass();
        File dexFile = convertToDex();
        return Pair.of(dexFile, mapping);
    }

    /**
     * Compile java source code into class-file
     */
    private Map<String, String> compileToClass() throws IOException, InterruptedException, CompilerException {
        //Find all java files and write to sources.txt
        Map<String, String> classpathMapping = new HashMap<>();
        File sourceListFile = new File(getTemporaryDirectory(), SOURCE_FILENAME);
        try(PrintWriter writer = new PrintWriter(new FileWriter(sourceListFile))) {
            for(File sourceFile : FileUtils.listFiles(new File(this.sourceDirectory), new String[] { "java" }, true)      ) {

                try(BufferedReader reader = new BufferedReader(new FileReader(sourceFile))) {
                    //Map the local file paths to the resulting classpath
                    try {
                        String curLine = "";
                        for(curLine = reader.readLine(); !curLine.startsWith("package "); curLine = reader.readLine()) {
                        }

                        String packageName = curLine.replace("package ", "");
                        packageName = packageName.replace(";", "");

                        String className = FilenameUtils.removeExtension(sourceFile.getName());

                        classpathMapping.put(sourceFile.getAbsolutePath(), packageName + "." + className);
                    } catch(IOException ex) {
                        throw new RuntimeException("File %s has no package defined");
                    }

                }


                writer.println(sourceFile.getAbsolutePath());
            }
        }


        File buildDir = new File(getTemporaryDirectory(), BUILD_DIRNAME);
        buildDir.mkdir();

        //TODO remove local paths
        //This requires JDK7 because of android-22
        String buildString = "\"C:\\Program Files\\Java\\jdk1.7.0_79\\bin\\javac.exe\" -cp \"DEPENDENCIES\" -d \"BUILDDIR\" @SOURCELISTFILE";
        buildString = buildString.replace("DEPENDENCIES", String.join(";", DEPENDENCIES));
        buildString = buildString.replace("BUILDDIR", buildDir.getAbsolutePath());
        buildString = buildString.replace("SOURCELISTFILE", SOURCE_FILENAME);

        Runtime.getRuntime().exec(buildString, null, getTemporaryDirectory()).waitFor();

        if(buildDir.list().length == 0) {
            throw new CompilerException("Unable to generate class files with buildstring: " + buildString);
        }

        return classpathMapping;
    }

    /**
     * Compile class-file into jar
     */
    private void compileToJar() throws IOException {

    }

    /**
     * Convert class-file into android-specific dex-file
     */
    private File convertToDex() throws IOException, InterruptedException, CompilerException {
        //TODO: use ANDROID_HOME
        String buildString = "C:\\Users\\AbreuM\\AppData\\Local\\Android\\sdk\\build-tools\\24.0.0\\dx.bat --dex --output DEXFILE BUILDDIR";
        buildString = buildString.replace("DEXFILE", DEX_FILENAME);
        buildString = buildString.replace("BUILDDIR", BUILD_DIRNAME);

        Runtime.getRuntime().exec(buildString, null, getTemporaryDirectory()).waitFor();

        File dexFile = new File(getTemporaryDirectory(), DEX_FILENAME);

        if(!dexFile.exists()) {
            throw new CompilerException("Unable to generate dex file with buildstring: " + buildString);
        }

        return dexFile;
    }
}
