package org.motechproject.mds.listener;

import org.motechproject.mds.domain.InstanceLifecycleListenerType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Contains essential information about InstanceLifecycleListeners, which are provided by
 * {@link org.motechproject.mds.annotations.InstanceLifecycleListener} annotation.
 *
 * @see org.motechproject.mds.annotations.InstanceLifecycleListener
 */
public class MotechLifecycleListener {
    private Class service;
    private Map<InstanceLifecycleListenerType, Set<String>> methodsByType = new HashMap<>();
    private String entity;

    public MotechLifecycleListener(Class service, String methodName, String entity, InstanceLifecycleListenerType[] types) {
        this.service = service;
        this.entity = entity;
        for (InstanceLifecycleListenerType type : types) {
            Set<String> methods = new HashSet<>();
            methods.add(methodName);
            methodsByType.put(type, methods);
        }
    }

    public Class getService() {
        return service;
    }

    public Map<InstanceLifecycleListenerType, Set<String>> getMethodsByType() {
        return methodsByType;
    }

    public String getEntity() {
        return entity;
    }

    public void addMethod(Map<InstanceLifecycleListenerType, Set<String>> method) {
        for (InstanceLifecycleListenerType type : method.keySet()) {
            if (methodsByType.containsKey(type)) {
                methodsByType.get(type).addAll(method.get(type));
            } else {
                methodsByType.put(type, method.get(type));
            }
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(entity, service);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        MotechLifecycleListener other = (MotechLifecycleListener) obj;

        return Objects.equals(this.entity, other.entity) &&
               Objects.equals(this.service, other.service);
    }
}
