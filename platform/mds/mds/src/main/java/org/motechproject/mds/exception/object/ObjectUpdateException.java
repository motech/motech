package org.motechproject.mds.exception.object;

/**
 * Signals that it was not possible to update object instance from the provided data.
 */
public class ObjectUpdateException extends ObjectException {

    private static final long serialVersionUID = -6214111291407582493L;

    public ObjectUpdateException(String entityName, Long id, Throwable cause) {
        super("Unable to update object of entity " + entityName + " with id " + id + ". " + getMessageFromCause(cause), "mds.error.objectUpdateError", cause);
    }
}
