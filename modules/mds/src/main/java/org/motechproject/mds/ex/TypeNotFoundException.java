package org.motechproject.mds.ex;

/**
 * The <code>TypeNotFoundException</code> exception signals a situation in which a type with
 * given name does not exist in database.
 */
public class TypeNotFoundException extends MdsException {
    private static final long serialVersionUID = 6089832418588401708L;

    /**
     * Constructs a new TypeNotFoundException with <i>mds.error.typeNotFound</i> as
     * a message key.
     */
    public TypeNotFoundException() {
        super("mds.error.typeNotFound");
    }
}
