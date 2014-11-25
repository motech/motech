package org.motechproject.mds.event;

/**
 * The <code>MDSEventsAction</code> enum represents CRUD operations which
 * send events, this option can be enabled only for entities.
 *
 * @see org.motechproject.mds.annotations.CrudEvents
 */
public enum CrudEventType {
    CREATE, UPDATE, DELETE, ALL
}
