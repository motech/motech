package org.motechproject.mds.ex;

/**
 * The <code>EntitySchemaMismatch</code> exception signals a situation in which a user wants
 * to revert their instance to a version on a different schema version.
 */
public class EntitySchemaMismatchException extends MdsException {
    private static final long serialVersionUID = 6141631600950051360L;

    /**
     * Constructs a new EntitySchemaMismatch with <i>mds.error.entitySchemaMismatch</i> as
     * a message key.
     */
    public EntitySchemaMismatchException() {
        super("mds.error.entitySchemaMismatch");
    }
}
