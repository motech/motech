package org.motechproject.mds.exception.field;

import org.motechproject.mds.exception.MdsException;

/**
 * This exception signals that a given field was not found for the Entity.
 */
public class FieldNotFoundException extends MdsException {

    private static final long serialVersionUID = 4823302726665732484L;

    /**
     * @param entityClassName class name of the entity
     * @param fieldName name of the field
     */
    public FieldNotFoundException(String entityClassName, String fieldName) {
        super("Field " + fieldName + " not found in " + entityClassName, null, "mds.error.fieldNotFound");
    }

    /**
     * @param entityClassName class name of the entity
     * @param fieldId the id of the field
     */
    public FieldNotFoundException(String entityClassName, Long fieldId) {
        super("Field with ID " + fieldId + " not found in " + entityClassName, null, "mds.error.fieldNotFound");
    }
}
