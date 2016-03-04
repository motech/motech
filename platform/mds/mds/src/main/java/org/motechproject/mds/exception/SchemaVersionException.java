package org.motechproject.mds.exception;

/**
 * Signals that a record has an outdated schema version.
 */
public class SchemaVersionException extends RuntimeException {

    private static final long serialVersionUID = 3514787944828315824L;

    public SchemaVersionException(Long expectedVersion, Long actualVersion, Long recordId, String recordClassName) {
        super(String.format("Record %s with id %d  has wrong schema version. Schema version - %d, required - %d",
                recordClassName, recordId, actualVersion, expectedVersion));
    }
}
