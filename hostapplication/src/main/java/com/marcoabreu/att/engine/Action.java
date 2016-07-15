package com.marcoabreu.att.engine;

import java.util.function.Supplier;

/**
 * Basic composite to actually *do* something
 * Created by AbreuM on 30.06.2016.
 */
public class Action extends Composite {
    //TODO: Condition
    //private Object successCondition; //Allows action to succeed earlier than the defined timeframe

    //private int fixedDurationMs; //Action has a fixed time, it will succeed after this timeframe
    //private int timeoutDurationMs; //Action fails if success condition was not met within this timeframe
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
