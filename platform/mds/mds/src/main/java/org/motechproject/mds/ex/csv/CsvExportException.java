package org.motechproject.mds.ex.csv;

/**
 * Signals an error when exporting CSV to a stream.
 */
public class CsvExportException extends RuntimeException {

    private static final long serialVersionUID = 6501598651458021876L;

    public CsvExportException() {
    }

    public CsvExportException(String message) {
        super(message);
    }

    public CsvExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
