package org.motechproject.tasks.exception;

/**
 * Thrown when the requested action doesn't exists.
 */
public class ActionNotFoundException extends Exception {

    public ActionNotFoundException(String message) {
        super(message);
    }

}
