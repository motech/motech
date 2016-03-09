package org.motechproject.mds.exception.csv;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals an error when exporting tabular data.
 */
public class DataExportException extends MdsException {

    private static final long serialVersionUID = 6501598651458021876L;

    public DataExportException(String message) {
        super(message);
    }

    public DataExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
