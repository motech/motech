package org.motechproject.mds.exception.entity;

import org.motechproject.mds.exception.MdsException;

/**
 * The <code>EntityReadOnlyException</code> exception signals a situation in which a user wants
 * to make changes on an entity which is read only (it was created by a module).
 */
public class EntityReadOnlyException extends MdsException {

    private static final long serialVersionUID = -4030249523587627059L;

    /**
     * Constructs a new EntityReadOnlyException with <i>mds.error.entityIsReadOnly</i> as
     * a message key.
     * @param entityName name of the entity
     */
    public EntityReadOnlyException(String entityName) {
        super("Unable to edit entity " + entityName + " since it's readonly", null, "mds.error.entityIsReadOnly");
    }
}
