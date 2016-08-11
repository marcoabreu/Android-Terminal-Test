package com.marcoabreu.att.profile;

import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.engine.Executor;
import com.marcoabreu.att.engine.IfStatement;
import com.marcoabreu.att.engine.RunStatus;
import com.marcoabreu.att.engine.Sequence;
import com.marcoabreu.att.profile.data.AttComposite;
import com.marcoabreu.att.profile.data.AttProfile;

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
    private final AttProfile profile;
    private Executor executor;
    private Set<ProfileExecutionListener> listeners;
    private Map<Composite, AttComposite> compositeMapping;

    public ProfileExecutor(AttProfile profile) {
        this.profile = profile;
        this.compositeMapping = new HashMap<>();
        listeners = new HashSet<>();
    }

    public void start() throws Exception {
        if(getRunState() == RunStatus.RUNNING) {
            throw new IllegalStateException("Profile is already being executed");
        }

        try {
            Composite profileComposite = profile.convertLogic(this);
            Composite hookedComposite = applyHooks(profileComposite);

            this.executor = new Executor(hookedComposite);

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

    public void pause() {
        //An Action itself cannot be paused, but we use our hooks to alter the execution - this means we can pause BETWEEN executions but not a running action itself

        //TODO: implement
    }

    public void stop() {
        this.executor.abort();
    }

    public RunStatus getRunState() {
        if(this.executor == null) {
            return RunStatus.FAILURE;
        }
        return this.executor.execute(0);
    }

    public ExecutionException getLastExecutionException() {
        if(this.executor == null) {
            return null;
        }

        return this.executor.getLastExecutionException();
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
}
