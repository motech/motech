package org.motechproject.commons.couchdb.service;

public class DbConnectionException extends RuntimeException {

    public DbConnectionException(String message) {
        super(message);
    }

    public DbConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
