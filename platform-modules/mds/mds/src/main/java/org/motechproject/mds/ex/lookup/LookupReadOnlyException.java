package org.motechproject.mds.ex.lookup;

import org.motechproject.mds.ex.MdsException;

/**
 * The <code>LookupReadOnlyException</code> exception signals an attempt to edit read only lookup.
 */
public class LookupReadOnlyException extends MdsException {

    private static final long serialVersionUID = 1447788494810601583L;

    public LookupReadOnlyException(String message) {
        super(message);
    }
}
