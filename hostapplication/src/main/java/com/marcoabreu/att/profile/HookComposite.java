package com.marcoabreu.att.profile;

import com.marcoabreu.att.engine.Composite;
import com.marcoabreu.att.engine.Sequence;

/**
 * Created by AbreuM on 11.08.2016.
 */
public class HookComposite extends Sequence {
    private final ProfileExecutor profileExecutor;
    private final Composite hookedComposite;

    public HookComposite(ProfileExecutor profileExecutor, Composite hookedComposite) {
        super(hookedComposite);
        this.profileExecutor = profileExecutor;
        this.hookedComposite = hookedComposite;
    }

    @Override
    public boolean start() {
        this.profileExecutor.invokeStartComposite(hookedComposite);

        boolean ret = super.start();

        return ret;
    }

    @Override
    public void stop() {
        super.stop();

        this.profileExecutor.invokeEndComposite(hookedComposite);
    }

    @Override
    protected void onTickChild(Composite childComposite) {
        super.onTickChild(childComposite);

        this.profileExecutor.invokeTickComposite(hookedComposite);
    }
}
