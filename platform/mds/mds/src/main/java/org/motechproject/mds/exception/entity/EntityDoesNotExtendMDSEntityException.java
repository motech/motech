package org.motechproject.mds.exception.entity;

import org.motechproject.mds.exception.MdsException;

/**
 * Created by user on 25.08.16.
 */
public class EntityDoesNotExtendMDSEntityException extends MdsException{
    public EntityDoesNotExtendMDSEntityException(Class superClazz){
        super("Class " + superClazz.getName() + "is not an MDS Entity", null, "mds.error.entityDoesNotExtendMDSEntityException");
    }
}
