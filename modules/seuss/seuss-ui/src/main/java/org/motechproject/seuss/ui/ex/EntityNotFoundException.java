package org.motechproject.seuss.ui.ex;

/**
 * The <code>EntityNotFoundException</code> exception signals a situation in which an entity with
 * a given id does not exist in database.
 */
public class EntityNotFoundException extends RuntimeException {
    private static final long serialVersionUID = -4030249523587627059L;

    public EntityNotFoundException() {
        super("Not found entity with given id");
    }
}
