package org.motechproject.tasks.it;

import com.google.gson.reflect.TypeToken;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.commons.api.json.MotechJsonReader;
import org.motechproject.config.domain.SettingsRecord;
import org.motechproject.config.mds.SettingsDataService;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.util.Order;
import org.motechproject.tasks.contract.ActionEventRequest;
import org.motechproject.tasks.contract.ChannelRequest;
import org.motechproject.tasks.contract.json.ActionEventRequestDeserializer;
import org.motechproject.tasks.domain.mds.channel.Channel;
import org.motechproject.tasks.domain.mds.channel.builder.ChannelBuilder;
import org.motechproject.tasks.domain.mds.task.Task;
import org.motechproject.tasks.domain.mds.task.TaskActionInformation;
import org.motechproject.tasks.domain.mds.task.TaskActivity;
import org.motechproject.tasks.domain.enums.TaskActivityType;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.exception.ActionNotFoundException;
import org.motechproject.tasks.repository.ChannelsDataService;
import org.motechproject.tasks.repository.TaskActivitiesDataService;
import org.motechproject.tasks.service.ChannelService;
import org.motechproject.tasks.service.TaskService;
import org.motechproject.tasks.service.TriggerHandler;
import org.motechproject.testing.osgi.BasePaxIT;
import org.motechproject.testing.osgi.container.MotechNativeTestContainerFactory;
import org.motechproject.testing.osgi.wait.Wait;
import org.motechproject.testing.osgi.wait.WaitCondition;
import org.motechproject.testmodule.domain.TaskTestObject;
import org.motechproject.testmodule.service.TasksTestService;
import org.ops4j.pax.exam.ExamFactory;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerSuite;
import org.osgi.framework.BundleContext;

import javax.inject.Inject;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertTrue;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerSuite.class)
@ExamFactory(MotechNativeTestContainerFactory.class)
public class ActionParametersBundleIT extends BasePaxIT {

    private static final String TASK_TEST_CHANNEL_NAME = "motech-tasks-test-bundle";
    private static final String TASK_TEST_SERVICE_INTERFACE = "org.motechproject.testmodule.service.TasksTestService";
    private static final String TASK_TEST_SERVICE_METHOD = "createTestObjectWithPostActionParameter";
    private static final String TASK_TEST_ACTION_NAME = "Test Action return Post Action Params";
    private static final String MDS_CHANNEL_NAME = "org.motechproject.motech-platform-dataservices-entities";
    private static final String VERSION = "0.29";
    private static final String TRIGGER_SUBJECT = "mds.crud.serverconfig.SettingsRecord.CREATE";

    private static final Integer MAX_RETRIES_BEFORE_FAIL = 20;
    private static final Integer WAIT_TIME = 2000;

    @Inject
    private ChannelService channelService;

    @Inject
    private ChannelsDataService channelsDataService;

    @Inject
    private BundleContext bundleContext;

    @Inject
    private TaskService taskService;

    @Inject
    private TasksTestService tasksTestService;

    @Inject
    private TriggerHandler triggerHandler;

    @Inject
    private SettingsDataService settingsDataService;

    @Inject
    private TaskActivitiesDataService taskActivitiesDataService;


    private MotechJsonReader motechJsonReader = new MotechJsonReader();

    private Long taskID;

    @Override
    protected Collection<String> getAdditionalTestDependencies() {
        return Arrays.asList(
                "org.motechproject:motech-tasks-test-bundle"
        );
    }

    @Before
    public void setUp() throws IOException, InterruptedException {
        createAdminUser();
        login();
        Channel channel = loadChannel();
        setUpSecurityContext("motech", "motech", "manageTasks");

        channelService.addOrUpdate(channel);
        waitForChannel(MDS_CHANNEL_NAME);
    }

    @After
    public void clean() {
        deleteTask(taskID);
        removeChannels(channelsDataService.retrieveAll());
    }

    @Test
    public void testActionWithPostActionParameters() throws InterruptedException, IOException, ActionNotFoundException {
        taskID = createTestTask();

        activateTrigger();
        assertTrue(waitForTaskExecution(taskID));

        List<TaskTestObject> fetchedTaskTestObjects = tasksTestService.getTaskTestObjects();

        List<TaskTestObject> expectedTaskTestObject = prepareExpectedTaskTestObject();

        assertTrue(fetchedTaskTestObjects.contains(expectedTaskTestObject.get(0)));
        assertTrue(fetchedTaskTestObjects.contains(expectedTaskTestObject.get(1)));
        assertTrue(fetchedTaskTestObjects.contains(expectedTaskTestObject.get(2)));
    }

