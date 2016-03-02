package org.motechproject.mds.exception.scheduler;

import org.motechproject.mds.exception.MdsException;

/**
 * The <code>MdsSchedulerException</code> exception signals problems
 * with scheduling MDS jobs
 */
public class MdsSchedulerException extends MdsException {

    public MdsSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }
}
