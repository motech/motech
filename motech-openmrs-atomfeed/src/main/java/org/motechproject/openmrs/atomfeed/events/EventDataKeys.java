package org.motechproject.openmrs.atomfeed.events;

public final class EventDataKeys {

    private EventDataKeys() {
    }

    /**
     * The UUID of the entity that was changed
     */
    public static final String UUID = "UUID";

    /**
     * The user who changed the entity
     */
    public static final String AUTHOR = "AUTHOR";

    /**
     * The action that was taken on the entity: create, update, voided, or
     * deleted
     */
    public static final String ACTION = "ACTION";

    /**
     * The URL to access the entity through the OpenMRS REST Web Services module
     */
    public static final String LINK = "LINK";

    /**
     * The time the change took place
     */
    public static final String UPDATED = "UPDATED";
}
