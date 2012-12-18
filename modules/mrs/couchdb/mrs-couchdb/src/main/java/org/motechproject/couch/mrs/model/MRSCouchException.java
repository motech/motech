package org.motechproject.couch.mrs.model;

/**
 * Wrapper for any exception during adding or retrieving MRS entity.
 */
public class MRSCouchException extends Exception {

    public MRSCouchException(String message, Throwable e) {
        super(message, e);
    }

}
