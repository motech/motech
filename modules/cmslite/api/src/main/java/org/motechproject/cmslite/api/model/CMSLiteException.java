package org.motechproject.cmslite.api.model;


/**
 * \ingroup cmslite
 * Wrapper for any exception during adding or retrieving content.
 */
public class CMSLiteException extends Exception {
    private static final long serialVersionUID = 658022791019499914L;

    public CMSLiteException(String message) {
        super(message);
    }

    public CMSLiteException(String message, Throwable e) {
        super(message, e);
    }
}
