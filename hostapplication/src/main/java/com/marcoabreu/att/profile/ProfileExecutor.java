package com.marcoabreu.att.profile;

import com.marcoabreu.att.engine.Action;
import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.engine.Executor;
import com.marcoabreu.att.engine.IfStatement;
import com.marcoabreu.att.engine.RunStatus;
import com.marcoabreu.att.engine.Sequence;
import com.marcoabreu.att.profile.data.AttProfile;

/**
 * Service to execute a single profile run
 * Created by AbreuM on 01.08.2016.
 */
public class ProfileExecutor implements AutoCloseable{
    private final AttProfile profile;
    private Executor executor;

    public ProfileExecutor(AttProfile profile) {
        this.profile = profile;
    }

    public void start() {
        Composite profileComposite = profile.convertLogic();
        Composite hookedComposite = applyHooks(profileComposite);

        this.executor = new Executor(hookedComposite);

        this.executor.start();
    }

    public void pause() {
        //An Action itself cannot be paused, but we use our hooks to alter the execution - this means we can pause BETWEEN executions but not a running action itself

        //TODO: implement
    }

    public void stop() {
        this.executor.abort();
    }

    public RunStatus getRunState() {
        return this.executor.execute(0);
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

        return new Sequence(
                //TODO: Start-Action-Hook
                new Action(() -> {
                    System.out.println("Start composite");
                    return true;
                }),
                baseComposite
        );
    }

    @Override
    public void close() throws Exception {
        if(executor != null) {
            executor.close();
        }
    }
}
