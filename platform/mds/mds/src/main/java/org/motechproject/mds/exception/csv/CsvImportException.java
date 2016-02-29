package org.motechproject.mds.exception.csv;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals that CSV import failed.
 */
public class CsvImportException extends MdsException {

    private static final long serialVersionUID = -982424055742014159L;

    public CsvImportException(String message) {
        super(message);
    }

    public CsvImportException(String message, Throwable cause) {
        super(message, cause);
    }
}
