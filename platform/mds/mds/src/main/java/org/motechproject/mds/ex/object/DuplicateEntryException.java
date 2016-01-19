package org.motechproject.mds.ex.object;

import org.motechproject.mds.ex.MdsException;

import javax.jdo.JDODataStoreException;
import java.sql.SQLIntegrityConstraintViolationException;

/**
 * Signals that it was not possible to update object instance from the provided data.
 */
public class DuplicateEntryException extends MdsException {

    private static final long serialVersionUID = -6214111291407582493L;

    public DuplicateEntryException(String entityName, Throwable cause) {
        super("Can't insert entry: " + getCauseMessage(cause) + " in entity " + entityName + ". Thrown by: ", cause, "mds.error.objectUpdateError");
    }

    public static String getCauseMessage(Throwable cause) {
        String message = "";
        if (cause instanceof JDODataStoreException) {
            for (Throwable ex : ((JDODataStoreException) cause).getNestedExceptions()) {
                if (ex instanceof SQLIntegrityConstraintViolationException) {
                    message += ex.getMessage();
                    break;
                }
            }
        }
        return message;
    }
}
