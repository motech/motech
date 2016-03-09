package org.motechproject.mds.exception.object;

/**
 * Signals that it was not possible to update object instance from the provided data.
 */
public class ObjectCreateException extends ObjectException {

    private static final long serialVersionUID = -6214111291407582493L;

    public ObjectCreateException(String entityName, Throwable cause) {
        super("Unable to create of entity " + entityName + ". " + getMessageFromCause(cause), "mds.error.objectCreateError", cause);
    }
}
