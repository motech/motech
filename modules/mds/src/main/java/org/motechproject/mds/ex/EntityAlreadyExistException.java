package org.motechproject.mds.ex;

/**
 * The <code>EntityAlreadyExistException</code> exception signals a situation in which a user wants
 * to create a new entity with a name that already exist in database.
 */
public class EntityAlreadyExistException extends RuntimeException {
    private static final long serialVersionUID = -4030249523587627059L;

    public EntityAlreadyExistException() {
        super("Entity with given name already exist");
    }
}
