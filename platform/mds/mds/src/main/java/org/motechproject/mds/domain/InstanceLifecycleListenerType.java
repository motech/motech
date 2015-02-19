package org.motechproject.mds.domain;

/**
 * The <code>InstanceLifecycleListenerType</code> enum represents persistence
 * event types.
 *
 * @see org.motechproject.mds.annotations.InstanceLifecycleListener
 */
public enum InstanceLifecycleListenerType {
    POST_CREATE, PRE_DELETE, POST_DELETE, POST_LOAD, PRE_STORE, POST_STORE
}
