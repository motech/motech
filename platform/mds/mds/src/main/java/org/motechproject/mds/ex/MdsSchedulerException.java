package org.motechproject.mds.ex;

/**
 * The <code>MdsSchedulerException</code> exception signals problems
 * with scheduling MDS jobs
 */
public class MdsSchedulerException extends RuntimeException {

    public MdsSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }
}
