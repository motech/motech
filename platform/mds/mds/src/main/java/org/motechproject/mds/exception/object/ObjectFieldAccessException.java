package org.motechproject.mds.exception.object;

import org.motechproject.commons.api.ThreadSuspender;
import org.motechproject.mds.exception.MdsException;

/**
 * Created by user on 29.08.16.
 */
public class ObjectFieldAccessException extends MdsException {
    public ObjectFieldAccessException(Throwable cause) {
        super("Unable to get access to field in object", cause, "mds.error.objectFieldAccessException");
    }
}
