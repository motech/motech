package org.motechproject.mds.exception.field;

import org.motechproject.mds.exception.MdsException;

/**
 * The <code>FieldReadOnlyException</code> exception signals an attempt to edit read only field.
 */
public class FieldReadOnlyException extends MdsException {

    private static final long serialVersionUID = 8964737006637613242L;

    /**
     * @param entityName name of the entity
     * @param fieldName name of the readonly field
     */
    public FieldReadOnlyException(String entityName, String fieldName) {
        super("Field " + fieldName + " is readonly in entity " + entityName);
    }
}
