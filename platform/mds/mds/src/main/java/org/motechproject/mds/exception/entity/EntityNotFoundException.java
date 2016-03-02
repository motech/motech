package org.motechproject.mds.exception.entity;

import org.motechproject.mds.exception.MdsException;

/**
 * The <code>EntityNotFoundException</code> exception signals a situation in which an entity with
 * a given id does not exist in database.
 */
public class EntityNotFoundException extends MdsException {

    private static final long serialVersionUID = -4030249523587627059L;

    /**
     * Constructs a new EntityNotFoundException with <i>mds.error.entityNotFound</i> as
     * a message key.
     * @param entityName the name of entity not found
     */
    public EntityNotFoundException(String entityName) {
        super(entityName + " not found", null, "mds.error.entityNotFound");
    }

    /**
     * Constructs a new EntityNotFoundException with <i>mds.error.entityNotFound</i> as
     * a message key.
     * @param id the id of entity not found
     */
    public EntityNotFoundException(Long id) {
        super("Entity not found with id " + id, null, "mds.error.entityNotFound");
    }
}
