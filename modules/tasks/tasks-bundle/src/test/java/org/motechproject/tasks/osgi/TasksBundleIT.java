package org.motechproject.tasks.osgi;

import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.domain.TaskEventInformation;
import org.motechproject.tasks.repository.AllChannels;
import org.motechproject.tasks.repository.AllTaskDataProviders;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceReference;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.util.List;

import static java.util.Arrays.asList;

public class TasksBundleIT extends BaseOsgiIT {
    private static final Integer TRIES_COUNT = 50;

    static boolean firstTime = true;
    static int channelsLoadedOnStartup;

    @Override
    protected void onSetUp() throws Exception {
        super.onSetUp();

        // @BeforeClass workaround for junit3
        if (firstTime) {
            firstTime = false;
            ChannelService channelService = getService(ChannelService.class);
            channelsLoadedOnStartup = channelService.getAllChannels().size();
        }
    }

    public void testCoreServiceReferences() {
        assertNotNull(bundleContext.getServiceReference(PlatformSettingsService.class.getName()));
        assertNotNull(bundleContext.getServiceReference(EventRelay.class.getName()));
        assertNotNull(bundleContext.getServiceReference(TaskService.class.getName()));
    }

    public void testOnlyChannelsCreatedDuringStartupArePresent() throws BundleException, InterruptedException {
        assertEquals(2, channelsLoadedOnStartup);    // one each from motech-tasks-test-bundle and TasksBundleIT bundle
    }

    public void testChannelService() throws InterruptedException {
        ChannelService channelService = getService(ChannelService.class);
        Channel fromFile;
        int tries = 0;

        String testBundleName = bundleContext.getBundle().getSymbolicName();
        do {
            fromFile = channelService.getChannel(testBundleName);
            ++tries;
            Thread.sleep(500);
        } while (fromFile == null && tries < TRIES_COUNT);

        assertNotNull(fromFile);

        AllChannels allChannels = getApplicationContext().getBean(AllChannels.class);
        Channel fromDB = allChannels.byModuleName(testBundleName);

        assertNotNull(fromDB);
        assertEquals(fromDB, fromFile);
    }

    public void testDataProviderService() throws InterruptedException {
        TaskDataProviderService taskDataProviderService = getService(TaskDataProviderService.class);
        Resource resource = applicationContext.getResource("classpath:task-data-provider.json");
        try {
            taskDataProviderService.registerProvider(resource.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        TaskDataProvider fromFile;
        int tries = 0;

        do {
            fromFile = taskDataProviderService.getProvider("mrs.name");
            ++tries;
            Thread.sleep(500);
        } while (fromFile == null && tries < TRIES_COUNT);

        assertNotNull(fromFile);

        AllTaskDataProviders allTaskDataProviders = getApplicationContext().getBean(AllTaskDataProviders.class);
        TaskDataProvider fromDB = allTaskDataProviders.byName("mrs.name");

        assertNotNull(fromDB);
        assertEquals(fromDB, fromFile);
    }

    public void testChannelRegistration() throws BundleException, InterruptedException {
        String moduleName = "motech-tasks-test-bundle";

        ChannelService channelService = getService(ChannelService.class);

        Channel channel = channelService.getChannel(moduleName);
        assertNotNull(channel);
    }

    public void testChannelDeregistrationAndTaskDeActivationWhenBundleStops() throws BundleException, InterruptedException {
        TaskService taskService = getService(TaskService.class);

        String moduleName = "motech-tasks-test-bundle";

        TaskEventInformation trigger = new TaskEventInformation("Test Task", "testChannel", moduleName, "0.1", "triggerEvent");
        Task task = new Task("testTask", trigger, asList(new TaskActionInformation("Test Action", "testChannel", moduleName, "0.1", "actionEvent")), null, true, true);
        taskService.save(task);

        Bundle module = findBundleByName(moduleName);
        module.stop();

        ChannelService channelService = getService(ChannelService.class);
        Channel channel = channelService.getChannel(moduleName);
        assertNull(channel);

        for (int i = 0; i < TRIES_COUNT; i++) {
            Task existingTask = findTask(taskService, "testTask");
            if (!existingTask.hasRegisteredChannel()) {
                return;
            }
        }
        fail();
    }

    private Task findTask(TaskService taskService, String name) {
        for (Task task : taskService.getAllTasks()) {
            if (task.getName().equals(name)) {
                return task;
            }
        }
        return null;
    }

    private Bundle findBundleByName(String name) {
        for (Bundle bundle : bundleContext.getBundles()) {
            String symbolicName = bundle.getSymbolicName();
            if (symbolicName != null && symbolicName.contains(name)) {
                return bundle;
            }
        }
        return null;
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
