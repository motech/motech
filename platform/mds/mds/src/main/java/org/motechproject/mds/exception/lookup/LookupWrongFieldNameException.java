package org.motechproject.mds.exception.lookup;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals wrong field name in Lookup
 */
public class LookupWrongFieldNameException extends MdsException {

    public LookupWrongFieldNameException(String message) {
        super(message);
    }
}
