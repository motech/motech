package org.motechproject.mds.ex.scheduler;

import org.motechproject.mds.ex.MdsException;

/**
 * The <code>MdsSchedulerException</code> exception signals problems
 * with scheduling MDS jobs
 */
public class MdsSchedulerException extends MdsException {

    public MdsSchedulerException(String message, Throwable cause) {
        super(message, cause);
    }
}
