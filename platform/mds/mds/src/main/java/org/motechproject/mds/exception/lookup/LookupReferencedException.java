package org.motechproject.mds.exception.lookup;

import org.motechproject.mds.exception.MdsException;

/**
 * The <code>LookupReferencedException</code> exception signals a situation in which a lookup is
 * used somewhere
 */
public class LookupReferencedException extends MdsException {

    private static final long serialVersionUID = -2666225480961955432L;

    public LookupReferencedException(String entity, String lookups) {
        super("Lookups " + lookups + " not found " + entity, null, "mds.error.lookupReferenced", lookups);
    }
}
