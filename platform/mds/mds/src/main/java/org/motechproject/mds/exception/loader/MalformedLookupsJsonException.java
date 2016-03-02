package org.motechproject.mds.exception.loader;

import org.motechproject.mds.exception.MdsException;

/**
 * Thrown when the "mds-lookups.json" file is malformed.
 */
public class MalformedLookupsJsonException extends MdsException {

    private static final String MESSAGE = "File \"mds-lookups.json\" from bundle \"%s\" is malformed.";

    public MalformedLookupsJsonException(String bundleSymbolicName, Throwable cause) {
        super(String.format(MESSAGE, bundleSymbolicName), cause);
    }
}
