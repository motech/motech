package org.motechproject.mds.util;

/**
 * Represents a restriction on entity instances
 */
public class InstanceSecurityRestriction {

    private boolean byOwner;
    private boolean byCreator;

    /**
     * @return true, if only owners of an instance should be able to access it; false otherwise
     */
    public boolean isByOwner() {
        return byOwner;
    }

    public void setByOwner(boolean byOwner) {
        this.byOwner = byOwner;
    }

    /**
     * @return true, if only creators of an instance should be able to access it; false otherwise
     */
    public boolean isByCreator() {
        return byCreator;
    }

    public void setByCreator(boolean byCreator) {
        this.byCreator = byCreator;
    }

    /**
     * @return true, if access to the instance is not limited to creator or owner; false otherwise
     */
    public boolean isEmpty() {
        return !byCreator && !byOwner;
    }
}
