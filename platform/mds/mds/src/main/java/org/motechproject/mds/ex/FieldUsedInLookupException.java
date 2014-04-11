package org.motechproject.mds.ex;

/**
 * Exception indicating that a field cannot be removed, since it is used in a lookup.
 */
public class FieldUsedInLookupException extends MdsException {

    private static final long serialVersionUID = -3950357333708462369L;

    public FieldUsedInLookupException(String fieldName, String lookupNames) {
        super("mds.error.fieldUsedInLookup", fieldName, lookupNames);
    }
}
