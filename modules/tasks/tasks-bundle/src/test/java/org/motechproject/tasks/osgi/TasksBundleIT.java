package org.motechproject.tasks.osgi;

import org.apache.commons.collections.Predicate;
import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.repository.AllChannels;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.ArrayList;
import java.util.List;

import static java.util.Arrays.asList;
import static org.apache.commons.collections.CollectionUtils.find;
import static org.apache.commons.lang.StringUtils.containsIgnoreCase;

public class TasksBundleIT extends BaseOsgiIT {
    private static final Integer TRIES_COUNT = 15;

    public void testTasksService() {
        assertNotNull(bundleContext.getServiceReference(PlatformSettingsService.class.getName()));
        assertNotNull(bundleContext.getServiceReference(EventListenerRegistryService.class.getName()));
        assertNotNull(bundleContext.getServiceReference(EventRelay.class.getName()));

        ServiceReference channelServiceOsgi;
        int channelServiceOsgiTries = 0;

        do {
            channelServiceOsgi = bundleContext.getServiceReference(ChannelService.class.getName());
            ++channelServiceOsgiTries;
        } while (channelServiceOsgi == null && channelServiceOsgiTries < TRIES_COUNT);

        assertNotNull(channelServiceOsgi);

        ChannelService channelService = (ChannelService) bundleContext.getService(channelServiceOsgi);
        assertNotNull(channelService);

        Channel fromFile;
        int channelTries = 0;

        do {
            fromFile = channelService.getChannel("test", "test", "0.15");
            ++channelTries;
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

    @Override
    protected String[] getTestBundlesNames() {
        // bundles are started one by one, server-bundle need server-config to run
        // but server-config is later in list than server-bundle
        List<String> list = new ArrayList<>(asList(super.getTestBundlesNames()));

        String serverConfig = (String) find(list, new ContainsPredicate("motech-platform-server-config"));
        String serverBundle = (String) find(list, new ContainsPredicate("motech-platform-server-bundle"));

        list.remove(serverConfig);
        list.add(list.indexOf(serverBundle), serverConfig);

        return list.toArray(new String[list.size()]);
    }

    private class ContainsPredicate implements Predicate {
        private String match;

        private ContainsPredicate(String match) {
            this.match = match;
        }

        @Override
        public boolean evaluate(Object object) {
            return containsIgnoreCase(String.valueOf(object), match);
        }
    }

}
