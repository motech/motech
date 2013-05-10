package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.TaskDataProvider;

import java.io.InputStream;
import java.util.List;

public interface TaskDataProviderService {

    TaskDataProvider registerProvider(String json);

    TaskDataProvider registerProvider(final InputStream stream);

    TaskDataProvider getProvider(String name);

    TaskDataProvider getProviderById(String providerId);

    List<TaskDataProvider> getProviders();
}
