package org.motechproject.tasks.it;

import org.eclipse.gemini.blueprint.util.OsgiBundleUtils;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.tasks.domain.Channel;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.domain.TaskActionInformation;
import org.motechproject.tasks.domain.TaskDataProvider;
import org.motechproject.tasks.domain.TaskTriggerInformation;
import org.motechproject.tasks.repository.ChannelsDataService;
import org.motechproject.tasks.repository.TasksDataService;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.DataProviderDataService;
import org.motechproject.tasks.service.TaskDataProviderService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.osgi.helper.ServiceRetriever;
import org.motechproject.testing.osgi.wait.Wait;
import org.motechproject.testing.osgi.wait.WaitCondition;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class TasksBundleIT extends BasePaxIT {

    private static final Integer TRIES_COUNT = 50;

    @Inject
    private ChannelService channelService;
    @Inject
    private EventRelay eventRelay;
    @Inject
    private TaskService taskService;
    @Inject
    private TasksDataService taskDataService;
    @Inject
    private TaskDataProviderService taskDataProviderService;
    @Inject
    private DataProviderDataService dataProviderDataService;
    @Inject
    private BundleContext bundleContext;

    @Override
    protected Collection<String> getAdditionalTestDependencies() {
        return asList("org.motechproject:motech-tasks-test-bundle");
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

        ChannelsDataService channelsDataService = getTasksContext().getBean(ChannelsDataService.class);
        Channel fromDB = channelsDataService.findByModuleName(testBundleName);

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

        TaskDataProvider fromDB = dataProviderDataService.findByName("mrs.name");

        assertNotNull(fromDB);
        assertEquals(fromDB, fromFile);
    }

    private ApplicationContext getTasksContext() {
        return ServiceRetriever.getWebAppContext(bundleContext, "org.motechproject.motech-tasks");
    }

    @After
    public void tearDown() {
        taskDataService.deleteAll();
    }
}
