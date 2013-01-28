package org.motechproject.couch.mrs.model;

import org.motechproject.mrs.exception.MRSException;

/**
 * Wrapper for any exception during adding or retrieving MRS entity.
 */
public class MRSCouchException extends MRSException {

    public MRSCouchException(String message, Throwable e) {
        super(message, e);
    }

}
