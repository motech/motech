package org.motechproject.server.config.db;

public class DbConnectionException extends Exception {

    public DbConnectionException(String message) {
        super(message);
    }

    public DbConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
