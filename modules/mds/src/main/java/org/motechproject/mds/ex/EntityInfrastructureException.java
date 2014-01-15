package org.motechproject.mds.ex;

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
     * @param cause the cause of exception.
     */
    public EntityInfrastructureException(Throwable cause) {
        super("mds.error.entityInfrastructureFailure", cause);
    }

}
