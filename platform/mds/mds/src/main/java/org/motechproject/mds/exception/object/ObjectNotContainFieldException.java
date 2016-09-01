package org.motechproject.mds.exception.object;

import org.motechproject.mds.exception.MdsException;

/**
 * Created by user on 29.08.16.
 */
public class ObjectNotContainFieldException extends MdsException {
    public ObjectNotContainFieldException(Throwable cause) {
        super("Unable to find field in object", cause, "mds.error.objectNotContainField");
    }
}
