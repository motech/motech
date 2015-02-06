package org.motechproject.mds.ex.rest;

/**
 * The <code>RestEntityNotFoundException</code> exception signals a situation in which an entity with
 * a given id does not exist in database.
 */
public class RestEntityNotFoundException extends RuntimeException{

    private static final long serialVersionUID = -8268264268131609330L;

    public RestEntityNotFoundException(String field, String value) {
        super(String.format("Could not retrieve entity for  %s == %s", field, value));
    }
}
