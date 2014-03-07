package org.motechproject.mds.ex;

/**
 * Signals that field/lookup name is invalid because it is a java keyword.
 */
public class ReservedKeywordException extends MdsException {

    private static final long serialVersionUID = -4840188009298951328L;

    public ReservedKeywordException(String keyword) {
        super("mds.error.javaKeyword", keyword);
    }
}
