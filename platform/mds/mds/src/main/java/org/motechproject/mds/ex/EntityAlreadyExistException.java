package org.motechproject.mds.ex;

/**
 * The <code>EntityAlreadyExistException</code> exception signals a situation in which a user wants
 * to create a new entity with a name that already exist in database.
 */
public class EntityAlreadyExistException extends MdsException {
    private static final long serialVersionUID = -4030249523587627059L;

    /**
     * Constructs a new EntityAlreadyExistException with <i>mds.error.entityAlreadyExist</i> as
     * a message key.
     */
    public EntityAlreadyExistException() {
        super("mds.error.entityAlreadyExist");
    }
}
