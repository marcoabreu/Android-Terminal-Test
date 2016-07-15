package com.marcoabreu.att.engine;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Executor to run Composites, best use via try-with-resources
 * Created by AbreuM on 30.06.2016.
 */
public class Executor implements AutoCloseable {
    //private static final ExecutorService threadPool = Executors.newFixedThreadPool(10); //TODO: Configurable
    private static final ExecutorService threadPool = Executors.newCachedThreadPool();
    private final Composite composite;
    private Future<RunStatus> executingFuture;

    public Executor(Composite compositeToExecute) {
        if(compositeToExecute == null) {
            throw new IllegalArgumentException("No composite passed");
        }

        this.composite = compositeToExecute;
    }

    /**
     * Starts the executor and aborts previous tasks
     */
    public void start() {
        //Clean up
        if(executingFuture != null) {
            abort();
        }

        executingFuture = this.threadPool.submit(composite);
    }

    /**
     * Method to get the status of the underlying composite
     * @param msToWait maximum time to wait for the result
     * @return Current execution status
     */
    public RunStatus execute(long msToWait) {
        if(executingFuture == null) {
            throw new IllegalStateException("Start the executor first");
        }

        if(executingFuture.isCancelled()) {
            return RunStatus.FAILURE;
        }

        try {
            return executingFuture.get(msToWait, TimeUnit.MILLISECONDS);
        } catch (InterruptedException e) {
            return RunStatus.FAILURE;
        } catch (ExecutionException e) {
            throw new RuntimeException("Unknown exception", e);
        } catch (TimeoutException e) {
            return RunStatus.RUNNING;
        }
    }

    /**
     * Abort currently executed composite
     */
    public void abort() {
        if(executingFuture == null) {
            throw new IllegalStateException("Start the executor first");
        }

        executingFuture.cancel(true);
    }

    /**
     * Cleans up the state of this executor
     */
    public void cleanUp() {
        if(executingFuture != null) {
            abort();
        }

        executingFuture = null;
    }

    @Override
    public void close() throws Exception {
        cleanUp();
    }
}
