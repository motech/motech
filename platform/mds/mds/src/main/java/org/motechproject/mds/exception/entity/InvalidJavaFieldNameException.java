package org.motechproject.mds.exception.entity;

import org.motechproject.mds.exception.MdsException;

/**
 * The <code>InvalidJavaFieldNameException</code> exception signals a situation in which user
 * tries to create the field with a name not being valid java identifier.
 */
public class InvalidJavaFieldNameException extends MdsException {

    private static final long serialVersionUID = 1587564973377262326L;

    public InvalidJavaFieldNameException(String fieldName) {
        super("Value " + fieldName + " cannot be used as java identifier", null, "mds.error.javaIdentifier", fieldName);
    }
}
