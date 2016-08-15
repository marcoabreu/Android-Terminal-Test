package com.marcoabreu.att.profile;

import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.engine.Executor;
import com.marcoabreu.att.engine.IfStatement;
import com.marcoabreu.att.engine.RunStatus;
import com.marcoabreu.att.engine.Sequence;
import com.marcoabreu.att.profile.data.AttAction;
import com.marcoabreu.att.profile.data.AttComposite;
import com.marcoabreu.att.profile.data.AttProfile;

import org.apache.commons.lang3.time.StopWatch;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Service to execute a single profile run
 * Created by AbreuM on 01.08.2016.
 */
public class ProfileExecutor implements AutoCloseable{
    private static final Logger LOG = LogManager.getLogger();
    private final AttProfile profile;
    private Executor executor;
    private Set<ProfileExecutionListener> listeners;
    private Map<Composite, AttComposite> compositeMapping;
    private ProfileExecutionFlowWatcher profileExecutionFlowWatcher;

    public ProfileExecutor(AttProfile profile) {
        this.profile = profile;
        this.compositeMapping = new HashMap<>();
        listeners = new HashSet<>();

        profileExecutionFlowWatcher = new ProfileExecutionFlowWatcher(this);
        this.addListener(profileExecutionFlowWatcher);
    }



    public void start() throws Exception {
        if(this.getExecutionStatus().isRunning()) {
            throw new IllegalStateException("Profile is already being executed");
        }

        try {
            Composite profileComposite = profile.convertLogic(this);
            Composite hookedComposite = applyHooks(profileComposite);

            Executor.startThreadPool();
            this.executor = new Executor(hookedComposite);

            this.profileExecutionFlowWatcher.reset();
            this.executor.start();
        } catch (Exception e) {
            throw new Exception("Error during profile conversion", e);
        }
    }

    public void addListener(ProfileExecutionListener listener) {
        listeners.add(listener);
    }

    public void removeListener(ProfileExecutionListener listener) {
        listeners.remove(listener);
    }

    private AttComposite retrieveProfileComposite(Composite logicComposite) {
        AttComposite profileComposite = compositeMapping.get(logicComposite);

        if(profileComposite == null) {
            throw new NoSuchElementException("Composite " + logicComposite.toString() + " not mapped in profile executor");
        }

        return profileComposite;
    }

    public void invokeStartComposite(Composite logicComposite) {
        AttComposite profileComposite = retrieveProfileComposite(logicComposite);

        listeners.stream().forEach(listener -> listener.onStartComposite(profileComposite, logicComposite));
    }

    public void invokeEndComposite(Composite logicComposite) {
        AttComposite profileComposite = retrieveProfileComposite(logicComposite);

        listeners.stream().forEach(listener -> listener.onEndComposite(profileComposite, logicComposite));
    }

    public void invokeTickComposite(Composite logicComposite) {
        AttComposite profileComposite = retrieveProfileComposite(logicComposite);

        for(ProfileExecutionListener listener : listeners) {
            listener.onTickComposite(profileComposite, logicComposite);
        }
    }


    /**
     * Register a converted composite to allow state-tracking during execution
     * @param attComposite Input profile
     * @param resultingComposite Output logic
     * @return Returns resultingComposite for convenience use
     */
    public Composite registerComposite(AttComposite attComposite, Composite resultingComposite) {
        this.compositeMapping.put(resultingComposite, attComposite);

        return resultingComposite;
    }

    public void togglePause() {
        //An Action itself cannot be paused, but we use our hooks to alter the execution - this means we can pause BETWEEN executions but not a running action itself
        profileExecutionFlowWatcher.togglePause();
    }

    public void stop() {
        this.executor.abort();
        Executor.abortThreadPool();
    }

    public ExecutionException getLastExecutionException() {
        if(this.executor == null) {
            return null;
        }

        return this.executor.getLastExecutionException();
    }


    //Some stuff regarding the status of the engine to give the user more informations

    public enum ExecutionStatus {
        /**
         * Engine is running
         */
        RUNNING(true),

        /**
         * Engine is running and pause request
         */
        PAUSING(true),

        /**
         * Engine is paused and waiting to resume
         */
        PAUSED(true),

        /**
         * Engine stopped gracefully
         */
        STOPPED(false),

        /**
         * Execution failed
         */
        FAILED(false);

        private final boolean engineRunning;

        ExecutionStatus(boolean engineRunning) {
            this.engineRunning = engineRunning;
        }

        public boolean isRunning() {
            return engineRunning;
        }
    }

    public ExecutionStatus getExecutionStatus() {
        if(this.executor == null) {
            return ExecutionStatus.STOPPED;
        }

        RunStatus runStatus = this.executor.execute(0);

        if(runStatus == RunStatus.SUCCESS) {
            return ExecutionStatus.STOPPED;
        } else if (runStatus == RunStatus.FAILURE) {
            return ExecutionStatus.FAILED;
        } else {
            if(profileExecutionFlowWatcher.isPauseWaiting()) {
                return ExecutionStatus.PAUSED;
            }

            if(profileExecutionFlowWatcher.isPauseRequested()) {
                return ExecutionStatus.PAUSING;
            }

            return ExecutionStatus.RUNNING;
        }
    }

