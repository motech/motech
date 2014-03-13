package org.motechproject.mds.ex;

/**
 * The <code>EntityReadOnlyException</code> exception signals a situation in which a user wants
 * to make changes on an entity which is read only (it was created by a module).
 */
public class EntityReadOnlyException extends MdsException {
    private static final long serialVersionUID = -4030249523587627059L;

    /**
     * Constructs a new EntityReadOnlyException with <i>mds.error.entityIsReadOnly</i> as
     * a message key.
     */
    public EntityReadOnlyException() {
        super("mds.error.entityIsReadOnly");
    }
}
