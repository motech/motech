package org.motechproject.mds.exception.entity;

import org.motechproject.mds.exception.MdsException;

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
     * @param message the message for the logs
     */
    public EntityCreationException(String message) {
        super(message, null, "mds.error.entityBuilderFailure");
    }

    /**
     * Constructs a new EntityCreationException with <i>mds.error.entityBuilderFailure</i> as
     * a message key.
     *
     * @param message the message for the logs
     * @param cause the cause of exception.
     */
    public EntityCreationException(String message, Throwable cause) {
        super(message, cause, "mds.error.entityBuilderFailure");
    }
}
