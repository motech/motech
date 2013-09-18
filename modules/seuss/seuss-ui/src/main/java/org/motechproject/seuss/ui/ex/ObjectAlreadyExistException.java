package org.motechproject.seuss.ui.ex;

/**
 * The <code>ObjectAlreadyExistException</code> exception signals a situation in which a user wants
 * to create a new object with a name that already exist in database.
 */
public class ObjectAlreadyExistException extends RuntimeException {
    private static final long serialVersionUID = -4030249523587627059L;

    public ObjectAlreadyExistException() {
        super("Object with given name already exist");
    }
}