    private Long createTestTask() {
        TaskTriggerInformation triggerInformation = new TaskTriggerInformation("CREATE SettingsRecord", "data-services", MDS_CHANNEL_NAME,
                VERSION, TRIGGER_SUBJECT, TRIGGER_SUBJECT);

        List<TaskActionInformation> actions = new ArrayList<>();

        actions.add(prepareActionWithKeyValue("testName", "ActionValue"));
        actions.add(prepareActionWithKeyValue("testName", "{{pa.0.testNameWithPrefix}}"));
        actions.add(prepareActionWithKeyValue("testName", "{{pa.1.testNameWithPrefix}}"));

        Task task = new Task("TestTask", triggerInformation, actions, null, true, true);
        taskService.save(task);

        triggerHandler.registerHandlerFor(task.getTrigger().getEffectiveListenerSubject());

        return task.getId();
    }

    private void activateTrigger() {
        settingsDataService.create(new SettingsRecord());
    }

    private void waitForChannel(String channelName) throws InterruptedException {
        (new Wait(new WaitCondition() {
            public boolean needsToWait() {
                try {
                    return ActionParametersBundleIT.this.findChannel(channelName) == null;
                } catch (IOException var2) {
                    ActionParametersBundleIT.this.getLogger().error("Error while searching for channel " + channelName, var2);
                    return false;
                }
            }
        }, 20000)).start();
    }

    protected Channel findChannel(String channelName) throws IOException {
        this.getLogger().info(String.format("Looking for %s", new Object[]{channelName}));
        this.getLogger().info(String.format("There are %d channels in total", new Object[]{Integer.valueOf(this.channelService.getAllChannels().size())}));
        return this.channelService.getChannel(channelName);
    }

    private Channel loadChannel() throws IOException {
        Type type = new TypeToken<ChannelRequest>() {}.getType();

        HashMap<Type, Object> typeAdapters = new HashMap<>();
        typeAdapters.put(ActionEventRequest.class, new ActionEventRequestDeserializer());
        StringWriter writer = new StringWriter();

        try (InputStream stream = getClass().getResourceAsStream("/task-testmodule-channel.json")) {
            IOUtils.copy(stream, writer);
        }
            ChannelRequest channelRequest = (ChannelRequest) motechJsonReader.readFromString(writer.toString(), type, typeAdapters);
            channelRequest.setModuleName(TASK_TEST_CHANNEL_NAME);
            channelRequest.setModuleVersion("0.29.0.SNAPSHOT");

        return ChannelBuilder.fromChannelRequest(channelRequest).build();
    }

    private boolean waitForTaskExecution(Long taskID) throws InterruptedException {
        getLogger().info("testTasksIntegration starts waiting for task to execute");
        int retries = 0;
        while (retries < MAX_RETRIES_BEFORE_FAIL && !hasTaskExecuted(taskID)) {
            retries++;
            Thread.sleep(WAIT_TIME);
        }
        if (retries == MAX_RETRIES_BEFORE_FAIL) {
            getLogger().info("Task execution failed");
            return false;
        }
        getLogger().info("Task executed after " + retries + " retries, what took about "
                + (retries * WAIT_TIME) / 1000 + " seconds");
        return true;
    }

    private boolean hasTaskExecuted(Long taskID) {
        Set<TaskActivityType> activityTypes = new HashSet<>();
        activityTypes.add(TaskActivityType.SUCCESS);
        QueryParams queryParams = new QueryParams((Order) null);
        List<TaskActivity> taskActivities = taskActivitiesDataService.byTaskAndActivityTypes(taskID, activityTypes, queryParams);

        return taskActivities.size() == 1;
    }

    private TaskActionInformation prepareActionWithKeyValue(String key, String value) {
        TaskActionInformation actionInformation = new TaskActionInformation(TASK_TEST_ACTION_NAME, TASK_TEST_CHANNEL_NAME,
                TASK_TEST_CHANNEL_NAME, VERSION, TASK_TEST_SERVICE_INTERFACE, TASK_TEST_SERVICE_METHOD);

        Map<String, String> values = new HashMap<>();
        values.put(key, value);
        actionInformation.setValues(values);

        return actionInformation;
    }

    private List<TaskTestObject> prepareExpectedTaskTestObject() {
        List<TaskTestObject> expectedTaskTestObjects = new ArrayList<>();

        expectedTaskTestObjects.add(new TaskTestObject("ActionValue", "ActionValue - postActionParameter"));
        expectedTaskTestObjects.add(new TaskTestObject("ActionValue - postActionParameter", "ActionValue - postActionParameter - postActionParameter"));
        expectedTaskTestObjects.add(new TaskTestObject("ActionValue - postActionParameter - postActionParameter", "ActionValue - postActionParameter - postActionParameter - postActionParameter"));

        return expectedTaskTestObjects;
    }

    private void deleteTask(Long taskID) {
        taskService.deleteTask(taskID);
    }

    private void removeChannels(List<Channel> channels) {
        Iterator<Channel> it = channels.iterator();
        while (it.hasNext()) {
            Channel channel = it.next();
            if (StringUtils.equals(TASK_TEST_CHANNEL_NAME, channel.getModuleName()) || StringUtils.equals(MDS_CHANNEL_NAME, channel.getModuleName()) ) {
                channelService.unregisterChannel(channel.getModuleName());
            }
        }
    }
}
