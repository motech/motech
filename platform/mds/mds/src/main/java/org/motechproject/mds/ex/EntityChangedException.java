package org.motechproject.mds.ex;

/**
 * This exception signals that an Entity was changed(presumably by another user).
 */
public class EntityChangedException extends MdsException {

    private static final long serialVersionUID = -8535112651702892785L;

    public EntityChangedException() {
        super("mds.error.entityChanged");
    }
}
