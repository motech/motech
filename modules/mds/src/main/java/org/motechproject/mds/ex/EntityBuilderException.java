package org.motechproject.mds.ex;

/**
 * The <code>EntityBuilderException</code> exception signals a situation when there were problems
 * with creating new entity class.
 */
public class EntityBuilderException extends MdsException {
    private static final long serialVersionUID = -3828736679522726438L;

    /**
     * Constructs a new EntityBuilderException with <i>mds.error.entityBuilderFailure</i> as
     * a message key.
     *
     * @param cause the cause of exception.
     */
    public EntityBuilderException(Throwable cause) {
        super("mds.error.entityBuilderFailure", cause);
    }
}
