package org.motechproject.tasks.osgi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.listener.EventRelay;
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
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Set;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class TasksBundleIT extends BasePaxIT {

    private static final Integer TRIES_COUNT = 50;

    static boolean firstTime = true;
    static int channelsLoadedOnStartup;

    @Inject
    private ChannelService channelService;
    @Inject
    private EventRelay eventRelay;
    @Inject
    private TaskService taskService;
    @Inject
    private TaskDataProviderService taskDataProviderService;
    @Inject
    private BundleContext bundleContext;

    @Override
    protected Set<String> getTestDependencies() {
        Set<String> testDependencies = super.getTestDependencies();
        testDependencies.add("org.motechproject:motech-tasks-test-bundle");
        return testDependencies;
    }

    @Before
    public void setUp() throws Exception {
        if (firstTime) {
            firstTime = false;
            channelsLoadedOnStartup = channelService.getAllChannels().size();
        }
    }

    @Test
    public void testCoreServiceReferences() {
        assertNotNull(eventRelay);
        assertNotNull(taskService);
    }

    @Test
    public void testChannelService() throws InterruptedException {
        Channel fromFile;
        int tries = 0;

        String testBundleName = bundleContext.getBundle().getSymbolicName();
        do {
            fromFile = channelService.getChannel(testBundleName);
            ++tries;
            Thread.sleep(500);
        } while (fromFile == null && tries < TRIES_COUNT);

        assertNotNull(fromFile);

        AllChannels allChannels = getTasksContext().getBean(AllChannels.class);
        Channel fromDB = allChannels.byModuleName(testBundleName);

        assertNotNull(fromDB);
        assertEquals(fromDB, fromFile);
    }

    @Test
    public void testDataProviderService() throws InterruptedException {
        Resource resource = ServiceRetriever.getWebAppContext(bundleContext)
                .getResource("classpath:task-data-provider.json");

        try (InputStream in = resource.getInputStream()) {
            taskDataProviderService.registerProvider(in);
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

        AllTaskDataProviders allTaskDataProviders = getTasksContext().getBean(AllTaskDataProviders.class);
        TaskDataProvider fromDB = allTaskDataProviders.byName("mrs.name");

        assertNotNull(fromDB);
        assertEquals(fromDB, fromFile);
    }


    @Test
    public void testChannelRegistrationAndDeregistrationAndTaskDeActivationWhenBundleStops() throws BundleException{
        String moduleName = "motech-tasks-test-bundle";

        Channel channel = channelService.getChannel(moduleName);
        assertNotNull(channel);

        TaskEventInformation trigger = new TaskEventInformation("Test Task", "testChannel", moduleName, "0.1", "triggerEvent");
        Task task = new Task("testTask", trigger, asList(
                new TaskActionInformation("Test Action", "testChannel", moduleName, "0.1", "actionEvent")),
                null, true, true);
        taskService.save(task);

        Bundle module = findBundleByName(moduleName);
        module.stop();

        channel = channelService.getChannel(moduleName);
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

    private ApplicationContext getTasksContext() {
        return ServiceRetriever.getWebAppContext(bundleContext, "org.motechproject.motech-tasks");
    }
}
