package org.motechproject.mds.exception.object;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals that it was not possible to parse the object coming from the database.
 */
public class ObjectReadException extends MdsException {

    private static final long serialVersionUID = -6214111291407582493L;

    /**
     * @param entityName the name of the entity
     * @param cause the cause of the error
     */
    public ObjectReadException(String entityName, Throwable cause) {
        super("Unable to read objects of entity " + entityName, cause, "mds.error.objectReadError");
    }


    /**
     * @param entityId the id of the entity
     * @param cause the cause of the error
     */
    public ObjectReadException(Long entityId, Throwable cause) {
        super("Unable to read objects of entity with Id " + entityId, cause, "mds.error.objectReadError");
    }
}
