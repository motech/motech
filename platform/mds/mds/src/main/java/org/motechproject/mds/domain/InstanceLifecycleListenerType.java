package org.motechproject.mds.domain;

/**
 * The <code>InstanceLifecycleListenerType</code> enum represents persistence
 * event types.
 *
 * @see org.motechproject.mds.annotations.InstanceLifecycleListener
 */
public enum InstanceLifecycleListenerType {

    /**
     * Represents a point in time, right after an instance is made persistent.
     */
    POST_CREATE,

    /**
     * Represents a point in time, right before an instance is deleted.
     */
    PRE_DELETE,

    /**
     * Represents a point in time, right after an instance is deleted.
     */
    POST_DELETE,

    /**
     * Represents a point in time, right after loading instance from datastore.
     */
    POST_LOAD,

    /**
     * Represents a point in time, right before an instance is stored (eg. due to commit or flush)
     */
    PRE_STORE,

    /**
     * Represents a point in time, right after an instance is stored (eg. due to commit or flush)
     */
    POST_STORE
}
