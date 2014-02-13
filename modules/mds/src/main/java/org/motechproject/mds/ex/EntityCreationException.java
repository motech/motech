package org.motechproject.mds.ex;

/**
 * The <code>EntityCreationException</code> exception signals a situation when there were problems
 * with creating new entity class.
 */
public class EntityCreationException extends MdsException {
    private static final long serialVersionUID = -3828736679522726438L;

    /**
     * Constructs a new EntityCreationException with <i>mds.error.entityBuilderFailure</i> as
     * a message key.
     *
     * @param cause the cause of exception.
     */
    public EntityCreationException(Throwable cause) {
        super("mds.error.entityBuilderFailure", cause);
    }

    public EntityCreationException(String messageKey) {
        super(messageKey);
    }
}
