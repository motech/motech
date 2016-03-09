package org.motechproject.mds.exception;

/**
 * Signals that a trash instance with the provided id was not found.
 */
public class HistoryInstanceNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -6154282802410998304L;

    public HistoryInstanceNotFoundException(String entityClassName, Long instanceId, Long trashId) {
        super(String.format("History instance for entity - %s, id - %d, with historical id %d was not found",
                entityClassName, instanceId, trashId));
    }
}
