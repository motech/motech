package org.motechproject.mds.event;

/**
 * The <code>MDSEventsAction</code> enum represents CRUD operations which
 * send events, this option can be enabled only for entities.
 *
 * @see org.motechproject.mds.annotations.CrudEvents
 */
public enum CrudEventType {

    /**
     * One of the CRUD event types, representing creating an instance.
     */
    CREATE,

    /**
     * One of the CRUD event types, representing updating an instance.
     */
    UPDATE,

    /**
     * One of the CRUD event types, representing deleting an instance.
     */
    DELETE,

    /**
     * Represents all CRUD event types.
     */
    ALL,

    /**
     * Represents zero CRUD event types.
     */
    NONE
}
