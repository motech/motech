package org.motechproject.mds.exception.entity;

import org.motechproject.mds.exception.MdsException;

/**
 * This exception occurs when class annotated by @ExtensionEntity does not extend other class
 */
public class EntityDoesNotExtendEntityException extends MdsException {
    public EntityDoesNotExtendEntityException(Class clazz){
        super("Class " + clazz.getName() + " does not extend any class", null, "mds.error.entityDoesNotExtendEntity");
    }
}
