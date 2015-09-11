package org.motechproject.mds.service;

import org.motechproject.mds.annotations.InstanceLifecycleListenerType;
import org.motechproject.mds.annotations.internal.AnnotationProcessingContext;
import org.motechproject.mds.listener.MotechLifecycleListener;

import java.util.List;
import java.util.Set;

/**
 * Gives access to the registry of listeners for persistence events.
 */
public interface JdoListenerRegistryService {

    /**
     * Registers the listener. If the registry already has listener
     * for this type of persistence event, the methods from
     * the given listener will be added to the existed one.
     *
     * @param listener the listener to be registered
     */
    void registerListener(MotechLifecycleListener listener);

    /**
     * Updates entity names for package listeners
     *
     */
    void updateEntityNames(AnnotationProcessingContext context);

    /**
     * Gets entities from listeners in one string, where every
     * entity name is in a new line.
     *
     * @return the entities from listeners
     */
    String getEntitiesListenerStr();

    /**
     * Gets the list of methods from the listener for the given type of persistence event.
     *
     * @param listener the listener for persistence object
     * @param type the type of persistence event
     * @return the list of methods
     */
    Set<String> getMethods(MotechLifecycleListener listener, InstanceLifecycleListenerType type);

    /**
     * Removes the listener from the registry.
     *
     * @param listener the listener to be removed
     */
    void removeListener(MotechLifecycleListener listener);

    /**
     * Removes inactive listeners in the registry. This method checks
     * if entities in listeners are still persistable classes.
     *
     * @param entitiesNames the names of all active entities
     */
    void removeInactiveListeners(String entitiesNames);

    /**
     * Gets the listeners from the registry.
     *
     * @return the list of listeners
     */
    List<MotechLifecycleListener> getListeners();

    /**
     * Gets the list of listeners for the given entity and type of persistence event.
     *
     * @param entity the class name of persistence object
     * @param type the type of persistence event
     * @return the list of listeners
     */
    List<MotechLifecycleListener> getListeners(String entity, InstanceLifecycleListenerType type);

    /**
     * Adds the given entity to the list of entities for which there may exist instance
     * lifecycle listeners.
     *
     * @param entity the class name of the entity to add
     */
    void registerEntityWithListeners(String entity);
}
