package org.motechproject.mds.ex;

/**
 * The <code>LookupNameIsRepeatedException</code> exception signals a situation when are more
 * than one lookups in the entity with the same name.
 */
public class LookupNameIsRepeatedException extends MdsException{
    private static final long serialVersionUID = -8789166346628385203L;

    public LookupNameIsRepeatedException() {
        super("mds.warning.lookupName.repeat");
    }
}
