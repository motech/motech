package org.motechproject.mds.ex.entity;

/**
 * Thrown when there were some error during combobox data migration.
 */
public class DataMigrationFailedException extends RuntimeException {

    private static final long serialVersionUID = 8752686601917633293L;

    public DataMigrationFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}
