package org.motechproject.seuss.ui.ex;

/**
 * The <code>ObjectNotFoundException</code> exception signals a situation in which an object with
 * a given id does not exist in database.
 */
public class ObjectNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -4030249523587627059L;

    public ObjectNotFoundException() {
        super("Not found object with given id");
    }
}
