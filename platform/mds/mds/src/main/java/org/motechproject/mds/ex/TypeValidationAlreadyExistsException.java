package org.motechproject.mds.ex;

/**
 * The <code>TypeValidationAlreadyExistsException</code> is thrown, if the user attempts to add a type validation
 * when there is already validation for given type
 */
public class TypeValidationAlreadyExistsException extends MdsException {

    public TypeValidationAlreadyExistsException() {
        super("mds.error.typeValidationAlreadyExists");
    }
}
