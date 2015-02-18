package org.motechproject.mds.service.impl;

import org.motechproject.mds.domain.InstanceLifecycleListenerType;
import org.motechproject.mds.listener.MotechLifecycleListener;
import org.motechproject.mds.service.JdoListenerRegistryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public String getEntitiesListenerStr() {
        StringBuilder entityListenerNames = new StringBuilder();
        Set<String> entityNames = new HashSet<>();

        //There can be few listeners for one entity, it's why we have to remove
        // duplicate entity names using set collection.
        for (MotechLifecycleListener listener : listeners) {
            entityNames.add(listener.getEntity());
        }

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
        LOGGER.warn("The InstanceLifecycleListeners from service {} for {} were removed", listener.getService(), listener.getEntity());
    }

    @Override
    public void removeInactiveListeners(String entitiesNames) {
        Set<String> entitiesList = getEntities(entitiesNames);
        List<MotechLifecycleListener> listenersToRemove = new ArrayList<>();

        for (MotechLifecycleListener listener : listeners) {
            if (!entitiesList.contains(listener.getEntity())) {
                listenersToRemove.add(listener);
                LOGGER.warn("The InstanceLifecycleListeners from service {} for {} were removed, " +
                        "because {} is not a persistable class", listener.getService(), listener.getEntity(), listener.getEntity());
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
            if (listener.getEntity().equals(entity) && listener.getMethodsByType().containsKey(type)) {
                lifecycleListeners.add(listener);
            }
        }

        return lifecycleListeners;
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