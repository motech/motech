package org.motechproject.server.startup.db;

public class DbConnectionException extends Exception {

    public DbConnectionException(String message) {
        super(message);
    }

    public DbConnectionException(String message, Throwable cause) {
        super(message, cause);
    }
}
