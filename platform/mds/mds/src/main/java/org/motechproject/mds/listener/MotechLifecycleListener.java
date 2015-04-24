package org.motechproject.mds.listener;

import org.motechproject.mds.domain.InstanceLifecycleListenerType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
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
    private String parameterType;
    private String packageName;
    private List<String> entityNames;

    public MotechLifecycleListener(Class service, String methodName, String parameterType, String packageName,
                                   InstanceLifecycleListenerType[] types, List<String> entityNames) {
        this.service = service;
        this.parameterType = parameterType;
        this.packageName = packageName;
        for (InstanceLifecycleListenerType type : types) {
            Set<String> methods = new HashSet<>();
            methods.add(methodName);
            methodsByType.put(type, methods);
        }
        this.entityNames = entityNames;
    }

    public Class getService() {
        return service;
    }

    public Map<InstanceLifecycleListenerType, Set<String>> getMethodsByType() {
        return methodsByType;
    }

    public String getParameterType() {
        return parameterType;
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

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public List<String> getEntityNames() {
        return entityNames;
    }

    public void setEntityNames(List<String> entityNames) {
        this.entityNames = entityNames;
    }

    @Override
    public int hashCode() {
        return Objects.hash(parameterType, service, packageName);
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

        return Objects.equals(this.parameterType, other.parameterType) &&
               Objects.equals(this.service, other.service) &&
                Objects.equals(this.packageName, other.packageName);
    }
}
