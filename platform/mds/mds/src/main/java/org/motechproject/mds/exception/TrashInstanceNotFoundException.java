package org.motechproject.mds.exception;

/**
 * Signals that a trash instance with the provided id was not found.
 */
public class TrashInstanceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -3954150263642033164L;

    public TrashInstanceNotFoundException(String entityClassName, Long trashId) {
        super(String.format("Trash instance for entity %s with id %d was not found", entityClassName, trashId));
    }
}
