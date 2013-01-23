package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.TaskDataProvider;

import java.io.InputStream;
import java.util.List;

public interface TaskDataProviderService {

    void registerProvider(String json);

    void registerProvider(final InputStream stream);

    TaskDataProvider getProvider(String name);

    List<TaskDataProvider> getProviders();

}
