package com.marcoabreu.att.device;

import com.marcoabreu.att.utilities.Configuration;
import com.marcoabreu.att.utilities.FileHelper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Class to turn java source code into a class (JavaSE) or dex (Android) file ready to be loaded on runtime
 * Created by AbreuM on 04.08.2016.
 */
public class RuntimeCompiler {
    //Inspired by http://stackoverflow.com/questions/29348327/creating-a-dex-file-from-java-source-code
    private static final Logger LOG = LogManager.getLogger();
    private static final String SOURCE_FILENAME = "sources.txt";
    private static final String BUILD_DIRNAME = "build";
    private static final String DEX_FILENAME = "classes.dex";

    private static final String[] DEPENDENCIES;

    static {
        try {
            DEPENDENCIES = new String[]{
                    //"C:\\Users\\AbreuM\\AppData\\Local\\Android\\sdk\\platforms\\android-22\\android.jar", // Android library
                    //Android app, but have no idea how so we just put everything into TerminalCommunication
                    //"C:\\Users\\AbreuM\\AndroidStudioProjects\\AndroidTerminalTest\\hostapplication\\libs\\android-appcompat-v7-24.0.0-classes.jar", //Android support library
                    //"C:\\Users\\AbreuM\\AndroidStudioProjects\\AndroidTerminalTest\\hostapplication\\libs\\android-support-v4-24.0.0-classes.jar", //Android support library

                    //These two are required but are already contained in the fatJar, re-add this if we remove the fatJar
                    //"C:\\Users\\AbreuM\\AndroidStudioProjects\\AndroidTerminalTest\\terminalcommunication\\build\\libs\\terminalcommunication.jar", //Shared communication library
                    //"C:\\Users\\AbreuM\\AndroidStudioProjects\\AndroidTerminalTest\\hostapplication\\build\\libs\\hostapplication.jar"//Host application
                    FileHelper.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath()
            };
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private final String sourceDirectory;
    private File tempDir = null;

    public RuntimeCompiler(String sourceDirectory) {
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
     * @return File pointing to dex-file, Map representing 'filepath -> classpath'
     * @throws IOException
     * @throws InterruptedException
     */
    public Pair<File, Map<String, String>> convertDex(File libDir) throws IOException, InterruptedException, CompilerException {
        Map<String, String> mapping = compileToClass(libDir);
        File dexFile = compileToDex();
        return Pair.of(dexFile, mapping);
    }

    /**
     * Convert the input directory into class files and return mapping from local filepaths to resulting classpaths
     * @return File pointing to main directory containing classfile, Map representing 'filepath -> classpath'
     */
    public Pair<File, Map<String, String>> convertClass(File libDir) throws InterruptedException, CompilerException, IOException {
        Map<String, String> mapping = compileToClass(libDir);
        File classDir = new File(getTemporaryDirectory(), BUILD_DIRNAME);
        return Pair.of(classDir, mapping);
    }

    /**
     * Compile java source code into class-file
     * @param libDir Directory containing libs required for execution
     */
    private Map<String, String> compileToClass(File libDir) throws IOException, InterruptedException, CompilerException {
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

        //Load all script libs
        File [] libFiles = libDir.listFiles((dir, name) -> {
            return name.endsWith(".jar");
        });

        //Merge static dependencies with user specific libs
        ArrayList<String> dependencyPaths = new ArrayList<>();
        for(File file : libFiles) {
            dependencyPaths.add(file.getAbsolutePath());
        }
        dependencyPaths.addAll(Arrays.asList(DEPENDENCIES));

        //This requires JDK7 because of android-22
        String buildString = "\"" + getJavaCompilerFile().getAbsolutePath() + "\" -nowarn -cp \"DEPENDENCIES\" -d \"BUILDDIR\" @\"TEMPDIR\\SOURCELISTFILE\"";
        buildString = buildString.replace("DEPENDENCIES", String.join(";", dependencyPaths));
        buildString = buildString.replace("BUILDDIR", buildDir.getAbsolutePath());
        buildString = buildString.replace("SOURCELISTFILE", SOURCE_FILENAME);
        buildString = buildString.replace("TEMPDIR", getTemporaryDirectory().getAbsolutePath());

        Process buildProcess = Runtime.getRuntime().exec(buildString, null, getTemporaryDirectory());
        StringBuilder sb = new StringBuilder();
        try (BufferedReader processOutput = new BufferedReader(new InputStreamReader(buildProcess.getErrorStream()))) {
            String line;
            while ((line = processOutput.readLine()) != null) {
                sb.append(line);
                sb.append("\n");
            }
        }
        buildProcess.waitFor(15, TimeUnit.SECONDS);

        String buildResult = sb.toString();

        if(buildDir.list().length == 0 || !buildResult.isEmpty()) {
            throw new CompilerException("Unable to generate class files with buildstring: " + buildString + "\n" + buildResult);
        }

        return classpathMapping;
    }

    /**
     * Reads the java compiler filepath from the config or asks the user to enter it
     * @return
     */
    private File getJavaCompilerFile() {
        File javacFile = null;
        if(Configuration.getInstance().getJdk7path().isEmpty()) {
            //Ask user
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select javac.exe from JDK7");
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    ".exe files", "exe");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                javacFile = chooser.getSelectedFile();

                if(!javacFile.getName().endsWith("javac.exe")) {
                    throw new RuntimeException("You need to select the java compiler called javac.exe");
                }

                Configuration.getInstance().setJdk7path(javacFile.getAbsolutePath());
            } else {
                throw new RuntimeException("You did not select a javac compiler");
            }
        } else {
            javacFile = new File(Configuration.getInstance().getJdk7path());
        }

        return javacFile;
    }

    /**
     * Convert class-file into android-specific dex-file
     */
    private File compileToDex() throws IOException, InterruptedException, CompilerException {
        String buildString = "\"" + getDexBatFile().getAbsolutePath() + "\" --dex --output DEXFILE BUILDDIR";
        buildString = buildString.replace("DEXFILE", DEX_FILENAME);
        buildString = buildString.replace("BUILDDIR", BUILD_DIRNAME);

        Runtime.getRuntime().exec(buildString, null, getTemporaryDirectory()).waitFor(15, TimeUnit.SECONDS);

        File dexFile = new File(getTemporaryDirectory(), DEX_FILENAME);

        if(!dexFile.exists()) {
            throw new CompilerException("Unable to generate dex file with buildstring: " + buildString);
        }

        return dexFile;
    }

    /**
     * Reads the dex compiler filepath from the config or asks the user to enter it
     * @return
     */
    private File getDexBatFile() {
        File dexFile = null;
        if(Configuration.getInstance().getAndroidSdkPath().isEmpty()) {
            //Ask user
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select dx.bat Android-SDK build-tools");
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    ".bat files", "bat");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(null);
            if(returnVal == JFileChooser.APPROVE_OPTION) {
                dexFile = chooser.getSelectedFile();

                if(!dexFile.getName().endsWith("dx.bat")) {
                    throw new RuntimeException("You need to select the dex compiler called dx.bat");
                }

                Configuration.getInstance().setAndroidSdkPath(dexFile.getAbsolutePath());
            } else {
                throw new RuntimeException("You did not select a dex compiler");
            }
        } else {
            dexFile = new File(Configuration.getInstance().getAndroidSdkPath());
        }

        return dexFile;
    }
}
