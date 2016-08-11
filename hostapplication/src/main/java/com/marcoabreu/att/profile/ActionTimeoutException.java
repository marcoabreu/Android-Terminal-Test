package com.marcoabreu.att.profile;

/**
 * Exception thrown when an action exceeds its allowed running duration
 * Created by AbreuM on 11.08.2016.
 */
public class ActionTimeoutException extends RuntimeException {
    public ActionTimeoutException() {
        super();
    }

    public ActionTimeoutException(String message) {
        super(message);
    }

    public ActionTimeoutException(String message, Throwable cause) {
        super(message, cause);
    }

    public ActionTimeoutException(Throwable cause) {
        super(cause);
    }

    protected ActionTimeoutException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
