package org.motechproject.mds.exception.object;

import org.motechproject.mds.exception.MdsException;

import javax.jdo.JDODataStoreException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Set;

/**
 * Signals that it was not possible to insert object data into database.
 */
public abstract class ObjectException extends MdsException {

    protected ObjectException(String message, String keyError, Throwable cause) {
        super(message, cause, keyError, getMessageFromCause(cause));
    }

    protected static String getMessageFromCause(Throwable cause) {
        String message = "Persistence error";
        if (cause instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) cause).getConstraintViolations();
            message = "";
            for (ConstraintViolation violation : violations) {
                message += (String.format("Field %s %s\n", violation.getPropertyPath(), violation.getMessage()));
            }
        } else if (cause instanceof JDODataStoreException) {
            for (Throwable exception : ((JDODataStoreException) cause).getNestedExceptions()) {
                if (exception instanceof SQLIntegrityConstraintViolationException) {
                    message = exception.getMessage();
                    break;
                }
            }
        }
        return message;
    }
}
