package org.motechproject.tasks.ex;

/**
 * Thrown when the requested action doesn't exists.
 */
public class ActionNotFoundException extends Exception {

    public ActionNotFoundException(String message) {
        super(message);
    }

}
