package org.motechproject.mds.exception.field;

import org.motechproject.mds.exception.MdsException;

/**
 * Exception indicating that a field cannot be removed, since it is used in a lookup.
 */
public class FieldUsedInLookupException extends MdsException {

    private static final long serialVersionUID = -3950357333708462369L;

    /**
     * @param fieldName the name of the field
     * @param lookupNames names of the lookups the field is used
     */
    public FieldUsedInLookupException(String fieldName, String lookupNames) {
        super("Field " + fieldName + " is used in lookups: " + lookupNames, null, "mds.error.fieldUsedInLookup",
                fieldName, lookupNames);
    }
}
