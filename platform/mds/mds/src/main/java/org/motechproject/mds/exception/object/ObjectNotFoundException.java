package org.motechproject.mds.exception.object;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals that the expected object was not found in the database.
 */
public class ObjectNotFoundException extends MdsException {

    private static final long serialVersionUID = 478668621463294074L;

    /**
     * @param entityName name of the entity
     * @param id id of the object we were unable to retrieve
     */
    public ObjectNotFoundException(String entityName, Long id) {
        super("Object for entity " + entityName + " with id " + id + " not found",
                null, "mds.error.objectNotFound");
    }
}
