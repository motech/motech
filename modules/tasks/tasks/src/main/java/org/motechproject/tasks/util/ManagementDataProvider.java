package org.motechproject.tasks.util;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.tasks.service.TaskTriggerHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

public class ManagementDataProvider implements OsgiServiceLifecycleListener {
    private static final Logger LOG = LoggerFactory.getLogger(ChannelRegister.class);
    private TaskTriggerHandler handler;

    @Autowired
    public ManagementDataProvider(TaskTriggerHandler handler) {
        this.handler = handler;
    }

    @Override
    public void bind(Object service, Map serviceProperties) throws IOException {
        if (service instanceof DataProvider) {
            DataProvider provider = (DataProvider) service;
            handler.addDataProvider(provider);
            LOG.info(String.format("Added data provider: %s", provider.getName()));
        }
    }

    @Override
    public void unbind(Object service, Map serviceProperties) {
        if (service instanceof DataProvider) {
            DataProvider provider = (DataProvider) service;
            handler.removeDataProvider(provider);
            LOG.info(String.format("Removed data provider: %s", provider.getName()));
        }
    }

}