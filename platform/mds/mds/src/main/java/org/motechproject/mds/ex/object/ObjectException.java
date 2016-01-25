package org.motechproject.mds.ex.object;

import org.motechproject.mds.ex.MdsException;

import javax.jdo.JDODataStoreException;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Set;

/**
 * Created by mateusz on 25.01.16.
 */
public abstract class ObjectException extends MdsException {

    public ObjectException(String message, String keyError, Throwable cause) {
        super(message, cause, keyError, getCauseMessage(cause));
    }

    protected static String getMessageFromCause(Throwable cause) {
        String message = "";
        if (cause instanceof ConstraintViolationException) {
            Set<ConstraintViolation<?>> violations = ((ConstraintViolationException) cause).getConstraintViolations();
            for (ConstraintViolation violation : violations) {
                message += String.format("Field %s %s\n", violation.getPropertyPath(), violation.getMessage());
            }
        }
        return message;
    }

    protected static String getCauseMessage(Throwable cause) {
        String message = "";
        if (cause instanceof JDODataStoreException) {
            for (Throwable exception : ((JDODataStoreException) cause).getNestedExceptions()) {
                if (exception instanceof SQLIntegrityConstraintViolationException) {
                    message += exception.getMessage();
                }
            }
        }
        return message;
    }
}
