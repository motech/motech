package org.motechproject.mds.ex.importexport;

/**
 * The <code>ImportExportException</code> indicates that a problem occurred during importing or exporting
 * MDS schema or data.
 */
public class ImportExportException extends RuntimeException {

    private static final long serialVersionUID = 3879013141884467764L;

    public ImportExportException(String message) {
        super(message);
    }

    public ImportExportException(String message, Throwable cause) {
        super(message, cause);
    }
}
