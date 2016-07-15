package com.marcoabreu.att.test.speed;

import com.marcoabreu.att.engine.Action;
import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.engine.Executor;
import com.marcoabreu.att.engine.RunStatus;
import com.marcoabreu.att.engine.Sequence;

import java.lang.ref.WeakReference;
import java.text.NumberFormat;
import java.util.Date;

/**
 * Not an actual test, just save this code somewhere
 * Created by AbreuM on 01.07.2016.
 */
public class DepthTest {
    private static final int MAX_DEPTH = 5;
    public static void depthTest() {
        Date startDate = new Date();
        Date startedDate = new Date();
        Date actionDate = new Date();
        Date finishDate = new Date();

        Composite ss = new Action(() -> {
            actionDate.setTime(new Date().getTime());
            return true;
        });
        for(int i = 0; i < MAX_DEPTH; i++) {
            ss = new Sequence(ss);
        }

        //We're using a cached thread pool, so make sure we run a few times before we test the real results.
        //We don't want init of the thread pool to scramble up our results
        for(int i = 1; i <= 10; i++) {
            try(Executor ex = new Executor(ss)) {
                startDate = new Date();
                ex.start();
                startedDate = new Date();

                while(ex.execute(100) == RunStatus.RUNNING) {
                }
                finishDate = new Date();
            } catch(Exception e) {

            }
        }

        Date now = new Date();
        System.out.println("Start: " + (now.getTime() - startDate.getTime()));
        System.out.println("Started: " + (now.getTime() - startedDate.getTime()));
        System.out.println("Action: " + (now.getTime() - actionDate.getTime()));
        System.out.println("Finished: " + (now.getTime() - finishDate.getTime()));
    }

    private static String memUsage() {
        gc();
        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        StringBuilder sb = new StringBuilder();
        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();

        sb.append("free memory: " + format.format(freeMemory / 1024) + "\n");
        sb.append("allocated memory: " + format.format(allocatedMemory / 1024) + "\n");
        sb.append("max memory: " + format.format(maxMemory / 1024) + "\n");
        sb.append("total free memory: " + format.format((freeMemory + (maxMemory - allocatedMemory)) / 1024) + "\n");
        return sb.toString();
    }

    /**
     * This method guarantees that garbage collection is
     * done unlike <code>{@link System#gc()}</code>
     */
    private static void gc() {
        Object obj = new Object();
        WeakReference ref = new WeakReference<Object>(obj);
        obj = null;
        while(ref.get() != null) {
            System.gc();
        }
    }
}
