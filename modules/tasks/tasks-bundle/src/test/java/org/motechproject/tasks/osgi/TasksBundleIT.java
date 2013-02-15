package org.motechproject.tasks.osgi;

import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.repository.AllChannels;
import org.motechproject.tasks.repository.AllTaskDataProviders;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.List;

import static java.util.Arrays.asList;

public class TasksBundleIT extends BaseOsgiIT {
    private static final Integer TRIES_COUNT = 50;

    public void testCoreServiceReferences() {
        assertNotNull(bundleContext.getServiceReference(PlatformSettingsService.class.getName()));
        assertNotNull(bundleContext.getServiceReference(EventListenerRegistryService.class.getName()));
        assertNotNull(bundleContext.getServiceReference(EventRelay.class.getName()));
    }

    public void testChannelService() throws InterruptedException {
        ChannelService channelService = getService(ChannelService.class);
        Channel fromFile;
        int tries = 0;

        do {
            fromFile = channelService.getChannel("test", "test", "0.15");
            ++tries;
            Thread.sleep(500);
        } while (fromFile == null && tries < TRIES_COUNT);

        assertNotNull(fromFile);

        AllChannels allChannels = getApplicationContext().getBean(AllChannels.class);
        Channel fromDB = allChannels.byChannelInfo("test", "test", "0.15");

        assertNotNull(fromDB);
        assertEquals(fromDB, fromFile);
    }

    public void testDataProviderService() throws InterruptedException {
        TaskDataProviderService taskDataProviderService = getService(TaskDataProviderService.class);
        TaskDataProvider fromFile;
        int tries = 0;

        do {
            fromFile = taskDataProviderService.getProvider("data.provider.mrs.name");
            ++tries;
            Thread.sleep(500);
        } while (fromFile == null && tries < TRIES_COUNT);

        assertNotNull(fromFile);

        AllTaskDataProviders allTaskDataProviders = getApplicationContext().getBean(AllTaskDataProviders.class);
        TaskDataProvider fromDB = allTaskDataProviders.byName("data.provider.mrs.name");

        assertNotNull(fromDB);
        assertEquals(fromDB, fromFile);
    }

    @Override
    protected List<String> getImports() {
        return asList(
                "org.motechproject.tasks.util",
                "org.motechproject.server.config",
                "org.motechproject.commons.couchdb.service",
                "org.motechproject.commons.api"
        );
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"META-INF/osgi/testApplicationTasksBundle.xml"};
    }

    private <T> T getService(Class<T> clazz) throws InterruptedException {
        T service = clazz.cast(bundleContext.getService(getServiceReference(clazz)));

        assertNotNull(service);

        return service;
    }

    private <T> ServiceReference getServiceReference(Class<T> clazz) throws InterruptedException {
        ServiceReference serviceReference;
        int tries = 0;

        do {
            serviceReference = bundleContext.getServiceReference(clazz.getName());
            ++tries;
            Thread.sleep(500);
        } while (serviceReference == null && tries < TRIES_COUNT);

        assertNotNull(serviceReference);

        return serviceReference;
    }

}
