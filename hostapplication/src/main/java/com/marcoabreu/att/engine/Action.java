package com.marcoabreu.att.engine;

import java.util.function.Supplier;

/**
 * Basic composite to actually *do* something
 * Created by AbreuM on 30.06.2016.
 */
public class Action extends Composite {
    //TODO: add timeout
    private final Supplier<Boolean> runnable;

    /**
     * Instantiate an Action with the passed runnable
     * @param runnable Method which will be executed
     */
    public Action(Supplier<Boolean> runnable) {
        this.runnable = runnable;
    }

    @Override
    protected boolean start() {
        if(this.runnable == null) {
            throw new IllegalArgumentException("No runnable defined");
        }

        return true;
    }

    @Override
    protected boolean run() {
        return runnable.get();
    }

    @Override
    protected void stop() {

    }
}
