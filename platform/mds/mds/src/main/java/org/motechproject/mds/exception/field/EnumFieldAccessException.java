package org.motechproject.mds.exception.field;

import org.motechproject.mds.exception.MdsException;

/**
 * Signals that getting value from an enum field failed.
 */
public class EnumFieldAccessException extends MdsException {

    private static final long serialVersionUID = 9196860125180733774L;

    public EnumFieldAccessException(Object obj, String field, Throwable cause) {
        super("Unable to get value from field \"" + field + "\" in enum \""+ obj.toString() +"\". The field must " +
                "either have a getter or public access modifier.", cause);
    }

    public EnumFieldAccessException(Object obj, String field) {
        this(obj, field, null);
    }
}
