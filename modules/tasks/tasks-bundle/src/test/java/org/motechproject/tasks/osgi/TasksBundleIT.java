package org.motechproject.tasks.osgi;

import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.repository.AllChannels;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.List;

import static java.util.Arrays.asList;

public class TasksBundleIT extends BaseOsgiIT {
    private static final Integer TRIES_COUNT = 50;

    public void testTasksService() throws InterruptedException {
        assertNotNull(bundleContext.getServiceReference(PlatformSettingsService.class.getName()));
        assertNotNull(bundleContext.getServiceReference(EventListenerRegistryService.class.getName()));
        assertNotNull(bundleContext.getServiceReference(EventRelay.class.getName()));

        ServiceReference channelServiceOsgi;
        int channelServiceOsgiTries = 0;

        do {
            channelServiceOsgi = bundleContext.getServiceReference(ChannelService.class.getName());
            ++channelServiceOsgiTries;
            Thread.sleep(500);
        } while (channelServiceOsgi == null && channelServiceOsgiTries < TRIES_COUNT);

        assertNotNull(channelServiceOsgi);

        ChannelService channelService = (ChannelService) bundleContext.getService(channelServiceOsgi);
        assertNotNull(channelService);

        Channel fromFile;
        int channelTries = 0;

        do {
            fromFile = channelService.getChannel("test", "test", "0.15");
            ++channelTries;
            Thread.sleep(500);
        } while (fromFile == null && channelTries < TRIES_COUNT);

        assertNotNull(fromFile);

        AllChannels allChannels = getApplicationContext().getBean(AllChannels.class);
        Channel fromDB = allChannels.byChannelInfo("test", "test", "0.15");

        assertNotNull(fromDB);
        assertEquals(fromDB, fromFile);

        allChannels.remove(fromDB);

        fromDB = allChannels.byChannelInfo("test", "test", "0.15");
        assertNull(fromDB);
    }

    @Override
    protected List<String> getImports() {
        return asList("org.motechproject.tasks.util", "org.motechproject.server.config");
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/osgi/testApplicationTasksBundle.xml"};
    }

}
