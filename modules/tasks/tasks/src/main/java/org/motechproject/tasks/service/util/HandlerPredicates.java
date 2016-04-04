package org.motechproject.tasks.service.util;

import org.apache.commons.collections.Predicate;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;
import org.motechproject.tasks.domain.mds.task.Task;

/**
 * Utility class defining filters over some collections.
 */
public final class HandlerPredicates {

    /**
     * Utility class, should not be instantiated.
     */
    private HandlerPredicates() {
    }

    /**
     * Returns the predicate for fetching tasks with channels that are currently registered.
     *
     * @return  the predicate for fetching tasks
     */
    public static Predicate tasksWithRegisteredChannel() {
        return new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object instanceof Task && ((Task) object).hasRegisteredChannel();
            }
        };
    }

    /**
     * Returns the predicate for fetching {@code MotechListenerEventProxy} with the given name.
     *
     * @param serviceName  the name of the service
     * @return  the predicate for fetching {@code MotechListenerEventProxy}
     */
    public static Predicate withServiceName(final String serviceName) {
        return new Predicate() {
            @Override
            public boolean evaluate(Object object) {
                return object instanceof MotechListenerEventProxy && ((MotechListenerEventProxy) object).getIdentifier().equalsIgnoreCase(serviceName);
            }
        };
    }
}
