package org.motechproject.mds.exception.entity;

import org.motechproject.mds.exception.MdsException;

/**
 * Exception occurs when ExtensionEntity try to add field that already exist
 */
public class FieldExistInExtendedEntityException extends MdsException {
    public FieldExistInExtendedEntityException(String fieldName, Class clazz, Class superClazz) {
        super("Field " + fieldName + " from " + clazz.getName() + " already exist in " + superClazz.getName(), null, "mds.error.fieldExistInExtendedEntity");
    }
}
