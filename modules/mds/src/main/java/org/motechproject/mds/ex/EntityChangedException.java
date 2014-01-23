package org.motechproject.mds.ex;


public class EntityChangedException extends MdsException {

    private static final long serialVersionUID = -8535112651702892785L;

    public EntityChangedException() {
        super("mds.error.entityChanged");
    }
}
