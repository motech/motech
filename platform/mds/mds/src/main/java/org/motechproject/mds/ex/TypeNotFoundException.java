package org.motechproject.mds.ex;

/**
 * The <code>TypeNotFoundException</code> exception signals a situation in which a type with
 * given name does not exist in database.
 */
public class TypeNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 6089832418588401708L;

    public TypeNotFoundException(String msg) {
        super(msg);
    }
}
