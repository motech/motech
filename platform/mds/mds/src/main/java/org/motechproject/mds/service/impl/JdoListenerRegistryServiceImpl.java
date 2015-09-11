package org.motechproject.mds.service.impl;

import org.motechproject.mds.annotations.InstanceLifecycleListenerType;
import org.motechproject.mds.annotations.internal.AnnotationProcessingContext;
import org.motechproject.mds.domain.Entity;
import org.motechproject.mds.listener.MotechLifecycleListener;
import org.motechproject.mds.service.JdoListenerRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;

/**
 * Implementation of the {@link org.motechproject.mds.service.JdoListenerRegistryService}.
 */
@Service("jdoListenerRegistryService")
public class JdoListenerRegistryServiceImpl implements JdoListenerRegistryService {
    private static final Logger LOGGER = LoggerFactory.getLogger(JdoListenerRegistryServiceImpl.class);

    private List<MotechLifecycleListener> listeners = new ArrayList<>();
    private Set<String> entitiesWithListeners = new HashSet<>();

    @Override
    public void registerListener(MotechLifecycleListener listener) {
        if (listeners.contains(listener)) {
            int index = listeners.indexOf(listener);
            MotechLifecycleListener replace = listeners.get(index);

            replace.addMethod(listener.getMethodsByType());
            listeners.set(index, replace);
        } else {
            listeners.add(listener);
        }
    }

    @Override
    public void updateEntityNames(AnnotationProcessingContext context) {
        for (MotechLifecycleListener listener : listeners) {
            String packageName = listener.getPackageName();
            if (!packageName.isEmpty()) {
                List<String> entityNames = new ArrayList<>();
                for (Entity entity : context.findEntitiesByPackage(packageName)) {
                    entityNames.add(entity.getClassName());
                }
                listener.setEntityNames(entityNames);
            }
        }
    }

    @Override
    public String getEntitiesListenerStr() {
        StringBuilder entityListenerNames = new StringBuilder();
        Set<String> entityNames = new HashSet<>();

        //There can be few listeners for one entity, it's why we have to remove
        // duplicate entity names using set collection.
        for (MotechLifecycleListener listener : listeners) {
            entityNames.addAll(listener.getEntityNames());
        }

        entityNames.addAll(entitiesWithListeners);

        for (String entityName : entityNames) {
            entityListenerNames.append(entityName).append('\n');
        }

        return entityListenerNames.toString();
    }

    @Override
    public Set<String> getMethods(MotechLifecycleListener listener, InstanceLifecycleListenerType type) {
        return listener.getMethodsByType().get(type);
    }

    @Override
    public void removeListener(MotechLifecycleListener listener) {
        listeners.remove(listener);
        LOGGER.warn("The InstanceLifecycleListener from service {} for {} was removed", listener.getService(), listener.getParameterType());
    }

    @Override
    public void removeInactiveListeners(String entitiesNames) {
        Set<String> entitiesList = getEntities(entitiesNames);
        List<MotechLifecycleListener> listenersToRemove = new ArrayList<>();

        for (MotechLifecycleListener listener : listeners) {
            if (Collections.disjoint(entitiesList, listener.getEntityNames())) {
                listenersToRemove.add(listener);
                if (!listener.getPackageName().isEmpty() && listener.getEntityNames().isEmpty()) {
                    LOGGER.warn("The InstanceLifecycleListener from service {} for {} was removed, " +
                            "because in its package {} there were no persistable classes.", listener.getService(),
                            listener.getParameterType(), listener.getPackageName());
                } else {
                    LOGGER.warn("The InstanceLifecycleListener from service {} for {} was removed, " +
                            "because {} is not a persistable class.", listener.getService(),
                            listener.getParameterType(), listener.getParameterType());
                }
            }
        }

        listeners.removeAll(listenersToRemove);
    }

    @Override
    public List<MotechLifecycleListener> getListeners() {
        return listeners;
    }

    @Override
    public List<MotechLifecycleListener> getListeners(String entity, InstanceLifecycleListenerType type) {
        List<MotechLifecycleListener> lifecycleListeners = new ArrayList<>();

        for (MotechLifecycleListener listener : listeners) {
            if (listener.getEntityNames().contains(entity) && listener.getMethodsByType().containsKey(type)) {
                lifecycleListeners.add(listener);
            }
        }

        return lifecycleListeners;
    }

    @Override
    public void registerEntityWithListeners(String entity) {
        entitiesWithListeners.add(entity);
    }

    private Set<String> getEntities(String entities) {
        Set<String> entitiesList = new HashSet<>();

        Scanner scanner = new Scanner(entities);
        while (scanner.hasNextLine()) {
            entitiesList.add(scanner.nextLine());
        }
        scanner.close();

        return entitiesList;
    }
}
