package org.motechproject.mds.ex.csv;

/**
 * Signals that CSV import failed.
 */
public class CsvImportException extends RuntimeException {

    private static final long serialVersionUID = -982424055742014159L;

    public CsvImportException() {
    }

    public CsvImportException(String message) {
        super(message);
    }

    public CsvImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
