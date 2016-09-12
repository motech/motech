package org.motechproject.mds.exception.entity;

import org.motechproject.mds.exception.MdsException;

/**
 * Exception thrown when Class annotated by @EntityExtension does not extends MDS Entity class
 */
public class EntityDoesNotExtendMDSEntityException extends MdsException {
    public EntityDoesNotExtendMDSEntityException(Class superClazz){
        super("Class " + superClazz.getName() + "is not an MDS Entity", null, "mds.error.entityDoesNotExtendMDSEntityException");
    }
}
