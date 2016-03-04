package org.motechproject.mds.exception.entity;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals that field/lookup name is invalid because it is a java keyword.
 */
public class ReservedKeywordException extends MdsException {

    private static final long serialVersionUID = -4840188009298951328L;

    public ReservedKeywordException(String keyword) {
        super("Unable to use the reserved keyword " + keyword, null, "mds.error.javaKeyword", keyword);
    }
}
