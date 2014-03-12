package org.motechproject.mds.testutil;

import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.service.TaskDataProviderService;

import java.io.InputStream;
import java.util.List;

/**
 * This mock is required for ITs, so that the context is created properly.
 * It does not do anything.
 */
public class MockTaskDataProviderService implements TaskDataProviderService {
    @Override
    public TaskDataProvider registerProvider(String json) {
        return null;
    }

    @Override
    public TaskDataProvider registerProvider(InputStream stream) {
        return null;
    }

    @Override
    public TaskDataProvider getProvider(String name) {
        return null;
    }

    @Override
    public TaskDataProvider getProviderById(String providerId) {
        return null;
    }

    @Override
    public List<TaskDataProvider> getProviders() {
        return null;
    }
}
