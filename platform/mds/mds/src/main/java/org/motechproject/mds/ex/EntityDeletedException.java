package org.motechproject.mds.ex;

/**
 * This exception signals that the Entity was deleted(presumably by an another user).
 */
public class EntityDeletedException extends MdsException {

    private static final long serialVersionUID = 3976054031966691815L;

    public EntityDeletedException() {
        super("mds.error.entityDeleted");
    }
}
