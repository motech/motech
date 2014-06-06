package org.motechproject.commons.couchdb.service;

/**
 * @deprecated As of release 0.24, MDS replaces CouchDB for persistence
 */
@Deprecated
public class DbConnectionException extends RuntimeException {

    public DbConnectionException(String message) {
        super(message);
    }

    public DbConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
