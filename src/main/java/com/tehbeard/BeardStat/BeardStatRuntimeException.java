package com.tehbeard.BeardStat;

/**
 * Represents an error that might occur during the operation of BeardStat.
 * 
 * @author James
 * 
 */
public class BeardStatRuntimeException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 3658834482344482602L;

    private boolean           recoverable      = false;

    public BeardStatRuntimeException(String message, Throwable cause, boolean recoverable) {
        super(message, cause);
        this.recoverable = recoverable;
    }

    public boolean isRecoverable() {
        return this.recoverable;
    }
}
