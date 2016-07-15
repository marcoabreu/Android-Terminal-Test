package com.marcoabreu.att.compiler;

/**
 * Created by AbreuM on 14.07.2016.
 */
public class CompilerException extends Exception {

    public CompilerException() {
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

    public CompilerException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
