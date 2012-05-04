package org.motechproject.cmslite.api.model;


/**
 * \ingroup cmslite
 * Wrapper for any exception during adding or retrieving content.
 */
public class CMSLiteException extends Exception {

    public CMSLiteException(String message, Throwable e) {
        super(message, e);
    }
}
