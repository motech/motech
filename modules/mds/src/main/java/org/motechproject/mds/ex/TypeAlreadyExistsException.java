package org.motechproject.mds.ex;

/**
 * The <code>TypeAlreadyExistsException</code> is thrown, if the user attempts to add a field type,
 * with a display name that already exists in the database
 */
public class TypeAlreadyExistsException extends MdsException {

    public TypeAlreadyExistsException() {
        super("mds.error.typeAlreadyExists");
    }

}
