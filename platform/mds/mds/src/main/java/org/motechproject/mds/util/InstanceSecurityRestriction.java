package org.motechproject.mds.util;

/**
 * Represents a restriction on entity instances
 */
public class InstanceSecurityRestriction {

    private boolean byOwner;
    private boolean byCreator;

    public boolean isByOwner() {
        return byOwner;
    }

    public void setByOwner(boolean byOwner) {
        this.byOwner = byOwner;
    }

    public boolean isByCreator() {
        return byCreator;
    }

    public void setByCreator(boolean byCreator) {
        this.byCreator = byCreator;
    }

    public boolean isEmpty() {
        return !byCreator && !byOwner;
    }
}
