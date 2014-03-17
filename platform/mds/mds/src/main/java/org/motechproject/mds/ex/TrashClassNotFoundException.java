package org.motechproject.mds.ex;

/**
 * An Exception thrown when MDS fails to load trash class for an entity
 */
public class TrashClassNotFoundException extends MdsException {

    private static final long serialVersionUID = -807516858165841272L;

    public TrashClassNotFoundException(String className) {
        super("mds.error.trashClassNotFound", className);
    }
}
