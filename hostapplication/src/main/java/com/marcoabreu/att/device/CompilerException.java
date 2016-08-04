package com.marcoabreu.att.device;

/**
 * Created by AbreuM on 04.08.2016.
 */
public class CompilerException extends  Exception {
    public CompilerException() {
        super();
    }

    public CompilerException(String message) {
        super(message);
    }

    public CompilerException(String message, Throwable cause) {
        super(message, cause);
    }

    public CompilerException(Throwable cause) {
        super(cause);
    }

    protected CompilerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
