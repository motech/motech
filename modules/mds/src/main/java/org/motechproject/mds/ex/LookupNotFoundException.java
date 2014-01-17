package org.motechproject.mds.ex;

/**
 * The <code>LookupNotFoundException</code> exception signals a situation in which a lookup with
 * given id does not exist in database.
 */
public class LookupNotFoundException extends MdsException {
    private static final long serialVersionUID = -7984274188275593330L;

    /**
     * Constructs a new LookupNotFoundException with <i>mds.error.lookupNotFound</i> as
     * a message key.
     */
    public LookupNotFoundException() {
        super("mds.error.lookupNotFound");
    }
}