    /**
     * Returns the time spent on the running action in seconds
     * @return
     */
    public long getElapsedActionTimeSeconds() {
        return profileExecutionFlowWatcher.getElapsedTimeNano() / 1000000000;
    }

    /**
     * Returns the amount of seconds at which the running action will time out. 0 means no timeout
     * @return
     */
    public long getTimeoutActionTimeSeconds() {
        long timeout = profileExecutionFlowWatcher.getTimeoutNano();
        if(timeout != Long.MAX_VALUE) {
            return timeout / 1000000000;
        } else {
            return Long.MAX_VALUE;
        }

    }

    /**
     * Return time in seconds until the execution times out. 0 means it will not timeout
     */
    public long getTimeoutTimeLeftSeconds() {
        long timeLeft = profileExecutionFlowWatcher.getTimeLeftNano();

        if(timeLeft != Long.MAX_VALUE) {
            return timeLeft / 1000000000;
        } else {
            return Long.MAX_VALUE;
        }
    }




    /**
     * This method applies hooks to the passed composites and its children. This will allow to keep track of the execution. This will modify the passed composite.
     * @param baseComposite Composite to hook
     * @return Hooked composite
     */
    private Composite applyHooks(Composite baseComposite) {
        if(baseComposite == null) {
            throw new IllegalArgumentException("baseComposite may not be null");
        }

        //The following objects contain children, which need hooks aswell
        if(baseComposite instanceof Sequence) {
            Sequence sequenceComposite = (Sequence)baseComposite;

            for(Composite child : sequenceComposite.getChildren()) {
                sequenceComposite.replaceChild(child, applyHooks(child));
            }

        } else if(baseComposite instanceof IfStatement) {
            IfStatement conditionComposite = (IfStatement)baseComposite;

            if(conditionComposite.getSuccessComposite() != null) {
                conditionComposite.setSuccessComposite(applyHooks(conditionComposite.getSuccessComposite()));
            }

            if(conditionComposite.getFailComposite() != null) {
                conditionComposite.setFailComposite(applyHooks(conditionComposite.getFailComposite()));
            }
        }

        return new HookComposite(this, baseComposite);
    }

    @Override
    public void close() throws Exception {
        if(executor != null) {
            executor.close();
        }
    }

    private class ProfileExecutionFlowWatcher implements ProfileExecutionListener {
        private final ProfileExecutor profileExecutor;
        private StopWatch stopWatch = new StopWatch();
        private long timeoutNano;
        private boolean pauseRequested = false;
        private boolean pauseWaiting = false;

        public ProfileExecutionFlowWatcher(ProfileExecutor profileExecutor) {
            this.profileExecutor = profileExecutor;
        }

        /**
         * Reset the watcher and its states
         */
        public void reset() {
            this.pauseRequested = false;
            this.pauseWaiting = false;
            this.stopWatch.reset();
            this.timeoutNano = Long.MAX_VALUE;
        }

        public void togglePause() {
            this.pauseRequested = !this.pauseRequested;
        }

        @Override
        public void onStartComposite(AttComposite profileComposite, Composite engineComposite) {
            waitIfPaused();

            timeoutNano = Long.MAX_VALUE;
            //Start execution timer if action has a timeout
            if(profileComposite instanceof AttAction) {
                AttAction attAction = (AttAction) profileComposite;
                if(attAction.getTimeoutMs() > 0) {
                    timeoutNano = attAction.getTimeoutMs() * 1000000; //convert to nanoseconds
                }
            }

            stopWatch.reset();
            stopWatch.start();
        }

        @Override
        public void onEndComposite(AttComposite profileComposite, Composite engineComposite) {
            if(stopWatch.isStarted()) {
                stopWatch.stop();
            }

            waitIfPaused();
        }

        @Override
        public void onTickComposite(AttComposite profileComposite, Composite engineComposite) {
            //Check if we got a timer running and break execution if timeout reached
            if(timeoutNano != Long.MAX_VALUE && stopWatch.isStarted()) {
                if(stopWatch.getNanoTime() >= timeoutNano) {
                    stopWatch.stop();
                    throw new ActionTimeoutException("Maximum execution time exceeded");
                }
            }
        }

        /**
         * Helper method to block the current thread while the execution is paused
         */
        private void waitIfPaused() {
            if(this.pauseRequested) {
                try {
                    this.pauseRequested = true;
                    LOG.debug("ProfileExecutor has been paused, waiting");
                    while (this.pauseRequested) {
                        try {
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                        }
                    }
                    LOG.debug("Resuming execution");
                }finally {
                    this.pauseWaiting = false;
                }
            }
        }

        public boolean isPauseRequested() {
            return pauseRequested;
        }

        public boolean isPauseWaiting() {
            return pauseWaiting;
        }

        public long getElapsedTimeNano() {
            return this.stopWatch.getNanoTime();
        }

        /**
         * Long.MAX_VALUE if no timeout, time in nanoseconds otherwise
         */
        public long getTimeoutNano() {
            return timeoutNano;
        }

        /**
         * Long.MAX_VALUE if no time left, time in nanoseconds otherwise
         * @return
         */
        public long getTimeLeftNano() {
            if(getTimeoutNano() == Long.MAX_VALUE) {
                return Long.MAX_VALUE;
            }

            return getTimeoutNano() - getElapsedTimeNano();
        }
    }
}
