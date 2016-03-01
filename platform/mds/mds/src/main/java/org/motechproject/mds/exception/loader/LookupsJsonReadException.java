package org.motechproject.mds.exception.loader;

import org.motechproject.mds.exception.MdsException;

/**
 * Thrown when there were problems while loading editable lookups from "mds-lookups.json" file.
 */
public class LookupsJsonReadException extends MdsException {

    private static final String MESSAGE = "Couldn't load lookups from \"mds-lookups.json\" file from \"%s\" bundle";

    public LookupsJsonReadException(String bundleSymbolicName, Throwable cause) {
        super(String.format(MESSAGE, bundleSymbolicName), cause);
    }
}
