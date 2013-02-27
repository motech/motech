package org.motechproject.tasks.service;

import org.apache.commons.collections.Predicate;
import org.motechproject.event.listener.annotations.MotechListenerEventProxy;

class HandlerPredicate implements Predicate {
    private String serviceName;

    public HandlerPredicate(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public boolean evaluate(Object object) {
        return object instanceof MotechListenerEventProxy && ((MotechListenerEventProxy) object).getIdentifier().equalsIgnoreCase(serviceName);
    }
}
