package org.motechproject.tasks.util;

import org.eclipse.gemini.blueprint.service.importer.OsgiServiceLifecycleListener;
import org.motechproject.commons.api.DataProvider;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.util.Map;

public class DataProviderRegister implements OsgiServiceLifecycleListener {
    private static final Logger LOG = LoggerFactory.getLogger(ChannelRegister.class);
    private TaskDataProviderService taskDataProviderService;

    @Autowired
    public DataProviderRegister(TaskDataProviderService taskDataProviderService) {
        this.taskDataProviderService = taskDataProviderService;
    }

    @Override
    public void bind(Object service, Map serviceProperties) throws IOException {
        if (service instanceof DataProvider) {
            taskDataProviderService.registerProvider(((DataProvider) service).toJSON());
            LOG.info("Data provider registered");
        }
    }

    @Override
    public void unbind(Object service, Map serviceProperties) {
        LOG.info("TaskDataProviderService unregistered");
    }

}