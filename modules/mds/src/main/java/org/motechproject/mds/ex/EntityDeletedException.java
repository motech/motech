package org.motechproject.mds.ex;

public class EntityDeletedException extends MdsException {

    private static final long serialVersionUID = 3976054031966691815L;

    public EntityDeletedException() {
        super("mds.error.entityDeleted");
    }
}
