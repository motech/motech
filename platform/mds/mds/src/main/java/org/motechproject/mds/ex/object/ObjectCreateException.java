package org.motechproject.mds.ex.object;

import org.motechproject.mds.ex.MdsException;

/**
 * Signals that it was not possible to update object instance from the provided data.
 */
public class ObjectCreateException extends MdsException {

    private static final long serialVersionUID = -6214111291407582493L;

    public ObjectCreateException(String entityName, Throwable cause) {
        super("Unable to create of entity " + entityName, cause, "mds.error.objectUpdateError");
    }
}
