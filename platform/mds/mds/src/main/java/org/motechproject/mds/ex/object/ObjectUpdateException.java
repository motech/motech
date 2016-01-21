package org.motechproject.mds.ex.object;

import org.motechproject.mds.ex.MdsException;

import javax.jdo.JDODataStoreException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Set;

/**
 * Signals that it was not possible to update object instance from the provided data.
 */
public class ObjectUpdateException extends MdsException {

    private static final long serialVersionUID = -6214111291407582493L;

    public ObjectUpdateException(String entityName, Long id, Throwable cause) {
        super("Unable to update object of entity " + entityName + " with id " + id + ". " + getMessageFromCause(cause),
                cause, "mds.error.objectUpdateError");
    }

    private static String getMessageFromCause(Throwable cause) {
        String message = "";
        if (cause instanceof JDODataStoreException) {
            for (Throwable exception : ((JDODataStoreException) cause).getNestedExceptions()) {
                if (exception instanceof SQLIntegrityConstraintViolationException) {
                    message += exception.getMessage();
                }
            }
        } else if (cause instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) cause).getConstraintViolations();
            for (ConstraintViolation violation : violations) {
                message += String.format("Field %s %s\n", violation.getPropertyPath(), violation.getMessage());
            }
        }
        return message;
    }
}
