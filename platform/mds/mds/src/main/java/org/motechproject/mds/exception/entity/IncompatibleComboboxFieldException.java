package org.motechproject.mds.exception.entity;

import org.motechproject.mds.exception.MdsException;

/**
 * Throw when one of the combobox fields is not compatible (some instances are using multiple values for that field)
 * with single-select.
 */
public class IncompatibleComboboxFieldException extends MdsException {

    /**
     * Constructs the exception with <i>mds.error.comboboxIncompatible</i> as the message key.
     * @param entityName name of the entity
     * @param fieldName name of the field that caused the issue
     */
    public IncompatibleComboboxFieldException(String entityName, String fieldName) {
        super("Not compatible combobbox field " + fieldName + " in entity " + entityName, null,
                "mds.error.comboboxIncompatible", fieldName);
    }
}
