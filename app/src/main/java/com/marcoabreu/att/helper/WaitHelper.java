package com.marcoabreu.att.helper;

import java.util.function.Supplier;

/**
 * Created by AbreuM on 15.07.2016.
 */
public class WaitHelper {
    private static final int TICK_DURATION_MS = 10;
    private WaitHelper() {
        throw new RuntimeException("Singleton");
    }

    /**
     * Wait while condition is met
     * @param condition Condition
     * @return True if wait successful, false if interrupted
     */
    public static boolean waitWhile(Supplier<Boolean> condition) {
        try {
            while(condition.get()) {
                Thread.sleep(TICK_DURATION_MS);
            }
        } catch (InterruptedException e) {
            return false;
        }

        return true;
    }
}
