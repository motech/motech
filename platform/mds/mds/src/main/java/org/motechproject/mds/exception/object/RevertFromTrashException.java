package org.motechproject.mds.exception.object;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals an error when reverting an instance from trash.
 */
public class RevertFromTrashException extends MdsException {

    private static final long serialVersionUID = -6552911189906472938L;

    public RevertFromTrashException(String entityName, Long instanceId, Throwable cause) {
        super("Unable to revert instance of entity " + entityName + ", with id " + instanceId + " from trash", cause);
    }
}
