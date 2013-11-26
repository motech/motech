package org.motechproject.mds.ex;

/**
 * The <code>FieldNotFoundException</code> exception signals a situation in which the given field
 * does not exist in an entity.
 */
public class FieldNotFoundException extends MdsException {
    private static final long serialVersionUID = 1528655875763460954L;

    /**
     * Constructs a new FieldNotFoundException with <i>mds.error.fieldNotFound</i> as
     * a message key.
     */
    public FieldNotFoundException() {
        super("mds.error.fieldNotFound");
    }
}
