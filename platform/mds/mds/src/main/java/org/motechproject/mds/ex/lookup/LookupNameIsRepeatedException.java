package org.motechproject.mds.ex.lookup;

import org.motechproject.mds.ex.MdsException;

/**
 * The <code>LookupNameIsRepeatedException</code> exception signals a situation, when there is more
 * than one lookup in the entity with the same name.
 */
public class LookupNameIsRepeatedException extends MdsException {
    private static final long serialVersionUID = -8789166346628385203L;

    public LookupNameIsRepeatedException() {
        super("mds.warning.lookupName.repeat");
    }
}
