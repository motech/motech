package org.motechproject.mds.ex.csv;

/**
 * Signals an error when exporting tabular data.
 */
public class DataExportException extends RuntimeException {

    private static final long serialVersionUID = 6501598651458021876L;

    public DataExportException() {
    }

    public DataExportException(String message) {
        super(message);
    }

    public DataExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
