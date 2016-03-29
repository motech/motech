package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.mds.task.TaskDataProvider;

import java.io.InputStream;
import java.util.List;

/**
 * Service for managing data providers.
 */
public interface TaskDataProviderService {

    /**
     * Registers the data provider defined by the given JSON {@code String}.
     *
     * @param json  the data provider as JSON, not null
     */
    void registerProvider(String json);

    /**
     * Registers the data provider defined by the JSON represented by the given stream.
     *
     * @param stream  the data provider as stream, not null
     */
    void registerProvider(final InputStream stream);

    /**
     * Returns the data provider with the given name.
     *
     * @param name  the name of the data provider, null returns null
     * @return  the data provider with the given name, null if {@code name} was null
     */
    TaskDataProvider getProvider(String name);

    /**
     * Returns the data provider with the given ID.
     *
     * @param providerId  the ID of the data provider, null returns null
     * @return  the data provider with the given ID, null if {@code name} was null
     */
    TaskDataProvider getProviderById(Long providerId);

    /**
     * Returns all data providers.
     *
     * @return  the list of all data providers
     */
    List<TaskDataProvider> getProviders();

    /**
     * Unregisters the given data provider.
     *
     * @param providerName the unique name of the task data provider
     */
    void unregister(String providerName);
}
