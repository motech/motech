package org.motechproject.tasks.util;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.service.TaskTriggerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

/**
 * Service for managing data providers.
 */
@Component("managementDataProvider")
public class ManagementDataProvider implements OsgiServiceLifecycleListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementDataProvider.class);

    private TaskTriggerHandler handler;
    private TaskDataProviderService taskDataProviderService;

    /**
     * Service constructor.
     *
     * @param taskDataProviderService  the task data provider service, not null
     */
    public ManagementDataProvider(TaskDataProviderService taskDataProviderService) {
        this(null, taskDataProviderService);
    }

    /**
     * Service constructor.
     *
     * @param handler  the task trigger handler
     * @param taskDataProviderService  the task data provider service, not null
     */
    @Autowired
    public ManagementDataProvider(TaskTriggerHandler handler, TaskDataProviderService taskDataProviderService) {
        this.handler = handler;
        this.taskDataProviderService = taskDataProviderService;
    }

    /**
     * Checks if the given service is a data provider and, if so, registers it in the data provider service.
     *
     * @param service  the service to be registered, null will do nothing
     * @param serviceProperties  unused
     */
    @Override
    public void bind(Object service, Map serviceProperties) throws IOException {
        if (service instanceof DataProvider) {
            DataProvider provider = (DataProvider) service;

            taskDataProviderService.registerProvider(provider.toJSON());

            if (handler != null) {
                handler.addDataProvider(provider);
            }

            LOGGER.info(String.format("Added data provider: %s", provider.getName()));
        }
    }

    /**
     * Checks if given service is a data provider and, if so, unregisters it from the data provider service.
     *
     * @param service  the service to be unregistered, null will do nothing
     * @param serviceProperties  unused
     */
    @Override
    public void unbind(Object service, Map serviceProperties) {
        if (service instanceof DataProvider) {
            DataProvider provider = (DataProvider) service;

            if (handler != null) {
                handler.removeDataProvider(provider.getName());
            }

            LOGGER.info(String.format("Removed data provider: %s", provider.getName()));
        }
    }

}
