package org.motechproject.mds.exception;

/**
 * The <code>EntityAlreadyExistException</code> exception signals a situation in which a user wants
 * to create a new entity with a name that already exist in database.
 */
public class EntityAlreadyExistException extends MDSValidationException {
    private static final long serialVersionUID = -4030249523587627059L;

    public EntityAlreadyExistException(String message) {
        super(message);
    }
}
