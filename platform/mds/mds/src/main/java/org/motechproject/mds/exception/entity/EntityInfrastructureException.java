package org.motechproject.mds.exception.entity;

import org.motechproject.mds.exception.MdsException;

/**
 * The <code>EntityInfrastructureException</code> exception signals a situation when there were
 * problems with creating repository/service interface/service class for entity.
 */
public class EntityInfrastructureException extends MdsException {

    private static final long serialVersionUID = 5863094045198892951L;

    /**
     * Constructs a new EntityInfrastructureException with
     * <i>mds.error.entityInfrastructureFailure</i> as a message key.
     *
     * @param className name of the class building which caused the issue
     * @param cause the cause of exception.
     */
    public EntityInfrastructureException(String className, Throwable cause) {
        super("Unable to build infrastructure class " + className, cause, "mds.error.entityInfrastructureFailure");
    }
}
