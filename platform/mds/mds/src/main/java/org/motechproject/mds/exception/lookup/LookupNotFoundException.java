package org.motechproject.mds.exception.lookup;

import org.motechproject.mds.exception.MdsException;

/**
 * The <code>LookupNotFoundException</code> exception signals a situation in which a lookup with
 * given id does not exist in database.
 */
public class LookupNotFoundException extends MdsException {
    private static final long serialVersionUID = -7984274188275593330L;

    /**
     * Constructs a new LookupNotFoundException with <i>mds.error.lookupNotFound</i> as
     * a message key.
     * @param entityName the name of the entity
     * @param lookupName the name of the lookup
     */
    public LookupNotFoundException(String entityName, String lookupName) {
        super("Lookup " + lookupName + " not found in entity" + entityName, null, "mds.error.lookupNotFound");
    }

    /**
     * Constructs a new LookupNotFoundException with <i>mds.error.lookupNotFound</i> as
     * a message key.
     * @param entityId the id of the entity
     * @param lookupName the name of the lookup
     */
    public LookupNotFoundException(Long entityId, String lookupName) {
        super("Lookup " + lookupName + " not found in entity with ID" + entityId, null, "mds.error.lookupNotFound");
    }
}
