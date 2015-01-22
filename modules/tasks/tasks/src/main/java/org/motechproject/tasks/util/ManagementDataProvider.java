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

@Component("managementDataProvider")
public class ManagementDataProvider implements OsgiServiceLifecycleListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(ManagementDataProvider.class);
    private TaskTriggerHandler handler;
    private TaskDataProviderService taskDataProviderService;

    public ManagementDataProvider(TaskDataProviderService taskDataProviderService) {
        this(null, taskDataProviderService);
    }

    @Autowired
    public ManagementDataProvider(TaskTriggerHandler handler, TaskDataProviderService taskDataProviderService) {
        this.handler = handler;
        this.taskDataProviderService = taskDataProviderService;
    }

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
